package com.prisma.view;

import com.prisma.config.AppSession;
import com.prisma.controller.BaseController;
import com.prisma.controller.ProgramTvController;
import com.prisma.model.Admin;
import com.prisma.model.ProgramTv;
import com.prisma.util.Fmt;
import com.prisma.util.Options;
import com.prisma.view.component.FormDialog;

import javax.swing.*;

public class ProgramTvPanel extends CrudPanel<ProgramTv> {
    private static final long serialVersionUID = 1L;

    private final ProgramTvController controller = new ProgramTvController();

    public ProgramTvPanel(boolean editable) {
        super("Program TV", "Daftar program televisi yang dikelola.", "Program", editable);
        init();
    }

    @Override protected BaseController<ProgramTv, ?> controller() { return controller; }
    @Override protected String[] columnNames() { return new String[] {"ID", "Nama Program", "Kategori", "URL", "Status"}; }
    @Override protected int statusColumn() { return 4; }
    @Override protected int[] columnWidths() { return new int[] {50, 200, 130, 280, 90}; }

    @Override
    protected Object cellValue(ProgramTv p, int c) {
        switch (c) {
            case 0: return p.getIdProgram();
            case 1: return p.getNamaProgram();
            case 2: return p.getKategoriProgram();
            case 3: return Fmt.dash(p.getUrlProgram());
            default: return p.getStatusProgram();
        }
    }

    @Override
    protected boolean showForm(ProgramTv existing) {
        final ProgramTv p = existing == null ? new ProgramTv() : existing;
        FormDialog d = new FormDialog(owner(), existing == null ? "Tambah Program TV" : "Ubah Program TV");
        JTextField nama = d.addText("Nama Program *", p.getNamaProgram());
        JComboBox<String> kategori = d.addCombo("Kategori Program *", Options.KATEGORI_PROGRAM, p.getKategoriProgram());
        JTextField url = d.addText("URL Program", p.getUrlProgram());
        JComboBox<String> status = d.addCombo("Status *", Options.STATUS_AKTIF, p.getStatusProgram());
        d.setSaveAction(() -> {
            p.setNamaProgram(nama.getText());
            p.setKategoriProgram((String) kategori.getSelectedItem());
            p.setUrlProgram(url.getText());
            p.setStatusProgram((String) status.getSelectedItem());
            if (p.getAdmin() == null) p.setAdmin(AppSession.requireUser(Admin.class));
            controller.save(p);
        });
        return d.showDialog();
    }
}
