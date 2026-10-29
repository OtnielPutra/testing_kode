package com.prisma.view;

import com.prisma.config.AppSession;
import com.prisma.controller.BaseController;
import com.prisma.controller.KerjaSamaController;
import com.prisma.model.Admin;
import com.prisma.model.KerjaSama;
import com.prisma.model.Konten;
import com.prisma.model.Mitra;
import com.prisma.model.ProgramTv;
import com.prisma.util.Fmt;
import com.prisma.util.Options;
import com.prisma.util.Validator;
import com.prisma.view.component.DateTimeField;
import com.prisma.view.component.FormDialog;

import javax.swing.*;

public class KerjaSamaPanel extends CrudPanel<KerjaSama> {
    private static final long serialVersionUID = 1L;

    private final KerjaSamaController controller = new KerjaSamaController();

    public KerjaSamaPanel(boolean editable) {
        super("Kerja Sama", "Kelola kontrak yang terhubung dengan mitra.", "Kerja Sama", editable);
        init();
    }

    @Override protected BaseController<KerjaSama, ?> controller() { return controller; }
    @Override protected String[] columnNames() {
        return new String[] {"ID", "Judul Kerja Sama", "Mitra", "Jenis", "Periode", "Nilai Kontrak", "Program TV", "Status"};
    }
    @Override protected int statusColumn() { return 7; }
    @Override protected int[] columnWidths() { return new int[] {45, 250, 200, 140, 190, 130, 150, 100}; }

    @Override
    protected Object cellValue(KerjaSama k, int c) {
        switch (c) {
            case 0: return k.getIdKerjaSama();
            case 1: return k.getJudulKerjaSama();
            case 2: return k.getMitra().getNamaMitra();
            case 3: return k.getJenisKerjaSama();
            case 4: return Fmt.date(k.getTanggalMulai()) + " s/d " + (k.getTanggalAkhir() == null ? "-" : Fmt.date(k.getTanggalAkhir()));
            case 5: return Fmt.rupiah(k.getNilaiKontrak());
            case 6: return k.getProgramTv() == null ? "-" : k.getProgramTv().getNamaProgram();
            default: return k.getStatusKerjaSama();
        }
    }

    @Override
    protected boolean showForm(KerjaSama existing) {
        final KerjaSama k = existing == null ? new KerjaSama() : existing;
        FormDialog d = new FormDialog(owner(), existing == null ? "Tambah Kerja Sama" : "Ubah Kerja Sama");
        JTextField judul = d.addText("Judul Kerja Sama *", k.getJudulKerjaSama());
        JTextField jenis = d.addText("Jenis Kerja Sama *", k.getJenisKerjaSama());
        JComboBox<Object> mitra = d.addEntityCombo("Mitra Terkait *", controller.daftarMitra(), k.getMitra(), null);
        DateTimeField mulai = d.addDateTime("Tanggal Mulai *", new DateTimeField(DateTimeField.Mode.DATE, false));
        mulai.setDate(k.getTanggalMulai());
        DateTimeField akhir = d.addDateTime("Tanggal Akhir", new DateTimeField(DateTimeField.Mode.DATE, true));
        akhir.setDate(k.getTanggalAkhir());
        JComboBox<String> status = d.addCombo("Status Kerja Sama *", Options.STATUS_KERJASAMA, k.getStatusKerjaSama());
        JTextField nilai = d.addText("Nilai Kontrak (Rp)",
                k.getNilaiKontrak() == null ? "" : k.getNilaiKontrak().toPlainString());
        JComboBox<Object> program = d.addEntityCombo("Program TV Terkait", controller.daftarProgram(),
                k.getProgramTv(), "-- Tidak ada --");
        JComboBox<Object> konten = d.addEntityCombo("Konten Terkait", controller.daftarKonten(),
                k.getKonten(), "-- Tidak ada --");
        d.setSaveAction(() -> {
            k.setJudulKerjaSama(judul.getText());
            k.setJenisKerjaSama(jenis.getText());
            k.setMitra(FormDialog.pick(mitra, Mitra.class));
            k.setTanggalMulai(mulai.getDate());
            k.setTanggalAkhir(akhir.getDate());
            k.setStatusKerjaSama((String) status.getSelectedItem());
            k.setNilaiKontrak(Validator.money(nilai.getText(), "Nilai kontrak"));
            k.setProgramTv(FormDialog.pick(program, ProgramTv.class));
            k.setKonten(FormDialog.pick(konten, Konten.class));
            if (k.getAdmin() == null) k.setAdmin(AppSession.requireUser(Admin.class));
            controller.save(k);
        });
        return d.showDialog();
    }
}
