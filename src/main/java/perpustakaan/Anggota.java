package perpustakaan;
public class Anggota {
    private final String id; private final boolean aktif;
    public Anggota(String id, boolean aktif) { this.id = id; this.aktif = aktif; }
    public String getId() { return id; }
    public boolean isAktif() { return aktif; }
}
