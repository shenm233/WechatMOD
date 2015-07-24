package dg.shenm233.wechatmod.hooks.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import dg.shenm233.wechatmod.Common;
import dg.shenm233.wechatmod.R;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static dg.shenm233.wechatmod.ObfuscationHelper.MM_Classes;

public class MMFragmentActivity {
    public static void init(final XC_LoadPackage.LoadPackageParam lpparam) {
        findAndHookMethod(MM_Classes.MMFragmentActivity, "onCreate", Bundle.class, new XC_MethodHook() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Activity activity = (Activity) param.thisObject;
                Window window = activity.getWindow();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(Common.MOD_RES.getColor(R.color.wechat_status_bar_color));
                }
            }
        });
    }
}
