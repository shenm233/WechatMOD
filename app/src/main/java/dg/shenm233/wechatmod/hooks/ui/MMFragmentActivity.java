package dg.shenm233.wechatmod.hooks.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import dg.shenm233.wechatmod.Common;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static dg.shenm233.wechatmod.BuildConfig.DEBUG;
import static dg.shenm233.wechatmod.ObfuscationHelper.MM_Classes;
import static dg.shenm233.wechatmod.ObfuscationHelper.MM_Fields;
import static dg.shenm233.wechatmod.ObfuscationHelper.MM_Methods;
import static dg.shenm233.wechatmod.ObfuscationHelper.MM_Res;

public class MMFragmentActivity {
    public static int actionBarColor = Color.parseColor("#263238");
    private static ColorDrawable actionBarColorDrawable = new ColorDrawable();

    public static void init(final XC_LoadPackage.LoadPackageParam lpparam) {
        findAndHookMethod(MM_Classes.MMFragmentActivity, "onCreate", Bundle.class, new XC_MethodHook() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Activity activity = (Activity) param.thisObject;
                Window window = activity.getWindow();
                String activityName = activity.getClass().getName();
                Common.XMOD_PREFS.reload();
                actionBarColor = getActionBarColorFromPrefs();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    //fix for com.tencent.mm.plugin.gallery.ui.ImagePreviewUI
                    if (!"com.tencent.mm.plugin.gallery.ui.ImagePreviewUI".equals(activityName)) {
                        if ("com.tencent.mm.ui.LauncherUI".equals(activityName)) {
                            /*
                             * enabling status bar color will cause THE CHATTING TEXTVIEW
                             * show below the Navigation Bar,so it just disable status bar color
                             * for LauncherUI to fix.
                             */
                            if (Common.XMOD_PREFS.getBoolean(Common.KEY_FORCE_STATUSBAR_COLOR, false)) {
                                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            } else {
                                window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            }
                        } else {
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        }
                    }
                }
            }
        });

        findAndHookMethod(MM_Classes.MMFragmentActivity, "onResume", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Activity activity = (Activity) param.thisObject;
                String activityName = activity.getClass().getName();
                if (!"com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyIndexUI".equals(activityName) &&
                        !"com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyPrepareUI".equals(activityName)) {
                    actionBarColor = getActionBarColorFromPrefs();
                    Object actionbar = callMethod(activity, "getActionBar");
                    actionBarColorDrawable.setColor(actionBarColor);
                    if (actionbar != null) {
                        callMethod(actionbar, "setBackgroundDrawable", actionBarColorDrawable);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            Window window = activity.getWindow();
                            window.setStatusBarColor(Common.getDarkerColor(actionBarColor, 0.85f));
                        }
                    }
                }
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Activity activity = (Activity) param.thisObject;
                Object actionbar = callMethod(activity, "getActionBar");
                if (actionbar != null) {
                    ViewGroup customView = (ViewGroup) callMethod(actionbar, "getCustomView");
                    ((View) customView.findViewById(MM_Res.divider)).setVisibility(View.INVISIBLE);
                }
            }
        });

        findAndHookMethod(MM_Classes.ChattingUInonActivity, MM_Methods.setActionBarView, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (DEBUG) XposedBridge.log("changing ChattingUInonActivity ActionBar color!");
                ViewGroup actionBarContainer = (ViewGroup) getObjectField(param.thisObject, MM_Fields.actionBarContainer);
                if (actionBarContainer != null) {
                    ViewGroup actionbarview = (ViewGroup) actionBarContainer.findViewById(MM_Res.custom_action_bar);
                    actionbarview.setBackgroundColor(actionBarColor);
                    View divider = (View) actionbarview.findViewById(MM_Res.divider);
                    divider.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public static int getActionBarColorFromPrefs() {
        int actionbar_color;
        if (Common.XMOD_PREFS != null) {
            Common.XMOD_PREFS.reload();
            if (Common.XMOD_PREFS.getAll().size() > 0) {
                actionbar_color = Color.parseColor(Common.XMOD_PREFS.getString(Common.KEY_ACTIONBAR_COLOR, "#263238"));
            } else {
                actionbar_color = Color.parseColor("#263238");
            }
        } else {
            actionbar_color = Color.parseColor("#263238");
        }
        return actionbar_color;
    }
}
