package com.prisma.view;

import com.prisma.controller.BaseController;
import com.prisma.controller.MitraController;
import com.prisma.model.Mitra;
import com.prisma.util.Fmt;
import com.prisma.util.Options;
import com.prisma.view.component.FormDialog;

import javax.swing.*;

public class MitraPanel extends CrudPanel<Mitra> {
    private static final long serialVersionUID = 1L;

    private final MitraController controller = new MitraController();

    public MitraPanel(boolean editable) {
        super("Data Mitra", "Kelola data perusahaan atau pihak yang bekerja sama.", "Mitra", editable);
        init();
    }

    @Override protected BaseController<Mitra, ?> controller() { return controller; }
    @Override protected String[] columnNames() {
        return new String[] {"ID Mitra", "Nama Mitra", "Jenis", "Kontak", "Telepon", "Email", "Alamat", "Status"};
    }
    @Override protected int statusColumn() { return 7; }
    @Override protected int[] columnWidths() { return new int[] {60, 220, 170, 120, 110, 220, 300, 80}; }

    @Override
    protected Object cellValue(Mitra m, int c) {
        switch (c) {
            case 0: return m.getIdMitra();
            case 1: return m.getNamaMitra();
            case 2: return m.getJenisMitra();
            case 3: return m.getNamaKontak();
            case 4: return Fmt.dash(m.getNomorTelepon());
            case 5: return Fmt.dash(m.getEmail());
            case 6: return Fmt.dash(m.getAlamat());
            default: return m.getStatusMitra();
        }
    }

    @Override
    protected boolean showForm(Mitra existing) {
        final Mitra m = existing == null ? new Mitra() : existing;
        FormDialog d = new FormDialog(owner(), existing == null ? "Tambah Mitra" : "Ubah Mitra");
        JTextField nama = d.addText("Nama Mitra *", m.getNamaMitra());
        JTextField jenis = d.addText("Jenis Mitra *", m.getJenisMitra());
        JTextField kontak = d.addText("Nama Kontak *", m.getNamaKontak());
        JTextField email = d.addText("Email", m.getEmail());
        JTextField telp = d.addText("Nomor Telepon", m.getNomorTelepon());
        JTextArea alamat = d.addTextArea("Alamat", m.getAlamat(), 3);
        JComboBox<String> status = d.addCombo("Status *", Options.STATUS_AKTIF, m.getStatusMitra());
        d.setSaveAction(() -> {
            m.setNamaMitra(nama.getText());
            m.setJenisMitra(jenis.getText());
            m.setNamaKontak(kontak.getText());
            m.setEmail(email.getText());
            m.setNomorTelepon(telp.getText());
            m.setAlamat(alamat.getText());
            m.setStatusMitra((String) status.getSelectedItem());
            controller.save(m);
        });
        return d.showDialog();
    }
}
