package com.prisma.controller;

import com.prisma.config.AppSession;
import com.prisma.dao.PenggunaDao;
import com.prisma.model.Admin;
import com.prisma.model.Pengguna;
import com.prisma.model.PetugasJadwal;
import com.prisma.model.PetugasKonten;
import com.prisma.util.Options;
import com.prisma.util.ValidationException;
import com.prisma.util.Validator;

import java.time.LocalDate;

public class PenggunaController extends BaseController<Pengguna, Integer> {

    private final PenggunaDao penggunaDao;

    public PenggunaController() { this(new PenggunaDao()); }

    private PenggunaController(PenggunaDao dao) {
        super(dao);
        this.penggunaDao = dao;
    }

    @Override
    protected void validate(Pengguna p) throws ValidationException {
        p.setNamaLengkap(Validator.required(p.getNamaLengkap(), "Nama lengkap", 100));
        p.setUsername(Validator.username(p.getUsername()));
        p.setPassword(Validator.password(p.getPassword()));
        p.setStatusAkun(Validator.oneOf(p.getStatusAkun(), "Status akun", Options.STATUS_AKTIF));
        Validator.notNull(p.getTanggalPenugasan(), "Tanggal penugasan");
        if (!(p instanceof Admin)) {
            p.setDetail(Validator.required(p.getDetail(), p.getDetailLabel(), 100));
        }
    }

    /** POLYMORPHISM: peran menentukan subclass yang dibuat. */
    private Pengguna buat(String role) throws ValidationException {
        String r = Validator.oneOf(role, "Peran", Options.ROLES);
        if (Options.ROLE_ADMIN.equals(r)) return new Admin();
        if (Options.ROLE_JADWAL.equals(r)) return new PetugasJadwal();
        return new PetugasKonten();
    }

    private void isi(Pengguna p, String nama, String user, String pass, String status, String detail, LocalDate tgl) {
        p.setNamaLengkap(nama);
        p.setUsername(user);
        p.setPassword(pass);
        p.setStatusAkun(status);
        p.setDetail(detail);
        p.setTanggalPenugasan(tgl);
    }

    public Pengguna tambahAkun(String nama, String user, String pass, String status,
                               String role, String detail, LocalDate tgl) throws ValidationException {
        Pengguna p = buat(role);
        isi(p, nama, user, pass, status, detail, tgl);
        return save(p);
    }

    public void ubahAkun(Pengguna existing, String nama, String user, String pass, String status,
                         String newRole, String detail, LocalDate tgl) throws ValidationException {
        Pengguna login = AppSession.getUser();
        boolean diriSendiri = login != null && login.getIdPengguna().equals(existing.getIdPengguna());
        if (diriSendiri && (!existing.getRole().equals(newRole) || "nonaktif".equals(status))) {
            throw new ValidationException("Anda tidak dapat mengubah peran atau menonaktifkan akun Anda sendiri.");
        }

        if (existing.getRole().equals(newRole)) {
            isi(existing, nama, user, pass, status, detail, tgl);
            save(existing);
            return;
        }
        // ganti peran: validasi dulu lewat objek sementara bertipe peran baru
        Pengguna temp = buat(newRole);
        isi(temp, nama, user, pass, status, detail, tgl);
        validate(temp);
        penggunaDao.updateWithRoleChange(existing.getIdPengguna(), temp.getNamaLengkap(), temp.getUsername(),
                temp.getPassword(), temp.getStatusAkun(), existing.getRole(), newRole,
                temp.getDetail(), temp.getTanggalPenugasan());
    }

    @Override
    public void delete(Pengguna p) throws ValidationException {
        Pengguna login = AppSession.getUser();
        if (login != null && login.getIdPengguna().equals(p.getIdPengguna())) {
            throw new ValidationException("Akun yang sedang digunakan tidak dapat dihapus.");
        }
        super.delete(p);
    }
}
