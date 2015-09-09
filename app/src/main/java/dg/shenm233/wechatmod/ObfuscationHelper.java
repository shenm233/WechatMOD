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
    public static final int MM_6_2_2_54 = 563;
    public static final int MM_6_2_2_54_nonplay = 581; //6.2.2.54_rec1912d 国内版
    public static final int MM_6_2_4_49 = 581;
    public static final int MM_6_2_4_49_nonplay = 600; //6.2.4.49_r8d971a2国内版
    public static final int MM_6_2_4_51 = 582;
    public static final int MM_6_2_4_51_nonplay = 600; //6.2.4.51_rdf8da56国内版
    public static final int MM_6_2_5_49_nonplay = 620; //6.2.5.49_r7ead8bf国内版

    //a helper for analyzing StackTrace,I want to know who called method.
    public static XC_MethodHook getStackTraceHelper;

    /*init ObfuscationHelper according to versionname and versioncode
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
                        XposedBridge.log(i + ": " + stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + "\n");
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
        if (versionName.contains("6.2.0.50")) {
            return 0;
        } else if (versionName.contains("6.2.2.54")) {
            return 1;
        } else if (versionName.contains("6.2.4.49")) {
            return 2;
        } else if (versionName.contains("6.2.4.51")) {
            return 3;
        } else if (versionName.contains("6.2.5.49")) {
            return 4;
        } else if (versionName.contains("6.2.5.51")) {
            return 4;
        } else if (versionName.contains("6.2.5.52")) {
            return 4;
        } else {
            return -1;
        }
    }

    public static class MM_Classes {
        public static Class<?> LauncherUI;
        public static Class<?> LauncherUIBottomTabView;
        public static Class<?> MMFragmentActivity;
        public static Class<?> ChattingUInonActivity;
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
//            String mainAddContactFragment = MM_UI_PACKAGENAME + new String[]{"v","v","f","f","f"}[idx];
//            String mainMoreFragment = MM_UI_PACKAGENAME + new String[]{"em","en","p","p","p"}[idx];

            LauncherUI = findClass("com.tencent.mm.ui.LauncherUI", lpparam.classLoader);
            LauncherUIBottomTabView = findClass(MM_UI_PACKAGENAME + "LauncherUIBottomTabView", lpparam.classLoader);
            MMFragmentActivity = findClass(MM_UI_PACKAGENAME + "MMFragmentActivity", lpparam.classLoader);
            ChattingUInonActivity = findClass(MM_UI_PACKAGENAME + "chatting.ChattingUI$a", lpparam.classLoader);
            Preference = findClass(MM_UI_PACKAGENAME + "base.preference.Preference", lpparam.classLoader);
            UserInfo = findClass(MM_MODEL_PACKAGENAME +
                    new String[]{"v", "v", "g", "g", "g"}[idx], lpparam.classLoader);
            UserNickName = findClass(MM_PLUGINSDK_UI_PACKNAME +
                    new String[]{"d.i", "d.e", "d.e", "d.e", "d.e"}[idx], lpparam.classLoader);
            Avatar = findClass(MM_PLUGINSDK_UI_PACKNAME +
                    new String[]{"a$b", "a$b", "a$b", "a$b", "a$b"}[idx], lpparam.classLoader);
            AccountStorage = findClass(MM_MODEL_PACKAGENAME +
                    new String[]{"ax", "ax", "ag", "ag", "ah"}[idx], lpparam.classLoader);
            NewFriendMessage = findClass("com.tencent.mm." +
                    new String[]{"ag.m", "ag.m", "ah.l", "ah.l", "ai.l"}[idx], lpparam.classLoader);
            Bottle = findClass(MM_MODEL_PACKAGENAME +
                    new String[]{"x", "x", "i", "i", "i"}[idx], lpparam.classLoader);
            WTFClazz = findClass("com.tencent.mm.pluginsdk." +
                    new String[]{"l$ag", "l$ag", "h$ah", "h$ah", "h$ah"}[idx], lpparam.classLoader);
            PluginToolClazz = findClass("com.tencent.mm." +
                    new String[]{"aj.c", "aj.c", "am.c", "am.c", "an.c"}[idx], lpparam.classLoader);
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
        public static String getActionBarColor;

        //methods in ChattingUInonActivity class:
        public static String setActionBarView;

        //methods in LauncherUIBottomTabView class:
        public static String setMainTabUnread;
        public static String setContactTabUnread;
        public static String setFriendTabUnread;
        public static String setShowFriendPoint;

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
        public static String isMMcoreReady;

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
            /*com.tencent.mm.ui.LauncherUI*/
            startMainUI = new String[]{"aKw", "aLJ", "aNp", "aNr", "aPu"}[idx]; /*"on main tab create"*/
            createTabView = new String[]{"aKC"}[0]; //above 6.2.2 never has this method :(
            setCurrentPagerItem = new String[]{"nc", "nx", "nU", "nU", "oh"}[idx]; /*change tab to %d, cur tab %d*/
            getFragment = new String[]{"nd", "ny", "nV", "nV", "oi"}[idx]; /*createFragment index:%d*/
            initActionBar = new String[]{"aKD", "aLP", "aNv", "aNx", "aPA"}[idx]; /*setLogo((Drawable)new ColorDrawable*/
            getActionBarColor = new String[]{"aKG", "aLS", "aNy", "aNA", "aPD"}[idx]; /*return this.getResources().getColor(com.tencent.mm.a.f.action_bar_color)*/
            /*******************************************************/
            /*com.tencent.mm.ui.chatting.ChattingUI$a*/
            setActionBarView = new String[]{"aON", "aQb", "aRE", "aRG", "aTD"}[idx]; /*mActionBarContainer*/
            /******************************************************/
            /*com.tencent.mm.ui.LauncherUIBottomTabView*/
            setMainTabUnread = new String[]{"mV", "np", "nN", "nN", "oa"}[idx]; /*updateMainTabUnread*/
            setContactTabUnread = new String[]{"mW", "nq", "nO", "nO", "ob"}[idx]; /*方法getContactTabUnread()访问的成员变量，有其他的方法修改该成员变量*/
            setFriendTabUnread = new String[]{"mX", "nr", "nP", "nP", "oc"}[idx]; /*方法getFriendTabUnread()访问的成员变量，有其他的方法修改该成员变量*/
            setShowFriendPoint = new String[]{"eR", "eV", "fv", "fv", "fF"}[idx]; /*方法getShowFriendPoint()访问的成员变量，有其他的方法修改该成员变量*/
            /*****************************************************/
            /*com.tencent.mm.ui.(MainAddContactFragment)*/
            startMMActivity = new String[]{"a", "a", "a", "a", "a"}[idx]; /*"sns", ".ui.SnsTimeLineUI"*/
            /*****************************************************/
            /*com.tencent.mm.ui.(MainMoreFragment)*/
            getUsername = new String[]{"rO", "rW", "sc", "sc", "sp"}[idx]; /*final AccountInfoPreference accountInfoPreference = 的下第一行*/
            getOrigUsername = new String[]{"rN", "rV", "sb", "sb", "so"}[idx]; /*final AccountInfoPreference accountInfoPreference = 的下第三行*/
            /****************************************************/
            /*com.tencent.mm.ui.(MainMoreFragment)*/
            getNickname = new String[]{"a", "a", "a", "a", "a"}[idx]; /*("settings_my_address"); 的上面第二行(即含有accountInfoPreference)*/
            /****************************************************/
            /*com.tencent.mm.pluginsdk.ui.preference.AccountInfoPreference)*/
            setAvatarByOrigUsername = new String[]{"b", "b", "b", "b", "b"}[idx]; /*imageView != null) { 的下一行*/
            /****************************************************/
            /*com.tencent.mm.plugin.setting.ui.setting.SettingsPersonalInfoUI*/
            getAccStg = new String[]{"tg", "to", "tu", "tu", "tI"}[idx]; /*onResume()里形如final String s = (String)ah.tI().rB().get(4, (Object)null);*/
            /*com.tencent.mm.ui.LauncherUI*/
            isMMcoreReady = new String[]{"qU", "rc", "ri", "ri", "rv"}[idx]; /*mmcore has not ready 的上面第三行if(...) break Label...*/
            /****************************************************/
            /*com.tencent.mm.plugin.setting.ui.setting.SettingsPersonalInfoUI*/
            getUserInfoFromDB = new String[]{"ra", "ri", "ro", "ro", "rB"}[idx]; /*onResume()里形如final String s = (String)ah.tI().rB().get(4, (Object)null);*/
            /****************************************************/
            getLBSVerifyMessage = new String[]{"BH", "Cl", "CB", "CB", "Dc"}[idx];
            getShakeVerifyMessage = new String[]{"BI", "Cm", "CC", "CC", "Dd"}[idx];
            /****************************************************/
            getVerifyMessageCount = new String[]{"BA", "Ce", "Ct", "Ct", "CU"}[idx];
            /****************************************************/
            getBottleUnreadCount = new String[]{"sA", "sI", "sO", "sO", "tb"}[idx];
            /****************************************************/
            getMomentsUnreadCount = new String[]{"BA", "Ce", "Ct", "Ct", "CU"}[idx]; /*toString()*/
            /****************************************************/
            startPluginActivity = new String[]{"c", "c", "c", "c", "c"}[idx];
            /****************************************************/
        }
    }

    public static class MM_Fields {
        //fields in LauncherUI class:
        public static String customViewPager;
        public static String tabView;
        public static String main_tab; //this is main_tab that including customViewPager,tabView...
        public static String actionBar;
        public static String isMainTabCreated;
        public static String curTabNum;

        //fields in ChattingUInonActivity class:
        public static String actionBarContainer;

        //fields in MainAddContactFragment class;
        public static String discovery_preferenceInterface; //Type:com.tencent.mm.ui.base.preference.?
        //fields in MainMoreFragment class;
        public static String me_preferenceInterface;        //Type:com.tencent.mm.ui.base.preference.?

        //fields in WTFClazz
        public static String moments_jj;

        //fields in Preference
        public static String preferenceKey;

        private static void init(int idx) throws Throwable {
            /*com.tencent.mm.ui.LauncherUI*/
            customViewPager = new String[]{"imA", "iwF", "iHl", "iHn", "iUy"}[idx]; /*CustomViewPager*/
            tabView = new String[]{"imz", "iwE", "iHk", "iHm", "iUx"}[idx]; /*= launcheruibottomtabview*/
            main_tab = new String[]{"cuW", "czt", "cAI", "cAI", "cDq"}[idx]; /*inflate(com.tencent.mm.a.k.main_tab*/
            actionBar = new String[]{"iZ", "jA", "jz", "jz", "jz"}[idx]; /*ActionBar*/
            isMainTabCreated = new String[]{"imr", "iwv", "iHb", "iHd", "iUo"}[idx]; /*on main tab create*/
            curTabNum = new String[]{"imQ", "iwV", "iHB", "iHD", "iUO"}[idx]; /*change tab to %d, cur tab %d, has init tab %B,*/
            /****************************************************/
            actionBarContainer = new String[]{"iZk", "jjs", "jul", "jun", "jHs"}[idx]; /*ActionBarContainer*/
            /****************************************************/
            discovery_preferenceInterface = new String[]{"bXk", "cbC", "ccQ", "ccQ", "cfq"}[idx]; /*类型为com.tencent.mm.ui.base.preference.?的成员变量*/
            /****************************************************/
            me_preferenceInterface = new String[]{"bXk", "cbC", "ccQ", "ccQ", "cfq"}[idx]; /*类型为com.tencent.mm.ui.base.preference.?的成员变量*/
            /****************************************************/
            moments_jj = new String[]{"gJE", "gTe", "hey", "heA", "hos"}[idx];
            /****************************************************/
            preferenceKey = new String[]{"bTL", "bXW", "bZi", "bZi", "cbG"}[idx]; /*you can get preference.? from method startMMActivity*/
            /****************************************************/
        }
    }

    //this class is used for get resource(such as layout,drawable..) id
    public static class MM_Res {
        public static String strings;
        public static String layout;
        public static String id;

        //layout id
        public static int main_tab;

        //strings
        public static int app_name;
        public static int main_chat;
        public static int main_contact;
        public static int main_addcontact;
        public static int main_more;
        public static int sns_dyna_photo_ui_title;
        public static int find_friends_by_qrcode;
        public static int shake_report_title;
        public static int nearby_friend_title;
        public static int bottle_beach_title;
        public static int game_recommand;
        public static int settings_my_album_new;
        public static int settings_mm_favorite_new;
        public static int settings_mm_wallet_new;
        public static int settings_mm_card_package_new;
        public static int settings_emoji_store;
        public static int settings_title;
        public static int settings_username;

        //id
        public static int custom_action_bar;
        public static int divider;

        private static void init(int idx, LoadPackageParam lpparam) throws Throwable {
            String R = "com.tencent.mm.a";
            if (idx < 5) idx = 0;  //For 6.2.x,these name may be same
            layout = new String[]{"$k"}[idx];
            strings = new String[]{"$n"}[idx];
            id = new String[]{"$i"}[idx];

            Class<?> Layout = findClass(R + layout, lpparam.classLoader);
            Class<?> Strings = findClass(R + strings, lpparam.classLoader);
            Class<?> Id = findClass(R + id, lpparam.classLoader);

            //layout id
            main_tab = getStaticIntField(Layout, "main_tab");

            //strings!!!!
            app_name = getStaticIntField(Strings, "app_name");
            main_chat = getStaticIntField(Strings, "main_chat");
            main_contact = getStaticIntField(Strings, "main_contact");
            main_addcontact = getStaticIntField(Strings, "main_addcontact");
            main_more = getStaticIntField(Strings, "main_more");
            sns_dyna_photo_ui_title = getStaticIntField(Strings, "sns_dyna_photo_ui_title");
            find_friends_by_qrcode = getStaticIntField(Strings, "find_friends_by_qrcode");
            shake_report_title = getStaticIntField(Strings, "shake_report_title");
            nearby_friend_title = getStaticIntField(Strings, "nearby_friend_title");
            bottle_beach_title = getStaticIntField(Strings, "bottle_beach_title");
            game_recommand = getStaticIntField(Strings, "game_recommand");
            settings_my_album_new = getStaticIntField(Strings, "settings_my_album_new");
            settings_mm_favorite_new = getStaticIntField(Strings, "settings_mm_favorite_new");
            settings_mm_wallet_new = getStaticIntField(Strings, "settings_mm_wallet_new");
            settings_mm_card_package_new = getStaticIntField(Strings, "settings_mm_card_package_new");
            settings_emoji_store = getStaticIntField(Strings, "settings_emoji_store");
            settings_title = getStaticIntField(Strings, "settings_title");
            settings_username = getStaticIntField(Strings, "settings_username");

            //id
            custom_action_bar = getStaticIntField(Id, "custom_action_bar");
            divider = getStaticIntField(Id, "divider");
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
