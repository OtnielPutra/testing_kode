package com.prisma.model;

/** Menu aplikasi. Menu mana yang tampil ditentukan oleh peran pengguna (lihat Pengguna#getMenus). */
public enum Menu {
    DASHBOARD("Dashboard", false),
    KONTEN("Manajemen Konten", false),
    PROGRAM("Program TV", false),
    JADWAL("Jadwal Tayang", false),
    PUBLIKASI("Publikasi Konten", false),
    LAMPIRAN("Lampiran Konten", false),
    MONITOR("Monitor Data", false),
    KANAL("Data Kanal", true),
    MITRA("Data Mitra", true),
    KERJASAMA("Kerja Sama", true),
    AKUN("Akun Pengguna", true);

    private final String label;
    private final boolean pengaturanSistem;

    Menu(String label, boolean pengaturanSistem) {
        this.label = label;
        this.pengaturanSistem = pengaturanSistem;
    }

    public String getLabel() { return label; }
    public boolean isPengaturanSistem() { return pengaturanSistem; }
}
