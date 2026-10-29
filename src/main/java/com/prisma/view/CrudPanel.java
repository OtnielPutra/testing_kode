package com.prisma.view;

import com.prisma.controller.BaseController;
import com.prisma.util.DataAccessException;
import com.prisma.util.ValidationException;
import com.prisma.view.component.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;

/**
 * ABSTRACTION (Template Method): kerangka halaman CRUD (judul, pencarian, tabel, tombol
 * Tambah/Ubah/Hapus). Subclass hanya mendefinisikan kolom, isi sel, dan form input.
 * Subclass WAJIB memanggil init() di akhir konstruktornya.
 */
public abstract class CrudPanel<T> extends JPanel implements Refreshable {
    private static final long serialVersionUID = 1L;

    private final String title;
    private final String subtitle;
    private final String noun;
    protected final boolean editable;

    private EntityTableModel<T> tableModel;
    protected DataTable table;
    protected JTextField searchField;
    private JLabel countLabel;
    protected final JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));

    protected CrudPanel(String title, String subtitle, String noun, boolean editable) {
        this.title = title;
        this.subtitle = subtitle;
        this.noun = noun;
        this.editable = editable;
        setLayout(new BorderLayout(0, 16));
        setOpaque(false);
    }

    // ------------------------------------------------ dikustomisasi subclass

    protected abstract BaseController<T, ?> controller();
    protected abstract String[] columnNames();
    protected abstract Object cellValue(T item, int column);
    /** Menampilkan form tambah (existing == null) atau ubah. @return true bila tersimpan. */
    protected abstract boolean showForm(T existing);

    protected int statusColumn() { return -1; }
    protected int[] columnWidths() { return null; }
    protected void addExtraActions(JPanel bar) { }

    protected JComponent createSearchControls() {
        searchField = new JTextField(24);
        Theme.styleInput(searchField);
        searchField.setToolTipText("Ketik kata kunci lalu tekan Enter");
        searchField.addActionListener(e -> refresh());
        return searchField;
    }

    protected List<T> querySearch() {
        return controller().search(searchField.getText().trim());
    }

    protected void resetSearch() { searchField.setText(""); }

    // ------------------------------------------------------------- bangunan UI

    protected final void init() {
        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        JPanel texts = new JPanel();
        texts.setOpaque(false);
        texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));
        JLabel h1 = Theme.label(title, Font.BOLD, 24, Theme.TEXT);
        JLabel sub = Theme.label(subtitle, Font.PLAIN, 14, Theme.MUTED);
        h1.setAlignmentX(LEFT_ALIGNMENT);
        sub.setAlignmentX(LEFT_ALIGNMENT);
        texts.add(h1);
        texts.add(Box.createVerticalStrut(6));
        texts.add(sub);
        head.add(texts, BorderLayout.WEST);
        if (editable) {
            AnimatedButton add = new AnimatedButton("+ Tambah " + noun, AnimatedButton.Kind.PRIMARY);
            add.addActionListener(e -> openForm(null));
            head.add(add, BorderLayout.EAST);
        }
        add(head, BorderLayout.NORTH);

        RoundedPanel card = new RoundedPanel();
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 12, 16));

        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);
        left.add(createSearchControls());
        AnimatedButton btnSearch = new AnimatedButton("Cari", AnimatedButton.Kind.PRIMARY);
        AnimatedButton btnReset = new AnimatedButton("Reset", AnimatedButton.Kind.SECONDARY);
        btnSearch.addActionListener(e -> refresh());
        btnReset.addActionListener(e -> { resetSearch(); refresh(); });
        left.add(btnSearch);
        left.add(btnReset);
        toolbar.add(left, BorderLayout.WEST);

        actionBar.setOpaque(false);
        if (editable) {
            AnimatedButton btnEdit = new AnimatedButton("Ubah", AnimatedButton.Kind.SECONDARY);
            AnimatedButton btnDelete = new AnimatedButton("Hapus", AnimatedButton.Kind.DANGER);
            btnEdit.addActionListener(e -> editSelected());
            btnDelete.addActionListener(e -> deleteSelected());
            actionBar.add(btnEdit);
            addExtraActions(actionBar);
            actionBar.add(btnDelete);
        }
        toolbar.add(actionBar, BorderLayout.EAST);
        card.add(toolbar, BorderLayout.NORTH);

        tableModel = new EntityTableModel<>(columnNames(), this::cellValue);
        table = new DataTable(tableModel);
        table.setStatusColumn(statusColumn());
        if (columnWidths() != null) table.setColumnWidths(columnWidths());
        if (editable) {
            table.addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) editSelected();
                }
            });
        }
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.LINE));
        scroll.getViewport().setBackground(Color.WHITE);
        card.add(scroll, BorderLayout.CENTER);

        countLabel = Theme.label(" ", Font.PLAIN, 12, Theme.MUTED);
        card.add(countLabel, BorderLayout.SOUTH);
        add(card, BorderLayout.CENTER);

        refresh();
    }

    // -------------------------------------------------------------- aksi

    @Override
    public void refresh() {
        List<T> data;
        try {
            data = querySearch();
        } catch (DataAccessException ex) {
            showError(ex.getMessage());
            data = Collections.emptyList();
        }
        tableModel.setRows(data);
        countLabel.setText(data.size() + " data");
    }

    protected Window owner() { return SwingUtilities.getWindowAncestor(this); }

    protected T selectedItem() {
        int v = table.getSelectedRow();
        return v < 0 ? null : tableModel.getItem(table.convertRowIndexToModel(v));
    }

    protected void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Kesalahan", JOptionPane.ERROR_MESSAGE);
    }

    private void openForm(T existing) {
        boolean saved = showForm(existing);
        refresh();
        if (saved) Toast.show(this, "Data berhasil disimpan.");
    }

    private void editSelected() {
        T item = selectedItem();
        if (item == null) {
            JOptionPane.showMessageDialog(this, "Pilih satu baris data terlebih dahulu.",
                    "Informasi", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        openForm(item);
    }

    private void deleteSelected() {
        T item = selectedItem();
        if (item == null) {
            JOptionPane.showMessageDialog(this, "Pilih satu baris data terlebih dahulu.",
                    "Informasi", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus data ini?", "Konfirmasi Hapus",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ok != JOptionPane.YES_OPTION) return;
        try {
            controller().delete(item);
            refresh();
            Toast.show(this, "Data berhasil dihapus.");
        } catch (ValidationException | DataAccessException ex) {
            showError(ex.getMessage());
        }
    }
}
