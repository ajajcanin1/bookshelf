package ba.unsa.etf.rma.ajdin_jajcanin.spirala2;

        import android.graphics.Bitmap;
        import android.net.Uri;
        import android.os.Parcel;
        import android.os.Parcelable;
        import android.renderscript.ScriptGroup;
        import android.text.TextUtils;
        import android.widget.ImageView;

        import java.io.InputStream;
        import java.io.Serializable;
        import java.net.URI;
        import java.net.URL;
        import java.util.ArrayList;
        import java.util.Date;
        import java.util.List;
        import java.util.stream.Collectors;

/**
 * Created by x on 25/03/2018.
 */

public class Knjiga implements Serializable, Parcelable{
    private String id;
    private String naziv;
    private ArrayList<Autor> autori = new ArrayList<>();
    private String opis;
    private String datumObjavljivanja;
    private URL slika;

    private int redniBr;
    private int brojStranica;
    private String title;
    private String zanr;
    private Boolean daLiJePlav;

    //za spiralu2
    private static int brojac = 0;
    private String naslovna;


    public Knjiga(String id, String naziv, ArrayList<Autor> autori, String opis, String datumObjavljivanja, URL slika, int brojStranica) {
        this.id = id;
        this.naziv = naziv;
        this.autori = autori;
        this.opis = opis;
        this.datumObjavljivanja = datumObjavljivanja;
        this.slika = slika;
        this.brojStranica = brojStranica;
        this.daLiJePlav = false;
    }



    public Knjiga(Autor autor, String naziv, String zanr, String naslovna, Boolean daLiJePlav, String title) {
        this.autori.add(autor);
        this.naziv = naziv;
        this.zanr = zanr;
        this.naslovna = naslovna;
        this.daLiJePlav = daLiJePlav;
        redniBr = brojac++;
        this.title = title;
        this.daLiJePlav = false;
    }

    protected Knjiga(Parcel in) {
        id = in.readString();
        naziv = in.readString();
        autori = in.createTypedArrayList(Autor.CREATOR);
        opis = in.readString();
        datumObjavljivanja = in.readString();
        redniBr = in.readInt();
        brojStranica = in.readInt();
        title = in.readString();
        zanr = in.readString();
        byte tmpDaLiJePlav = in.readByte();
        daLiJePlav = tmpDaLiJePlav == 0 ? null : tmpDaLiJePlav == 1;
        naslovna = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(naziv);
        dest.writeTypedList(autori);
        dest.writeString(opis);
        dest.writeString(datumObjavljivanja);
        dest.writeInt(redniBr);
        dest.writeInt(brojStranica);
        dest.writeString(title);
        dest.writeString(zanr);
        dest.writeByte((byte) (daLiJePlav == null ? 0 : daLiJePlav ? 1 : 2));
        dest.writeString(naslovna);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Knjiga> CREATOR = new Creator<Knjiga>() {
        @Override
        public Knjiga createFromParcel(Parcel in) {
            return new Knjiga(in);
        }

        @Override
        public Knjiga[] newArray(int size) {
            return new Knjiga[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public List<Autor> getAutori() {
        return autori;
    }

    public void setAutori(ArrayList<Autor> autori) {
        this.autori = autori;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public String getDatumObjavljivanja() {
        return datumObjavljivanja;
    }

    public void setDatumObjavljivanja(String datumObjavljivanja) {
        this.datumObjavljivanja = datumObjavljivanja;
    }

    public URL getSlika() {
        return slika;
    }

    public void setSlika(URL slika) {
        this.slika = slika;
    }

    public int getBrojStranica() {
        return brojStranica;
    }

    public void setBrojStranica(int brojStranica) {
        this.brojStranica = brojStranica;
    }

    public String getZanr() {
        return zanr;
    }

    public void setZanr(String zanr) {
        this.zanr = zanr;
    }
    public Boolean getDaLiJePlav() {
        return daLiJePlav;
    }

    public void setDaLiJePlav(Boolean daLiJePlav) {
        this.daLiJePlav = daLiJePlav;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNaslovna() {
        return naslovna;
    }

    public void setNaslovna(String naslovna) {
        this.naslovna = naslovna;
    }
    public int getRedniBr() {
        return redniBr;
    }

    public void setRedniBr(int redniBr) {
        this.redniBr = redniBr;
    }

    public String getImeAutora() {
        return TextUtils.join(";", autori.stream().map(x-> x.getImeiPrezime()).collect(Collectors.toList()));
    }
    public void dodijeliRedniBr() {
        this.redniBr = brojac++;
    }

}
