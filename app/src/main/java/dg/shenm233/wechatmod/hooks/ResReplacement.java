package dg.shenm233.wechatmod.hooks;

import android.content.Context;
import android.content.pm.PackageManager;

import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import dg.shenm233.wechatmod.ObfuscationHelper;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static dg.shenm233.wechatmod.Common.WECHAT_PACKAGENAME;

public class ResReplacement {
    public static void init(XC_InitPackageResources.InitPackageResourcesParam resparam) {
        Object activityThread = callStaticMethod(findClass("android.app.ActivityThread", null), "currentActivityThread");
        Context context = (Context) callMethod(activityThread, "getSystemContext");
        try {
            String versionName = context.getPackageManager().getPackageInfo(WECHAT_PACKAGENAME, 0).versionName;
            int versionCode = context.getPackageManager().getPackageInfo(WECHAT_PACKAGENAME, 0).versionCode;
            int versionIndex = ObfuscationHelper.isSupportedVersion(versionCode, versionName);
            if (versionIndex < 0) return;
            
        } catch (PackageManager.NameNotFoundException e) {

        }
    }
}
