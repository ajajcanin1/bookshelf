package ba.unsa.etf.rma.ajdin_jajcanin.spirala2;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import static android.app.DownloadManager.STATUS_RUNNING;

public class KnjigePoznanika extends IntentService {

    public int STATUS_START = 0;
    public int STATUS_FINISH = 1;
    public int STATUS_ERROR = 2;

    ArrayList<Knjiga> rez = new ArrayList<>();
    public KnjigePoznanika() {
        super(null);
    }

    public KnjigePoznanika(String s) {
        super(s);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
   /* @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        startBackgroundTask(intent, startId);
        return Service.START_STICKY;
    }*/

    @Override
    protected void onHandleIntent(Intent intent) {
        ResultReceiver receiver = intent.getParcelableExtra("resultReceiver");
        Bundle bundle = new Bundle();
        try {
            String idKorisnika = intent.getStringExtra("idKorisnika");
            receiver.send(STATUS_START, Bundle.EMPTY);
            String query = URLEncoder.encode(idKorisnika, "utf-8");
            String url1 = "https://www.googleapis.com/books/v1/users/" + query + "/bookshelves";
            URL url = new URL(url1);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            ArrayList<String> idBookshelf = new ArrayList<>();
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JSONObject jo = new JSONObject(convertStreamToString(in));
                JSONArray items = jo.getJSONArray("items");

                for(int i = 0; i < items.length(); i++){
                    JSONObject bookshelf = items.getJSONObject(i);
                    if(bookshelf.has("id")) idBookshelf.add(bookshelf.getString("id"));
                }
            }
            for(int k=0; k<idBookshelf.size(); k++) {
                String url2 = "https://www.googleapis.com/books/v1/users/" + query + "/bookshelves/" + idBookshelf.get(k) + "/volumes";
                url = new URL(url2);
                urlConnection = (HttpURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    JSONObject jo = new JSONObject(convertStreamToString(in));

                    if (jo.has("items")) {
                        JSONArray items = jo.getJSONArray("items");
                        for (int i = 0; i < items.length(); i++) {
                            ArrayList<Autor> autori = new ArrayList<>();
                            JSONObject book = items.getJSONObject(i);
                            String id = "", naziv = "", opis = "", datumObjavljivanja = "";
                            int brojStranica = 0;
                            URL slika = null;
                            if (book.has("id")) id = book.getString("id");
                            JSONObject info = new JSONObject();
                            if (book.has("volumeInfo")) {
                                info = book.getJSONObject("volumeInfo");
                                if (info.has("title")) naziv = info.getString("title");
                                JSONArray bookAuthors = new JSONArray();
                                if (info.has("authors")) bookAuthors = info.getJSONArray("authors");
                                //za autora ukoliko je dodan isti autor sa razlicitim knjigama slucaj
                                for (int j = 0; j < bookAuthors.length(); j++) {
                                    Autor a = new Autor(bookAuthors.getString(j));
                                    Boolean nijePronadjen = true;
                                    for (Autor p : autori) {
                                        if (p.getImeiPrezime().toLowerCase().equals(a.getImeiPrezime().toLowerCase())) {
                                            nijePronadjen = false;
                                            p.dodajKnjigu(id);
                                            // a = p; <---------------------------------------------------------------------------------
                                        }
                                    }
                                    if (nijePronadjen) {
                                        a.dodajKnjigu(id);
                                        autori.add(a);
                                    }
                                }
                                if (info.has("description")) opis = info.getString("description");
                                if (info.has("publishedDate"))
                                    datumObjavljivanja = info.getString("publishedDate");

                                slika = null; //--------------------------------------------------------------------------------------------- HARDCODE
                                if (info.has("pageCount")) brojStranica = info.getInt("pageCount");
                            }
                            rez.add(new Knjiga(id, naziv, autori, opis, datumObjavljivanja, slika, brojStranica));
                            bundle.putParcelableArrayList("result", rez);
                            receiver.send(STATUS_FINISH, bundle);
                        }
                    }
                }
            }

        } catch (Exception e) {
            receiver.send(STATUS_ERROR, bundle);
        }


    }
    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new
                InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
        } finally {
            try {
                is.close();
            } catch (IOException e) {
            }
        }
        return sb.toString();

    }
}

