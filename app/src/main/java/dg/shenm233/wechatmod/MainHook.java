package dg.shenm233.wechatmod;

import android.app.Activity;
import android.content.Context;
import android.content.res.XModuleResources;
import android.os.Bundle;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import dg.shenm233.wechatmod.hooks.ui.LauncherUI;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;

import static dg.shenm233.wechatmod.ObfuscationHelper.MM_Classes;
import static dg.shenm233.wechatmod.ObfuscationHelper.MM_Methods;

import static dg.shenm233.wechatmod.Common.Wechat_PACKAGENAME;
import static dg.shenm233.wechatmod.Common.MOD_PACKAGENAME;
import static dg.shenm233.wechatmod.BuildConfig.DEBUG;

public class MainHook extends XC_MethodHook implements IXposedHookZygoteInit, IXposedHookLoadPackage, IXposedHookInitPackageResources {
    private static String MODULE_PATH = null;

    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) throws Throwable {
        MODULE_PATH = startupParam.modulePath;
    }

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals(Wechat_PACKAGENAME)) return;

        try {
            ////////////////////////////////////////////////////
            // thanks to KeepChat for the following snippet:
            // http://git.io/JJZPaw
            Object activityThread = callStaticMethod(findClass("android.app.ActivityThread", null), "currentActivityThread");
            Context context = (Context) callMethod(activityThread, "getSystemContext");
            ///////////////////////////////////////////////////

            String versionName = context.getPackageManager().getPackageInfo(Wechat_PACKAGENAME, 0).versionName;
            int versionCode = context.getPackageManager().getPackageInfo(Wechat_PACKAGENAME, 0).versionCode;
            if (DEBUG)
                XposedBridge.log("Wechat versionName: " + versionName + " versionCode: " + Integer.toString(versionCode));

            //If it's not supported,goodbye!
            if (!ObfuscationHelper.init(versionCode, lpparam)) return;

            //Let do it!
            findAndHookMethod(MM_Classes.LauncherUI, "onCreate", Bundle.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (DEBUG) XposedBridge.log("wechat onCreate hook!");
                    Common.LauncherUI_INSTANCE = (Activity) param.thisObject;
                    Common.MM_Context = (Context) callMethod(Common.LauncherUI_INSTANCE, "getApplicationContext");
                    Common.MOD_Context = Common.LauncherUI_INSTANCE.createPackageContext(MOD_PACKAGENAME, Context.CONTEXT_IGNORE_SECURITY);
                }
            });

            LauncherUI.init(lpparam);
        } catch (Throwable l) {
            XposedBridge.log(l);
        }
    }

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable {
        if (!resparam.packageName.equals(Wechat_PACKAGENAME)) return;
        Common.MOD_RES = XModuleResources.createInstance(MODULE_PATH, resparam.res);
    }
}
