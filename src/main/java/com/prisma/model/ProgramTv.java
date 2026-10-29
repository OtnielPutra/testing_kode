package com.prisma.model;

import jakarta.persistence.*;

@Entity
@Table(name = "program_tv")
public class ProgramTv extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_program")
    private Integer idProgram;

    @Column(name = "nama_program", nullable = false, length = 100)
    private String namaProgram;

    @Column(name = "kategori_program", nullable = false, length = 30)
    private String kategoriProgram;

    @Column(name = "url_program", length = 200)
    private String urlProgram;

    @Column(name = "status_program", nullable = false, length = 10)
    private String statusProgram = "aktif";

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "admin_id_pengguna", nullable = false)
    private Admin admin;

    @Override public Object getId() { return idProgram; }
    public Integer getIdProgram() { return idProgram; }
    public String getNamaProgram() { return namaProgram; }
    public void setNamaProgram(String v) { this.namaProgram = v; }
    public String getKategoriProgram() { return kategoriProgram; }
    public void setKategoriProgram(String v) { this.kategoriProgram = v; }
    public String getUrlProgram() { return urlProgram; }
    public void setUrlProgram(String v) { this.urlProgram = v; }
    public String getStatusProgram() { return statusProgram; }
    public void setStatusProgram(String v) { this.statusProgram = v; }
    public Admin getAdmin() { return admin; }
    public void setAdmin(Admin admin) { this.admin = admin; }

    @Override public String toString() { return namaProgram; }
}
