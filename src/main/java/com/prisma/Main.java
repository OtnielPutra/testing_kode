package com.prisma;

import com.prisma.config.DatabaseManager;
import com.prisma.view.LoginFrame;
import com.prisma.view.component.Theme;

import javax.swing.*;
import java.awt.*;

/** Titik masuk aplikasi PRISMA TV. */
public class Main {

    public static void main(String[] args) {
        Theme.install();
        Runtime.getRuntime().addShutdownHook(new Thread(DatabaseManager::shutdown));

        SwingUtilities.invokeLater(() -> {
            JWindow splash = buildSplash();
            splash.setVisible(true);

            // inisialisasi Hibernate + pool koneksi di thread terpisah supaya UI tidak membeku
            new SwingWorker<Void, Void>() {
                @Override protected Void doInBackground() {
                    DatabaseManager.getInstance();
                    return null;
                }

                @Override protected void done() {
                    splash.dispose();
                    try {
                        get();
                        new LoginFrame().setVisible(true);
                    } catch (Exception ex) {
                        Throwable root = ex;
                        while (root.getCause() != null) root = root.getCause();
                        JOptionPane.showMessageDialog(null,
                                "Tidak dapat terhubung ke database.\n\n" + root.getMessage()
                                        + "\n\nPastikan MySQL berjalan, database 'prisma_tv' sudah diimpor,\n"
                                        + "dan konfigurasi di src/main/resources/database.properties sudah benar.",
                                "Koneksi Database Gagal", JOptionPane.ERROR_MESSAGE);
                        System.exit(1);
                    }
                }
            }.execute();
        });
    }

    private static JWindow buildSplash() {
        JWindow w = new JWindow();
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(Theme.NAVY);
        p.setBorder(BorderFactory.createEmptyBorder(26, 40, 26, 40));
        JLabel title = Theme.label("PRISMA", Font.BOLD, 30, Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel info = Theme.label("Menghubungkan ke database...", Font.PLAIN, 13, new Color(0xbf, 0xdb, 0xfe));
        info.setHorizontalAlignment(SwingConstants.CENTER);
        JProgressBar bar = new JProgressBar();
        bar.setIndeterminate(true);
        p.add(title, BorderLayout.NORTH);
        p.add(info, BorderLayout.CENTER);
        p.add(bar, BorderLayout.SOUTH);
        w.setContentPane(p);
        w.pack();
        w.setSize(Math.max(w.getWidth(), 360), w.getHeight());
        w.setLocationRelativeTo(null);
        return w;
    }
}
