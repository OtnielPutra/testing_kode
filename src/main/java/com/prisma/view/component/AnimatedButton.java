package com.prisma.view.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Tombol dengan transisi warna halus saat di-hover (animasi berbasis javax.swing.Timer). */
public class AnimatedButton extends JButton {
    private static final long serialVersionUID = 1L;

    public enum Kind { PRIMARY, SECONDARY, DANGER, SIDEBAR }

    private final Kind kind;
    private float hover = 0f;
    private boolean hovering = false;
    private boolean active = false;
    private final Timer timer;

    public AnimatedButton(String text, Kind kind) {
        super(text);
        this.kind = kind;
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (kind == Kind.SIDEBAR) {
            setFont(Theme.font(Font.PLAIN, 14));
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(BorderFactory.createEmptyBorder(11, 20, 11, 12));
        } else {
            setFont(Theme.font(Font.BOLD, 13));
            setBorder(BorderFactory.createEmptyBorder(9, 16, 9, 16));
        }
        setForeground(baseForeground());

        timer = new Timer(15, e -> step());
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hovering = true; timer.start(); }
            @Override public void mouseExited(MouseEvent e) { hovering = false; timer.start(); }
        });
    }

    public void setActive(boolean active) {
        this.active = active;
        repaint();
    }

    private void step() {
        float target = hovering ? 1f : 0f;
        hover += (target - hover) * 0.3f;
        if (Math.abs(target - hover) < 0.03f) {
            hover = target;
            timer.stop();
        }
        repaint();
    }

    private Color baseForeground() {
        switch (kind) {
            case PRIMARY: return Color.WHITE;
            case DANGER: return Theme.DANGER;
            case SIDEBAR: return new Color(0xdb, 0xea, 0xfe);
            default: return Theme.TEXT;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth(), h = getHeight();

        if (kind == Kind.SIDEBAR) {
            float a = active ? 1f : hover;
            Color c = Theme.NAVY_LIGHT;
            g2.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), Math.round(a * 255)));
            g2.fillRoundRect(0, 0, w, h, 10, 10);
            g2.setColor(new Color(96, 165, 250, Math.round(a * 255)));
            g2.fillRoundRect(0, 9, 4, h - 18, 4, 4);
            setForeground(Theme.lerp(baseForeground(), Color.WHITE, a));
        } else {
            Color base, over;
            switch (kind) {
                case PRIMARY: base = Theme.BLUE; over = Theme.BLUE_DARK; break;
                case DANGER: base = Theme.DANGER_BG; over = new Color(0xfe, 0xca, 0xca); break;
                default: base = Theme.LINE; over = new Color(0xcb, 0xd5, 0xe1); break;
            }
            Color c = Theme.lerp(base, over, hover);
            if (getModel().isPressed()) c = c.darker();
            if (!isEnabled()) c = Theme.lerp(c, Color.WHITE, 0.5f);
            g2.setColor(c);
            g2.fillRoundRect(0, 0, w, h, 12, 12);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
