package org.mozilla.focus.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.mozilla.focus.BuildConfig;
import org.mozilla.focus.R;
import org.mozilla.focus.web.WebViewProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class CaptureActivity extends Activity {
    public static final String CAPTURE_SUCCESS = "CAPTURE_SUCCESS";
    public static final String TARGET_URL = "TARGET_URL";

    private boolean captured = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Looks like something is leaked when Webview is initialized on another thread. Will just
        // hide it for now, hehe.
        if(BuildConfig.DEBUG) {
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
        super.onCreate(savedInstanceState);
        String targetUrl = getIntent().getStringExtra(TARGET_URL);
        WebView.enableSlowWholeDocumentDraw();
        setContentView(R.layout.activity_capture);
        WebView webView = (WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(final WebView view, String url) {
                if(!captured) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setResult(RESULT_OK, getIntent().putExtra(CAPTURE_SUCCESS, capturePage(view)));
                            CaptureActivity.this.finish();
                        }
                    }, 2000);
                }
                captured = true;
            }
        });
        webView.loadUrl(targetUrl);
        configureDefaultSettings(this, webView.getSettings());
    }
    public boolean capturePage(WebView webView) {
        try {
            Bitmap content = getPageBitmap(webView);
            if(content!=null) {
                saveBitmapToStorage(webView.getTitle() + "." + Calendar.getInstance().getTimeInMillis(), content);
                return true;
            } else {
                return false;
            }

        } catch (IOException ex) {
            return false;
        }
    }

    private Bitmap getPageBitmap(WebView webView) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        try {
            Bitmap bitmap = Bitmap.createBitmap(webView.getWidth(), (int) (webView.getContentHeight() * displaymetrics.density), Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            webView.draw(canvas);
            return bitmap;
            // OOM may occur, even if OOMError is not thrown, operations during Bitmap creation may
            // throw other Exceptions such as NPE when the bitmap is very large.
        } catch (Exception | OutOfMemoryError ex) {
            return null;
        }

    }

    private void saveBitmapToStorage(String fileName, Bitmap bitmap) throws IOException {
        File folder = new File(Environment.getExternalStorageDirectory(), "Zerda");
        if(!folder.exists() && !folder.mkdir()){
            throw new IOException("Can't create folder");
        }
        fileName = fileName.concat(".jpg");
        File file = new File(folder, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @SuppressLint("SetJavaScriptEnabled") // We explicitly want to enable JavaScript
    private static void configureDefaultSettings(Context context, WebSettings settings) {
        settings.setJavaScriptEnabled(true);

        // Enabling built in zooming shows the controls by default
        settings.setBuiltInZoomControls(true);

        // So we hide the controls after enabling zooming
        settings.setDisplayZoomControls(false);

        // To respect the html viewport:
        settings.setLoadWithOverviewMode(true);

        // Also increase text size to fill the viewport (this mirrors the behaviour of Firefox,
        // Chrome does this in the current Chrome Dev, but not Chrome release).
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);

        // Disable access to arbitrary local files by webpages - assets can still be loaded
        // via file:///android_asset/res, so at least error page images won't be blocked.
        settings.setAllowFileAccess(false);
        settings.setAllowFileAccessFromFileURLs(false);
        settings.setAllowUniversalAccessFromFileURLs(false);

        final String appName = context.getResources().getString(R.string.useragent_appname);
        settings.setUserAgentString(WebViewProvider.buildUserAgentString(context, settings, appName));

        // Right now I do not know why we should allow loading content from a content provider
        settings.setAllowContentAccess(false);

        // The default for those settings should be "false" - But we want to be explicit.
        settings.setAppCacheEnabled(false);
        settings.setDatabaseEnabled(false);
        settings.setDomStorageEnabled(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);

        // We do implement the callbacks - So let's enable it.
        settings.setGeolocationEnabled(true);

        // We do not want to save any data...
        settings.setSaveFormData(false);
        //noinspection deprecation - This method is deprecated but let's call it in case WebView implementations still obey it.
        settings.setSavePassword(false);
    }
}
