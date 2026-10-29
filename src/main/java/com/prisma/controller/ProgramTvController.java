package com.prisma.controller;

import com.prisma.dao.ProgramTvDao;
import com.prisma.model.ProgramTv;
import com.prisma.util.Options;
import com.prisma.util.ValidationException;
import com.prisma.util.Validator;

public class ProgramTvController extends BaseController<ProgramTv, Integer> {

    public ProgramTvController() { super(new ProgramTvDao()); }

    @Override
    protected void validate(ProgramTv p) throws ValidationException {
        p.setNamaProgram(Validator.required(p.getNamaProgram(), "Nama program", 100));
        p.setKategoriProgram(Validator.oneOf(p.getKategoriProgram(), "Kategori program", Options.KATEGORI_PROGRAM));
        p.setUrlProgram(Validator.url(p.getUrlProgram(), "URL program", 200));
        p.setStatusProgram(Validator.oneOf(p.getStatusProgram(), "Status program", Options.STATUS_AKTIF));
        Validator.notNull(p.getAdmin(), "Admin pengelola");
    }
}
