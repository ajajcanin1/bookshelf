package ba.unsa.etf.rma.ajdin_jajcanin.spirala2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by x on 27/05/2018.
 */

public class BazaOpenHelper extends SQLiteOpenHelper {

    public BazaOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public BazaOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static final int DATABASE_VERSION = 6;
    //baza
    public static final String DATABASE_NAME = "eKnjigeBaza.db";
    //tabele
    public static final String TABLE_CATEGORY = "Kategorija";
    public static final String TABLE_BOOK = "Knjiga";
    public static final String TABLE_AUTHOR = "Autor";
    public static final String TABLE_AUTHORSHIP = "Autorstvo";
    //kolone

    //kolone za Kategorija
    public static final String _ID = "_id";
    public static final String TITLE = "naziv";
    //kolone za Knjiga
    public static final String BOOK_ID = "_id";
    public static final String BOOK_TITLE = "naziv";
    public static final String BOOK_DESCRIPTION = "opis";
    public static final String BOOK_PUBLICATION_DATE = "datumObjavljivanja";
    public static final String BOOK_NUM_PAGES = "brojStranica";
    public static final String BOOK_ID_WEB_SERVICE = "idWebServis";
    public static final String BOOK_ID_CATEGORIES = "idkategorije";
    public static final String BOOK_IMAGE = "slika";
    public static final String BOOK_SELECTED = "pregledana";
    //kolone za Autor
    public static final String AUTHOR_NAME = "ime";
    //kolone za Autorstvo
    public static final String AUTHORSHIP_ID_AUTHOR = "idautora";
    public static final String AUTHORSHIP_ID_BOOK = "idknjige";


    private static final String CREATE_TABLE_CATEGORY = "create table " +
            TABLE_CATEGORY + " (" + _ID +
            " integer primary key autoincrement, " +
            TITLE + " text unique);";

    private static final String CREATE_TABLE_BOOK = "create table " +
            TABLE_BOOK + " (" + _ID +
            " integer primary key autoincrement, " +
            TITLE + " text, " + BOOK_DESCRIPTION + " text, " +
            BOOK_PUBLICATION_DATE + " text, " + BOOK_NUM_PAGES + " integer, " +
            BOOK_ID_WEB_SERVICE + " text, " + BOOK_ID_CATEGORIES + " integer, "
            + BOOK_IMAGE + " text, " + BOOK_SELECTED + " integer);";

    private static final String CREATE_TABLE_AUTHOR = "create table " +
            TABLE_AUTHOR + " (" + _ID +
            " integer primary key autoincrement, " + AUTHOR_NAME + " text unique);";

    private static final String CREATE_TABLE_AUTHORSHIP = "create table " +
            TABLE_AUTHORSHIP + " (" + _ID +
            " integer primary key autoincrement, " + AUTHORSHIP_ID_AUTHOR + " integer, " +
            AUTHORSHIP_ID_BOOK + " integer);";


    private String[] koloneRezultat = new String[]{BOOK_ID, BOOK_TITLE, BOOK_DESCRIPTION, BOOK_PUBLICATION_DATE,
            BOOK_NUM_PAGES, BOOK_ID_WEB_SERVICE, BOOK_ID_CATEGORIES, BOOK_IMAGE, BOOK_SELECTED};

