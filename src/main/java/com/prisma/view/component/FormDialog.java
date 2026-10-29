package com.prisma.view.component;

import com.prisma.util.DataAccessException;
import com.prisma.util.ValidationException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Dialog form input generik (pengganti modal pada preview.html).
 * Panel CRUD cukup menambahkan field lalu mengisi SaveAction; validasi dan error database
 * ditangani di sini sehingga dialog tetap terbuka bila input salah.
 */
public class FormDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    /** Aksi simpan; lempar ValidationException bila input tidak valid. */
    public interface SaveAction {
        void run() throws ValidationException;
    }

    private final JPanel fields = new JPanel(new GridBagLayout());
    private int row = 0;
    private boolean saved = false;
    private SaveAction action;

    public FormDialog(Window owner, String title) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        setUndecorated(true);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.setBorder(BorderFactory.createLineBorder(Theme.NAVY, 2));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.NAVY);
        header.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 12));
        header.add(Theme.label(title, Font.BOLD, 16, Color.WHITE), BorderLayout.WEST);
        CloseButton x = new CloseButton();
        x.setToolTipText("Tutup");
        x.addActionListener(e -> dispose());
        header.add(x, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        fields.setOpaque(false);
        fields.setBorder(BorderFactory.createEmptyBorder(18, 22, 8, 22));
        JScrollPane scroll = new JScrollPane(fields);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        root.add(scroll, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        footer.setOpaque(false);
        AnimatedButton cancel = new AnimatedButton("Batal", AnimatedButton.Kind.SECONDARY);
        AnimatedButton save = new AnimatedButton("Simpan Data", AnimatedButton.Kind.PRIMARY);
        cancel.addActionListener(e -> dispose());
        save.addActionListener(e -> doSave());
        footer.add(cancel);
        footer.add(save);
        root.add(footer, BorderLayout.SOUTH);

        setContentPane(root);
        getRootPane().setDefaultButton(save);
        getRootPane().registerKeyboardAction(e -> dispose(), KeyStroke.getKeyStroke("ESCAPE"),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    public void setSaveAction(SaveAction action) { this.action = action; }

    private void doSave() {
        if (action == null) return;
        try {
            action.run();
            saved = true;
            dispose();
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validasi Input", JOptionPane.WARNING_MESSAGE);
        } catch (DataAccessException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Kesalahan Database", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Menampilkan dialog (modal). @return true bila data berhasil disimpan. */
    public boolean showDialog() {
        pack();
        setSize(Math.max(getWidth(), 600), Math.min(getHeight() + 10, 680));
        setLocationRelativeTo(getOwner());
        fadeIn();
        setVisible(true);
        return saved;
    }

    private void fadeIn() {
        try {
            if (!GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
                    .isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.TRANSLUCENT)) return;
            setOpacity(0f);
            final float[] a = {0f};
            Timer t = new Timer(16, null);
            t.addActionListener((ActionEvent e) -> {
                a[0] = Math.min(1f, a[0] + 0.14f);
                try { setOpacity(a[0]); } catch (RuntimeException ex) { t.stop(); }
                if (a[0] >= 1f) t.stop();
            });
            t.start();
        } catch (RuntimeException ignored) {
            // tanpa animasi bila tidak didukung
        }
    }

    // --------------------------------------------------------- penambah field

    public JLabel addRow(String label, JComponent comp) {
        GridBagConstraints lc = new GridBagConstraints();
        lc.gridx = 0; lc.gridy = row; lc.anchor = GridBagConstraints.NORTHWEST;
        lc.insets = new Insets(11, 0, 8, 16);
        JLabel l = Theme.label(label, Font.BOLD, 13, Theme.TEXT);
        fields.add(l, lc);

        GridBagConstraints cc = new GridBagConstraints();
        cc.gridx = 1; cc.gridy = row; cc.weightx = 1; cc.fill = GridBagConstraints.HORIZONTAL;
        cc.anchor = GridBagConstraints.NORTHWEST;
        cc.insets = new Insets(6, 0, 6, 0);
        fields.add(comp, cc);
        row++;
        return l;
    }

    public JTextField addText(String label, String value) {
        JTextField f = new JTextField(value == null ? "" : value, 28);
        Theme.styleInput(f);
        addRow(label, f);
        return f;
    }

    public JPasswordField addPassword(String label, String value) {
        JPasswordField f = new JPasswordField(value == null ? "" : value, 28);
        Theme.styleInput(f);
        addRow(label, f);
        return f;
    }

    public JTextArea addTextArea(String label, String value, int rows) {
        JTextArea a = new JTextArea(value == null ? "" : value, rows, 28);
        a.setLineWrap(true);
        a.setWrapStyleWord(true);
        Theme.styleInput(a);
        JScrollPane sp = new JScrollPane(a);
        sp.setBorder(null);
        addRow(label, sp);
        return a;
    }

    public JComboBox<String> addCombo(String label, String[] options, String selected) {
        JComboBox<String> c = new JComboBox<>(options);
        if (selected != null) c.setSelectedItem(selected);
        Theme.styleCombo(c);
        addRow(label, c);
        return c;
    }

    /**
     * ComboBox berisi entity dari database. Bila emptyLabel != null, item pertama adalah pilihan kosong
     * (relasi opsional). Gunakan {@link #pick} untuk mengambil pilihan.
     */
    public JComboBox<Object> addEntityCombo(String label, List<?> items, Object selected, String emptyLabel) {
        JComboBox<Object> c = new JComboBox<>();
        if (emptyLabel != null) c.addItem(emptyLabel);
        for (Object o : items) c.addItem(o);
        if (selected != null) c.setSelectedItem(selected);
        else if (c.getItemCount() > 0) c.setSelectedIndex(0);
        Theme.styleCombo(c);
        addRow(label, c);
        return c;
    }

    public DateTimeField addDateTime(String label, DateTimeField field) {
        addRow(label, field);
        return field;
    }

    /** Mengambil entity terpilih; null bila yang dipilih adalah item kosong. */
    public static <E> E pick(JComboBox<Object> combo, Class<E> type) {
        Object o = combo.getSelectedItem();
        return type.isInstance(o) ? type.cast(o) : null;
    }
}
