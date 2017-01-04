package com.smalldoor.rwk.mileage_extra;

import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by quant on 30/10/2016.
 * For external links from the WebView
 */

public class WebViewTestClient extends WebViewClient {

    @Override
    public void onPageFinished(WebView view, String url) {
        view.loadUrl("");
    }
    /*public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if(Uri.parse(url).getHost().length() == 0) {
            return false;
        }
        //if(Uri.parse(url).getHost().endsWith("rwk_my_test_website.co.uk")) {
        //    return false;
        //}

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        view.getContext().startActivity(intent);
        return true;
    }*/

}
