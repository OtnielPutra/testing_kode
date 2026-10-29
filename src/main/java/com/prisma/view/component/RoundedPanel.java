package com.prisma.view.component;

import javax.swing.*;
import java.awt.*;

/** Kartu putih dengan sudut membulat dan garis tepi tipis (class .panel / .stat pada HTML). */
public class RoundedPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final int radius;

    public RoundedPanel() { this(14); }

    public RoundedPanel(int radius) {
        this.radius = radius;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        g2.setColor(Theme.LINE);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
        g2.dispose();
        super.paintComponent(g);
    }
}
