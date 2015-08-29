package dg.shenm233.wechatmod.hooks.ui;

import android.view.View;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import dg.shenm233.wechatmod.Common;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static dg.shenm233.wechatmod.ObfuscationHelper.MM_Classes;
import static dg.shenm233.wechatmod.ObfuscationHelper.MM_Methods;

public class LauncherUIBottomTabView {
    public static void init(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        findAndHookMethod(MM_Classes.LauncherUIBottomTabView, MM_Methods.setMainTabUnread, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                int i = (int) param.args[0];
                LauncherUI launcherUI = LauncherUI.tabViews.get(param.thisObject);
                if (launcherUI != null) {
                    launcherUI.drawerListAdapter.setMainChattingUnread(i);
                }
            }
        });
        findAndHookMethod(MM_Classes.LauncherUIBottomTabView, MM_Methods.setContactTabUnread, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                int i = (int) param.args[0];
                LauncherUI launcherUI = LauncherUI.tabViews.get(param.thisObject);
                if (launcherUI != null) {
                    launcherUI.drawerListAdapter.setContactUnread(i);
                }
            }
        });
        findAndHookMethod(MM_Classes.LauncherUIBottomTabView, MM_Methods.setFriendTabUnread, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (Common.item_sns_moments_enabled || Common.item_sns_drift_bottle_enabled
                        || Common.item_sns_people_nearby_enabled || Common.item_sns_shake_enabled) {
                    if ((int) param.args[0] > 0) {
                        LauncherUI launcherUI = LauncherUI.tabViews.get(param.thisObject);
                        if (launcherUI != null) {
                            launcherUI.drawer_indicator_poi.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
        findAndHookMethod(MM_Classes.LauncherUIBottomTabView, MM_Methods.setShowFriendPoint, boolean.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (Common.item_sns_moments_enabled && (boolean) param.args[0]) {
                    LauncherUI launcherUI = LauncherUI.tabViews.get(param.thisObject);
                    if (launcherUI != null) {
                        launcherUI.drawer_indicator_poi.setVisibility(View.VISIBLE);
                        launcherUI.drawerListAdapter.setMomentsPoint(true);
                    }
                }
            }
        });
    }
}
