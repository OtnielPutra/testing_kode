package com.prisma.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "publikasi_konten")
public class PublikasiKonten extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_publikasi")
    private Integer idPublikasi;

    @Column(name = "tanggal_publikasi", nullable = false)
    private LocalDateTime tanggalPublikasi = LocalDateTime.now();

    @Column(name = "status_publikasi", nullable = false, length = 20)
    private String statusPublikasi = "Direncanakan";

    @Column(name = "link_publikasi", length = 255)
    private String linkPublikasi;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "konten_id_konten", nullable = false)
    private Konten konten;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "kanal_id_kanal", nullable = false)
    private Kanal kanal;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "petugas_konten_id_pengguna", nullable = false)
    private PetugasKonten petugas;

    @Override public Object getId() { return idPublikasi; }
    public Integer getIdPublikasi() { return idPublikasi; }
    public LocalDateTime getTanggalPublikasi() { return tanggalPublikasi; }
    public void setTanggalPublikasi(LocalDateTime v) { this.tanggalPublikasi = v; }
    public String getStatusPublikasi() { return statusPublikasi; }
    public void setStatusPublikasi(String v) { this.statusPublikasi = v; }
    public String getLinkPublikasi() { return linkPublikasi; }
    public void setLinkPublikasi(String v) { this.linkPublikasi = v; }
    public Konten getKonten() { return konten; }
    public void setKonten(Konten v) { this.konten = v; }
    public Kanal getKanal() { return kanal; }
    public void setKanal(Kanal v) { this.kanal = v; }
    public PetugasKonten getPetugas() { return petugas; }
    public void setPetugas(PetugasKonten v) { this.petugas = v; }
}
