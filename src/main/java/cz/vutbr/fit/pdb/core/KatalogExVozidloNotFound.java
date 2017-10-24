/**
 * VUT FIT PDB project
 *
 * @author Matúš Bútora
 * @author Andrej Klocok
 * @author Tomáš Vlk
 */

package cz.vutbr.fit.pdb.core;

public class KatalogExVozidloNotFound extends Exception {

    private Vozidlo vozidlo = null;

    public KatalogExVozidloNotFound(String message, Vozidlo vozidlo) {
        super(message);
        this.vozidlo = vozidlo;
    }

    public void setVozidlo(Vozidlo vozidlo) {
        this.vozidlo = vozidlo;
    }

    public Vozidlo getVozidlo() {
        return this.vozidlo;
    }
}
