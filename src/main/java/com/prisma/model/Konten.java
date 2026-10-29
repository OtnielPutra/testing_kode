package com.prisma.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "konten")
public class Konten extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_konten")
    private Integer idKonten;

    @Column(name = "judul_konten", nullable = false, length = 150)
    private String judulKonten;

    @Column(name = "jenis_konten", nullable = false, length = 20)
    private String jenisKonten;

    @Column(name = "kategori", nullable = false, length = 50)
    private String kategori;

    @Column(name = "ringkasan_konten", nullable = false, length = 255)
    private String ringkasanKonten;

    @Column(name = "tanggal_pencatatan", nullable = false)
    private LocalDate tanggalPencatatan = LocalDate.now();

    @Column(name = "status_konten", nullable = false, length = 20)
    private String statusKonten = "Dalam Proses";

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "program_tv_id_program")
    private ProgramTv programTv;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "petugas_konten_id_pengguna", nullable = false)
    private PetugasKonten petugas;

    @Override public Object getId() { return idKonten; }
    public Integer getIdKonten() { return idKonten; }
    public String getJudulKonten() { return judulKonten; }
    public void setJudulKonten(String v) { this.judulKonten = v; }
    public String getJenisKonten() { return jenisKonten; }
    public void setJenisKonten(String v) { this.jenisKonten = v; }
    public String getKategori() { return kategori; }
    public void setKategori(String v) { this.kategori = v; }
    public String getRingkasanKonten() { return ringkasanKonten; }
    public void setRingkasanKonten(String v) { this.ringkasanKonten = v; }
    public LocalDate getTanggalPencatatan() { return tanggalPencatatan; }
    public void setTanggalPencatatan(LocalDate v) { this.tanggalPencatatan = v; }
    public String getStatusKonten() { return statusKonten; }
    public void setStatusKonten(String v) { this.statusKonten = v; }
    public ProgramTv getProgramTv() { return programTv; }
    public void setProgramTv(ProgramTv v) { this.programTv = v; }
    public PetugasKonten getPetugas() { return petugas; }
    public void setPetugas(PetugasKonten v) { this.petugas = v; }

    @Override public String toString() { return judulKonten; }
}
