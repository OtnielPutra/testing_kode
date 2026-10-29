package com.prisma.view;

import com.prisma.config.AppSession;
import com.prisma.controller.BaseController;
import com.prisma.controller.LampiranController;
import com.prisma.model.Konten;
import com.prisma.model.LampiranKonten;
import com.prisma.model.PetugasKonten;
import com.prisma.util.Fmt;
import com.prisma.util.ValidationException;
import com.prisma.view.component.AnimatedButton;
import com.prisma.view.component.FormDialog;
import com.prisma.view.component.Theme;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.time.LocalDateTime;

public class LampiranPanel extends CrudPanel<LampiranKonten> {
    private static final long serialVersionUID = 1L;

    private final LampiranController controller = new LampiranController();

    public LampiranPanel(boolean editable) {
        super("Lampiran Konten", "Daftar file pendukung yang terhubung ke konten.", "Lampiran", editable);
        init();
    }

    @Override protected BaseController<LampiranKonten, ?> controller() { return controller; }
    @Override protected String[] columnNames() {
        return new String[] {"ID", "Konten", "Nama File", "Lokasi File", "Tanggal Unggah"};
    }
    @Override protected int[] columnWidths() { return new int[] {45, 330, 300, 300, 130}; }

    @Override
    protected Object cellValue(LampiranKonten l, int c) {
        switch (c) {
            case 0: return l.getIdLampiran();
            case 1: return l.getKonten().getJudulKonten();
            case 2: return l.getNamaFile();
            case 3: return l.getLokasiFile();
            default: return Fmt.dateTime(l.getTanggalUnggah());
        }
    }

    @Override
    protected boolean showForm(LampiranKonten existing) {
        final LampiranKonten l = existing == null ? new LampiranKonten() : existing;
        FormDialog d = new FormDialog(owner(), existing == null ? "Tambah Lampiran" : "Ubah Lampiran");
        JComboBox<Object> konten = d.addEntityCombo("Konten *", controller.daftarKonten(), l.getKonten(), null);
        JTextField nama = d.addText("Nama File *", l.getNamaFile());
        JTextField lokasi = d.addText("Lokasi File *", l.getLokasiFile());

        AnimatedButton pilih = new AnimatedButton("Pilih File dari Komputer...", AnimatedButton.Kind.SECONDARY);
        pilih.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(d) == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                try {
                    lokasi.setText(controller.simpanFile(f));
                    nama.setText(f.getName());
                } catch (ValidationException ex) {
                    JOptionPane.showMessageDialog(d, ex.getMessage(), "Kesalahan", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setOpaque(false);
        row.add(pilih);
        d.addRow(" ", row);

        d.setSaveAction(() -> {
            l.setKonten(FormDialog.pick(konten, Konten.class));
            l.setNamaFile(nama.getText());
            l.setLokasiFile(lokasi.getText());
            if (existing == null) l.setTanggalUnggah(LocalDateTime.now());
            if (l.getPetugas() == null) l.setPetugas(AppSession.requireUser(PetugasKonten.class));
            controller.save(l);
        });
        return d.showDialog();
    }
}
