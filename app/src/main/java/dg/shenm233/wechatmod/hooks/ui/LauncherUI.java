package dg.shenm233.wechatmod.hooks.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import dg.shenm233.wechatmod.Common;

import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getAdditionalInstanceField;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.setAdditionalInstanceField;
import static dg.shenm233.wechatmod.BuildConfig.DEBUG;
import static dg.shenm233.wechatmod.ObfuscationHelper.MM_Classes;
import static dg.shenm233.wechatmod.ObfuscationHelper.MM_Fields;
import static dg.shenm233.wechatmod.ObfuscationHelper.MM_Methods;


public class LauncherUI {
    public static final String TAG = "launcherUIMod";

    public static void init(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XC_MethodHook methodHook;

        methodHook = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (DEBUG) XposedBridge.log("wechat onCreate hook!");
                Activity launcherUIInstance = (Activity) param.thisObject;
                Common.MM_AppContext = launcherUIInstance.getApplicationContext();

                LauncherUI launcherUIMod = new LauncherUI(launcherUIInstance);
                setAdditionalInstanceField(launcherUIInstance, TAG, launcherUIMod);
            }
        };
        findAndHookMethod(MM_Classes.LauncherUI, "onCreate", Bundle.class, methodHook);

        methodHook = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                LauncherUI launcherUIMod = (LauncherUI) getAdditionalInstanceField(param.thisObject, TAG);
                launcherUIMod.isMainTabCreated = (boolean) getObjectField(param.thisObject,
                        MM_Fields.LauncherUI.isMainTabCreated);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                LauncherUI launcherUIMod = (LauncherUI) getAdditionalInstanceField(param.thisObject, TAG);
                if (!launcherUIMod.isMainTabCreated) {
                    if (DEBUG) XposedBridge.log("on maintab create");
                    if ((boolean) callStaticMethod(MM_Classes.AccountStorage,
                            MM_Methods.AccountStorage.isMMcoreReady)) {
                        launcherUIMod.moveTabsToTop();
                    } else {
                        if (DEBUG)
                            XposedBridge.log("mmcore has not ready, finish LauncherUI hook");
                    }
                }
            }
        };
        findAndHookMethod(MM_Classes.LauncherUI, MM_Methods.LauncherUI.startMainUI, methodHook);
    }

    /***********************************
     * Let's do magic!
     ***********************************/
    private boolean isMainTabCreated;
    private final Activity mLauncherUI;

    public LauncherUI(Activity launcherUI) {
        mLauncherUI = launcherUI;
    }

    private void moveTabsToTop() {
        ViewGroup customViewPager = (ViewGroup) getObjectField(mLauncherUI,
                MM_Fields.LauncherUI.customViewPager);
        View tabView = (View) getObjectField(mLauncherUI, MM_Fields.LauncherUI.tabView);
        LinearLayout parent = (LinearLayout) customViewPager.getParent();
        parent.removeView(tabView);
        parent.addView(tabView, 0);
    }
}
