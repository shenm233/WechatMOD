package dg.shenm233.wechatmod.hooks.ui;

import android.app.Activity;
import android.content.Intent;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import dg.shenm233.wechatmod.Common;
import dg.shenm233.wechatmod.ObfuscationHelper;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.setObjectField;
import static dg.shenm233.wechatmod.ObfuscationHelper.MM_Classes;
import static dg.shenm233.wechatmod.ObfuscationHelper.MM_Fields;
import static dg.shenm233.wechatmod.ObfuscationHelper.MM_Methods;

public class MainFragments {
    public void init(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
//        if (DEBUG) {
//            if (ObfuscationHelper.getStackTraceHelper != null) {
//                findAndHookMethod(MM_Classes.MainAddContactFragment, MM_Methods.startMMActivity,
//                        "com.tencent.mm.ui.base.preference.l", "com.tencent.mm.ui.base.preference.Preference",
//                        ObfuscationHelper.getStackTraceHelper);
//                findAndHookMethod(MM_Classes.MainMoreFragment, MM_Methods.startMMActivity,
//                        "com.tencent.mm.ui.base.preference.l", "com.tencent.mm.ui.base.preference.Preference",
//                        ObfuscationHelper.getStackTraceHelper);
//            }
//        }
    }

    public static void switchMMFragment(Activity activity, int i) {
        if (i < 4) {
            //switch PagerView.
            // 0 for main_chatting,
            // 1 for contact,
            // 2 for discovery,
            // 3 for me
            callMethod(activity, ObfuscationHelper.MM_Methods.setCurrentPagerItem, i);
        }
    }

    public static void callMMFragmentFeature(Activity activity, int fragmentIndex, String preferenceKey) {
        try {
            //Fix NPE when start SettingsUI Activity
            if ("more_setting".equals(preferenceKey)) {
                callStaticMethod(MM_Classes.PluginToolClazz, MM_Methods.startPluginActivity,
                        Common.MM_Context, "setting", ".ui.setting.SettingsUI", new Intent());
                return;
            }
            Object fragment = callMethod(activity, ObfuscationHelper.MM_Methods.getFragment, fragmentIndex);
            Object preference = XposedHelpers.newInstance(MM_Classes.Preference, Common.MM_Context);
            setObjectField(preference, MM_Fields.preferenceKey, preferenceKey);
            Object tempObject = new Object();
            //从Fragment获取类型为com.tencent.mm.ui.base.preference.?（接口）的成员变量，用于寻找需要的函数（接口不能实例化）
            if (fragmentIndex == 2) {
                tempObject = getObjectField(fragment, MM_Fields.discovery_preferenceInterface);
            } else if (fragmentIndex == 3) {
                tempObject = getObjectField(fragment, MM_Fields.me_preferenceInterface);
            }
            //这个方法的第一个参数不影响该方法的执行（方法体不用这个参数）
            callMethod(fragment, MM_Methods.startMMActivity, tempObject, preference);
        } catch (Throwable l) {
            XposedBridge.log(l);
        }
    }
}
