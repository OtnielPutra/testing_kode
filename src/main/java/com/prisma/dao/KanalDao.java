package com.prisma.dao;

import com.prisma.model.Kanal;

public class KanalDao extends AbstractJpaDao<Kanal, Integer> {

    public KanalDao() { super(Kanal.class, "idKanal"); }

    @Override
    protected String[] searchPaths() {
        return new String[] {"e.namaKanal", "e.jenisKanal", "e.platform", "e.statusKanal"};
    }
}
