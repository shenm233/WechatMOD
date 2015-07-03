package dg.shenm233.wechatmod;

import android.app.Activity;
import android.content.Context;
import android.content.res.XModuleResources;

public class Common {
    public static final String MOD_PACKAGENAME = Common.class.getPackage().getName();
    public static final String WECHAT_PACKAGENAME = "com.tencent.mm";

    public static Context MM_Context;
    public static Context MOD_Context;

    public static Activity LauncherUI_INSTANCE;

    public static XModuleResources MOD_RES;
}
