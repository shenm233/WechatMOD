package dg.shenm233.wechatmod;

import android.content.Context;
import android.content.res.XModuleResources;

import de.robv.android.xposed.XSharedPreferences;

public class Common {
    public static final String MOD_PACKAGENAME = Common.class.getPackage().getName();
    public static final String WECHAT_PACKAGENAME = "com.tencent.mm";
    public static final String MOD_PREFS = "mod_settings";

    public static Context MM_Context;
    public static Context MOD_Context;

    public static XModuleResources MOD_RES;
    public static XSharedPreferences XMOD_PREFS;

    public static String KEY_SETNAV = "setnav";

    //**************************************************************************
    public static final int item_main_chat = 0;
    public static final int item_main_contact = 1;

    public static final int item_main_addcontact = 20; //category Discovery
    public static final int item_sns_moments = 21;
    public static final int item_sns_scan = 22;
    public static final int item_sns_shake = 23;
    public static final int item_sns_people_nearby = 24;
    public static final int item_sns_drift_bottle = 25;
    public static final int item_sns_shopping = 26;
    public static final int item_sns_games = 27;

    public static final int item_main_more = 30; //category Me
    public static final int item_me_posts = 31;
    public static final int item_me_favorites = 32;
    public static final int item_me_wallet = 33;
    public static final int item_me_settings = 34;
    //**************************************************************************

    public static class DrawerListItem {
        public int ICON_ID;
        public int TEXT_ID;
        public String unread = "";

        public DrawerListItem(int TextResid) {
            TEXT_ID = TextResid;
        }

        public DrawerListItem(int IconResid, int TextResid) {
            ICON_ID = IconResid;
            TEXT_ID = TextResid;
        }
    }
}
