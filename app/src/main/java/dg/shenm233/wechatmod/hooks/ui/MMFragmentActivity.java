package dg.shenm233.wechatmod.hooks.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import dg.shenm233.wechatmod.Common;
import dg.shenm233.wechatmod.ObfuscationHelper;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static dg.shenm233.wechatmod.ObfuscationHelper.MM_Classes;
import static dg.shenm233.wechatmod.ObfuscationHelper.MM_Fields;
import static dg.shenm233.wechatmod.ObfuscationHelper.MM_Methods;
import static dg.shenm233.wechatmod.ObfuscationHelper.MM_Res;
import static dg.shenm233.wechatmod.BuildConfig.DEBUG;

public class MMFragmentActivity {
    private static ColorDrawable actionBarColorDrawable = new ColorDrawable();

    public static void init(final XC_LoadPackage.LoadPackageParam lpparam) {
        findAndHookMethod(MM_Classes.MMFragmentActivity, "onCreate", Bundle.class, new XC_MethodHook() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Activity activity = (Activity) param.thisObject;
                Window window = activity.getWindow();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                }
            }
        });

        findAndHookMethod(MM_Classes.MMFragmentActivity, "onResume", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                int actionbar_color;
                actionbar_color = getActionBarColorFromPrefs();
                Activity activity = (Activity) param.thisObject;
                Object actionbar = callMethod(activity, "getActionBar");
                actionBarColorDrawable.setColor(actionbar_color);
                if (actionbar != null) {
                    callMethod(actionbar, "setBackgroundDrawable", actionBarColorDrawable);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Window window = activity.getWindow();
                        window.setStatusBarColor(actionbar_color);
                    }
                }
            }
        });

        findAndHookMethod(MM_Classes.ChattingUInonActivity, MM_Methods.setActionBarView, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (DEBUG) XposedBridge.log("changing ChattingUInonActivity ActionBar color!");
                int actionbar_color;
                Object actionBarContainer = getObjectField(param.thisObject, MM_Fields.actionBarContainer);
                if (actionBarContainer != null) {
                    actionbar_color = getActionBarColorFromPrefs();
                    ViewGroup actionbarview = (ViewGroup) callMethod(actionBarContainer, "findViewById", MM_Res.custom_action_bar);
                    actionbarview.setBackgroundColor(actionbar_color);
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
