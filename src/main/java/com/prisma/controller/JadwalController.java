package com.prisma.controller;

import com.prisma.dao.JadwalTayangDao;
import com.prisma.dao.KanalDao;
import com.prisma.dao.ProgramTvDao;
import com.prisma.model.JadwalTayang;
import com.prisma.model.Kanal;
import com.prisma.model.ProgramTv;
import com.prisma.util.Options;
import com.prisma.util.ValidationException;
import com.prisma.util.Validator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class JadwalController extends BaseController<JadwalTayang, Integer> {

    private final JadwalTayangDao jadwalDao;
    private final ProgramTvDao programDao = new ProgramTvDao();
    private final KanalDao kanalDao = new KanalDao();

    public JadwalController() { this(new JadwalTayangDao()); }

    private JadwalController(JadwalTayangDao dao) {
        super(dao);
        this.jadwalDao = dao;
    }

    public List<ProgramTv> daftarProgram() { return programDao.findAll(); }
    public List<Kanal> daftarKanal() { return kanalDao.findAll(); }

    public List<JadwalTayang> cari(LocalDate tanggal, LocalTime dari, LocalTime sampai) {
        return jadwalDao.searchByWaktu(tanggal, dari, sampai);
    }

    /** Menu "Nonaktifkan Jadwal": status jadwal diubah menjadi Dibatalkan. */
    public void nonaktifkan(JadwalTayang j) throws ValidationException {
        j.setStatusJadwal("Dibatalkan");
        save(j);
    }

    @Override
    protected void validate(JadwalTayang j) throws ValidationException {
        Validator.notNull(j.getProgramTv(), "Program TV");
        Validator.notNull(j.getKanal(), "Kanal");
        Validator.notNull(j.getTanggalTayang(), "Tanggal tayang");
        Validator.notNull(j.getWaktuMulai(), "Waktu mulai");
        Validator.notNull(j.getWaktuSelesai(), "Waktu selesai");
        if (!j.getWaktuSelesai().isAfter(j.getWaktuMulai())) {
            throw new ValidationException("Waktu selesai harus setelah waktu mulai.");
        }
        j.setStatusJadwal(Validator.oneOf(j.getStatusJadwal(), "Status jadwal", Options.STATUS_JADWAL));
        Validator.notNull(j.getPetugas(), "Petugas jadwal");

        if (!"Dibatalkan".equals(j.getStatusJadwal())) {
            long bentrok = jadwalDao.countOverlap(j.getTanggalTayang(), j.getWaktuMulai(), j.getWaktuSelesai(),
                    j.getKanal(), j.getIdJadwal());
            if (bentrok > 0) {
                throw new ValidationException("Jadwal bertabrakan dengan " + bentrok
                        + " jadwal lain pada kanal dan tanggal yang sama.");
            }
        }
    }
}
