package com.prisma.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "kerja_sama")
public class KerjaSama extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_kerja_sama")
    private Integer idKerjaSama;

    @Column(name = "judul_kerja_sama", nullable = false, length = 150)
    private String judulKerjaSama;

    @Column(name = "jenis_kerja_sama", nullable = false, length = 100)
    private String jenisKerjaSama;

    @Column(name = "tanggal_mulai", nullable = false)
    private LocalDate tanggalMulai = LocalDate.now();

    @Column(name = "tanggal_akhir")
    private LocalDate tanggalAkhir;

    @Column(name = "status_kerja_sama", nullable = false, length = 20)
    private String statusKerjaSama = "Direncanakan";

    @Column(name = "nilai_kontrak", precision = 15, scale = 2)
    private BigDecimal nilaiKontrak;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "admin_id_pengguna", nullable = false)
    private Admin admin;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "program_tv_id_program")
    private ProgramTv programTv;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "konten_id_konten")
    private Konten konten;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "mitra_id_mitra", nullable = false)
    private Mitra mitra;

    @Override public Object getId() { return idKerjaSama; }
    public Integer getIdKerjaSama() { return idKerjaSama; }
    public String getJudulKerjaSama() { return judulKerjaSama; }
    public void setJudulKerjaSama(String v) { this.judulKerjaSama = v; }
    public String getJenisKerjaSama() { return jenisKerjaSama; }
    public void setJenisKerjaSama(String v) { this.jenisKerjaSama = v; }
    public LocalDate getTanggalMulai() { return tanggalMulai; }
    public void setTanggalMulai(LocalDate v) { this.tanggalMulai = v; }
    public LocalDate getTanggalAkhir() { return tanggalAkhir; }
    public void setTanggalAkhir(LocalDate v) { this.tanggalAkhir = v; }
    public String getStatusKerjaSama() { return statusKerjaSama; }
    public void setStatusKerjaSama(String v) { this.statusKerjaSama = v; }
    public BigDecimal getNilaiKontrak() { return nilaiKontrak; }
    public void setNilaiKontrak(BigDecimal v) { this.nilaiKontrak = v; }
    public Admin getAdmin() { return admin; }
    public void setAdmin(Admin v) { this.admin = v; }
    public ProgramTv getProgramTv() { return programTv; }
    public void setProgramTv(ProgramTv v) { this.programTv = v; }
    public Konten getKonten() { return konten; }
    public void setKonten(Konten v) { this.konten = v; }
    public Mitra getMitra() { return mitra; }
    public void setMitra(Mitra v) { this.mitra = v; }

    @Override public String toString() { return judulKerjaSama; }
}