    private String[] koloneKnjiga = new String[]{BOOK_ID, BOOK_TITLE};
    private String[] koloneAutorstva = new String[]{AUTHORSHIP_ID_AUTHOR, AUTHORSHIP_ID_BOOK};
    private String[] koloneAutora = new String[]{_ID, AUTHOR_NAME};
    private String[] koloneKategorija = new String[]{_ID, TITLE};

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CATEGORY);
        db.execSQL(CREATE_TABLE_BOOK);
        db.execSQL(CREATE_TABLE_AUTHOR);
        db.execSQL(CREATE_TABLE_AUTHORSHIP);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AUTHOR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AUTHORSHIP);
        onCreate(db);
    }

    public long dodajKategoriju(String naziv) {
        ContentValues novi = new ContentValues();
        novi.put(TITLE, naziv);
        SQLiteDatabase db = getWritableDatabase();
        return db.insertWithOnConflict(TABLE_CATEGORY, null, novi, SQLiteDatabase.CONFLICT_IGNORE);
    }

    public long dodajKnjigu(Knjiga knjiga) {
        ContentValues novi = new ContentValues();
        novi.put(BOOK_TITLE, knjiga.getNaziv());
        novi.put(BOOK_DESCRIPTION, knjiga.getOpis());
        novi.put(BOOK_PUBLICATION_DATE, knjiga.getDatumObjavljivanja());
        novi.put(BOOK_NUM_PAGES, knjiga.getBrojStranica());
        novi.put(BOOK_ID_WEB_SERVICE, knjiga.getId());
        novi.put(BOOK_ID_CATEGORIES, getIdZanr(knjiga.getZanr()));
        novi.put(BOOK_IMAGE, knjiga.getSlika() == null ? "" : knjiga.getSlika().toString());
        novi.put(BOOK_SELECTED, knjiga.getDaLiJePlav() ? 1 : 0);
        SQLiteDatabase db = getWritableDatabase();
        long idKnjige = -1;
        if (!(knjiga.getId().isEmpty()) || findBook(knjiga.getNaziv(), knjiga.getImeAutora()).isEmpty()) {
            idKnjige = db.insertWithOnConflict(TABLE_BOOK, null, novi, SQLiteDatabase.CONFLICT_IGNORE);
            Log.d("KAMIJON", String.valueOf(idKnjige));
            if (idKnjige != -1) {
                for (Autor x : knjiga.getAutori()) {
                    ContentValues noviAutor = new ContentValues();
                    noviAutor.put(AUTHOR_NAME, x.getImeiPrezime());
                    long idAutora = db.insertWithOnConflict(TABLE_AUTHOR, null, noviAutor, SQLiteDatabase.CONFLICT_IGNORE);
                    ContentValues novoAutorstvo = new ContentValues();
                    novoAutorstvo.put(AUTHORSHIP_ID_AUTHOR, idAutora == -1 ? getIdAutora(x.getImeiPrezime()) : idAutora);
                    novoAutorstvo.put(AUTHORSHIP_ID_BOOK, idKnjige);
                    db.insertWithOnConflict(TABLE_AUTHORSHIP, null, novoAutorstvo, SQLiteDatabase.CONFLICT_IGNORE);
                }
            }
        }
        return idKnjige;
    }

    public ArrayList<Knjiga> knjigeKategorije(long idKategorije) {
        ArrayList<Knjiga> knjige = new ArrayList<>();
        ArrayList<Autor> autori = new ArrayList<>();
        Cursor c = getCursor(TABLE_BOOK, koloneRezultat, BOOK_ID_CATEGORIES + "=" + idKategorije);
        if (c.moveToFirst()) {
            do {
                addKnjige(c, knjige, autori);
            } while (c.moveToNext());
        }
        return knjige;
    }

    public ArrayList<Knjiga> knjigeAutora(long idAutora) {
        ArrayList<Knjiga> knjige = new ArrayList<>();
        ArrayList<Autor> autori = new ArrayList<>();
        Cursor c = getCursor(TABLE_AUTHORSHIP, koloneAutorstva, koloneAutorstva[0] + "=\"" + idAutora + "\"");
        if (c.moveToFirst()) {
            do {
                long idKnjige = getLong(c, koloneAutorstva[1]);
                Cursor cKnjiga = getCursor(TABLE_BOOK, koloneRezultat, koloneKnjiga[0] + "=\"" + idKnjige + "\"");
                if (cKnjiga.moveToFirst()) {
                    do {
                        addKnjige(cKnjiga, knjige, autori);
                    } while (cKnjiga.moveToNext());
                }
            } while (c.moveToNext());
        }
        return knjige;
    }

    private Cursor getCursor(String tabela, String[] kolone, String where) {
        SQLiteDatabase db = getWritableDatabase();
        return db.query(tabela, kolone, where, null, null, null, null);
    }

    private ArrayList<Autor> getAutore(long idKnjige) {
        ArrayList<Autor> a = new ArrayList<>();
        Cursor cAutorstvo = getCursor(TABLE_AUTHORSHIP, koloneAutorstva, AUTHORSHIP_ID_BOOK + "=" + idKnjige);
        if (cAutorstvo.moveToFirst()) {
            do {
                long idAutorstvo = getLong(cAutorstvo, koloneAutorstva[0]);
                Cursor cAutor = getCursor(TABLE_AUTHOR, koloneAutora, koloneAutora[0] + "=" + idAutorstvo);
                if (cAutor.moveToFirst()) {
                    do {
                        a.add(new Autor((getString(cAutor, koloneAutora[1])),
                                getKnjigeAutora(getLong(cAutor, koloneRezultat[0]))));
                    } while (cAutor.moveToNext());
                }
            } while (cAutorstvo.moveToNext());
        }
        return a;
    }

    private ArrayList<String> getKnjigeAutora(long idAutor) {
        ArrayList<String> k = new ArrayList<>();
        Cursor cAutorstvo = getCursor(TABLE_AUTHORSHIP, koloneAutorstva, AUTHORSHIP_ID_AUTHOR + "=" + idAutor);
        if (cAutorstvo.moveToFirst()) {
            do {
                long idKnjige = getLong(cAutorstvo, koloneAutorstva[1]);
                Cursor cKnjiga = getCursor(TABLE_BOOK, koloneRezultat, koloneKnjiga[0] + "=" + idKnjige);
                if (cKnjiga.moveToFirst()) {
                    do {
                        k.add(new String(getString(cKnjiga, koloneRezultat[5])));
                    } while (cKnjiga.moveToNext());
                }
            } while (cAutorstvo.moveToNext());
        }
        return k;
    }

    private String getString(Cursor c, String kolona) {
        return c.getString(c.getColumnIndexOrThrow(kolona));
    }

    private long getLong(Cursor c, String kolona) {
        return c.getLong(c.getColumnIndexOrThrow(kolona));
    }

    private int getInt(Cursor c, String kolona) {
        return c.getInt(c.getColumnIndexOrThrow(kolona));
    }

    private void addKnjige(Cursor c, ArrayList<Knjiga> knjige, ArrayList<Autor> autori) {
        try {
            autori = getAutore(getLong(c, koloneRezultat[0]));
            Knjiga k = null;
            if (getString(c, koloneRezultat[7]).startsWith("http")) {
                k = new Knjiga(getString(c, koloneRezultat[5]), getString(c, koloneRezultat[1]),
                        autori, getString(c, koloneRezultat[2]), getString(c, koloneRezultat[3]),
                        new URL(getString(c, koloneRezultat[7])), getInt(c, koloneRezultat[4]));
                k.setDaLiJePlav(getInt(c, koloneRezultat[8]) == 1);
                k.setNaslovna("");
                k.dodijeliRedniBr();
                Log.d("kamijon", "teeest");
            } else {
                k = new Knjiga(getString(c, koloneRezultat[5]), getString(c, koloneRezultat[1]),
                        autori, getString(c, koloneRezultat[2]), getString(c, koloneRezultat[3]),
                        null, getInt(c, koloneRezultat[4]));
                k.setDaLiJePlav(getInt(c, koloneRezultat[8]) == 1);
                k.setNaslovna(getString(c, koloneRezultat[7]));
                k.dodijeliRedniBr();
                Log.d("kamijon10", k.getDaLiJePlav().toString());
            }
            knjige.add(k);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getKategorije() {
        ArrayList<String> k = new ArrayList<>();
        Cursor c = getCursor(TABLE_CATEGORY, koloneKategorija, null);
        if (c.moveToFirst()) {
            do {
                k.add(getString(c, koloneKategorija[1]));
            }
            while (c.moveToNext());
        }

        return k;
    }

    public ArrayList<Autor> getAutori() {
        ArrayList<Autor> a = new ArrayList();
        Cursor c = getCursor(TABLE_AUTHOR, koloneAutora, null);
        if (c.moveToFirst()) {
            do {
                a.add(new Autor((getString(c, koloneAutora[1])),
                        getKnjigeAutora(getLong(c, koloneAutora[0]))));
            }
            while (c.moveToNext());
        }
        return a;
    }

    public long getIdZanr(String zanr) {
        Cursor c = getCursor(TABLE_CATEGORY, koloneKategorija, koloneKategorija[1] + "=\"" + zanr + "\"");
        if (c.moveToFirst()) {
            return getLong(c, koloneKategorija[0]);
        }
        return -1;
    }

    public long getIdAutora(String autor) {
        Cursor c = getCursor(TABLE_AUTHOR, koloneAutora, koloneAutora[1] + "=\"" + autor + "\"");
        if (c.moveToFirst()) {
            return getLong(c, koloneAutora[0]);
        }
        return -1;
    }

    public Knjiga getKnjiga(String knjigaId) {
        ArrayList<Knjiga> knjige = new ArrayList<>();
        ArrayList<Autor> autori = new ArrayList<>();
        Cursor c = getCursor(TABLE_BOOK, koloneRezultat, BOOK_ID_WEB_SERVICE + "=\"" + knjigaId + "\"");
        if (c.moveToFirst()) {
            do {
                addKnjige(c, knjige, autori);
            } while (c.moveToNext());
        }
        if (knjige.size() > 0)
            return knjige.get(0);
        return null;
    }

    public void setBackground(String knjigaId) {
        ContentValues cv = new ContentValues();
        cv.put(BOOK_SELECTED, 1);
        this.getWritableDatabase().update(TABLE_BOOK, cv, BOOK_ID+ "=\"" + knjigaId + "\"", null);
    }

    public void setBackground(Knjiga k) {
        ContentValues cv = new ContentValues();
        cv.put(BOOK_SELECTED, 1);
        this.getWritableDatabase().update(TABLE_BOOK, cv, BOOK_ID_WEB_SERVICE + "=\"" + k.getId() + "\"", null);
    }

    public String findBook(String naziv, String autor) {
        Cursor c = getCursor(TABLE_BOOK, koloneRezultat, koloneRezultat[1] + "=\"" + naziv + "\" AND " +
                koloneRezultat[5] + "=\"\"");
        if (c.moveToFirst()) {
            do {
                long idKnjige = getLong(c, koloneRezultat[0]);
                Cursor cAutorstva = getCursor(TABLE_AUTHORSHIP, koloneAutorstva, koloneAutorstva[1] + "=" + idKnjige);
                if (cAutorstva.moveToFirst()) {
                    do {
                        long idAutori = getLong(cAutorstva, koloneAutorstva[0]);
                        Cursor cAutori = getCursor(TABLE_AUTHOR, koloneAutora, koloneAutora[0] + "=" + idAutori + " AND " +
                                koloneAutora[1] + "=\"" + autor + "\"");
                        if (cAutori.moveToFirst()) return String.valueOf(idKnjige);
                    } while (cAutorstva.moveToNext());
                }
            } while (c.moveToNext());
        }
        return "";
    }
}
