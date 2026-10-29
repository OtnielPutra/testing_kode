package com.prisma.view.component;

import javax.swing.*;
import java.awt.*;

/** Panel yang bisa muncul dengan efek fade-in (dipakai saat berpindah halaman). */
public class FadePanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private float alpha = 1f;
    private Timer timer;

    public FadePanel(LayoutManager layout) {
        super(layout);
        setOpaque(false);
    }

    public void fadeIn() {
        if (timer != null) timer.stop();
        alpha = 0f;
        timer = new Timer(16, e -> {
            alpha = Math.min(1f, alpha + 0.12f);
            repaint();
            if (alpha >= 1f) timer.stop();
        });
        timer.start();
    }

    @Override
    public void paint(Graphics g) {
        if (alpha >= 1f) {
            super.paint(g);
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setComposite(AlphaComposite.SrcOver.derive(alpha));
        super.paint(g2);
        g2.dispose();
    }
}
