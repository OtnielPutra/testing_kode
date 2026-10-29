package com.prisma.view.component;

import javax.swing.*;
import java.awt.*;

/** Notifikasi kecil di pojok kanan bawah yang muncul dan menghilang dengan animasi fade. */
public class Toast extends JComponent {
    private static final long serialVersionUID = 1L;

    private final String message;
    private float alpha = 0f;

    private Toast(String message) {
        this.message = message;
        setFont(Theme.font(Font.BOLD, 13));
        FontMetrics fm = getFontMetrics(getFont());
        setSize(fm.stringWidth(message) + 44, 44);
    }

    public static void show(Component origin, String message) {
        JRootPane root = SwingUtilities.getRootPane(origin);
        if (root == null) return;
        JLayeredPane layer = root.getLayeredPane();
        Toast toast = new Toast(message);
        toast.setLocation(layer.getWidth() - toast.getWidth() - 24, layer.getHeight() - toast.getHeight() - 24);
        layer.add(toast, JLayeredPane.POPUP_LAYER);
        toast.animate(layer);
    }

    private void animate(JLayeredPane layer) {
        final int[] tick = {0};
        Timer t = new Timer(16, null);
        t.addActionListener(e -> {
            tick[0]++;
            if (tick[0] <= 12) alpha = tick[0] / 12f;                 // fade in
            else if (tick[0] > 150) alpha = Math.max(0f, 1f - (tick[0] - 150) / 20f); // fade out
            if (tick[0] > 170) {
                t.stop();
                layer.remove(this);
                layer.repaint();
            } else {
                repaint();
            }
        });
        t.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setComposite(AlphaComposite.SrcOver.derive(alpha));
        g2.setColor(Theme.NAVY);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
        g2.setColor(new Color(0x34, 0xd3, 0x99));
        g2.fillRoundRect(0, 10, 4, getHeight() - 20, 4, 4);
        g2.setColor(Color.WHITE);
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(message, 22, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
        g2.dispose();
    }
}
