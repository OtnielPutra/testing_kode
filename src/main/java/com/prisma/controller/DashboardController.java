package com.prisma.controller;

import com.prisma.dao.JadwalTayangDao;
import com.prisma.dao.KerjaSamaDao;
import com.prisma.dao.KontenDao;
import com.prisma.dao.ProgramTvDao;
import com.prisma.model.JadwalTayang;

import java.util.List;

/** Menyediakan data ringkasan untuk halaman Dashboard dan Monitor Data. */
public class DashboardController {

    public static final class Stats {
        public final long konten, program, jadwal, kerjaSama;
        public Stats(long konten, long program, long jadwal, long kerjaSama) {
            this.konten = konten; this.program = program; this.jadwal = jadwal; this.kerjaSama = kerjaSama;
        }
    }

    private final KontenDao kontenDao = new KontenDao();
    private final ProgramTvDao programDao = new ProgramTvDao();
    private final JadwalTayangDao jadwalDao = new JadwalTayangDao();
    private final KerjaSamaDao kerjaSamaDao = new KerjaSamaDao();

    public Stats ringkasan() {
        return new Stats(kontenDao.count(), programDao.count(), jadwalDao.count(), kerjaSamaDao.count());
    }

    public List<JadwalTayang> jadwalTerbaru(int limit) { return jadwalDao.findTerbaru(limit); }

    public List<Object[]> kerjaSamaPerMitra(int limit) { return kerjaSamaDao.countPerMitra(limit); }
}
