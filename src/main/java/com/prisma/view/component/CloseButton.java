package com.prisma.view.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Tombol silang (X) untuk menutup aplikasi pada jendela tanpa title bar. */
public class CloseButton extends JButton {
    private static final long serialVersionUID = 1L;

    private float hover = 0f;
    private boolean hovering = false;
    private final Timer timer;

    public CloseButton() {
        setPreferredSize(new Dimension(34, 34));
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setToolTipText("Tutup aplikasi");
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        timer = new Timer(15, e -> step());
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hovering = true; timer.start(); }
            @Override public void mouseExited(MouseEvent e) { hovering = false; timer.start(); }
        });
    }

    private void step() {
        float target = hovering ? 1f : 0f;
        hover += (target - hover) * 0.3f;
        if (Math.abs(target - hover) < 0.03f) { hover = target; timer.stop(); }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Theme.lerp(Color.WHITE, new Color(0xef, 0x44, 0x44), hover));
        g2.fillOval(2, 2, getWidth() - 4, getHeight() - 4);
        g2.setColor(Theme.lerp(Theme.MUTED, Color.WHITE, hover));
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int c = getWidth() / 2, r = 5;
        g2.drawLine(c - r, c - r, c + r, c + r);
        g2.drawLine(c + r, c - r, c - r, c + r);
        g2.dispose();
    }
}
