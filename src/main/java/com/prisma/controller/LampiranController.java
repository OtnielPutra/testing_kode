package com.prisma.controller;

import com.prisma.dao.KontenDao;
import com.prisma.dao.LampiranKontenDao;
import com.prisma.model.Konten;
import com.prisma.model.LampiranKonten;
import com.prisma.util.FileStorage;
import com.prisma.util.ValidationException;
import com.prisma.util.Validator;

import java.io.File;
import java.util.List;

public class LampiranController extends BaseController<LampiranKonten, Integer> {

    private final KontenDao kontenDao = new KontenDao();

    public LampiranController() { super(new LampiranKontenDao()); }

    public List<Konten> daftarKonten() { return kontenDao.findAll(); }

    /** Menyalin file ke folder uploads dan mengembalikan lokasi relatifnya. */
    public String simpanFile(File file) throws ValidationException { return FileStorage.store(file); }

    @Override
    protected void validate(LampiranKonten l) throws ValidationException {
        Validator.notNull(l.getKonten(), "Konten");
        l.setNamaFile(Validator.required(l.getNamaFile(), "Nama file", 255));
        l.setLokasiFile(Validator.required(l.getLokasiFile(), "Lokasi file", 255));
        Validator.notNull(l.getTanggalUnggah(), "Tanggal unggah");
        Validator.notNull(l.getPetugas(), "Petugas konten");
    }
}
