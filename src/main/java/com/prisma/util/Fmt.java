package com.prisma.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** Format tampilan tanggal, waktu, dan rupiah. */
public final class Fmt {
    private Fmt() { }

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static String date(LocalDate d) { return d == null ? "-" : d.format(DATE); }
    public static String time(LocalTime t) { return t == null ? "-" : t.format(TIME); }
    public static String dateTime(LocalDateTime d) { return d == null ? "-" : d.format(DATETIME); }
    public static String dash(String s) { return s == null || s.isBlank() ? "-" : s; }

    public static String rupiah(BigDecimal v) {
        if (v == null) return "-";
        return NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID")).format(v);
    }
}
