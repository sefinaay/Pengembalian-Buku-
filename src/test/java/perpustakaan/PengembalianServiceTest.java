package perpustakaan;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.*;
import perpustakaan.PengembalianService.Kondisi;

class PengembalianServiceTest {
    // jatuh tempo semua peminjaman: 10-11-2026 (pinjam 03-11-2026 + 7 hari)
    static final LocalDate TEMPO = LocalDate.of(2026, 11, 10);
    Map<String, Peminjaman> data = new HashMap<>();
    Buku buku = new Buku("B001", "Pemrograman Java", 4);
    Anggota anggota = new Anggota("M-02", true);
    PengembalianService service = new PengembalianService(data::get);

    @BeforeEach
    void siapkanDataUji() {
        data.put("P-01", new Peminjaman(anggota, buku, LocalDate.of(2026, 11, 3)));
        data.put("P-02", new Peminjaman(anggota, buku, LocalDate.of(2026, 11, 3)));
        data.put("P-03", new Peminjaman(anggota, buku, LocalDate.of(2026, 11, 3)));
        data.put("P-04", new Peminjaman(anggota, buku, LocalDate.of(2026, 11, 3)));
        data.put("P-99", new Peminjaman(anggota, buku, LocalDate.of(2026, 11, 3)));
        data.get("P-99").tandaiDikembalikan(); // sudah dikembalikan
    }

    @Test @DisplayName("TC-01 | P1: peminjaman tidak ditemukan")
    void tc01() {
        var e = assertThrows(KembaliException.class,
                () -> service.kembalikan("P-XX", TEMPO, Kondisi.BAIK, 0));
        assertEquals("Peminjaman tidak ditemukan", e.getMessage());
    }

    @Test @DisplayName("TC-02 | P2: buku sudah dikembalikan")
    void tc02() {
        var e = assertThrows(KembaliException.class,
                () -> service.kembalikan("P-99", TEMPO, Kondisi.BAIK, 0));
        assertEquals("Buku sudah dikembalikan", e.getMessage());
        assertEquals(4, buku.getStok());
    }

    @Test @DisplayName("TC-03 | P3: tepat waktu, baik, tanpa denda")
    void tc03() {
        var b = service.kembalikan("P-01", TEMPO, Kondisi.BAIK, 0);
        assertEquals(0, b.denda());
        assertEquals("Berhasil tanpa denda", b.pesan());
        assertTrue(data.get("P-01").isDikembalikan());
        assertEquals(5, buku.getStok());
        assertEquals("B001", buku.getId());
        assertEquals("M-02", anggota.getId());
    }

    @Test @DisplayName("TC-04 | P4: terlambat 3 hari, denda 3000 dibayar")
    void tc04() {
        var b = service.kembalikan("P-02", LocalDate.of(2026, 11, 13), Kondisi.BAIK, 3000);
        assertEquals(3000, b.denda());
        assertEquals("Berhasil, denda dibayar", b.pesan());
    }

    @Test @DisplayName("TC-05 | P5: buku rusak, denda 20000")
    void tc05() {
        var b = service.kembalikan("P-03", TEMPO, Kondisi.RUSAK, 20000);
        assertEquals(20000, b.denda());
        assertEquals("Berhasil, denda dibayar", b.pesan());
    }

    @Test @DisplayName("TC-06 | P6: pembayaran kurang, tidak dicatat")
    void tc06() {
        var e = assertThrows(KembaliException.class,
                () -> service.kembalikan("P-04", LocalDate.of(2026, 11, 13), Kondisi.BAIK, 2000));
        assertEquals("Pembayaran kurang", e.getMessage());
        assertFalse(data.get("P-04").isDikembalikan());
        assertEquals(4, buku.getStok());
    }

    @Test @DisplayName("TC-07 | terlambat lama (82 hari), tanpa batas maksimum")
    void tc07() {
        assertEquals(82000, service.hitungDenda(TEMPO, LocalDate.of(2027, 1, 31)));
    }

    @Test @DisplayName("TC-08 | batas terlambat 1 hari = 1000")
    void tc08() {
        assertEquals(1000, service.hitungDenda(TEMPO, LocalDate.of(2026, 11, 11)));
    }
}
