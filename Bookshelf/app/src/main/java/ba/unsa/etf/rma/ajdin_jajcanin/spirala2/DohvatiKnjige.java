package ba.unsa.etf.rma.ajdin_jajcanin.spirala2;

import android.os.AsyncTask;
import android.util.Log;
import android.view.ViewDebug;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by x on 11/05/2018.
 */

public class DohvatiKnjige extends AsyncTask<String, Integer, Void> {


    public interface IDohvatiKnjigeDone {
        public void onDohvatiDone(ArrayList<Knjiga> rez);
    }

    ArrayList<Knjiga> rez = new ArrayList<>();
    private IDohvatiKnjigeDone pozivatelj;

    public DohvatiKnjige(IDohvatiKnjigeDone p) {
        pozivatelj = p;
    }

    @Override
    protected Void doInBackground(String... strings) {
        try {
            for (int k = 0; k < strings.length; k++) {
                String query = URLEncoder.encode((String) strings[k], "utf-8");
                String url1 = "https://www.googleapis.com/books/v1/volumes?q=intitle:" + query + "&maxResults=5";
                URL url = new URL(url1);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    JSONObject jo = new JSONObject(convertStreamToString(in));
                    if (jo.has("items")) {
                        JSONArray items = jo.getJSONArray("items");
                        for (int i = 0; i < items.length(); i++) {
                            String id = "", naziv = "", opis = "", datumObjavljivanja = "";
                            URL slika = null;
                            ArrayList<Autor> autori = new ArrayList<>();
                            JSONObject book = items.getJSONObject(i);
                            int brojStranica = 0;
                            if (book.has("id")) id = book.getString("id");
                            JSONObject info = new JSONObject();
                            if (book.has("id")) {
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
                                JSONObject img = new JSONObject();
                                if (info.has("imageLinks")) {
                                    img = info.getJSONObject("imageLinks");
                                    if(img.has("smallThumbnail")) slika = new URL(img.getString("smallThumbnail"));
                                    else if(img.has("thumbnail")) slika = new URL(img.getString("thumbnail"));//--------------------------------------------------------------------------------------------- HARDCODE
                                }
                                if (info.has("pageCount")) brojStranica = info.getInt("pageCount");
                            }
                            rez.add(new Knjiga(id, naziv, autori, opis, datumObjavljivanja, slika, brojStranica));
                        }
                    }
                }
            }
            } catch(IOException | JSONException e){
                e.printStackTrace();
            } catch (Exception e){
            }

        return null;
    }
    @Override
    public void onPostExecute(Void aVoid){
        super.onPostExecute(aVoid);
        pozivatelj.onDohvatiDone(rez);
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