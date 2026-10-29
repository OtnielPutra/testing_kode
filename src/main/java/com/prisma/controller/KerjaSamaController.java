package com.prisma.controller;

import com.prisma.dao.KerjaSamaDao;
import com.prisma.dao.KontenDao;
import com.prisma.dao.MitraDao;
import com.prisma.dao.ProgramTvDao;
import com.prisma.model.KerjaSama;
import com.prisma.model.Konten;
import com.prisma.model.Mitra;
import com.prisma.model.ProgramTv;
import com.prisma.util.Options;
import com.prisma.util.ValidationException;
import com.prisma.util.Validator;

import java.util.List;

public class KerjaSamaController extends BaseController<KerjaSama, Integer> {

    private final ProgramTvDao programDao = new ProgramTvDao();
    private final KontenDao kontenDao = new KontenDao();
    private final MitraDao mitraDao = new MitraDao();

    public KerjaSamaController() { super(new KerjaSamaDao()); }

    public List<ProgramTv> daftarProgram() { return programDao.findAll(); }
    public List<Konten> daftarKonten() { return kontenDao.findAll(); }
    public List<Mitra> daftarMitra() { return mitraDao.findAll(); }

    @Override
    protected void validate(KerjaSama k) throws ValidationException {
        k.setJudulKerjaSama(Validator.required(k.getJudulKerjaSama(), "Judul kerja sama", 150));
        k.setJenisKerjaSama(Validator.required(k.getJenisKerjaSama(), "Jenis kerja sama", 100));
        Validator.notNull(k.getTanggalMulai(), "Tanggal mulai");
        if (k.getTanggalAkhir() != null && k.getTanggalAkhir().isBefore(k.getTanggalMulai())) {
            throw new ValidationException("Tanggal akhir tidak boleh sebelum tanggal mulai.");
        }
        k.setStatusKerjaSama(Validator.oneOf(k.getStatusKerjaSama(), "Status kerja sama", Options.STATUS_KERJASAMA));
        Validator.notNull(k.getMitra(), "Mitra");
        Validator.notNull(k.getAdmin(), "Admin pengelola");
    }
}
