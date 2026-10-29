package com.prisma.view;

import com.prisma.config.AppSession;
import com.prisma.controller.BaseController;
import com.prisma.controller.PublikasiController;
import com.prisma.model.Kanal;
import com.prisma.model.Konten;
import com.prisma.model.PetugasKonten;
import com.prisma.model.PublikasiKonten;
import com.prisma.util.Fmt;
import com.prisma.util.Options;
import com.prisma.view.component.DateTimeField;
import com.prisma.view.component.FormDialog;

import javax.swing.*;

public class PublikasiPanel extends CrudPanel<PublikasiKonten> {
    private static final long serialVersionUID = 1L;

    private final PublikasiController controller = new PublikasiController();

    public PublikasiPanel(boolean editable) {
        super("Publikasi Konten", "Catat publikasi konten melalui kanal yang tersedia.", "Publikasi", editable);
        init();
    }

    @Override protected BaseController<PublikasiKonten, ?> controller() { return controller; }
    @Override protected String[] columnNames() {
        return new String[] {"ID", "Konten", "Kanal", "Tanggal", "Link", "Status"};
    }
    @Override protected int statusColumn() { return 5; }
    @Override protected int[] columnWidths() { return new int[] {45, 330, 130, 130, 280, 110}; }

    @Override
    protected Object cellValue(PublikasiKonten p, int c) {
        switch (c) {
            case 0: return p.getIdPublikasi();
            case 1: return p.getKonten().getJudulKonten();
            case 2: return p.getKanal().getNamaKanal();
            case 3: return Fmt.dateTime(p.getTanggalPublikasi());
            case 4: return Fmt.dash(p.getLinkPublikasi());
            default: return p.getStatusPublikasi();
        }
    }

    @Override
    protected boolean showForm(PublikasiKonten existing) {
        final PublikasiKonten p = existing == null ? new PublikasiKonten() : existing;
        FormDialog d = new FormDialog(owner(), existing == null ? "Tambah Publikasi Konten" : "Ubah Publikasi Konten");
        JComboBox<Object> konten = d.addEntityCombo("Konten *", controller.daftarKonten(), p.getKonten(), null);
        JComboBox<Object> kanal = d.addEntityCombo("Kanal Publikasi *", controller.daftarKanal(), p.getKanal(), null);
        DateTimeField tanggal = d.addDateTime("Tanggal Publikasi *", new DateTimeField(DateTimeField.Mode.DATETIME, false));
        tanggal.setDateTime(p.getTanggalPublikasi());
        JComboBox<String> status = d.addCombo("Status Publikasi *", Options.STATUS_PUBLIKASI, p.getStatusPublikasi());
        JTextField link = d.addText("Link Publikasi", p.getLinkPublikasi());
        d.setSaveAction(() -> {
            p.setKonten(FormDialog.pick(konten, Konten.class));
            p.setKanal(FormDialog.pick(kanal, Kanal.class));
            p.setTanggalPublikasi(tanggal.getDateTime());
            p.setStatusPublikasi((String) status.getSelectedItem());
            p.setLinkPublikasi(link.getText());
            if (p.getPetugas() == null) p.setPetugas(AppSession.requireUser(PetugasKonten.class));
            controller.save(p);
        });
        return d.showDialog();
    }
}
