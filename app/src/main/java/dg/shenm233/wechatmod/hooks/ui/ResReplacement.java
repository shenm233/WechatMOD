package dg.shenm233.wechatmod.hooks.ui;

import android.content.Context;
import android.content.pm.PackageManager;

import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import dg.shenm233.wechatmod.Common;
import dg.shenm233.wechatmod.ObfuscationHelper;
import dg.shenm233.wechatmod.R;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static dg.shenm233.wechatmod.Common.WECHAT_PACKAGENAME;

public class ResReplacement {
    public static void init(XC_InitPackageResources.InitPackageResourcesParam resparam) {
        Object activityThread = callStaticMethod(findClass("android.app.ActivityThread", null), "currentActivityThread");
        Context context = (Context) callMethod(activityThread, "getSystemContext");
        try {
            int versionCode = context.getPackageManager().getPackageInfo(WECHAT_PACKAGENAME, 0).versionCode;
            int versionIndex = ObfuscationHelper.isSupportedVersion(versionCode);
            if (versionIndex < 0) return;
            String action_bar_color = new String[]{"b6", "bp"}[versionIndex];

            resparam.res.setReplacement(Common.WECHAT_PACKAGENAME, "color", action_bar_color,
                    Common.MOD_RES.getColor(R.color.wechat_action_bar_color));
        } catch (PackageManager.NameNotFoundException e) {

        }
    }
}
