package com.prisma.view.component;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

/**
 * Date Picker / Time Picker berbasis JSpinner.
 * Bila nullable, tersedia kotak centang "Kosong" (dipakai pada pencarian dan tanggal akhir opsional).
 */
public class DateTimeField extends JPanel {
    private static final long serialVersionUID = 1L;

    public enum Mode { DATE, TIME, DATETIME }

    private final JSpinner spinner;
    private final JCheckBox empty;

    public DateTimeField(Mode mode, boolean nullable) {
        super(new FlowLayout(FlowLayout.LEFT, 8, 0));
        setOpaque(false);

        spinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        String pattern = mode == Mode.DATE ? "yyyy-MM-dd" : mode == Mode.TIME ? "HH:mm:ss" : "yyyy-MM-dd HH:mm";
        spinner.setEditor(new JSpinner.DateEditor(spinner, pattern));
        spinner.setFont(Theme.font(Font.PLAIN, 13));
        spinner.setPreferredSize(new Dimension(mode == Mode.DATETIME ? 190 : 140, 32));
        add(spinner);

        empty = new JCheckBox("Kosong");
        empty.setOpaque(false);
        empty.setFont(Theme.font(Font.PLAIN, 12));
        if (nullable) {
            empty.addActionListener(e -> spinner.setEnabled(!empty.isSelected()));
            add(empty);
        }
        if (!nullable) empty.setSelected(false);
    }

    public boolean isEmptyValue() { return empty.isSelected(); }

    public void setEmptyValue(boolean value) {
        empty.setSelected(value);
        spinner.setEnabled(!value);
    }

    private LocalDateTime current() {
        Date d = (Date) spinner.getValue();
        return LocalDateTime.ofInstant(d.toInstant(), ZoneId.systemDefault());
    }

    private void put(LocalDateTime v) {
        spinner.setValue(Date.from(v.atZone(ZoneId.systemDefault()).toInstant()));
    }

    public LocalDate getDate() { return isEmptyValue() ? null : current().toLocalDate(); }
    public LocalTime getTime() { return isEmptyValue() ? null : current().toLocalTime().withNano(0); }
    public LocalDateTime getDateTime() { return isEmptyValue() ? null : current().withNano(0); }

    public void setDate(LocalDate d) {
        if (d == null) { setEmptyValue(true); return; }
        put(d.atStartOfDay());
        setEmptyValue(false);
    }

    public void setTime(LocalTime t) {
        if (t == null) { setEmptyValue(true); return; }
        put(LocalDate.now().atTime(t));
        setEmptyValue(false);
    }

    public void setDateTime(LocalDateTime v) {
        if (v == null) { setEmptyValue(true); return; }
        put(v);
        setEmptyValue(false);
    }
}
