package ba.unsa.etf.rma.ajdin_jajcanin.spirala2;

/**
 * Created by x on 11/04/2018.
 */

public class DaLiJeAutor {
    private boolean daLiJeAutor = false;

    public boolean isDaLiJeAutor() {
        return daLiJeAutor;
    }

    public void setDaLiJeAutor(boolean daLiJeAutor) {
        this.daLiJeAutor = daLiJeAutor;
    }

    private static final DaLiJeAutor ourInstance = new DaLiJeAutor();

    public static DaLiJeAutor getInstance() {
        return ourInstance;
    }

    private DaLiJeAutor() {
    }
}
