package perpustakaan;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class PengembalianService {
    static final long TARIF_PER_HARI = 1000;
    static final long DENDA_RUSAK = 20000;

    public enum Kondisi { BAIK, RUSAK }

    public record Bukti(long denda, String pesan) {}

    private final PengembalianRepository repo;

    public PengembalianService(PengembalianRepository repo) { this.repo = repo; }

    public Bukti kembalikan(String idPeminjaman, LocalDate tglKembali,
                            Kondisi kondisi, long bayar) {
        Peminjaman p = repo.cari(idPeminjaman);                       
        if (p == null) {                                              
            throw new KembaliException("Peminjaman tidak ditemukan");  
        }
        if (p.isDikembalikan()) {                                      
            throw new KembaliException("Buku sudah dikembalikan");
        }
        long denda = hitungDenda(p.getJatuhTempo(), tglKembali);       
        if (kondisi == Kondisi.RUSAK) {                                
            denda += DENDA_RUSAK;                                      
        }
        if (bayar < denda) {                                           
            throw new KembaliException("Pembayaran kurang");     
        }
        p.getBuku().tambahStok();                                      
        p.tandaiDikembalikan();                                        
        return new Bukti(denda,                                        
                denda > 0 ? "Berhasil, denda dibayar" : "Berhasil tanpa denda");
    }                                                                  

    long hitungDenda(LocalDate jatuhTempo, LocalDate kembali) {
        if (!kembali.isAfter(jatuhTempo)) {
            return 0;
        }
        long hari = ChronoUnit.DAYS.between(jatuhTempo, kembali);
        return hari * TARIF_PER_HARI;
    }
}
