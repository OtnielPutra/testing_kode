package com.prisma.config;

import com.prisma.model.Pengguna;
import com.prisma.util.ValidationException;

/** Singleton sederhana yang menyimpan pengguna yang sedang login. */
public final class AppSession {
    private AppSession() { }

    private static Pengguna user;

    public static synchronized void setUser(Pengguna u) { user = u; }
    public static synchronized Pengguna getUser() { return user; }
    public static synchronized void clear() { user = null; }

    /** Mengambil pengguna login dengan tipe tertentu (mis. Admin) atau menolak akses. */
    public static <U extends Pengguna> U requireUser(Class<U> type) throws ValidationException {
        Pengguna u = getUser();
        if (type.isInstance(u)) return type.cast(u);
        throw new ValidationException("Anda tidak memiliki hak akses untuk aksi ini.");
    }
}
