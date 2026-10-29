package com.prisma.view;

import com.prisma.controller.DashboardController;
import com.prisma.model.JadwalTayang;
import com.prisma.model.Pengguna;
import com.prisma.util.DataAccessException;
import com.prisma.util.Fmt;
import com.prisma.view.component.*;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;

/** Dashboard: kartu statistik (angka berputar naik) + jadwal tayang terbaru + relasi mitra. */
public class DashboardPanel extends JPanel implements Refreshable {
    private static final long serialVersionUID = 1L;

    private final DashboardController controller = new DashboardController();
    private final StatCard cKonten = new StatCard("Total Konten");
    private final StatCard cProgram = new StatCard("Program TV");
    private final StatCard cJadwal = new StatCard("Jadwal Tayang");
    private final StatCard cKerjaSama = new StatCard("Kerja Sama");

    private final EntityTableModel<JadwalTayang> jadwalModel = new EntityTableModel<>(
            new String[] {"Program", "Kanal", "Tanggal", "Waktu", "Status"},
            (j, c) -> {
                switch (c) {
                    case 0: return j.getProgramTv().getNamaProgram();
                    case 1: return j.getKanal().getNamaKanal();
                    case 2: return Fmt.date(j.getTanggalTayang());
                    case 3: return Fmt.time(j.getWaktuMulai()) + " - " + Fmt.time(j.getWaktuSelesai());
                    default: return j.getStatusJadwal();
                }
            });
    private final EntityTableModel<Object[]> mitraModel = new EntityTableModel<>(
            new String[] {"Mitra", "Jumlah Kerja Sama"},
            (row, c) -> c == 0 ? String.valueOf(row[0]) : row[1] + " kerja sama");

    public DashboardPanel(Pengguna user) {
        setLayout(new BorderLayout(0, 18));
        setOpaque(false);

        JPanel head = new JPanel();
        head.setOpaque(false);
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        JLabel h1 = Theme.label("Selamat datang, " + user.getNamaLengkap(), Font.BOLD, 24, Theme.TEXT);
        JLabel sub = Theme.label("Anda masuk sebagai " + user.getRole() + ". Ringkasan pengelolaan konten dan jadwal tayang.",
                Font.PLAIN, 14, Theme.MUTED);
        h1.setAlignmentX(LEFT_ALIGNMENT);
        sub.setAlignmentX(LEFT_ALIGNMENT);
        head.add(h1);
        head.add(Box.createVerticalStrut(6));
        head.add(sub);
        add(head, BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        JPanel stats = new JPanel(new GridLayout(1, 4, 15, 0));
        stats.setOpaque(false);
        stats.add(cKonten);
        stats.add(cProgram);
        stats.add(cJadwal);
        stats.add(cKerjaSama);
        stats.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        stats.setAlignmentX(LEFT_ALIGNMENT);
        body.add(stats);
        body.add(Box.createVerticalStrut(18));

        DataTable jadwalTable = new DataTable(jadwalModel);
        jadwalTable.setStatusColumn(4);
        body.add(tableCard("Jadwal Tayang Terbaru", null, jadwalTable, 5));
        body.add(Box.createVerticalStrut(18));
        body.add(tableCard("Relasi Mitra dan Kerja Sama",
                "Setiap kerja sama mengacu pada satu mitra terdaftar melalui ID Mitra.", new DataTable(mitraModel), 6));

        JScrollPane sp = new JScrollPane(body);
        sp.setBorder(null);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        add(sp, BorderLayout.CENTER);

        refresh();
    }

    private JComponent tableCard(String title, String note, DataTable table, int rows) {
        table.setRowSorter(null);
        RoundedPanel card = new RoundedPanel();
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        JLabel t = Theme.label(title, Font.BOLD, 16, Theme.TEXT);
        t.setAlignmentX(LEFT_ALIGNMENT);
        top.add(t);
        if (note != null) {
            JLabel n = Theme.label(note, Font.PLAIN, 12, Theme.MUTED);
            n.setAlignmentX(LEFT_ALIGNMENT);
            top.add(Box.createVerticalStrut(4));
            top.add(n);
        }
        card.add(top, BorderLayout.NORTH);

        JPanel holder = new JPanel(new BorderLayout());
        holder.setOpaque(false);
        holder.add(table.getTableHeader(), BorderLayout.NORTH);
        holder.add(table, BorderLayout.CENTER);
        card.add(holder, BorderLayout.CENTER);
        card.setAlignmentX(LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90 + 38 + rows * 37));
        return card;
    }

    @Override
    public void refresh() {
        List<JadwalTayang> jadwal = Collections.emptyList();
        List<Object[]> mitra = Collections.emptyList();
        DashboardController.Stats s = new DashboardController.Stats(0, 0, 0, 0);
        try {
            s = controller.ringkasan();
            jadwal = controller.jadwalTerbaru(5);
            mitra = controller.kerjaSamaPerMitra(6);
        } catch (DataAccessException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
        cKonten.animateTo(s.konten);
        cProgram.animateTo(s.program);
        cJadwal.animateTo(s.jadwal);
        cKerjaSama.animateTo(s.kerjaSama);
        jadwalModel.setRows(jadwal);
        mitraModel.setRows(mitra);
    }

    /** Kartu statistik dengan angka yang naik perlahan (ease-out). */
    private static class StatCard extends RoundedPanel {
        private static final long serialVersionUID = 1L;
        private final JLabel value = Theme.label("0", Font.BOLD, 28, Theme.TEXT);

        StatCard(String caption) {
            setLayout(new BorderLayout(0, 10));
            setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
            add(Theme.label(caption, Font.PLAIN, 13, Theme.MUTED), BorderLayout.NORTH);
            add(value, BorderLayout.CENTER);
        }

        void animateTo(long target) {
            final int steps = 30;
            final int[] i = {0};
            Timer t = new Timer(20, null);
            t.addActionListener(e -> {
                i[0]++;
                double p = 1 - Math.pow(1 - (double) i[0] / steps, 3);
                value.setText(String.valueOf(Math.round(target * p)));
                if (i[0] >= steps) t.stop();
            });
            t.start();
        }
    }
}
