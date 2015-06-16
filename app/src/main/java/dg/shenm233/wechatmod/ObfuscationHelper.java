package dg.shenm233.wechatmod;

import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static de.robv.android.xposed.XposedHelpers.findClass;

public class ObfuscationHelper {
    public static final int MM_6_2_0_50 = 524;

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
        return true;
    }

    public static class MM_Classes {
        public static Class<?> LauncherUI;

        private static void initClasses(int idx, LoadPackageParam lpparam) throws Throwable {
            LauncherUI = findClass("com.tencent.mm.ui.LauncherUI", lpparam.classLoader);
        }
    }

    public static class MM_Methods {
        public static String MainUI;
        public static String CreateTabView;

        private static void initMethods(int idx) throws Throwable {
            MainUI = new String[]{"aKw"}[idx];
            CreateTabView = new String[]{"aKC"}[idx];
        }
    }

    public static class MM_Fields {
        public static String customViewPager;
        public static String tabView;

        private static void initFields(int idx) throws Throwable {
            customViewPager = new String[]{"imA"}[idx];
            tabView = new String[]{"imz"}[idx];
        }
    }
}
