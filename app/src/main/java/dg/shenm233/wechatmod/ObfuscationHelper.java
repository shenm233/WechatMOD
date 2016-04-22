package dg.shenm233.wechatmod;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static de.robv.android.xposed.XposedHelpers.findClass;
import static dg.shenm233.wechatmod.BuildConfig.DEBUG;

public class ObfuscationHelper {
    public static final int MM_6_3_16_49 = 780; // 国内版

    //a helper for analyzing StackTrace,I want to know who called method.
    public static XC_MethodHook getStackTraceHelper;

    /**
     * init ObfuscationHelper according to versionname and versioncode
     * if it is supported,return true,otherwise return false
     */
    public static boolean init(int versioncode, String versionName, LoadPackageParam lpparam) throws Throwable {
        int versionIndex;
        versionIndex = isSupportedVersion(versioncode, versionName);
        if (versionIndex < 0) {
            return false;
        }

        MM_Classes.init(versionIndex, lpparam);
        MM_Methods.init(versionIndex);
        MM_Fields.init(versionIndex);
        MM_Res.init(versionIndex, lpparam);

        if (DEBUG) {
            getStackTraceHelper = new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    XposedBridge.log("********");
                    StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
                    for (int i = 0; i < stackTraceElements.length; i++) {
                        StackTraceElement stackTraceElement = stackTraceElements[i];
                        XposedBridge.log(i + ": " + stackTraceElement.getClassName() + "."
                                + stackTraceElement.getMethodName() + "\n");
                    }
                    XposedBridge.log("********");
                }
            };
        }
        return true;
    }

    /*
    *if it is supported,return >= 0,otherwise return -1
    */
    public static int isSupportedVersion(int versioncode, String versionName) {
        if (versionName.contains("6.3.16.49")) {
            return 0;
        } else {
            return -1;
        }
    }

    public static class MM_Classes {
        public static Class<?> LauncherUI;
        public static Class<?> LauncherUIBottomTabView;
        public static Class<?> LauncherUIBottomTabView$Tab;

        public static Class<?> AccountStorage;

        private static void init(int idx, LoadPackageParam lpparam) throws Throwable {
            String MM_UI_PACKAGENAME = "com.tencent.mm.ui.";
            String MM_PLUGINSDK_UI_PACKNAME = "com.tencent.mm.pluginsdk.ui.";
            String MM_MODEL_PACKAGENAME = "com.tencent.mm.model.";

            LauncherUI = findClass("com.tencent.mm.ui.LauncherUI", lpparam.classLoader);
            LauncherUIBottomTabView = findClass(MM_UI_PACKAGENAME + "LauncherUIBottomTabView", lpparam.classLoader);
            LauncherUIBottomTabView$Tab = findClass(MM_UI_PACKAGENAME + "LauncherUIBottomTabView" +
                    new String[]{"$a"}[idx], lpparam.classLoader);
            AccountStorage = findClass(MM_MODEL_PACKAGENAME +
                    new String[]{"ah"}[idx], lpparam.classLoader);
        }
    }

    public static class MM_Methods {

        //methods in LauncherUI class:
        public static class LauncherUI {
            public static String startMainUI;
        }

        //methods in LauncherUIBottomTabView class:
        public static class LauncherUIBottomTabView {
            public static String initSingleTab;
        }

        //methods in AccountStorage class:
        public static class AccountStorage {
            public static String isMMcoreReady;
        }

        private static void init(int idx) throws Throwable {
            /*com.tencent.mm.ui.LauncherUI*/
            LauncherUI.startMainUI = new String[]{"bbu"}[idx]; /*"on main tab create"*/

            /*com.tencent.mm.ui.LauncherUIBottomTabView*/
            LauncherUIBottomTabView.initSingleTab = new String[]{"a"}[idx];

            /*com.tencent.mm.ui.LauncherUI*/
            AccountStorage.isMMcoreReady = new String[]{"qy"}[idx]; /*mmcore has not ready 的上面第三行if(...) break Label...*/
        }
    }

    public static class MM_Fields {

        //fields in LauncherUI class:
        public static class LauncherUI {
            public static String customViewPager;
            public static String tabView;
            public static String isMainTabCreated;
        }

        //fields in LauncherUIBottomTabView$Tab class:
        public static class LauncherUIBottomTabView$Tab {
            public static String tabName;
            public static String tabBackground;
        }

        private static void init(int idx) throws Throwable {
            /*com.tencent.mm.ui.LauncherUI*/
            LauncherUI.customViewPager = new String[]{"koj"}[idx]; /*CustomViewPager*/
            LauncherUI.tabView = new String[]{"koi"}[idx]; /*= launcheruibottomtabview*/
            LauncherUI.isMainTabCreated = new String[]{"knZ"}[idx]; /*on main tab create*/

            /*com.tencent.mm.ui.LauncherUIBottomTabView$Tab*/
            LauncherUIBottomTabView$Tab.tabName = new String[]{"kqx"}[idx];
            LauncherUIBottomTabView$Tab.tabBackground = new String[]{"kqv"}[idx];
        }
    }

    //this class is used for get resource(such as layout,drawable..) id
    public static class MM_Res {

        public static class color {
            public static int action_bar_color;
        }

        private static void init(int idx, LoadPackageParam lpparam) throws Throwable {
            color.action_bar_color = new int[]{2131231144}[idx];
        }
    }

    //a tool for analysis,because obfuscation is toooo crazy.
    public static void getRawXml(int resid, Context context) {
        try {
            XmlResourceParser xml = context.getResources().getXml(resid);

            // check state
            int eventType = xml.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) {
                    XposedBridge.log("Start document resid:" + Integer.toString(resid));
                } else if (eventType == XmlPullParser.START_TAG) {
                    XposedBridge.log("Start tag : " + xml.getName());
                } else if (eventType == XmlPullParser.END_TAG) {
                    XposedBridge.log("End tag : " + xml.getName());
                } else if (eventType == XmlPullParser.TEXT) {
                    XposedBridge.log("Text : " + xml.getText());
                }
                eventType = xml.next();
            }
            // indicate app done reading the resource.
            xml.close();
        } catch (Resources.NotFoundException e) {
            XposedBridge.log(e);
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }
    }
}
