package dg.shenm233.wechatmod;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.webkit.WebView;

public class LicenseActivity extends Activity implements DialogInterface.OnCancelListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView mWebView = new WebView(this);
        mWebView.loadUrl("file:///android_asset/license.html");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true)
                .setView(mWebView)
                .setTitle(R.string.preference_license)
                .setOnCancelListener(this)
                .show();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        finish();
    }
}
