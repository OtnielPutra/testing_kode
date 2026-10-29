package com.prisma.dao;

import com.prisma.model.Konten;

public class KontenDao extends AbstractJpaDao<Konten, Integer> {

    public KontenDao() { super(Konten.class, "idKonten"); }

    @Override protected String joins() { return " left join e.programTv p"; }

    @Override
    protected String[] searchPaths() {
        return new String[] {"e.judulKonten", "e.jenisKonten", "e.kategori", "e.statusKonten", "p.namaProgram"};
    }
}
