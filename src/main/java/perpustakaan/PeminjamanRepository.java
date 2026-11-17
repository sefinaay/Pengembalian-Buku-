package perpustakaan;
public interface PeminjamanRepository {
    int jumlahAktif(String idAnggota);
    Peminjaman simpan(Peminjaman p);
}
