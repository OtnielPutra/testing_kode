package com.prisma.view;

import com.prisma.config.AppSession;
import com.prisma.controller.BaseController;
import com.prisma.controller.JadwalController;
import com.prisma.model.JadwalTayang;
import com.prisma.model.Kanal;
import com.prisma.model.PetugasJadwal;
import com.prisma.model.ProgramTv;
import com.prisma.util.DataAccessException;
import com.prisma.util.Fmt;
import com.prisma.util.Options;
import com.prisma.util.ValidationException;
import com.prisma.view.component.AnimatedButton;
import com.prisma.view.component.DateTimeField;
import com.prisma.view.component.FormDialog;
import com.prisma.view.component.Theme;
import com.prisma.view.component.Toast;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class JadwalPanel extends CrudPanel<JadwalTayang> {
    private static final long serialVersionUID = 1L;

    private final JadwalController controller = new JadwalController();
    // kontrol pencarian: tanggal, waktu mulai, waktu selesai (boleh kosong 1 hingga 2)
    private DateTimeField cariTanggal, cariDari, cariSampai;

    public JadwalPanel(boolean editable) {
        super("Jadwal Tayang", "Atur tanggal dan waktu penayangan program.", "Jadwal", editable);
        init();
    }

    @Override protected BaseController<JadwalTayang, ?> controller() { return controller; }
    @Override protected String[] columnNames() {
        return new String[] {"ID", "Program TV", "Kanal", "Tanggal", "Waktu", "Petugas", "Status"};
    }
    @Override protected int statusColumn() { return 6; }
    @Override protected int[] columnWidths() { return new int[] {45, 220, 130, 100, 150, 150, 100}; }

    @Override
    protected Object cellValue(JadwalTayang j, int c) {
        switch (c) {
            case 0: return j.getIdJadwal();
            case 1: return j.getProgramTv().getNamaProgram();
            case 2: return j.getKanal().getNamaKanal();
            case 3: return Fmt.date(j.getTanggalTayang());
            case 4: return Fmt.time(j.getWaktuMulai()) + " - " + Fmt.time(j.getWaktuSelesai());
            case 5: return j.getPetugas().getNamaLengkap();
            default: return j.getStatusJadwal();
        }
    }

    // ---- pencarian khusus jadwal: Date Picker + 2 Time Picker ----

    @Override
    protected JComponent createSearchControls() {
        cariTanggal = new DateTimeField(DateTimeField.Mode.DATE, true);
        cariDari = new DateTimeField(DateTimeField.Mode.TIME, true);
        cariSampai = new DateTimeField(DateTimeField.Mode.TIME, true);
        resetSearch();
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        p.setOpaque(false);
        p.add(Theme.label("Tanggal", Font.BOLD, 12, Theme.MUTED));
        p.add(cariTanggal);
        p.add(Theme.label("Mulai", Font.BOLD, 12, Theme.MUTED));
        p.add(cariDari);
        p.add(Theme.label("Selesai", Font.BOLD, 12, Theme.MUTED));
        p.add(cariSampai);
        return p;
    }

    @Override
    protected List<JadwalTayang> querySearch() {
        return controller.cari(cariTanggal.getDate(), cariDari.getTime(), cariSampai.getTime());
    }

    @Override
    protected void resetSearch() {
        cariTanggal.setEmptyValue(true);
        cariDari.setEmptyValue(true);
        cariSampai.setEmptyValue(true);
    }

    // ---- tombol tambahan: Nonaktifkan Jadwal ----

    @Override
    protected void addExtraActions(JPanel bar) {
        AnimatedButton nonaktif = new AnimatedButton("Nonaktifkan", AnimatedButton.Kind.SECONDARY);
        nonaktif.addActionListener(e -> {
            JadwalTayang j = selectedItem();
            if (j == null) {
                JOptionPane.showMessageDialog(this, "Pilih satu jadwal terlebih dahulu.",
                        "Informasi", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            int ok = JOptionPane.showConfirmDialog(this, "Nonaktifkan (batalkan) jadwal ini?", "Konfirmasi",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (ok != JOptionPane.YES_OPTION) return;
            try {
                controller.nonaktifkan(j);
                refresh();
                Toast.show(this, "Nonaktif jadwal berhasil dilakukan.");
            } catch (ValidationException | DataAccessException ex) {
                refresh();
                showError(ex.getMessage());
            }
        });
        bar.add(nonaktif);
    }

    @Override
    protected boolean showForm(JadwalTayang existing) {
        final JadwalTayang j = existing == null ? new JadwalTayang() : existing;
        FormDialog d = new FormDialog(owner(), existing == null ? "Tambah Jadwal Tayang" : "Ubah Jadwal Tayang");
        JComboBox<Object> program = d.addEntityCombo("Program TV *", controller.daftarProgram(), j.getProgramTv(), null);
        JComboBox<Object> kanal = d.addEntityCombo("Kanal *", controller.daftarKanal(), j.getKanal(), null);
        DateTimeField tanggal = d.addDateTime("Tanggal Tayang *", new DateTimeField(DateTimeField.Mode.DATE, false));
        tanggal.setDate(j.getTanggalTayang());
        DateTimeField mulai = d.addDateTime("Waktu Mulai *", new DateTimeField(DateTimeField.Mode.TIME, false));
        mulai.setTime(j.getWaktuMulai() == null ? java.time.LocalTime.of(8, 0) : j.getWaktuMulai());
        DateTimeField selesai = d.addDateTime("Waktu Selesai *", new DateTimeField(DateTimeField.Mode.TIME, false));
        selesai.setTime(j.getWaktuSelesai() == null ? java.time.LocalTime.of(9, 0) : j.getWaktuSelesai());
        JComboBox<String> status = d.addCombo("Status Jadwal *", Options.STATUS_JADWAL, j.getStatusJadwal());
        d.setSaveAction(() -> {
            j.setProgramTv(FormDialog.pick(program, ProgramTv.class));
            j.setKanal(FormDialog.pick(kanal, Kanal.class));
            j.setTanggalTayang(tanggal.getDate());
            j.setWaktuMulai(mulai.getTime());
            j.setWaktuSelesai(selesai.getTime());
            j.setStatusJadwal((String) status.getSelectedItem());
            if (j.getPetugas() == null) j.setPetugas(AppSession.requireUser(PetugasJadwal.class));
            controller.save(j);
        });
        return d.showDialog();
    }
}
