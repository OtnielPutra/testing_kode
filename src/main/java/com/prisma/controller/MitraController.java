package com.prisma.controller;

import com.prisma.dao.MitraDao;
import com.prisma.model.Mitra;
import com.prisma.util.Options;
import com.prisma.util.ValidationException;
import com.prisma.util.Validator;

public class MitraController extends BaseController<Mitra, Integer> {

    public MitraController() { super(new MitraDao()); }

    @Override
    protected void validate(Mitra m) throws ValidationException {
        m.setNamaMitra(Validator.required(m.getNamaMitra(), "Nama mitra", 100));
        m.setJenisMitra(Validator.required(m.getJenisMitra(), "Jenis mitra", 50));
        m.setNamaKontak(Validator.required(m.getNamaKontak(), "Nama kontak", 100));
        m.setEmail(Validator.email(m.getEmail(), "Email", 100));
        m.setNomorTelepon(Validator.phone(m.getNomorTelepon(), "Nomor telepon", 25));
        m.setAlamat(Validator.optional(m.getAlamat(), "Alamat", 200));
        m.setStatusMitra(Validator.oneOf(m.getStatusMitra(), "Status mitra", Options.STATUS_AKTIF));
    }
}
