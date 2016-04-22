package dg.shenm233.wechatmod.hooks.ui;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import dg.shenm233.wechatmod.Common;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static dg.shenm233.wechatmod.ObfuscationHelper.MM_Classes;
import static dg.shenm233.wechatmod.ObfuscationHelper.MM_Fields;
import static dg.shenm233.wechatmod.ObfuscationHelper.MM_Methods;
import static dg.shenm233.wechatmod.ObfuscationHelper.MM_Res;

public class LauncherUIBottomTabView {
    public static void init(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        XC_MethodHook methodHook;

        methodHook = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Object result = param.getResult();
                TextView tabBottom = (TextView) getObjectField(result,
                        MM_Fields.LauncherUIBottomTabView$Tab.tabName);
                tabBottom.setVisibility(View.GONE);
                View bg = (View) getObjectField(result,
                        MM_Fields.LauncherUIBottomTabView$Tab.tabBackground);
                bg.setBackgroundColor(Common.MM_AppContext.getResources().getColor(MM_Res.color.action_bar_color));
            }
        };
        findAndHookMethod(MM_Classes.LauncherUIBottomTabView,
                MM_Methods.LauncherUIBottomTabView.initSingleTab,
                int.class, ViewGroup.class, methodHook);
    }
}
