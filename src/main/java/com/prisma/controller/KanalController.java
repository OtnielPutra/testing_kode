package com.prisma.controller;

import com.prisma.dao.KanalDao;
import com.prisma.model.Kanal;
import com.prisma.util.Options;
import com.prisma.util.ValidationException;
import com.prisma.util.Validator;

public class KanalController extends BaseController<Kanal, Integer> {

    public KanalController() { super(new KanalDao()); }

    @Override
    protected void validate(Kanal k) throws ValidationException {
        k.setNamaKanal(Validator.required(k.getNamaKanal(), "Nama kanal", 100));
        k.setJenisKanal(Validator.oneOf(k.getJenisKanal(), "Jenis kanal", Options.JENIS_KANAL));
        k.setPlatform(Validator.required(k.getPlatform(), "Platform", 50));
        k.setStatusKanal(Validator.oneOf(k.getStatusKanal(), "Status kanal", Options.STATUS_AKTIF));
    }
}
