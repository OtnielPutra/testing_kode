package com.prisma.dao;

import com.prisma.model.PublikasiKonten;

public class PublikasiKontenDao extends AbstractJpaDao<PublikasiKonten, Integer> {

    public PublikasiKontenDao() { super(PublikasiKonten.class, "idPublikasi"); }

    @Override protected String joins() { return " join e.konten k join e.kanal c"; }

    @Override
    protected String[] searchPaths() {
        return new String[] {"k.judulKonten", "c.namaKanal", "e.statusPublikasi", "e.linkPublikasi"};
    }
}
