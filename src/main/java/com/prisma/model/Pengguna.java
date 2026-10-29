package com.prisma.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.List;

/**
 * ABSTRACTION + INHERITANCE: tabel `pengguna` adalah induk dari tabel admin,
 * petugas_jadwal, dan petugas_konten (strategi JOINED, PK yang sama = id_pengguna).
 * Subclass mengimplementasikan method abstract (POLYMORPHISM).
 */
@Entity
@Table(name = "pengguna")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Pengguna extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pengguna")
    private Integer idPengguna;

    @Column(name = "nama_lengkap", nullable = false, length = 100)
    private String namaLengkap;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password", nullable = false, length = 30)
    private String password;

    @Column(name = "status_akun", nullable = false, length = 10)
    private String statusAkun = "aktif";

    // ---- perilaku yang berbeda tiap peran (polymorphism) ----
    public abstract String getRole();
    public abstract String getDetailLabel();
    public abstract String getDetail();
    public abstract void setDetail(String detail);
    public abstract LocalDate getTanggalPenugasan();
    public abstract void setTanggalPenugasan(LocalDate tanggal);
    public abstract List<Menu> getMenus();

    public boolean isAktif() { return "aktif".equals(statusAkun); }

    @Override public Object getId() { return idPengguna; }
    public Integer getIdPengguna() { return idPengguna; }
    public void setIdPengguna(Integer idPengguna) { this.idPengguna = idPengguna; }
    public String getNamaLengkap() { return namaLengkap; }
    public void setNamaLengkap(String namaLengkap) { this.namaLengkap = namaLengkap; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getStatusAkun() { return statusAkun; }
    public void setStatusAkun(String statusAkun) { this.statusAkun = statusAkun; }

    @Override public String toString() { return namaLengkap; }
}
