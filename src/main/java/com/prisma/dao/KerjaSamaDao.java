package com.prisma.dao;

import com.prisma.model.KerjaSama;

import java.util.List;

public class KerjaSamaDao extends AbstractJpaDao<KerjaSama, Integer> {

    public KerjaSamaDao() { super(KerjaSama.class, "idKerjaSama"); }

    @Override protected String joins() { return " join e.mitra m"; }

    @Override
    protected String[] searchPaths() {
        return new String[] {"e.judulKerjaSama", "e.jenisKerjaSama", "e.statusKerjaSama", "m.namaMitra"};
    }

    /** Relasi Mitra (1) - Kerja Sama (N): [nama mitra, jumlah kerja sama]. */
    public List<Object[]> countPerMitra(int limit) {
        return read(em -> em.createQuery(
                "select m.namaMitra, count(k) from KerjaSama k join k.mitra m "
                        + "group by m.namaMitra order by count(k) desc, m.namaMitra", Object[].class)
                .setMaxResults(limit).getResultList());
    }
}
