package com.prisma.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "petugas_jadwal")
public class PetugasJadwal extends Pengguna {
    private static final long serialVersionUID = 1L;

    @Column(name = "cakupan_jadwal", nullable = false, length = 100)
    private String cakupanJadwal;

    @Column(name = "tanggal_penugasan", nullable = false)
    private LocalDate tanggalPenugasan = LocalDate.now();

    @Override public String getRole() { return "Petugas Jadwal"; }
    @Override public String getDetailLabel() { return "Cakupan Jadwal"; }
    @Override public String getDetail() { return cakupanJadwal; }
    @Override public void setDetail(String detail) { this.cakupanJadwal = detail; }
    @Override public LocalDate getTanggalPenugasan() { return tanggalPenugasan; }
    @Override public void setTanggalPenugasan(LocalDate t) { this.tanggalPenugasan = t; }

    @Override
    public List<Menu> getMenus() {
        return List.of(Menu.DASHBOARD, Menu.JADWAL, Menu.PROGRAM, Menu.MONITOR);
    }
}
