package ba.unsa.etf.rma.ajdin_jajcanin.spirala2;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by x on 11/05/2018.
 */

public class Autor implements Parcelable {
    private String imeiPrezime;
    private ArrayList<String> knjige;

    public Autor(String imeiPrezime, ArrayList<String> knjige) {
        this.imeiPrezime = imeiPrezime;
        this.knjige = knjige;
    }

    public Autor(String imeiPrezime){
        this.imeiPrezime = imeiPrezime;
        knjige = new ArrayList<>();
    }

    public String getImeiPrezime() {
        return imeiPrezime;
    }

    public void setImeiPrezime(String imeiPrezime) {
        this.imeiPrezime = imeiPrezime;
    }

    public ArrayList<String> getKnjige() {
        return knjige;
    }

    public void setKnjige(ArrayList<String> knjige) {
        this.knjige = knjige;
    }

    public void dodajKnjigu(String k){
        this.knjige.add(k);
    }

    protected Autor(Parcel in) {
        imeiPrezime = in.readString();
        knjige = in.createStringArrayList();
    }

    public static final Creator<Autor> CREATOR = new Creator<Autor>() {
        @Override
        public Autor createFromParcel(Parcel in) {
            return new Autor(in);
        }

        @Override
        public Autor[] newArray(int size) {
            return new Autor[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(imeiPrezime);
        parcel.writeStringList(knjige);
    }

}
