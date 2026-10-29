package com.prisma.util;

/** Nilai ENUM dari database (Prisma_tv.sql) agar form dan database selalu selaras. */
public final class Options {
    private Options() { }

    public static final String ROLE_ADMIN = "Admin";
    public static final String ROLE_KONTEN = "Petugas Konten";
    public static final String ROLE_JADWAL = "Petugas Jadwal";
    public static final String[] ROLES = {ROLE_ADMIN, ROLE_KONTEN, ROLE_JADWAL};

    public static final String[] STATUS_AKTIF = {"aktif", "nonaktif"};
    public static final String[] KATEGORI_PROGRAM = {"Berita", "Berita Daerah", "Infotainment", "Talkshow",
            "Kuliner", "Dokumenter", "Olahraga", "Teknologi", "Edukasi", "Musik", "Film", "Drama", "Kartun",
            "Kartun Edukasi", "Hiburan Digital", "Feature"};
    public static final String[] JENIS_KANAL = {"Televisi", "Website", "YouTube", "Instagram", "TikTok"};
    public static final String[] STATUS_JADWAL = {"Terjadwal", "Selesai", "Dibatalkan"};
    public static final String[] STATUS_KERJASAMA = {"Direncanakan", "Berjalan", "Selesai", "Dibatalkan"};
    public static final String[] JENIS_KONTEN = {"Berita", "Artikel", "Video", "Infografik", "Wawancara"};
    public static final String[] STATUS_KONTEN = {"Dalam Proses", "Siap Tayang", "Sudah Tayang", "Diarsipkan"};
    public static final String[] STATUS_PUBLIKASI = {"Direncanakan", "Terbit", "Dibatalkan"};
}
