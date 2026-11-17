package perpustakaan;
public class Buku {
    private final String id; private final String judul; private int stok;
    public Buku(String id, String judul, int stok) { this.id = id; this.judul = judul; this.stok = stok; }
    public String getId() { return id; }
    public int getStok() { return stok; }
    public void kurangiStok() { stok--; }
    public void tambahStok() { stok++; }
}
