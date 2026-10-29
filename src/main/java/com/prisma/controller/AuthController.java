package com.prisma.controller;

import com.prisma.config.AppSession;
import com.prisma.dao.PenggunaDao;
import com.prisma.model.Pengguna;
import com.prisma.util.ValidationException;

/** Proses login sesuai flowchart: verifikasi akun -> cek terdaftar -> ambil peran. */
public class AuthController {

    private final PenggunaDao dao = new PenggunaDao();

    public Pengguna login(String username, String password) throws ValidationException {
        String u = username == null ? "" : username.trim();
        if (u.isEmpty() || password == null || password.isEmpty()) {
            throw new ValidationException("Username dan password wajib diisi.");
        }
        Pengguna p = dao.findByUsername(u);
        // equals() dipakai agar huruf besar/kecil dibedakan (collation MySQL tidak membedakan)
        if (p == null || !p.getUsername().equals(u) || !p.getPassword().equals(password)) {
            throw new ValidationException("Username Atau Password Salah");
        }
        if (!p.isAktif()) {
            throw new ValidationException("Akun Anda nonaktif. Hubungi admin.");
        }
        AppSession.setUser(p);
        return p;
    }
}
