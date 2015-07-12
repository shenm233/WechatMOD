package dg.shenm233.wechatmod;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

@SuppressLint("WorldReadableFiles")
public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private SharedPreferences prefs;

    private Preference mLicense;
    private ListPreference mSetNav;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences(Common.MOD_PREFS, Context.MODE_WORLD_READABLE);
        addPreferencesFromResource(R.xml.preference);
        PackageManager pm = getPackageManager();
        StringBuilder ver = new StringBuilder("MOD Version: ");
        ver.append(BuildConfig.VERSION_NAME).append("\n");
        ver.append("Wechat Version: ");
        try {
            int versionCode = pm.getPackageInfo(Common.WECHAT_PACKAGENAME, 0).versionCode;
            String versionName = pm.getPackageInfo(Common.WECHAT_PACKAGENAME, 0).versionName;
            ver.append(versionName).append("(").append(versionCode).append(")");
        } catch (PackageManager.NameNotFoundException e) {
            ver.append("not installed.");
        }
        findPreference("version").setSummary(ver);

        findPreference("dev").setSummary("shenm233 (darkgenlotus@gmail.com)");

        mLicense = findPreference("license");
        mSetNav = (ListPreference) findPreference(Common.KEY_SETNAV);
        mLicense.setOnPreferenceClickListener(this);
        mSetNav.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        String navMode = prefs.getString(Common.KEY_SETNAV, "default");
        int index = mSetNav.findIndexOfValue(navMode);
        CharSequence[] entries = mSetNav.getEntries();
        mSetNav.setValueIndex(index);
        mSetNav.setSummary(entries[index]);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        prefs = null;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mSetNav) {
            CharSequence[] entries = mSetNav.getEntries();
            String key = (String) newValue;
            int index = mSetNav.findIndexOfValue(key);
            mSetNav.setSummary(entries[index]);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(Common.KEY_SETNAV, key);
            editor.commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference == mLicense) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MAIN);
            intent.setClass(this, LicenseActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }
}
