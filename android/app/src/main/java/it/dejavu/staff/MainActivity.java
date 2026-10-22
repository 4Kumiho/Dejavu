package it.dejavu.staff;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

/**
 * L'app è la stessa web app di staff/ (copiata dentro l'APK), aperta in una WebView a tutto schermo.
 * I file vengono serviti da un indirizzo https "finto" (appassets.androidplatform.net): così i percorsi
 * relativi, fetch() e la memoria del browser funzionano come sul sito.
 */
public class MainActivity extends Activity {
    static final String HOST = "appassets.androidplatform.net";
    static final String HOME = "https://" + HOST + "/staff/index.html";

    private static final int SCELTA_FILE = 1;
    private WebView web;
    private ValueCallback<Uri[]> fileScelto; // «Carica copia» del menu

    @Override
    protected void onCreate(Bundle stato) {
        super.onCreate(stato);
        web = new WebView(this);
        web.setBackgroundColor(Color.rgb(10, 10, 11));
        setContentView(web);

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);          // tavoli, menu e chiave restano salvati come nel browser
        s.setAllowFileAccess(false);
        s.setAllowContentAccess(false);
        s.setTextZoom(100);                    // la dimensione del testo di sistema non rompe l'impaginazione
        s.setMediaPlaybackRequiresUserGesture(true);

        web.addJavascriptInterface(new Ponte(this), "DejavuAndroid");
        web.setWebChromeClient(new WebChromeClient() { // alert(), conferme e scelta dei file
            @Override
            public boolean onShowFileChooser(WebView v, ValueCallback<Uri[]> cb, FileChooserParams p) {
                if (fileScelto != null) fileScelto.onReceiveValue(null);
                fileScelto = cb;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT).addCategory(Intent.CATEGORY_OPENABLE).setType("*/*");
                try {
                    startActivityForResult(i, SCELTA_FILE);
                } catch (Exception e) {
                    fileScelto = null;
                    return false;
                }
                return true;
            }
        });
        web.setWebViewClient(new Client());

        if (stato == null || web.restoreState(stato) == null) web.loadUrl(HOME);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void onActivityResult(int richiesta, int esito, Intent dati) {
        if (richiesta != SCELTA_FILE || fileScelto == null) { super.onActivityResult(richiesta, esito, dati); return; }
        Uri u = esito == RESULT_OK && dati != null ? dati.getData() : null;
        fileScelto.onReceiveValue(u == null ? null : new Uri[]{u});
        fileScelto = null;
    }

    @Override
    protected void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        web.saveState(out);
    }

    // Tasto indietro: lo gestisce l'app (chiude le finestre, torna alla pagina precedente);
    // sulla pagina iniziale l'app va in secondo piano senza chiudersi.
    @Override
    @SuppressWarnings("deprecation")
    public void onBackPressed() {
        web.evaluateJavascript("window.dejavuIndietro ? window.dejavuIndietro() : false", (r) -> {
            if (!"true".equals(r)) moveTaskToBack(true);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        web.onResume();
    }

    @Override
    protected void onPause() {
        web.onPause();
        super.onPause();
    }

    void apriFuori(String url) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } catch (Exception ignorata) {
            // nessun browser installato
        }
    }

    private class Client extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest req) {
            if (HOST.equals(req.getUrl().getHost())) return false;
            apriFuori(req.getUrl().toString()); // i link esterni si aprono nel browser
            return true;
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest req) {
            Uri u = req.getUrl();
            if (!HOST.equals(u.getHost())) return null; // menu online, GitHub: rete normale
            String percorso = u.getPath() == null ? "" : u.getPath().replaceFirst("^/+", "");
            if (percorso.isEmpty() || percorso.endsWith("/")) percorso += "index.html";
            try {
                InputStream in = getAssets().open(percorso);
                WebResourceResponse r = new WebResourceResponse(tipo(percorso), null, in);
                r.setResponseHeaders(Collections.singletonMap("Cache-Control", "no-cache"));
                return r;
            } catch (IOException e) {
                return new WebResourceResponse("text/plain", "utf-8", 404, "Not Found",
                        Collections.emptyMap(), null);
            }
        }
    }

    static String tipo(String p) {
        String e = p.substring(p.lastIndexOf('.') + 1).toLowerCase();
        switch (e) {
            case "html": return "text/html";
            case "js": return "application/javascript";
            case "css": return "text/css";
            case "json": return "application/json";
            case "png": return "image/png";
            case "jpg": case "jpeg": return "image/jpeg";
            case "svg": return "image/svg+xml";
            case "webp": return "image/webp";
            case "woff2": return "font/woff2";
            case "woff": return "font/woff";
            default: return "application/octet-stream";
        }
    }
}
