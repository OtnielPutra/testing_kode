package com.prisma.view.component;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/** Warna & gaya yang disalin dari preview.html (variabel CSS :root). */
public final class Theme {
    private Theme() { }

    public static final Color NAVY = new Color(0x17, 0x25, 0x54);
    public static final Color NAVY_LIGHT = new Color(0x2d, 0x42, 0x73);
    public static final Color BLUE = new Color(0x25, 0x63, 0xeb);
    public static final Color BLUE_DARK = new Color(0x1d, 0x4e, 0xd8);
    public static final Color BLUE_SOFT = new Color(0xdb, 0xea, 0xfe);
    public static final Color BG = new Color(0xf4, 0xf7, 0xfb);
    public static final Color TEXT = new Color(0x1e, 0x29, 0x3b);
    public static final Color MUTED = new Color(0x64, 0x74, 0x8b);
    public static final Color LINE = new Color(0xe2, 0xe8, 0xf0);
    public static final Color INPUT_LINE = new Color(0xcb, 0xd5, 0xe1);
    public static final Color HOVER_ROW = new Color(0xf1, 0xf5, 0xf9);
    public static final Color DANGER = new Color(0xb9, 0x1c, 0x1c);
    public static final Color DANGER_BG = new Color(0xfe, 0xe2, 0xe2);

    public static Font font(int style, float size) {
        return new Font("SansSerif", style, Math.round(size));
    }

    public static Color lerp(Color a, Color b, float t) {
        t = Math.max(0f, Math.min(1f, t));
        return new Color(
                Math.round(a.getRed() + (b.getRed() - a.getRed()) * t),
                Math.round(a.getGreen() + (b.getGreen() - a.getGreen()) * t),
                Math.round(a.getBlue() + (b.getBlue() - a.getBlue()) * t));
    }

    public static void install() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // pakai look and feel bawaan
        }
        Font base = font(Font.PLAIN, 13);
        for (String key : new String[] {"Label.font", "TextField.font", "PasswordField.font", "TextArea.font",
                "ComboBox.font", "CheckBox.font", "Table.font", "OptionPane.messageFont",
                "OptionPane.buttonFont", "Spinner.font", "TabbedPane.font", "Button.font"}) {
            UIManager.put(key, base);
        }
        UIManager.put("OptionPane.yesButtonText", "Ya");
        UIManager.put("OptionPane.noButtonText", "Tidak");
        UIManager.put("OptionPane.cancelButtonText", "Batal");
        UIManager.put("OptionPane.okButtonText", "OK");
    }

    public static Border inputBorder(Color line) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(line, 1, true),
                BorderFactory.createEmptyBorder(7, 10, 7, 10));
    }

    /** Memberi gaya input + efek fokus (garis biru). */
    public static void styleInput(JComponent c) {
        c.setFont(font(Font.PLAIN, 13));
        c.setBackground(Color.WHITE);
        c.setForeground(TEXT);
        c.setBorder(inputBorder(INPUT_LINE));
        c.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { c.setBorder(inputBorder(BLUE)); }
            @Override public void focusLost(FocusEvent e) { c.setBorder(inputBorder(INPUT_LINE)); }
        });
    }

    public static void styleCombo(JComboBox<?> c) {
        c.setFont(font(Font.PLAIN, 13));
        c.setBackground(Color.WHITE);
        c.setMaximumRowCount(10);
        c.setPreferredSize(new Dimension(380, 34));
    }

    public static JLabel label(String text, int style, float size, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font(style, size));
        l.setForeground(color);
        return l;
    }
}
