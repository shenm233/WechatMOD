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
}
