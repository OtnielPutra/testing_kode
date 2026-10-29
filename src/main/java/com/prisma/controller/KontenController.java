package com.prisma.controller;

import com.prisma.dao.KontenDao;
import com.prisma.dao.LampiranKontenDao;
import com.prisma.dao.ProgramTvDao;
import com.prisma.model.Konten;
import com.prisma.model.LampiranKonten;
import com.prisma.model.ProgramTv;
import com.prisma.util.FileStorage;
import com.prisma.util.Options;
import com.prisma.util.ValidationException;
import com.prisma.util.Validator;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

public class KontenController extends BaseController<Konten, Integer> {

    private final ProgramTvDao programDao = new ProgramTvDao();
    private final LampiranKontenDao lampiranDao = new LampiranKontenDao();

    public KontenController() { super(new KontenDao()); }

    public List<ProgramTv> daftarProgram() { return programDao.findAll(); }

    public LampiranKonten lampiranDari(Konten konten) {
        return konten.getIdKonten() == null ? null : lampiranDao.findByKonten(konten);
    }

    @Override
    protected void validate(Konten k) throws ValidationException {
        k.setJudulKonten(Validator.required(k.getJudulKonten(), "Judul konten", 150));
        k.setJenisKonten(Validator.oneOf(k.getJenisKonten(), "Jenis konten", Options.JENIS_KONTEN));
        k.setKategori(Validator.required(k.getKategori(), "Kategori", 50));
        k.setRingkasanKonten(Validator.required(k.getRingkasanKonten(), "Ringkasan konten", 255));
        Validator.notNull(k.getTanggalPencatatan(), "Tanggal pencatatan");
        k.setStatusKonten(Validator.oneOf(k.getStatusKonten(), "Status konten", Options.STATUS_KONTEN));
        Validator.notNull(k.getPetugas(), "Petugas konten");
    }

    /** Menyimpan konten; bila file dipilih, lampiran dibuat baru atau diganti (flowchart: "Ganti Lampiran"). */
    public Konten saveWithLampiran(Konten konten, File file) throws ValidationException {
        Konten saved = save(konten);
        if (file != null) {
            String lokasi = FileStorage.store(file);
            LampiranKonten l = lampiranDao.findByKonten(saved);
            if (l == null) {
                l = new LampiranKonten();
                l.setKonten(saved);
            }
            l.setNamaFile(file.getName());
            l.setLokasiFile(lokasi);
            l.setTanggalUnggah(LocalDateTime.now());
            l.setPetugas(konten.getPetugas());
            lampiranDao.save(l);
        }
        return saved;
    }
}
