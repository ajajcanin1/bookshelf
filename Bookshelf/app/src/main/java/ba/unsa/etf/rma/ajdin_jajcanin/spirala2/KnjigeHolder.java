package ba.unsa.etf.rma.ajdin_jajcanin.spirala2;

import java.util.ArrayList;

/**
 * Created by x on 28/03/2018.
 */

public class KnjigeHolder {
    private ArrayList<Knjiga> knjige = new ArrayList<Knjiga>();

    public ArrayList<Knjiga> getKnjige() {
        return knjige;
    }

    public void setKnjige(ArrayList<Knjiga> knjige) {
        this.knjige = knjige;
    }

    public int addKnjige(Knjiga k){
        this.knjige.add(k);
        return R.string.tAddedBook;
    }
    private static final KnjigeHolder ourInstance = new KnjigeHolder();

    public static KnjigeHolder getInstance() {
        return ourInstance;
    }

    private KnjigeHolder() {
    }
}
