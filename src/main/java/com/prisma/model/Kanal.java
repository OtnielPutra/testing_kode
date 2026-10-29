package com.prisma.model;

import jakarta.persistence.*;

@Entity
@Table(name = "kanal")
public class Kanal extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_kanal")
    private Integer idKanal;

    @Column(name = "nama_kanal", nullable = false, unique = true, length = 100)
    private String namaKanal;

    @Column(name = "jenis_kanal", nullable = false, length = 20)
    private String jenisKanal;

    @Column(name = "platform", nullable = false, length = 50)
    private String platform;

    @Column(name = "status_kanal", nullable = false, length = 10)
    private String statusKanal = "aktif";

    @Override public Object getId() { return idKanal; }
    public Integer getIdKanal() { return idKanal; }
    public String getNamaKanal() { return namaKanal; }
    public void setNamaKanal(String v) { this.namaKanal = v; }
    public String getJenisKanal() { return jenisKanal; }
    public void setJenisKanal(String v) { this.jenisKanal = v; }
    public String getPlatform() { return platform; }
    public void setPlatform(String v) { this.platform = v; }
    public String getStatusKanal() { return statusKanal; }
    public void setStatusKanal(String v) { this.statusKanal = v; }

    @Override public String toString() { return namaKanal; }
}
