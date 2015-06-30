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
import static de.robv.android.xposed.XposedHelpers.getStaticIntField;
import static dg.shenm233.wechatmod.BuildConfig.DEBUG;

public class ObfuscationHelper {
    public static final int MM_6_2_0_50 = 524;

    //a helper for analyzing StackTrace,I want to know who called method.
    public static XC_MethodHook getStackTraceHelper;

    /*init ObfuscationHelper according to versionname and versioncode
    * if it is supported,return true,otherwise return false
    */
    public static boolean init(int versioncode, LoadPackageParam lpparam) throws Throwable {
        int versionIndex;
        if (versioncode == MM_6_2_0_50) {
            versionIndex = 0;

        } else {
            return false;
        }
        MM_Classes.initClasses(versionIndex, lpparam);
        MM_Methods.initMethods(versionIndex);
        MM_Fields.initFields(versionIndex);
        MM_Res.initRes(versionIndex, lpparam);

        if (DEBUG) {
            getStackTraceHelper = new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    XposedBridge.log("********");
                    StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
                    for (int i = 0; i < stackTraceElements.length; i++) {
                        StackTraceElement stackTraceElement = stackTraceElements[i];
                        XposedBridge.log(i + ": " + stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + "\n");
                    }
                    XposedBridge.log("********");
                }
            };
        }
        return true;
    }

    public static class MM_Classes {
        public static Class<?> LauncherUI;
        //public static Class<?> MainAddContactFragment;  //discovery Fragment
        //public static Class<?> MainMoreFragment;        //me Fragment
        public static Class<?> Preference;

        private static void initClasses(int idx, LoadPackageParam lpparam) throws Throwable {
            String MM_UI_PACKAGENAME = "com.tencent.mm.ui.";
//            String mainAddContactFragment = MM_UI_PACKAGENAME + new String[]{"v"}[idx];
//            String mainMoreFragment = MM_UI_PACKAGENAME + new String[]{"em"}[idx];

            LauncherUI = findClass("com.tencent.mm.ui.LauncherUI", lpparam.classLoader);
            Preference = findClass(MM_UI_PACKAGENAME + "base.preference.Preference", lpparam.classLoader);
//            MainAddContactFragment = findClass(mainAddContactFragment, lpparam.classLoader);
//            MainMoreFragment = findClass(mainMoreFragment, lpparam.classLoader);
        }
    }

    public static class MM_Methods {
        //methods in LauncherUI class:
        public static String MainUI;
        public static String CreateTabView;
        public static String setCurrentPagerItem;
        public static String getFragment;

        //methods in MainAddContactFragment class;
        //methods in MainMoreFragment class;
        public static String startMMActivity;  //abstract method

        private static void initMethods(int idx) throws Throwable {
            MainUI = new String[]{"aKw"}[idx];
            CreateTabView = new String[]{"aKC"}[idx];
            setCurrentPagerItem = new String[]{"nc"}[idx];
            getFragment = new String[]{"nd"}[idx];
            startMMActivity = new String[]{"a"}[idx];
        }
    }

    public static class MM_Fields {
        //fields in LauncherUI class:
        public static String customViewPager;
        public static String tabView;
        public static String main_tab; //this is main_tab that including customViewPager,tabView...

        //fields in MainAddContactFragment class;
        public static String discovery_preferenceInterface; //Type:com.tencent.mm.ui.base.preference.?
        //fields in MainMoreFragment class;
        public static String me_preferenceInterface;        //Type:com.tencent.mm.ui.base.preference.?

        private static void initFields(int idx) throws Throwable {
            customViewPager = new String[]{"imA"}[idx];
            tabView = new String[]{"imz"}[idx];
            main_tab = new String[]{"cuW"}[idx];
            discovery_preferenceInterface = new String[]{"bXk"}[idx];
            me_preferenceInterface = new String[]{"bXk"}[idx];
        }
    }

    //this class is used for get resource(such as layout,drawable..) id
    public static class MM_Res {
        public static int main_tab;

        private static void initRes(int idx, LoadPackageParam lpparam) throws Throwable {
            String R = "com.tencent.mm.a";
            String main_tabInClazz = new String[]{"$k"}[idx];
            main_tab = getStaticIntField(findClass(R + main_tabInClazz, lpparam.classLoader), "main_tab");
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
