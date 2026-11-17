package perpustakaan;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PeminjamanServiceTest {
    Map<String, Anggota> anggota = new HashMap<>();
    Map<String, Buku> buku = new HashMap<>();
    FakePeminjamanRepo pinjamRepo = new FakePeminjamanRepo();
    PeminjamanService service =
        new PeminjamanService(anggota::get, buku::get, pinjamRepo);

    @BeforeEach
    void siapkanDataUji() {
        anggota.put("M-01", new Anggota("M-01", true));   // aktif
        anggota.put("M-02", new Anggota("M-02", true));   // aktif
        anggota.put("M-07", new Anggota("M-07", false));  // nonaktif
        pinjamRepo.setJumlahAktif("M-01", 3);             // kuota penuh
        buku.put("B001", new Buku("B001", "Pemrograman Java", 5));
        buku.put("B003", new Buku("B003", "RPL", 0));     // stok habis
    }

    @Test @DisplayName("TC-01 | P1: anggota nonaktif ditolak")
    void tc01() {
        var e = assertThrows(PinjamException.class, () -> service.pinjam("M-07", "B001"));
        assertEquals("Anggota tidak aktif", e.getMessage());
    }

    @Test @DisplayName("TC-02 | P2: kuota pinjam penuh")
    void tc02() {
        var e = assertThrows(PinjamException.class, () -> service.pinjam("M-01", "B001"));
        assertEquals("Kuota pinjam penuh", e.getMessage());
    }

    @Test @DisplayName("TC-03 | P3: stok buku habis")
    void tc03() {
        var e = assertThrows(PinjamException.class, () -> service.pinjam("M-02", "B003"));
        assertEquals("Stok buku habis", e.getMessage());
    }

    @Test @DisplayName("TC-04 | P4: peminjaman berhasil")
    void tc04() {
        Peminjaman p = service.pinjam("M-02", "B001");
        assertEquals(4, buku.get("B001").getStok());
        assertEquals(LocalDate.now().plusDays(7), p.getJatuhTempo());
        assertEquals(1, pinjamRepo.tersimpan.size());
    }
}
