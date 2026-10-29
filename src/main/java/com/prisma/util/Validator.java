package com.prisma.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Pattern;

/** Kumpulan aturan validasi input. Semua method mengembalikan nilai yang sudah di-trim. */
public final class Validator {
    private Validator() { }

    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE = Pattern.compile("^[0-9+\\-\\s()]{6,25}$");
    private static final Pattern USERNAME = Pattern.compile("^[A-Za-z0-9._]{3,50}$");

    public static String required(String v, String label, int max) throws ValidationException {
        String t = v == null ? "" : v.trim();
        if (t.isEmpty()) throw new ValidationException(label + " wajib diisi.");
        if (t.length() > max) throw new ValidationException(label + " maksimal " + max + " karakter.");
        return t;
    }

    public static String optional(String v, String label, int max) throws ValidationException {
        String t = v == null ? "" : v.trim();
        if (t.isEmpty()) return null;
        if (t.length() > max) throw new ValidationException(label + " maksimal " + max + " karakter.");
        return t;
    }

    public static <X> X notNull(X v, String label) throws ValidationException {
        if (v == null) throw new ValidationException(label + " wajib dipilih / diisi.");
        return v;
    }

    public static String oneOf(String v, String label, String[] allowed) throws ValidationException {
        if (v != null) for (String a : allowed) if (a.equals(v)) return v;
        throw new ValidationException(label + " tidak valid.");
    }

    public static String email(String v, String label, int max) throws ValidationException {
        String t = optional(v, label, max);
        if (t != null && !EMAIL.matcher(t).matches()) throw new ValidationException("Format " + label + " tidak valid.");
        return t;
    }

    public static String phone(String v, String label, int max) throws ValidationException {
        String t = optional(v, label, max);
        if (t != null && !PHONE.matcher(t).matches())
            throw new ValidationException(label + " hanya boleh berisi angka, spasi, +, -, ( ) (6-25 karakter).");
        return t;
    }

    public static String url(String v, String label, int max) throws ValidationException {
        String t = optional(v, label, max);
        if (t != null && !(t.startsWith("http://") || t.startsWith("https://")))
            throw new ValidationException(label + " harus diawali http:// atau https://");
        return t;
    }

    public static String username(String v) throws ValidationException {
        String t = required(v, "Username", 50);
        if (!USERNAME.matcher(t).matches())
            throw new ValidationException("Username 3-50 karakter, hanya huruf, angka, titik, dan underscore.");
        return t;
    }

    public static String password(String v) throws ValidationException {
        if (v == null || v.length() < 4) throw new ValidationException("Password minimal 4 karakter.");
        if (v.length() > 30) throw new ValidationException("Password maksimal 30 karakter (sesuai kolom database).");
        return v;
    }

    /** Nilai uang opsional. Menerima "25000000", "25000000.50", atau "Rp 25,000,000". */
    public static BigDecimal money(String raw, String label) throws ValidationException {
        String t = raw == null ? "" : raw.replace("Rp", "").replace(",", "").replace(" ", "").trim();
        if (t.isEmpty()) return null;
        BigDecimal v;
        try {
            v = new BigDecimal(t);
        } catch (NumberFormatException e) {
            throw new ValidationException(label + " harus berupa angka (contoh: 25000000).");
        }
        if (v.signum() < 0) throw new ValidationException(label + " tidak boleh negatif.");
        if (v.compareTo(new BigDecimal("9999999999999.99")) > 0) throw new ValidationException(label + " terlalu besar.");
        return v.setScale(2, RoundingMode.HALF_UP);
    }
}
