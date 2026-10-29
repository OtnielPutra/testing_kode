package com.prisma.dao;

import com.prisma.model.JadwalTayang;
import com.prisma.model.Kanal;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class JadwalTayangDao extends AbstractJpaDao<JadwalTayang, Integer> {

    public JadwalTayangDao() { super(JadwalTayang.class, "idJadwal"); }

    @Override protected String joins() { return " join e.programTv p join e.kanal c"; }
    @Override protected String orderBy() { return "e.tanggalTayang, e.waktuMulai"; }

    @Override
    protected String[] searchPaths() {
        return new String[] {"p.namaProgram", "c.namaKanal", "e.statusJadwal"};
    }

    /** Pencarian sesuai flowchart: tanggal, waktu mulai, waktu selesai (boleh kosong 1 hingga 2). */
    public List<JadwalTayang> searchByWaktu(LocalDate tanggal, LocalTime dari, LocalTime sampai) {
        StringBuilder q = new StringBuilder("select e from JadwalTayang e where 1=1");
        if (tanggal != null) q.append(" and e.tanggalTayang = :tgl");
        if (dari != null) q.append(" and e.waktuMulai >= :dari");
        if (sampai != null) q.append(" and e.waktuSelesai <= :sampai");
        q.append(" order by ").append(orderBy());
        return read(em -> {
            var query = em.createQuery(q.toString(), JadwalTayang.class);
            if (tanggal != null) query.setParameter("tgl", tanggal);
            if (dari != null) query.setParameter("dari", dari);
            if (sampai != null) query.setParameter("sampai", sampai);
            return query.getResultList();
        });
    }

    /** Jumlah jadwal aktif pada kanal & tanggal yang sama yang waktunya bertumpang tindih. */
    public long countOverlap(LocalDate tanggal, LocalTime mulai, LocalTime selesai, Kanal kanal, Integer excludeId) {
        String q = "select count(e) from JadwalTayang e where e.tanggalTayang = :tgl and e.kanal = :kanal "
                + "and e.statusJadwal <> 'Dibatalkan' and e.waktuMulai < :selesai and e.waktuSelesai > :mulai"
                + (excludeId != null ? " and e.idJadwal <> :ex" : "");
        return read(em -> {
            var query = em.createQuery(q, Long.class)
                    .setParameter("tgl", tanggal).setParameter("kanal", kanal)
                    .setParameter("mulai", mulai).setParameter("selesai", selesai);
            if (excludeId != null) query.setParameter("ex", excludeId);
            return query.getSingleResult();
        });
    }

    public List<JadwalTayang> findTerbaru(int limit) {
        return read(em -> em.createQuery(
                "select e from JadwalTayang e where e.statusJadwal <> 'Dibatalkan' order by " + orderBy(),
                JadwalTayang.class).setMaxResults(limit).getResultList());
    }
}
