package com.prisma.view;

import com.prisma.config.AppSession;
import com.prisma.config.DatabaseManager;
import com.prisma.model.Menu;
import com.prisma.model.Pengguna;
import com.prisma.view.component.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.EnumMap;
import java.util.Map;

/** Jendela utama: sidebar menu (sesuai peran), topbar, dan area konten berpindah halaman dengan fade. */
public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private final Pengguna user;
    private final CardLayout cards = new CardLayout();
    private final FadePanel host = new FadePanel(cards);
    private final Map<Menu, JPanel> pages = new EnumMap<>(Menu.class);
    private final Map<Menu, AnimatedButton> navButtons = new EnumMap<>(Menu.class);
    private final JLabel breadcrumb = Theme.label("Dashboard", Font.PLAIN, 13, Theme.MUTED);
    private final JLabel pageTitle = Theme.label("Dashboard", Font.BOLD, 22, Theme.TEXT);

    public MainFrame(Pengguna user) {
        super("PRISMA - Sistem Informasi Manajemen Konten");
        this.user = user;
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(1320, 780);
        setMinimumSize(new Dimension(1050, 650));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG);
        root.add(buildSidebar(), BorderLayout.WEST);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(buildTopbar(), BorderLayout.NORTH);
        host.setBorder(BorderFactory.createEmptyBorder(24, 26, 10, 26));
        center.add(host, BorderLayout.CENTER);
        JLabel footer = Theme.label("PRISMA · Sistem informasi manajemen konten · Terhubung ke database prisma_tv",
                Font.PLAIN, 12, Theme.MUTED);
        footer.setHorizontalAlignment(SwingConstants.CENTER);
        footer.setBorder(BorderFactory.createEmptyBorder(6, 0, 10, 0));
        center.add(footer, BorderLayout.SOUTH);
        root.add(center, BorderLayout.CENTER);
        setContentPane(root);

        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { confirmExit(); }
        });
        navigate(Menu.DASHBOARD);
    }

    private JComponent buildSidebar() {
        JPanel side = new JPanel();
        side.setBackground(Theme.NAVY);
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBorder(BorderFactory.createEmptyBorder(24, 14, 14, 14));
        side.setPreferredSize(new Dimension(245, 0));

        JLabel brand = Theme.label("PRISMA", Font.BOLD, 26, Color.WHITE);
        JLabel tag = Theme.label("CONTENT & BROADCAST", Font.PLAIN, 10, new Color(0xbf, 0xdb, 0xfe));
        brand.setAlignmentX(LEFT_ALIGNMENT);
        tag.setAlignmentX(LEFT_ALIGNMENT);
        brand.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));
        tag.setBorder(BorderFactory.createEmptyBorder(4, 13, 22, 0));
        side.add(brand);
        side.add(tag);

        addGroup(side, "MENU UTAMA", false);
        addGroup(side, "PENGATURAN SISTEM", true);
        side.add(Box.createVerticalGlue());
        return side;
    }

    private void addGroup(JPanel side, String caption, boolean system) {
        boolean header = false;
        for (Menu m : Menu.values()) {
            if (!user.getMenus().contains(m) || m.isPengaturanSistem() != system) continue;
            if (!header) {
                JLabel l = Theme.label(caption, Font.PLAIN, 11, new Color(0x94, 0xa3, 0xb8));
                l.setAlignmentX(LEFT_ALIGNMENT);
                l.setBorder(BorderFactory.createEmptyBorder(14, 12, 8, 0));
                side.add(l);
                header = true;
            }
            AnimatedButton b = new AnimatedButton(m.getLabel(), AnimatedButton.Kind.SIDEBAR);
            b.setAlignmentX(LEFT_ALIGNMENT);
            b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
            b.addActionListener(e -> navigate(m));
            navButtons.put(m, b);
            side.add(b);
            side.add(Box.createVerticalStrut(3));
        }
    }

    private JComponent buildTopbar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Color.WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.LINE),
                BorderFactory.createEmptyBorder(14, 28, 14, 24)));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        breadcrumb.setAlignmentX(LEFT_ALIGNMENT);
        pageTitle.setAlignmentX(LEFT_ALIGNMENT);
        left.add(breadcrumb);
        left.add(Box.createVerticalStrut(4));
        left.add(pageTitle);
        bar.add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        right.setOpaque(false);
        right.add(new Avatar(user.getNamaLengkap()));
        JPanel who = new JPanel();
        who.setOpaque(false);
        who.setLayout(new BoxLayout(who, BoxLayout.Y_AXIS));
        who.add(Theme.label(user.getNamaLengkap(), Font.BOLD, 13, Theme.TEXT));
        who.add(Theme.label(user.getRole(), Font.PLAIN, 12, Theme.MUTED));
        right.add(who);
        AnimatedButton logout = new AnimatedButton("Log Out", AnimatedButton.Kind.DANGER);
        logout.addActionListener(e -> logout());
        right.add(logout);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    /** Berpindah halaman. Halaman dibuat saat pertama dibuka, dan datanya dimuat ulang setiap kali dibuka. */
    public void navigate(Menu menu) {
        boolean baru = !pages.containsKey(menu);
        if (baru) {
            try {
                JPanel p = PanelFactory.create(menu, user);
                pages.put(menu, p);
                host.add(p, menu.name());
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(this, "Gagal membuka menu: " + ex.getMessage(),
                        "Kesalahan", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else if (pages.get(menu) instanceof Refreshable) {
            ((Refreshable) pages.get(menu)).refresh();
        }
        cards.show(host, menu.name());
        host.fadeIn();
        pageTitle.setText(menu.getLabel());
        breadcrumb.setText("PRISMA / " + menu.getLabel());
        navButtons.forEach((m, b) -> b.setActive(m == menu));
    }

    private void logout() {
        int ok = JOptionPane.showConfirmDialog(this, "Yakin ingin keluar dari akun ini?", "Log Out",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (ok != JOptionPane.YES_OPTION) return;
        AppSession.clear();
        dispose();
        new LoginFrame().setVisible(true);   // kembali ke Menu Awal (flowchart)
    }

    private void confirmExit() {
        int ok = JOptionPane.showConfirmDialog(this, "Tutup aplikasi PRISMA?", "Keluar",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (ok == JOptionPane.YES_OPTION) {
            dispose();
            DatabaseManager.shutdown();
            System.exit(0);
        }
    }

    /** Lingkaran berisi inisial nama pengguna. */
    private static class Avatar extends JComponent {
        private static final long serialVersionUID = 1L;
        private final String initial;

        Avatar(String name) {
            initial = name == null || name.isBlank() ? "?" : name.trim().substring(0, 1).toUpperCase();
            setPreferredSize(new Dimension(38, 38));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Theme.BLUE);
            g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
            g2.setColor(Color.WHITE);
            g2.setFont(Theme.font(Font.BOLD, 16));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(initial, (getWidth() - fm.stringWidth(initial)) / 2,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            g2.dispose();
        }
    }
}
