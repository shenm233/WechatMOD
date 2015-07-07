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
        public static Class<?> UserInfo;
        public static Class<?> UserNickName;
        public static Class<?> Avatar;
        public static Class<?> AccountStorage;
        public static Class<?> NewFriendMessage;
        public static Class<?> Bottle;
        public static Class<?> WTFClazz;                //i don't know WTF it is,i just want to get Moments unread count
        public static Class<?> PluginToolClazz;

        private static void init(int idx, LoadPackageParam lpparam) throws Throwable {
            String MM_UI_PACKAGENAME = "com.tencent.mm.ui.";
            String MM_PLUGINSDK_UI_PACKNAME = "com.tencent.mm.pluginsdk.ui.";
            String MM_MODEL_PACKAGENAME = "com.tencent.mm.model.";
//            String mainAddContactFragment = MM_UI_PACKAGENAME + new String[]{"v"}[idx];
//            String mainMoreFragment = MM_UI_PACKAGENAME + new String[]{"em"}[idx];

            LauncherUI = findClass("com.tencent.mm.ui.LauncherUI", lpparam.classLoader);
            Preference = findClass(MM_UI_PACKAGENAME + "base.preference.Preference", lpparam.classLoader);
            UserInfo = findClass(MM_MODEL_PACKAGENAME +
                    new String[]{"v"}[idx], lpparam.classLoader);
            UserNickName = findClass(MM_PLUGINSDK_UI_PACKNAME +
                    new String[]{"d.i"}[idx], lpparam.classLoader);
            Avatar = findClass(MM_PLUGINSDK_UI_PACKNAME +
                    new String[]{"a$b"}[idx], lpparam.classLoader);
            AccountStorage = findClass(MM_MODEL_PACKAGENAME +
                    new String[]{"ax"}[idx], lpparam.classLoader);
            NewFriendMessage = findClass("com.tencent.mm." +
                    new String[]{"ag.m"}[idx], lpparam.classLoader);
            Bottle = findClass(MM_MODEL_PACKAGENAME +
                    new String[]{"x"}[idx], lpparam.classLoader);
            WTFClazz = findClass("com.tencent.mm.pluginsdk." +
                    new String[]{"l$ag"}[idx], lpparam.classLoader);
            PluginToolClazz = findClass("com.tencent.mm." +
                    new String[]{"aj.c"}[idx], lpparam.classLoader);
//            MainAddContactFragment = findClass(mainAddContactFragment, lpparam.classLoader);
//            MainMoreFragment = findClass(mainMoreFragment, lpparam.classLoader);
        }
    }

    public static class MM_Methods {
        //methods in LauncherUI class:
        public static String startMainUI;
        public static String createTabView;
        public static String setCurrentPagerItem;
        public static String getFragment;
        public static String initActionBar;

        //methods in MainAddContactFragment class;
        //methods in MainMoreFragment class;
        public static String startMMActivity;  //abstract method

        //methods in UserInfo class:
        public static String getUsername;
        public static String getOrigUsername;

        //methods in UserNickName class:
        public static String getNickname;

        //methods in Avatar class:
        public static String setAvatarByOrigUsername;

        //methods in AccountStorage class:
        public static String getAccStg;

        public static String getUserInfoFromDB;

        //methods in NewFriendMessage class:
        public static String getLBSVerifyMessage;
        public static String getShakeVerifyMessage;

        public static String getVerifyMessageCount;

        //methods in Bottle class:
        public static String getBottleUnreadCount;

        public static String getMomentsUnreadCount;

        //methods in PluginToolClazz
        public static String startPluginActivity;

        private static void init(int idx) throws Throwable {
            startMainUI = new String[]{"aKw"}[idx];
            createTabView = new String[]{"aKC"}[idx];
            setCurrentPagerItem = new String[]{"nc"}[idx];
            getFragment = new String[]{"nd"}[idx];
            initActionBar = new String[]{"aKD"}[idx];
            startMMActivity = new String[]{"a"}[idx];
            getUsername = new String[]{"rO"}[idx];
            getOrigUsername = new String[]{"rN"}[idx];
            getNickname = new String[]{"a"}[idx];
            setAvatarByOrigUsername = new String[]{"b"}[idx];
            getAccStg = new String[]{"tg"}[idx];
            getUserInfoFromDB = new String[]{"ra"}[idx];
            getLBSVerifyMessage = new String[]{"BH"}[idx];
            getShakeVerifyMessage = new String[]{"BI"}[idx];
            getVerifyMessageCount = new String[]{"BA"}[idx];
            getBottleUnreadCount = new String[]{"sA"}[idx];
            getMomentsUnreadCount = new String[]{"BA"}[idx];
            startPluginActivity = new String[]{"c"}[idx];
        }
    }

    public static class MM_Fields {
        //fields in LauncherUI class:
        public static String customViewPager;
        public static String tabView;
        public static String main_tab; //this is main_tab that including customViewPager,tabView...
        public static String actionBar;

        //fields in MainAddContactFragment class;
        public static String discovery_preferenceInterface; //Type:com.tencent.mm.ui.base.preference.?
        //fields in MainMoreFragment class;
        public static String me_preferenceInterface;        //Type:com.tencent.mm.ui.base.preference.?

        //fields in WTFClazz
        public static String moments_jj;

        private static void init(int idx) throws Throwable {
            customViewPager = new String[]{"imA"}[idx];
            tabView = new String[]{"imz"}[idx];
            main_tab = new String[]{"cuW"}[idx];
            discovery_preferenceInterface = new String[]{"bXk"}[idx];
            me_preferenceInterface = new String[]{"bXk"}[idx];
            moments_jj = new String[]{"gJE"}[idx];
            actionBar = new String[]{"iZ"}[idx];
        }
    }

    //this class is used for get resource(such as layout,drawable..) id
    public static class MM_Res {
        public static int main_tab;
        public static int action_bar_color;
        public static int app_name;

        private static void init(int idx, LoadPackageParam lpparam) throws Throwable {
            String R = "com.tencent.mm.a";
            String main_tabInClazz = new String[]{"$k"}[idx];
            String action_bar_colorInClazz = new String[]{"$f"}[idx];
            String app_nameInClazz = new String[]{"$n"}[idx];
            main_tab = getStaticIntField(findClass(R + main_tabInClazz, lpparam.classLoader), "main_tab");
            action_bar_color = getStaticIntField(findClass(R + action_bar_colorInClazz, lpparam.classLoader), "action_bar_color");
            app_name = getStaticIntField(findClass(R + app_nameInClazz, lpparam.classLoader), "app_name");
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
