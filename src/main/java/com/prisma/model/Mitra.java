package com.prisma.model;

import jakarta.persistence.*;

@Entity
@Table(name = "mitra")
public class Mitra extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mitra")
    private Integer idMitra;

    @Column(name = "nama_mitra", nullable = false, unique = true, length = 100)
    private String namaMitra;

    @Column(name = "jenis_mitra", nullable = false, length = 50)
    private String jenisMitra;

    @Column(name = "nama_kontak", nullable = false, length = 100)
    private String namaKontak;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "nomor_telepon", length = 25)
    private String nomorTelepon;

    @Column(name = "alamat", length = 200)
    private String alamat;

    @Column(name = "status_mitra", nullable = false, length = 10)
    private String statusMitra = "aktif";

    @Override public Object getId() { return idMitra; }
    public Integer getIdMitra() { return idMitra; }
    public String getNamaMitra() { return namaMitra; }
    public void setNamaMitra(String v) { this.namaMitra = v; }
    public String getJenisMitra() { return jenisMitra; }
    public void setJenisMitra(String v) { this.jenisMitra = v; }
    public String getNamaKontak() { return namaKontak; }
    public void setNamaKontak(String v) { this.namaKontak = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }
    public String getNomorTelepon() { return nomorTelepon; }
    public void setNomorTelepon(String v) { this.nomorTelepon = v; }
    public String getAlamat() { return alamat; }
    public void setAlamat(String v) { this.alamat = v; }
    public String getStatusMitra() { return statusMitra; }
    public void setStatusMitra(String v) { this.statusMitra = v; }

    @Override public String toString() { return namaMitra; }
}
