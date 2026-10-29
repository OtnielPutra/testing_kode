package com.prisma.util;

/** Dibungkus dari error JPA/JDBC supaya View tidak perlu tahu detail database. */
public class DataAccessException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public DataAccessException(String message, Throwable cause) { super(message, cause); }
}
