package com.prisma.view;

import com.prisma.view.component.Refreshable;
import com.prisma.view.component.Theme;

import javax.swing.*;
import java.awt.*;

/** Menu Monitor Data (flowchart): menampilkan data Program TV, Konten, Jadwal Tayang, Kerja Sama, dan Mitra (read-only). */
public class MonitorPanel extends JPanel implements Refreshable {
    private static final long serialVersionUID = 1L;

    private final JPanel[] pages;

    public MonitorPanel() {
        setLayout(new BorderLayout(0, 14));
        setOpaque(false);

        JPanel head = new JPanel();
        head.setOpaque(false);
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        JLabel h1 = Theme.label("Monitor Data", Font.BOLD, 24, Theme.TEXT);
        JLabel sub = Theme.label("Pantau seluruh data utama sistem secara read-only.", Font.PLAIN, 14, Theme.MUTED);
        h1.setAlignmentX(LEFT_ALIGNMENT);
        sub.setAlignmentX(LEFT_ALIGNMENT);
        head.add(h1);
        head.add(Box.createVerticalStrut(6));
        head.add(sub);
        add(head, BorderLayout.NORTH);

        pages = new JPanel[] {new ProgramTvPanel(false), new KontenPanel(false), new JadwalPanel(false),
                new KerjaSamaPanel(false), new MitraPanel(false)};
        String[] names = {"Program TV", "Konten", "Jadwal Tayang", "Kerja Sama", "Mitra"};

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(Theme.font(Font.BOLD, 13));
        for (int i = 0; i < pages.length; i++) {
            JPanel wrap = new JPanel(new BorderLayout());
            wrap.setOpaque(false);
            wrap.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
            wrap.add(pages[i], BorderLayout.CENTER);
            tabs.addTab(names[i], wrap);
        }
        add(tabs, BorderLayout.CENTER);
    }

    @Override
    public void refresh() {
        for (JPanel p : pages) ((Refreshable) p).refresh();
    }
}
