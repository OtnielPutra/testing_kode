package com.prisma.dao;

import com.prisma.model.Mitra;

public class MitraDao extends AbstractJpaDao<Mitra, Integer> {

    public MitraDao() { super(Mitra.class, "idMitra"); }

    @Override
    protected String[] searchPaths() {
        return new String[] {"e.namaMitra", "e.jenisMitra", "e.namaKontak", "e.email", "e.nomorTelepon", "e.alamat", "e.statusMitra"};
    }
}
