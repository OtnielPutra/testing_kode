package com.prisma.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "admin")
public class Admin extends Pengguna {
    private static final long serialVersionUID = 1L;

    @Column(name = "tanggal_penugasan", nullable = false)
    private LocalDate tanggalPenugasan = LocalDate.now();

    @Override public String getRole() { return "Admin"; }
    @Override public String getDetailLabel() { return "-"; }
    @Override public String getDetail() { return "-"; }
    @Override public void setDetail(String detail) { /* admin tidak punya detail tambahan */ }
    @Override public LocalDate getTanggalPenugasan() { return tanggalPenugasan; }
    @Override public void setTanggalPenugasan(LocalDate t) { this.tanggalPenugasan = t; }

    @Override
    public List<Menu> getMenus() {
        return List.of(Menu.DASHBOARD, Menu.PROGRAM, Menu.MONITOR,
                Menu.KANAL, Menu.MITRA, Menu.KERJASAMA, Menu.AKUN);
    }
}
