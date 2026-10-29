package com.prisma.dao;

import com.prisma.model.Konten;
import com.prisma.model.LampiranKonten;

import java.util.List;

public class LampiranKontenDao extends AbstractJpaDao<LampiranKonten, Integer> {

    public LampiranKontenDao() { super(LampiranKonten.class, "idLampiran"); }

    @Override protected String joins() { return " join e.konten k"; }

    @Override
    protected String[] searchPaths() {
        return new String[] {"e.namaFile", "e.lokasiFile", "k.judulKonten"};
    }

    /** Lampiran pertama milik sebuah konten, atau null bila belum ada. */
    public LampiranKonten findByKonten(Konten konten) {
        List<LampiranKonten> list = read(em -> em.createQuery(
                "select l from LampiranKonten l where l.konten = :k order by l.idLampiran", LampiranKonten.class)
                .setParameter("k", konten).getResultList());
        return list.isEmpty() ? null : list.get(0);
    }
}
