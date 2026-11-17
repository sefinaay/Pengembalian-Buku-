package perpustakaan;
import java.time.LocalDate;
public class PeminjamanService {
    static final int MAKS = 3;
    private final AnggotaRepository anggotaRepo;
    private final BukuRepository bukuRepo;
    private final PeminjamanRepository pinjamRepo;

    public PeminjamanService(AnggotaRepository a, BukuRepository b, PeminjamanRepository p) {
        this.anggotaRepo = a; this.bukuRepo = b; this.pinjamRepo = p;
    }

    public Peminjaman pinjam(String idAnggota, String idBuku) {
        Anggota anggota = anggotaRepo.cari(idAnggota);          
        if (!anggota.isAktif()) {                               
            throw new PinjamException("Anggota tidak aktif");   
        }
        if (pinjamRepo.jumlahAktif(idAnggota) >= MAKS) {        
            throw new PinjamException("Kuota pinjam penuh");    
        }
        Buku buku = bukuRepo.cari(idBuku);
        if (buku.getStok() == 0) {        
            throw new PinjamException("Stok buku habis");
        }
        buku.kurangiStok();                                    
        Peminjaman p = new Peminjaman(anggota, buku, LocalDate.now());
        return pinjamRepo.simpan(p);                           
    }                                                          
}
