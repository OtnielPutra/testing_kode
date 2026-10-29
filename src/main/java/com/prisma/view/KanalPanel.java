package com.prisma.view;

import com.prisma.controller.BaseController;
import com.prisma.controller.KanalController;
import com.prisma.model.Kanal;
import com.prisma.util.Options;
import com.prisma.view.component.FormDialog;

import javax.swing.*;

public class KanalPanel extends CrudPanel<Kanal> {
    private static final long serialVersionUID = 1L;

    private final KanalController controller = new KanalController();

    public KanalPanel(boolean editable) {
        super("Data Kanal", "Kanal untuk penayangan dan publikasi konten.", "Kanal", editable);
        init();
    }

    @Override protected BaseController<Kanal, ?> controller() { return controller; }
    @Override protected String[] columnNames() { return new String[] {"ID", "Nama Kanal", "Jenis", "Platform", "Status"}; }
    @Override protected int statusColumn() { return 4; }
    @Override protected int[] columnWidths() { return new int[] {50, 200, 120, 200, 90}; }

    @Override
    protected Object cellValue(Kanal k, int c) {
        switch (c) {
            case 0: return k.getIdKanal();
            case 1: return k.getNamaKanal();
            case 2: return k.getJenisKanal();
            case 3: return k.getPlatform();
            default: return k.getStatusKanal();
        }
    }

    @Override
    protected boolean showForm(Kanal existing) {
        final Kanal k = existing == null ? new Kanal() : existing;
        FormDialog d = new FormDialog(owner(), existing == null ? "Tambah Kanal" : "Ubah Kanal");
        JTextField nama = d.addText("Nama Kanal *", k.getNamaKanal());
        JComboBox<String> jenis = d.addCombo("Jenis Kanal *", Options.JENIS_KANAL, k.getJenisKanal());
        JTextField platform = d.addText("Platform *", k.getPlatform());
        JComboBox<String> status = d.addCombo("Status Kanal *", Options.STATUS_AKTIF, k.getStatusKanal());
        d.setSaveAction(() -> {
            k.setNamaKanal(nama.getText());
            k.setJenisKanal((String) jenis.getSelectedItem());
            k.setPlatform(platform.getText());
            k.setStatusKanal((String) status.getSelectedItem());
            controller.save(k);
        });
        return d.showDialog();
    }
}
