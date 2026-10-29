package com.prisma.dao;

import com.prisma.model.Pengguna;
import com.prisma.util.Options;

import java.time.LocalDate;
import java.util.List;

public class PenggunaDao extends AbstractJpaDao<Pengguna, Integer> {

    public PenggunaDao() { super(Pengguna.class, "idPengguna"); }

    @Override
    protected String[] searchPaths() {
        return new String[] {"e.namaLengkap", "e.username", "e.statusAkun"};
    }

    /** Mencari akun berdasarkan username (hasilnya sudah berupa subclass Admin/PetugasJadwal/PetugasKonten). */
    public Pengguna findByUsername(String username) {
        List<Pengguna> list = read(em -> em.createQuery(
                "select p from Pengguna p where p.username = :u", Pengguna.class)
                .setParameter("u", username).getResultList());
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * Mengubah data dasar sekaligus peran akun dalam SATU transaksi.
     * Peran = baris pada tabel admin / petugas_jadwal / petugas_konten, jadi pindah peran
     * berarti memindahkan baris antar tabel (JPA tidak bisa mengubah tipe entity).
     */
    public void updateWithRoleChange(Integer id, String nama, String username, String password, String status,
                                     String oldRole, String newRole, String detail, LocalDate tanggal) {
        write(em -> {
            em.createNativeQuery("update pengguna set nama_lengkap = :n, username = :u, password = :p, "
                    + "status_akun = :s where id_pengguna = :id")
                    .setParameter("n", nama).setParameter("u", username).setParameter("p", password)
                    .setParameter("s", status).setParameter("id", id).executeUpdate();

            em.createNativeQuery("delete from " + tableOf(oldRole) + " where id_pengguna = :id")
                    .setParameter("id", id).executeUpdate();

            if (Options.ROLE_ADMIN.equals(newRole)) {
                em.createNativeQuery("insert into admin (id_pengguna, tanggal_penugasan) values (:id, :t)")
                        .setParameter("id", id).setParameter("t", tanggal).executeUpdate();
            } else if (Options.ROLE_JADWAL.equals(newRole)) {
                em.createNativeQuery("insert into petugas_jadwal (id_pengguna, cakupan_jadwal, tanggal_penugasan) "
                        + "values (:id, :d, :t)")
                        .setParameter("id", id).setParameter("d", detail).setParameter("t", tanggal).executeUpdate();
            } else {
                em.createNativeQuery("insert into petugas_konten (id_pengguna, spesialisasi_konten, tanggal_penugasan) "
                        + "values (:id, :d, :t)")
                        .setParameter("id", id).setParameter("d", detail).setParameter("t", tanggal).executeUpdate();
            }
            return null;
        });
    }

    private static String tableOf(String role) {
        if (Options.ROLE_ADMIN.equals(role)) return "admin";
        if (Options.ROLE_JADWAL.equals(role)) return "petugas_jadwal";
        return "petugas_konten";
    }
}
