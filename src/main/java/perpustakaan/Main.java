package perpustakaan;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import perpustakaan.PengembalianService.Kondisi;

public class Main {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final Scanner IN = new Scanner(System.in);

    public static void main(String[] args) {
        Map<String, Peminjaman> data = siapkanData();
        PengembalianService service = new PengembalianService(data::get);
        while (true) {
            System.out.println();
            System.out.println("=== SISTEM PERPUSTAKAAN ===");
            System.out.println("1. Kembalikan buku");
            System.out.println("2. Lihat daftar peminjaman");
            System.out.println("0. Keluar");
            String pilih = baca("Pilih menu");
            if (pilih.equals("1")) {
                kembalikan(data, service);
            } else if (pilih.equals("2")) {
                daftar(data);
            } else if (pilih.equals("0")) {
                System.out.println("Sampai jumpa.");
                return;
            } else {
                System.out.println("Menu tidak dikenal.");
            }
        }
    }

    private static Map<String, Peminjaman> siapkanData() {
        Anggota anggota = new Anggota("M-02", true);
        Buku buku = new Buku("B001", "Pemrograman Java", 4);
        LocalDate pinjam = LocalDate.of(2026, 11, 3);
        Map<String, Peminjaman> data = new LinkedHashMap<>();
        for (String id : new String[] {"P-01", "P-02", "P-03", "P-04"}) {
            data.put(id, new Peminjaman(anggota, buku, pinjam));
        }
        return data;
    }

    private static void daftar(Map<String, Peminjaman> data) {
        System.out.println();
        System.out.println("Kode   Buku   Jatuh tempo  Status       Stok buku");
        for (Map.Entry<String, Peminjaman> e : data.entrySet()) {
            Peminjaman p = e.getValue();
            System.out.printf("%-6s %-6s %-12s %-12s %d%n", e.getKey(), p.getBuku().getId(),
                    p.getJatuhTempo().format(FMT),
                    p.isDikembalikan() ? "Dikembalikan" : "Dipinjam", p.getBuku().getStok());
        }
    }

    private static void kembalikan(Map<String, Peminjaman> data, PengembalianService service) {
        System.out.println();
        System.out.println("--- PENGEMBALIAN BUKU ---");
        try {
            String id = baca("Kode peminjaman");
            LocalDate tgl = LocalDate.parse(baca("Tanggal kembali (dd-MM-yyyy)"), FMT);
            String k = baca("Kondisi buku (1=Baik, 2=Rusak)");
            Kondisi kondisi = k.equals("2") ? Kondisi.RUSAK : Kondisi.BAIK;

            Peminjaman p = data.get(id);
            long bayar = 0;
            if (p != null && !p.isDikembalikan()) {
                long denda = service.hitungDenda(p.getJatuhTempo(), tgl)
                        + (kondisi == Kondisi.RUSAK ? PengembalianService.DENDA_RUSAK : 0);
                System.out.println("Denda yang harus dibayar: " + rupiah(denda));
                bayar = Long.parseLong(baca("Pembayaran (Rp)"));
            }
            PengembalianService.Bukti bukti = service.kembalikan(id, tgl, kondisi, bayar);
            System.out.println("BERHASIL: " + bukti.pesan());
            System.out.println("Denda   : " + rupiah(bukti.denda()));
            System.out.println("Stok buku sekarang: " + data.get(id).getBuku().getStok());
        } catch (KembaliException e) {
            System.out.println("DITOLAK : " + e.getMessage());
        } catch (DateTimeParseException | NumberFormatException e) {
            System.out.println("Input tidak valid, ulangi dari menu.");
        }
    }

    private static String baca(String label) {
        System.out.print(label + ": ");
        return IN.nextLine().trim();
    }

    private static String rupiah(long n) {
        return String.format("Rp%,d", n).replace(',', '.');
    }
}
