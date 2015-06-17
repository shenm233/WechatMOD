package dg.shenm233.wechatmod.hooks.ui;

import android.view.View;
import android.view.ViewGroup;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

import static dg.shenm233.wechatmod.ObfuscationHelper.MM_Classes;
import static dg.shenm233.wechatmod.ObfuscationHelper.MM_Methods;
import static dg.shenm233.wechatmod.ObfuscationHelper.MM_Fields;


public class LauncherUI {
    public static void init(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        //remove tabview :P
        findAndHookMethod(MM_Classes.LauncherUI, MM_Methods.CreateTabView, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                ViewGroup customViewPager = (ViewGroup) getObjectField(param.thisObject, MM_Fields.customViewPager);
                Object tabView = getObjectField(param.thisObject, MM_Fields.tabView);
                ((ViewGroup) customViewPager.getParent()).removeView((View) tabView);
            }
        });
    }
}
