package com.prisma.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "petugas_konten")
public class PetugasKonten extends Pengguna {
    private static final long serialVersionUID = 1L;

    @Column(name = "spesialisasi_konten", nullable = false, length = 100)
    private String spesialisasiKonten;

    @Column(name = "tanggal_penugasan", nullable = false)
    private LocalDate tanggalPenugasan = LocalDate.now();

    @Override public String getRole() { return "Petugas Konten"; }
    @Override public String getDetailLabel() { return "Spesialisasi Konten"; }
    @Override public String getDetail() { return spesialisasiKonten; }
    @Override public void setDetail(String detail) { this.spesialisasiKonten = detail; }
    @Override public LocalDate getTanggalPenugasan() { return tanggalPenugasan; }
    @Override public void setTanggalPenugasan(LocalDate t) { this.tanggalPenugasan = t; }

    @Override
    public List<Menu> getMenus() {
        return List.of(Menu.DASHBOARD, Menu.KONTEN, Menu.PUBLIKASI, Menu.LAMPIRAN, Menu.PROGRAM, Menu.MONITOR);
    }
}
