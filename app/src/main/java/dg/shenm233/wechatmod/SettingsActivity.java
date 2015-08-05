package dg.shenm233.wechatmod;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;

import static dg.shenm233.wechatmod.Common.dipTopx;

@SuppressLint("WorldReadableFiles")
public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private SharedPreferences prefs;

    private int PICK_BG = 0;

    private Preference mLicense;
    private ListPreference mSetNav;
    private MultiSelectListPreference mDisabledItems;
    private ListPreference mActionBarColor;
    private CheckBoxPreference mForceStatusBarColor;
    private Preference mPickBg;
    private CheckBoxPreference mHideLauncherIcon;

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
        mDisabledItems = (MultiSelectListPreference) findPreference(Common.KEY_DISABLED_ITEMS);
        mActionBarColor = (ListPreference) findPreference(Common.KEY_ACTIONBAR_COLOR);
        mForceStatusBarColor = (CheckBoxPreference) findPreference(Common.KEY_FORCE_STATUSBAR_COLOR);
        mPickBg = findPreference("pickup_bg");
        mHideLauncherIcon = (CheckBoxPreference) findPreference("hide_launcher_icon");
        mLicense.setOnPreferenceClickListener(this);
        mSetNav.setOnPreferenceChangeListener(this);
        mDisabledItems.setOnPreferenceChangeListener(this);
        mActionBarColor.setOnPreferenceChangeListener(this);
        mForceStatusBarColor.setOnPreferenceChangeListener(this);
        mPickBg.setOnPreferenceClickListener(this);
        mHideLauncherIcon.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        String navMode = prefs.getString(Common.KEY_SETNAV, "default");
        int index = mSetNav.findIndexOfValue(navMode);
        CharSequence[] entries = mSetNav.getEntries();
        mSetNav.setValueIndex(index);
        mSetNav.setSummary(entries[index]);

        String actionBarColor = prefs.getString(Common.KEY_ACTIONBAR_COLOR, "#263238");
        index = mActionBarColor.findIndexOfValue(actionBarColor);
        entries = mActionBarColor.getEntries();
        mActionBarColor.setValueIndex(index);
        mActionBarColor.setSummary(entries[index]);

        boolean forceStatusbarColor = prefs.getBoolean(Common.KEY_FORCE_STATUSBAR_COLOR, false);
        mForceStatusBarColor.setChecked(forceStatusbarColor);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        prefs = null;
        mLicense.setOnPreferenceClickListener(null);
        mSetNav.setOnPreferenceChangeListener(null);
        mDisabledItems.setOnPreferenceChangeListener(null);
        mActionBarColor.setOnPreferenceChangeListener(null);
        mForceStatusBarColor.setOnPreferenceChangeListener(null);
        mPickBg.setOnPreferenceClickListener(null);
        mHideLauncherIcon.setOnPreferenceChangeListener(null);
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
            Toast.makeText(this, R.string.preference_reboot_note, Toast.LENGTH_SHORT).show();
            return true;
        } else if (preference == mDisabledItems) {
            Set<String> strs = (Set<String>) newValue;
            SharedPreferences.Editor editor = prefs.edit();
            editor.putStringSet(Common.KEY_DISABLED_ITEMS, strs);
            editor.commit();
            Toast.makeText(this, R.string.preference_reboot_note, Toast.LENGTH_SHORT).show();
            return true;
        } else if (preference == mActionBarColor) {
            CharSequence[] entries = mActionBarColor.getEntries();
            String key = (String) newValue;
            int index = mActionBarColor.findIndexOfValue(key);
            mActionBarColor.setSummary(entries[index]);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(Common.KEY_ACTIONBAR_COLOR, key);
            editor.commit();
            return true;
        } else if (preference == mForceStatusBarColor) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(Common.KEY_FORCE_STATUSBAR_COLOR, (boolean) newValue);
            editor.commit();
            Toast.makeText(this, R.string.preference_reboot_note, Toast.LENGTH_SHORT).show();
            return true;
        } else if (preference == mHideLauncherIcon) {
            PackageManager packageManager = this.getPackageManager();
            ComponentName aliasName = new ComponentName(this, Common.MOD_PACKAGENAME + ".SettingsActivityLauncher");
            if ((boolean) newValue) {
                packageManager.setComponentEnabledSetting(aliasName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            } else {
                packageManager.setComponentEnabledSetting(aliasName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            }
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
        } else if (preference == mPickBg) {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*")
                    .putExtra("crop", "true")
                    .putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString())
                    .putExtra("outputX", dipTopx(this, 296L)).putExtra("outputY", dipTopx(this, 160L))
                    .putExtra("aspectX", 2).putExtra("aspectY", 1)
                    .putExtra("scale", 1)
                    .putExtra(MediaStore.EXTRA_OUTPUT, getUriFromFile(getFile(Common.DRAWER_BG_PNG)));
//                    .putExtra("return-data", true);
            try {
                startActivityForResult(intent, PICK_BG);
            } catch (ActivityNotFoundException e) {
                Log.e("WechatMOD", "can not pick pic");
            }
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_BG) {
            compressBitmapFileAndcopyToFilesDir(getFile(Common.DRAWER_BG_PNG));
            Toast.makeText(this, "Done!", Toast.LENGTH_SHORT).show();
        }
    }

    private File getFile(String path) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = new File(this.getExternalFilesDir(null), "/" + path);
            return file;
        }
        return null;
    }

    private Uri getUriFromFile(File file) {
        if (file != null) {
            return Uri.fromFile(file);
        } else {
            return null;
        }
    }

    private void compressBitmapFileAndcopyToFilesDir(File file) {
        if (file != null) {
            FileInputStream fileInputStream = null;
            FileOutputStream fileOutputStream = null;
            try {
                fileInputStream = new FileInputStream(file);
                fileOutputStream = this.openFileOutput(file.getName(), Context.MODE_WORLD_READABLE);
                BitmapFactory.Options options = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream, null, options);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fileOutputStream);
                bitmap.recycle();
                fileInputStream.close();
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                Log.e("WechatMOD", "FileNotFoundException");
            } catch (IOException e) {
                Log.e("WechatMOD", "IOException");
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    Log.e("WechatMOD", "IOException:fileInputStream.close()");
                }
            }
        }
    }
}
