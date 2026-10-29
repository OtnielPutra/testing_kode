package com.prisma.view;

import com.prisma.config.AppSession;
import com.prisma.controller.BaseController;
import com.prisma.controller.KontenController;
import com.prisma.model.Konten;
import com.prisma.model.LampiranKonten;
import com.prisma.model.PetugasKonten;
import com.prisma.model.ProgramTv;
import com.prisma.util.Fmt;
import com.prisma.util.Options;
import com.prisma.view.component.AnimatedButton;
import com.prisma.view.component.DateTimeField;
import com.prisma.view.component.FormDialog;
import com.prisma.view.component.Theme;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class KontenPanel extends CrudPanel<Konten> {
    private static final long serialVersionUID = 1L;

    private final KontenController controller = new KontenController();

    public KontenPanel(boolean editable) {
        super("Manajemen Konten", "Kelola konten dan hubungkan dengan program TV.", "Konten", editable);
        init();
    }

    @Override protected BaseController<Konten, ?> controller() { return controller; }
    @Override protected String[] columnNames() {
        return new String[] {"ID", "Judul Konten", "Jenis", "Kategori", "Program TV", "Tgl Catat", "Status"};
    }
    @Override protected int statusColumn() { return 6; }
    @Override protected int[] columnWidths() { return new int[] {45, 340, 90, 130, 150, 100, 110}; }

    @Override
    protected Object cellValue(Konten k, int c) {
        switch (c) {
            case 0: return k.getIdKonten();
            case 1: return k.getJudulKonten();
            case 2: return k.getJenisKonten();
            case 3: return k.getKategori();
            case 4: return k.getProgramTv() == null ? "-" : k.getProgramTv().getNamaProgram();
            case 5: return Fmt.date(k.getTanggalPencatatan());
            default: return k.getStatusKonten();
        }
    }

    @Override
    protected boolean showForm(Konten existing) {
        final Konten k = existing == null ? new Konten() : existing;
        FormDialog d = new FormDialog(owner(), existing == null ? "Tambah Konten" : "Ubah Konten");
        JTextField judul = d.addText("Judul Konten *", k.getJudulKonten());
        JComboBox<String> jenis = d.addCombo("Jenis Konten *", Options.JENIS_KONTEN, k.getJenisKonten());
        JTextField kategori = d.addText("Kategori *", k.getKategori());
        JTextArea ringkasan = d.addTextArea("Ringkasan Konten *", k.getRingkasanKonten(), 3);
        DateTimeField tanggal = d.addDateTime("Tanggal Pencatatan *", new DateTimeField(DateTimeField.Mode.DATE, false));
        tanggal.setDate(k.getTanggalPencatatan());
        JComboBox<Object> program = d.addEntityCombo("Program Terkait", controller.daftarProgram(),
                k.getProgramTv(), "-- Tidak ada --");
        JComboBox<String> status = d.addCombo("Status Konten *", Options.STATUS_KONTEN, k.getStatusKonten());

        // lampiran: tampilkan lampiran lama, boleh diganti dengan memilih file baru
        LampiranKonten lama = existing == null ? null : controller.lampiranDari(existing);
        JTextField fileLabel = new JTextField(lama == null ? "(belum ada lampiran)" : lama.getNamaFile(), 20);
        fileLabel.setEditable(false);
        Theme.styleInput(fileLabel);
        final File[] pilihan = {null};
        AnimatedButton pilih = new AnimatedButton(lama == null ? "Pilih File..." : "Ganti Lampiran...",
                AnimatedButton.Kind.SECONDARY);
        pilih.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(d) == JFileChooser.APPROVE_OPTION) {
                pilihan[0] = fc.getSelectedFile();
                fileLabel.setText(pilihan[0].getName());
            }
        });
        JPanel fileRow = new JPanel(new BorderLayout(8, 0));
        fileRow.setOpaque(false);
        fileRow.add(fileLabel, BorderLayout.CENTER);
        fileRow.add(pilih, BorderLayout.EAST);
        d.addRow("Lampiran", fileRow);

        d.setSaveAction(() -> {
            k.setJudulKonten(judul.getText());
            k.setJenisKonten((String) jenis.getSelectedItem());
            k.setKategori(kategori.getText());
            k.setRingkasanKonten(ringkasan.getText());
            k.setTanggalPencatatan(tanggal.getDate());
            k.setProgramTv(FormDialog.pick(program, ProgramTv.class));
            k.setStatusKonten((String) status.getSelectedItem());
            if (k.getPetugas() == null) k.setPetugas(AppSession.requireUser(PetugasKonten.class));
            controller.saveWithLampiran(k, pilihan[0]);
        });
        return d.showDialog();
    }
}
