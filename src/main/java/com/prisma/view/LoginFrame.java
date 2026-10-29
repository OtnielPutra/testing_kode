package com.prisma.view;

import com.prisma.config.DatabaseManager;
import com.prisma.controller.AuthController;
import com.prisma.model.Pengguna;
import com.prisma.util.DataAccessException;
import com.prisma.util.ValidationException;
import com.prisma.view.component.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.concurrent.ExecutionException;

/**
 * Menu Awal: Login / Keluar (flowchart). Jendela tanpa title bar dengan sudut membulat,
 * animasi fade-in, latar bergerak, getar saat gagal login, dan tombol close untuk menutup aplikasi.
 */
public class LoginFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private final AuthController auth = new AuthController();
    private final JTextField tfUser = new JTextField();
    private final JPasswordField pfPass = new JPasswordField();
    private final JLabel lblError = Theme.label(" ", Font.PLAIN, 12, Theme.DANGER);
    private final AnimatedButton btnLogin = new AnimatedButton("Login", AnimatedButton.Kind.PRIMARY);

    public LoginFrame() {
        super("PRISMA - Login");
        setUndecorated(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(900, 520);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.add(new BrandPanel(this), BorderLayout.WEST);
        root.add(buildForm(), BorderLayout.CENTER);
        setContentPane(root);
        getRootPane().setDefaultButton(btnLogin);

        try {
            setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 28, 28));
        } catch (RuntimeException ignored) {
            // bentuk jendela tidak didukung di platform ini
        }
        addWindowListener(new WindowAdapter() {
            @Override public void windowOpened(WindowEvent e) { fadeIn(); tfUser.requestFocusInWindow(); }
        });
    }

    private JComponent buildForm() {
        JPanel form = new JPanel(new BorderLayout());
        form.setBackground(Color.WHITE);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        top.setOpaque(false);
        CloseButton close = new CloseButton();
        close.addActionListener(e -> keluar());
        top.add(close);
        form.add(top, BorderLayout.NORTH);

        JPanel box = new JPanel();
        box.setOpaque(false);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(BorderFactory.createEmptyBorder(0, 50, 30, 50));

        add(box, Theme.label("Selamat Datang", Font.BOLD, 28, Theme.TEXT), 0);
        add(box, Theme.label("Masuk untuk mengelola konten dan jadwal tayang.", Font.PLAIN, 14, Theme.MUTED), 24);

        add(box, Theme.label("Username", Font.BOLD, 13, Theme.TEXT), 6);
        Theme.styleInput(tfUser);
        tfUser.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        add(box, tfUser, 14);

        add(box, Theme.label("Password", Font.BOLD, 13, Theme.TEXT), 6);
        Theme.styleInput(pfPass);
        pfPass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        add(box, pfPass, 6);

        JCheckBox show = new JCheckBox("Tampilkan password");
        show.setOpaque(false);
        show.setFont(Theme.font(Font.PLAIN, 12));
        char echo = pfPass.getEchoChar();
        show.addActionListener(e -> pfPass.setEchoChar(show.isSelected() ? (char) 0 : echo));
        add(box, show, 4);

        add(box, lblError, 10);

        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnLogin.setHorizontalAlignment(SwingConstants.CENTER);
        btnLogin.addActionListener(e -> doLogin());
        add(box, btnLogin, 10);

        AnimatedButton btnKeluar = new AnimatedButton("Keluar", AnimatedButton.Kind.SECONDARY);
        btnKeluar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        btnKeluar.setHorizontalAlignment(SwingConstants.CENTER);
        btnKeluar.addActionListener(e -> keluar());
        add(box, btnKeluar, 0);

        form.add(box, BorderLayout.CENTER);
        return form;
    }

    private void add(JPanel box, JComponent c, int gapBelow) {
        c.setAlignmentX(LEFT_ALIGNMENT);
        box.add(c);
        if (gapBelow > 0) box.add(Box.createVerticalStrut(gapBelow));
    }

    // ---------------------------------------------------------------- aksi

    private void doLogin() {
        final String user = tfUser.getText();
        final String pass = new String(pfPass.getPassword());
        lblError.setText(" ");
        btnLogin.setEnabled(false);
        btnLogin.setText("Memproses...");

        new SwingWorker<Pengguna, Void>() {
            @Override protected Pengguna doInBackground() throws Exception {
                return auth.login(user, pass);
            }

            @Override protected void done() {
                btnLogin.setEnabled(true);
                btnLogin.setText("Login");
                try {
                    Pengguna p = get();
                    new MainFrame(p).setVisible(true);   // Login Admin / Petugas Konten / Petugas Jadwal
                    dispose();
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException ex) {
                    Throwable c = ex.getCause();
                    if (c instanceof ValidationException) {
                        lblError.setText(c.getMessage());
                    } else if (c instanceof DataAccessException) {
                        lblError.setText("Database bermasalah: " + c.getMessage());
                    } else {
                        lblError.setText("Terjadi kesalahan: " + c);
                    }
                    pfPass.setText("");
                    shake();
                }
            }
        }.execute();
    }

    /** Menu Awal > Keluar: tampilkan "Terima Kasih" lalu selesai. */
    private void keluar() {
        JOptionPane.showMessageDialog(this, "Terima Kasih", "PRISMA", JOptionPane.INFORMATION_MESSAGE);
        dispose();
        DatabaseManager.shutdown();
        System.exit(0);
    }

    // ------------------------------------------------------------ animasi

    private void fadeIn() {
        try {
            if (!GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
                    .isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.TRANSLUCENT)) return;
            setOpacity(0f);
            final float[] a = {0f};
            Timer t = new Timer(16, null);
            t.addActionListener(e -> {
                a[0] = Math.min(1f, a[0] + 0.08f);
                try { setOpacity(a[0]); } catch (RuntimeException ex) { t.stop(); }
                if (a[0] >= 1f) t.stop();
            });
            t.start();
        } catch (RuntimeException ignored) {
            // tanpa animasi
        }
    }

    private void shake() {
        final Point origin = getLocation();
        final int[] offsets = {-12, 12, -9, 9, -6, 6, -3, 3, 0};
        final int[] i = {0};
        Timer t = new Timer(30, null);
        t.addActionListener(e -> {
            setLocation(origin.x + offsets[i[0]], origin.y);
            if (++i[0] >= offsets.length) t.stop();
        });
        t.start();
    }

    // -------------------------------------------------------- panel merek

    /** Panel kiri bergradasi dengan lingkaran yang melayang pelan; juga bisa dipakai untuk menyeret jendela. */
    private static class BrandPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private final double[][] orbs = {
                {0.2, 0.25, 130, 0.0}, {0.8, 0.15, 90, 1.7}, {0.65, 0.7, 170, 3.1}, {0.15, 0.85, 80, 4.4}};
        private double phase = 0;
        private Point dragStart;

        BrandPanel(JFrame frame) {
            setPreferredSize(new Dimension(390, 0));
            Timer t = new Timer(33, e -> { phase += 0.03; repaint(); });
            t.start();
            addMouseListener(new MouseAdapter() {
                @Override public void mousePressed(MouseEvent e) { dragStart = e.getPoint(); }
            });
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override public void mouseDragged(MouseEvent e) {
                    Point p = frame.getLocation();
                    frame.setLocation(p.x + e.getX() - dragStart.x, p.y + e.getY() - dragStart.y);
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            g2.setPaint(new GradientPaint(0, 0, Theme.NAVY, w, h, new Color(0x25, 0x63, 0xeb)));
            g2.fillRect(0, 0, w, h);
            for (double[] o : orbs) {
                int d = (int) o[2];
                int x = (int) (o[0] * w + Math.sin(phase + o[3]) * 22 - d / 2.0);
                int y = (int) (o[1] * h + Math.cos(phase * 0.8 + o[3]) * 22 - d / 2.0);
                g2.setColor(new Color(255, 255, 255, 22));
                g2.fillOval(x, y, d, d);
            }
            g2.setColor(Color.WHITE);
            g2.setFont(Theme.font(Font.BOLD, 44));
            g2.drawString("PRISMA", 44, h / 2 - 6);
            g2.setColor(new Color(0xbf, 0xdb, 0xfe));
            g2.setFont(Theme.font(Font.PLAIN, 12));
            g2.drawString("C O N T E N T   &   B R O A D C A S T", 46, h / 2 + 18);
            g2.setFont(Theme.font(Font.PLAIN, 14));
            g2.setColor(new Color(255, 255, 255, 210));
            g2.drawString("Sistem informasi manajemen konten,", 46, h / 2 + 64);
            g2.drawString("program, jadwal tayang, dan kerja sama.", 46, h / 2 + 84);
            g2.dispose();
        }
    }
}
