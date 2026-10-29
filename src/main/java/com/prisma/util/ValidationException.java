package com.prisma.util;

/** Dilempar ketika input pengguna tidak valid (Proses Validasi Input pada flowchart). */
public class ValidationException extends Exception {
    private static final long serialVersionUID = 1L;
    public ValidationException(String message) { super(message); }
}
