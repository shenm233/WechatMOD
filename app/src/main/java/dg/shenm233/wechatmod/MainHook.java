package dg.shenm233.wechatmod;

import android.content.Context;
import android.content.res.XModuleResources;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import dg.shenm233.wechatmod.hooks.ResReplacement;
import dg.shenm233.wechatmod.hooks.ui.LauncherUI;
import dg.shenm233.wechatmod.hooks.ui.LauncherUIBottomTabView;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static dg.shenm233.wechatmod.BuildConfig.DEBUG;
import static dg.shenm233.wechatmod.Common.WECHAT_PACKAGENAME;

public class MainHook extends XC_MethodHook
        implements IXposedHookZygoteInit, IXposedHookLoadPackage, IXposedHookInitPackageResources {
    private static String MODULE_PATH = null;

    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam)
            throws Throwable {
        MODULE_PATH = startupParam.modulePath;
        Common.XMOD_PREFS = new XSharedPreferences(Common.MOD_PACKAGENAME, Common.MOD_PREFS);
        Common.XMOD_PREFS.makeWorldReadable();
    }

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam)
            throws Throwable {
        if (!lpparam.packageName.equals(WECHAT_PACKAGENAME)) return;

        try {
            ////////////////////////////////////////////////////
            // thanks to KeepChat for the following snippet:
            // http://git.io/JJZPaw
            Object activityThread = callStaticMethod(findClass("android.app.ActivityThread", null),
                    "currentActivityThread");
            Context context = (Context) callMethod(activityThread, "getSystemContext");
            ///////////////////////////////////////////////////

            String versionName = context.getPackageManager().getPackageInfo(WECHAT_PACKAGENAME, 0).versionName;
            int versionCode = context.getPackageManager().getPackageInfo(WECHAT_PACKAGENAME, 0).versionCode;
            if (DEBUG)
                XposedBridge.log("Wechat versionName: " + versionName + " versionCode: " + Integer.toString(versionCode));

            //If it's not supported,goodbye!
            if (!ObfuscationHelper.init(versionCode, versionName, lpparam)) return;

            Common.MOD_Context = context.createPackageContext(Common.MOD_PACKAGENAME,
                    Context.CONTEXT_IGNORE_SECURITY);

            //Let do it!
            LauncherUI.init(lpparam);
            LauncherUIBottomTabView.init(lpparam);
        } catch (Throwable l) {
            XposedBridge.log(l);
        }
    }

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam)
            throws Throwable {
        if (!resparam.packageName.equals(WECHAT_PACKAGENAME)) return;
        if (Common.MOD_RES == null)
            Common.MOD_RES = XModuleResources.createInstance(MODULE_PATH, null);
        ResReplacement.init(resparam);
    }
}
