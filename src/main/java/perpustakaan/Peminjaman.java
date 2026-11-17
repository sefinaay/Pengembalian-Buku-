package perpustakaan;
import java.time.LocalDate;
public class Peminjaman {
    private final Anggota anggota; private final Buku buku;
    private final LocalDate tglPinjam; private final LocalDate jatuhTempo;
    private boolean dikembalikan;
    public Peminjaman(Anggota anggota, Buku buku, LocalDate tglPinjam) {
        this.anggota = anggota; this.buku = buku;
        this.tglPinjam = tglPinjam; this.jatuhTempo = tglPinjam.plusDays(7);
    }
    public LocalDate getJatuhTempo() { return jatuhTempo; }
    public Buku getBuku() { return buku; }
    public boolean isDikembalikan() { return dikembalikan; }
    public void tandaiDikembalikan() { this.dikembalikan = true; }
}
