package perpustakaan;
import java.util.*;
class FakePeminjamanRepo implements PeminjamanRepository {
    private final Map<String, Integer> aktif = new HashMap<>();
    final List<Peminjaman> tersimpan = new ArrayList<>();
    void setJumlahAktif(String idAnggota, int n) { aktif.put(idAnggota, n); }
    public int jumlahAktif(String idAnggota) { return aktif.getOrDefault(idAnggota, 0); }
    public Peminjaman simpan(Peminjaman p) { tersimpan.add(p); return p; }
}
