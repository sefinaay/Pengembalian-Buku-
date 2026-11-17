package perpustakaan;

import static org.junit.jupiter.api.Assertions.*;
import java.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class JalurPenolakanTest {
    Map<String, Anggota> anggota = new HashMap<>();
    Map<String, Buku> buku = new HashMap<>();
    FakePeminjamanRepo pinjamRepo = new FakePeminjamanRepo();
    PeminjamanService service = new PeminjamanService(anggota::get, buku::get, pinjamRepo);

    @BeforeEach
    void siapkanDataUji() {
        anggota.put("M-01", new Anggota("M-01", true));
        anggota.put("M-02", new Anggota("M-02", true));
        anggota.put("M-07", new Anggota("M-07", false));
        pinjamRepo.setJumlahAktif("M-01", 3);
        buku.put("B001", new Buku("B001", "Pemrograman Java", 5));
        buku.put("B003", new Buku("B003", "RPL", 0));
    }

    @ParameterizedTest(name = "{0}: {3}")
    @CsvSource({
        "TC-01, M-07, B001, Anggota tidak aktif",
        "TC-02, M-01, B001, Kuota pinjam penuh",
        "TC-03, M-02, B003, Stok buku habis"
    })
    void jalurPenolakan(String tc, String idAnggota, String idBuku, String pesan) {
        var e = assertThrows(PinjamException.class,
                () -> service.pinjam(idAnggota, idBuku));
        assertEquals(pesan, e.getMessage());
    }
}
