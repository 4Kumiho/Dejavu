package it.dejavu.staff;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.webkit.JavascriptInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

/**
 * Il ponte tra la web app e il tablet (window.DejavuAndroid in JavaScript).
 *
 * Cronologia: i tavoli chiusi, un file per mese in dati/cronologia/AAAA-MM.json dentro l'app
 * (come Reef), con la copia precedente in AAAA-MM.json.bak nel caso qualcosa vada storto.
 */
public class Ponte {
    private final MainActivity app;
    private final File cronologia;

    Ponte(MainActivity app) {
        this.app = app;
        this.cronologia = new File(app.getFilesDir(), "dati/cronologia");
        //noinspection ResultOfMethodCallIgnored
        cronologia.mkdirs();
    }

    /* ---------- Cronologia ---------- */

    /** Tutti i tavoli chiusi, dal mese più vecchio al più recente. */
    @JavascriptInterface
    public synchronized String leggiCronologia() {
        JSONArray tutti = new JSONArray();
        for (File f : mesi()) {
            JSONArray lista = leggiLista(f);
            for (int i = 0; i < lista.length(); i++) tutti.put(lista.opt(i));
        }
        return tutti.toString();
    }

    /** Aggiunge un tavolo chiuso alla cronologia del mese in cui è stato chiuso. */
    @JavascriptInterface
    public synchronized boolean archivia(String testo) {
        try {
            JSONObject voce = new JSONObject(testo);
            String mese = new SimpleDateFormat("yyyy-MM", Locale.ITALY).format(new Date(voce.getLong("chiuso")));
            File f = new File(cronologia, mese + ".json");
            JSONArray lista = leggiLista(f);
            lista.put(voce);
            return salvaSicuro(f, lista.toString());
        } catch (JSONException e) {
            return false;
        }
    }

    @JavascriptInterface
    public synchronized boolean eliminaDaCronologia(String id) {
        for (File f : mesi()) {
            JSONArray lista = leggiLista(f), nuova = new JSONArray();
            for (int i = 0; i < lista.length(); i++) {
                JSONObject v = lista.optJSONObject(i);
                if (v == null || !id.equals(v.optString("id"))) nuova.put(lista.opt(i));
            }
            if (nuova.length() != lista.length()) return salvaSicuro(f, nuova.toString());
        }
        return false;
    }

    private File[] mesi() {
        File[] f = cronologia.listFiles((d, n) -> n.matches("\\d{4}-\\d{2}\\.json"));
        if (f == null) return new File[0];
        Arrays.sort(f);
        return f;
    }

    private static JSONArray leggiLista(File f) {
        for (File x : new File[]{f, new File(f.getPath() + ".bak")}) {
            try {
                return new JSONArray(leggi(x));
            } catch (IOException | JSONException ignorata) {
                // si prova con la copia
            }
        }
        return new JSONArray();
    }

    private static String leggi(File f) throws IOException {
        try (InputStream in = new FileInputStream(f)) {
            byte[] b = new byte[(int) f.length()];
            int letti = 0;
            while (letti < b.length) {
                int n = in.read(b, letti, b.length - letti);
                if (n < 0) break;
                letti += n;
            }
            return new String(b, 0, letti, StandardCharsets.UTF_8);
        }
    }

    /** Scrive prima su un file temporaneo e tiene la versione precedente in .bak */
    private static boolean salvaSicuro(File f, String testo) {
        File tmp = new File(f.getPath() + ".tmp");
        try (FileOutputStream o = new FileOutputStream(tmp)) {
            o.write(testo.getBytes(StandardCharsets.UTF_8));
            o.getFD().sync();
        } catch (IOException e) {
            return false;
        }
        File bak = new File(f.getPath() + ".bak");
        if (f.exists()) {
            //noinspection ResultOfMethodCallIgnored
            bak.delete();
            if (!f.renameTo(bak)) return false;
        }
        return tmp.renameTo(f);
    }

    /* ---------- Altri dati dell'app (es. il menu modificato: dati/menu.json) ---------- */

    @JavascriptInterface
    public synchronized String leggiDati(String nome) {
        File f = fileDati(nome);
        if (f == null) return "";
        for (File x : new File[]{f, new File(f.getPath() + ".bak")}) {
            try {
                return leggi(x);
            } catch (IOException ignorata) {
                // si prova con la copia
            }
        }
        return "";
    }

    @JavascriptInterface
    public synchronized boolean scriviDati(String nome, String testo) {
        File f = fileDati(nome);
        return f != null && salvaSicuro(f, testo);
    }

    /** Solo nomi semplici (es. "menu.json") dentro la cartella dati dell'app. */
    private File fileDati(String nome) {
        if (nome == null || !nome.matches("[a-z0-9-]+\\.json")) return null;
        return new File(cronologia.getParentFile(), nome);
    }

    /* ---------- File per l'utente (copie di menu e cronologia) ---------- */

    /** Salva un file nella cartella Download del tablet. Restituisce dove l'ha messo, o "" se non ci riesce. */
    @JavascriptInterface
    public String salvaFile(String nome, String testo) {
        byte[] dati = testo.getBytes(StandardCharsets.UTF_8);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentResolver cr = app.getContentResolver();
                ContentValues v = new ContentValues();
                v.put(MediaStore.MediaColumns.DISPLAY_NAME, nome);
                v.put(MediaStore.MediaColumns.MIME_TYPE, "application/json");
                v.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/Deja Vu");
                Uri uri = cr.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, v);
                if (uri == null) return "";
                try (OutputStream o = cr.openOutputStream(uri)) {
                    if (o == null) return "";
                    o.write(dati);
                }
                return "Download/Deja Vu";
            }
            File dir = new File(app.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "Deja Vu");
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
            try (OutputStream o = new FileOutputStream(new File(dir, nome))) {
                o.write(dati);
            }
            return dir.getPath();
        } catch (Exception e) {
            return "";
        }
    }

    /* ---------- Varie ---------- */

    @JavascriptInterface
    public String versione() {
        PackageInfo p = info();
        return p == null ? "" : p.versionName;
    }

    @JavascriptInterface
    @SuppressWarnings("deprecation")
    public int codice() {
        PackageInfo p = info();
        return p == null ? 0 : p.versionCode;
    }

    /** Apre un link nel browser del tablet (es. per scaricare l'APK nuovo). */
    @JavascriptInterface
    public void apriLink(String url) {
        if (url != null && url.startsWith("https://")) app.runOnUiThread(() -> app.apriFuori(url));
    }

    private PackageInfo info() {
        try {
            return app.getPackageManager().getPackageInfo(app.getPackageName(), 0);
        } catch (Exception e) {
            return null;
        }
    }
}
