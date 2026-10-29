package com.prisma.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "jadwal_tayang")
public class JadwalTayang extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_jadwal")
    private Integer idJadwal;

    @Column(name = "tanggal_tayang", nullable = false)
    private LocalDate tanggalTayang = LocalDate.now();

    @Column(name = "waktu_mulai", nullable = false)
    private LocalTime waktuMulai;

    @Column(name = "waktu_selesai", nullable = false)
    private LocalTime waktuSelesai;

    @Column(name = "status_jadwal", nullable = false, length = 20)
    private String statusJadwal = "Terjadwal";

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "petugas_jadwal_id_pengguna", nullable = false)
    private PetugasJadwal petugas;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "program_tv_id_program", nullable = false)
    private ProgramTv programTv;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "kanal_id_kanal", nullable = false)
    private Kanal kanal;

    @Override public Object getId() { return idJadwal; }
    public Integer getIdJadwal() { return idJadwal; }
    public LocalDate getTanggalTayang() { return tanggalTayang; }
    public void setTanggalTayang(LocalDate v) { this.tanggalTayang = v; }
    public LocalTime getWaktuMulai() { return waktuMulai; }
    public void setWaktuMulai(LocalTime v) { this.waktuMulai = v; }
    public LocalTime getWaktuSelesai() { return waktuSelesai; }
    public void setWaktuSelesai(LocalTime v) { this.waktuSelesai = v; }
    public String getStatusJadwal() { return statusJadwal; }
    public void setStatusJadwal(String v) { this.statusJadwal = v; }
    public PetugasJadwal getPetugas() { return petugas; }
    public void setPetugas(PetugasJadwal v) { this.petugas = v; }
    public ProgramTv getProgramTv() { return programTv; }
    public void setProgramTv(ProgramTv v) { this.programTv = v; }
    public Kanal getKanal() { return kanal; }
    public void setKanal(Kanal v) { this.kanal = v; }
}
