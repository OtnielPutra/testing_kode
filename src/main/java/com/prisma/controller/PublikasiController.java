package com.prisma.controller;

import com.prisma.dao.KanalDao;
import com.prisma.dao.KontenDao;
import com.prisma.dao.PublikasiKontenDao;
import com.prisma.model.Kanal;
import com.prisma.model.Konten;
import com.prisma.model.PublikasiKonten;
import com.prisma.util.Options;
import com.prisma.util.ValidationException;
import com.prisma.util.Validator;

import java.util.List;

public class PublikasiController extends BaseController<PublikasiKonten, Integer> {

    private final KontenDao kontenDao = new KontenDao();
    private final KanalDao kanalDao = new KanalDao();

    public PublikasiController() { super(new PublikasiKontenDao()); }

    public List<Konten> daftarKonten() { return kontenDao.findAll(); }
    public List<Kanal> daftarKanal() { return kanalDao.findAll(); }

    @Override
    protected void validate(PublikasiKonten p) throws ValidationException {
        Validator.notNull(p.getKonten(), "Konten");
        Validator.notNull(p.getKanal(), "Kanal publikasi");
        Validator.notNull(p.getTanggalPublikasi(), "Tanggal publikasi");
        p.setStatusPublikasi(Validator.oneOf(p.getStatusPublikasi(), "Status publikasi", Options.STATUS_PUBLIKASI));
        p.setLinkPublikasi(Validator.url(p.getLinkPublikasi(), "Link publikasi", 255));
        if ("Terbit".equals(p.getStatusPublikasi()) && p.getLinkPublikasi() == null) {
            throw new ValidationException("Link publikasi wajib diisi untuk status Terbit.");
        }
        Validator.notNull(p.getPetugas(), "Petugas konten");
    }
}
