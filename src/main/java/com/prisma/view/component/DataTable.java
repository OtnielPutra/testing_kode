package com.prisma.view.component;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** JTable bergaya modern: header abu-abu, baris menyorot saat kursor lewat, kolom status berbentuk pill. */
public class DataTable extends JTable {
    private static final long serialVersionUID = 1L;

    private int hoverRow = -1;

    public DataTable(TableModel model) {
        super(model);
        setRowHeight(36);
        setShowVerticalLines(false);
        setShowHorizontalLines(true);
        setGridColor(Theme.LINE);
        setIntercellSpacing(new Dimension(0, 1));
        setFont(Theme.font(Font.PLAIN, 13));
        setForeground(Theme.TEXT);
        setSelectionBackground(Theme.BLUE_SOFT);
        setSelectionForeground(Theme.TEXT);
        setFillsViewportHeight(true);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setAutoCreateRowSorter(true);

        CellRenderer renderer = new CellRenderer();
        setDefaultRenderer(Object.class, renderer);
        setDefaultRenderer(Number.class, renderer);

        JTableHeader header = getTableHeader();
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 38));
        header.setDefaultRenderer(new HeaderRenderer());

        MouseAdapter hover = new MouseAdapter() {
            @Override public void mouseMoved(MouseEvent e) {
                int r = rowAtPoint(e.getPoint());
                if (r != hoverRow) { hoverRow = r; repaint(); }
            }
            @Override public void mouseExited(MouseEvent e) { hoverRow = -1; repaint(); }
        };
        addMouseMotionListener(hover);
        addMouseListener(hover);
    }

    /** Menampilkan kolom tertentu sebagai pill berwarna (Status). */
    public void setStatusColumn(int column) {
        if (column >= 0 && column < getColumnCount()) {
            getColumnModel().getColumn(column).setCellRenderer(new PillRenderer());
        }
    }

    public void setColumnWidths(int... widths) {
        for (int i = 0; i < widths.length && i < getColumnCount(); i++) {
            getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    @Override
    public Component prepareRenderer(TableCellRenderer r, int row, int col) {
        Component c = super.prepareRenderer(r, row, col);
        Color bg = isRowSelected(row) ? Theme.BLUE_SOFT : (row == hoverRow ? Theme.HOVER_ROW : Color.WHITE);
        c.setBackground(bg);
        c.setForeground(Theme.TEXT);
        return c;
    }

    // ----------------------------------------------------------- renderers

    private static class CellRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean focus, int r, int c) {
            super.getTableCellRendererComponent(t, v, sel, false, r, c);
            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            setHorizontalAlignment(SwingConstants.LEFT);
            setToolTipText(v == null ? null : String.valueOf(v));
            return this;
        }
    }

    private static class HeaderRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean focus, int r, int c) {
            super.getTableCellRendererComponent(t, v, false, false, r, c);
            setOpaque(true);
            setBackground(new Color(0xf8, 0xfa, 0xfc));
            setForeground(new Color(0x47, 0x55, 0x69));
            setFont(Theme.font(Font.BOLD, 12));
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.LINE),
                    BorderFactory.createEmptyBorder(0, 10, 0, 10)));
            return this;
        }
    }

    private static class PillLabel extends JLabel {
        private static final long serialVersionUID = 1L;
        Color pillBg = Theme.LINE, pillFg = Theme.TEXT;

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(getBackground());
            g2.fillRect(0, 0, getWidth(), getHeight());
            String text = getText();
            if (text != null && !text.isBlank()) {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int pw = fm.stringWidth(text) + 22, ph = 22;
                int x = 10, y = (getHeight() - ph) / 2;
                g2.setColor(pillBg);
                g2.fillRoundRect(x, y, pw, ph, ph, ph);
                g2.setColor(pillFg);
                g2.drawString(text, x + 11, y + (ph + fm.getAscent() - fm.getDescent()) / 2);
            }
            g2.dispose();
        }
    }

    private static class PillRenderer implements TableCellRenderer {
        private final PillLabel label = new PillLabel();

        PillRenderer() {
            label.setOpaque(false);
            label.setFont(Theme.font(Font.BOLD, 11));
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean focus, int r, int c) {
            String s = v == null ? "" : String.valueOf(v);
            label.setText(s);
            switch (s) {
                case "aktif": case "Siap Tayang": case "Sudah Tayang": case "Terbit":
                    label.pillBg = new Color(0xdc, 0xfc, 0xe7); label.pillFg = new Color(0x16, 0x65, 0x34); break;
                case "Dalam Proses": case "Terjadwal": case "Direncanakan":
                    label.pillBg = new Color(0xfe, 0xf3, 0xc7); label.pillFg = new Color(0x92, 0x40, 0x0e); break;
                case "Berjalan": case "Selesai":
                    label.pillBg = Theme.BLUE_SOFT; label.pillFg = new Color(0x1d, 0x4e, 0xd8); break;
                case "Dibatalkan": case "nonaktif":
                    label.pillBg = Theme.DANGER_BG; label.pillFg = Theme.DANGER; break;
                default:
                    label.pillBg = Theme.LINE; label.pillFg = new Color(0x47, 0x55, 0x69);
            }
            return label;
        }
    }
}
