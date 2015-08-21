package dg.shenm233.wechatmod;

import android.content.Context;
import android.content.res.XModuleResources;
import android.graphics.Color;

import de.robv.android.xposed.XSharedPreferences;

public class Common {
    public static final String MOD_PACKAGENAME = Common.class.getPackage().getName();
    public static final String WECHAT_PACKAGENAME = "com.tencent.mm";
    public static final String MOD_PREFS = "mod_settings";

    public static Context MM_Context;
    public static Context MOD_Context;

    public static XModuleResources MOD_RES;
    public static XSharedPreferences XMOD_PREFS;

    public static final String KEY_SETNAV = "setnav";
    public static final String KEY_DISABLED_ITEMS = "disabled_items";
    public static final String KEY_ACTIONBAR_COLOR = "actionbar_color";
    public static final String KEY_FORCE_STATUSBAR_COLOR = "force_statusbar_color";
    public static final String DRAWER_BG_PNG = "drawer_bg.jpg";

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
    public static final int item_me_card_package = 34;
    public static final int item_me_emoji_store = 35;
    public static final int item_me_settings = 36;
    //**************************************************************************

    public static class DrawerListItem {
        public final int KEY;
        public int ICON_ID;
        public int TEXT_ID;
        public String unread = "";

        public DrawerListItem(int key, int TextResid) {
            KEY = key;
            TEXT_ID = TextResid;
        }

        public DrawerListItem(int key, int IconResid, int TextResid) {
            KEY = key;
            ICON_ID = IconResid;
            TEXT_ID = TextResid;
        }
    }

    public static int dipTopx(Context context, long dip) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f);
    }

    public static long getDrawerWidthdip(Context context) {
        final int widthPixels = context.getResources().getDisplayMetrics().widthPixels;
        if (widthPixels <= 480) {
            return 260L;
        } else {
            return 296L;
        }
    }

    ////////////////////////////////////////////////////
    // thanks to Lolistat for the following snippet:
    // https://github.com/PeterCxy/Lolistat/blob/aide/app/src/main/java/info/papdt/lolistat/support/Utility.java#L19
    public static int getDarkerColor(int color, float factor) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= factor; //value
        return Color.HSVToColor(hsv);
    }
    ////////////////////////////////////////////////////
}
