package com.prisma.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "lampiran_konten")
public class LampiranKonten extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lampiran")
    private Integer idLampiran;

    @Column(name = "nama_file", nullable = false, length = 255)
    private String namaFile;

    @Column(name = "lokasi_file", nullable = false, length = 255)
    private String lokasiFile;

    @Column(name = "tanggal_unggah", nullable = false)
    private LocalDateTime tanggalUnggah = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "konten_id_konten", nullable = false)
    private Konten konten;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "petugas_konten_id_pengguna", nullable = false)
    private PetugasKonten petugas;

    @Override public Object getId() { return idLampiran; }
    public Integer getIdLampiran() { return idLampiran; }
    public String getNamaFile() { return namaFile; }
    public void setNamaFile(String v) { this.namaFile = v; }
    public String getLokasiFile() { return lokasiFile; }
    public void setLokasiFile(String v) { this.lokasiFile = v; }
    public LocalDateTime getTanggalUnggah() { return tanggalUnggah; }
    public void setTanggalUnggah(LocalDateTime v) { this.tanggalUnggah = v; }
    public Konten getKonten() { return konten; }
    public void setKonten(Konten v) { this.konten = v; }
    public PetugasKonten getPetugas() { return petugas; }
    public void setPetugas(PetugasKonten v) { this.petugas = v; }

    @Override public String toString() { return namaFile; }
}
