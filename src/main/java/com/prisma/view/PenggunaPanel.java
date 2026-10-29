package com.prisma.view;

import com.prisma.controller.BaseController;
import com.prisma.controller.PenggunaController;
import com.prisma.model.Admin;
import com.prisma.model.Pengguna;
import com.prisma.model.PetugasJadwal;
import com.prisma.model.PetugasKonten;
import com.prisma.util.Fmt;
import com.prisma.util.Options;
import com.prisma.view.component.DateTimeField;
import com.prisma.view.component.FormDialog;

import javax.swing.*;

public class PenggunaPanel extends CrudPanel<Pengguna> {
    private static final long serialVersionUID = 1L;

    private final PenggunaController controller = new PenggunaController();

    public PenggunaPanel(boolean editable) {
        super("Akun Pengguna", "Data pengguna dan peran dalam sistem.", "Akun", editable);
        init();
    }

    @Override protected BaseController<Pengguna, ?> controller() { return controller; }
    @Override protected String[] columnNames() {
        return new String[] {"ID", "Nama Lengkap", "Username", "Peran", "Detail Penugasan", "Tgl Penugasan", "Status"};
    }
    @Override protected int statusColumn() { return 6; }
    @Override protected int[] columnWidths() { return new int[] {45, 180, 140, 120, 220, 110, 90}; }

    @Override
    protected Object cellValue(Pengguna p, int c) {
        switch (c) {
            case 0: return p.getIdPengguna();
            case 1: return p.getNamaLengkap();
            case 2: return p.getUsername();
            case 3: return p.getRole();            // polymorphism: tiap subclass punya peran sendiri
            case 4: return Fmt.dash(p.getDetail());
            case 5: return Fmt.date(p.getTanggalPenugasan());
            default: return p.getStatusAkun();
        }
    }

    private static String labelDetail(String role) {
        if (Options.ROLE_JADWAL.equals(role)) return new PetugasJadwal().getDetailLabel();
        if (Options.ROLE_KONTEN.equals(role)) return new PetugasKonten().getDetailLabel();
        return new Admin().getDetailLabel();
    }

    @Override
    protected boolean showForm(Pengguna existing) {
        final boolean baru = existing == null;
        FormDialog d = new FormDialog(owner(), baru ? "Tambah Akun" : "Ubah Akun");
        JTextField nama = d.addText("Nama Lengkap *", baru ? "" : existing.getNamaLengkap());
        JTextField username = d.addText("Username *", baru ? "" : existing.getUsername());
        JPasswordField password = d.addPassword("Password *", baru ? "" : existing.getPassword());
        JComboBox<String> role = d.addCombo("Role *", Options.ROLES, baru ? Options.ROLE_KONTEN : existing.getRole());
        JTextField detail = new JTextField(28);
        detail.setText(baru ? "" : (existing.getDetail() == null ? "" : existing.getDetail()));
        com.prisma.view.component.Theme.styleInput(detail);
        JLabel detailLabel = d.addRow(labelDetail((String) role.getSelectedItem()) + " *", detail);
        DateTimeField tanggal = d.addDateTime("Tanggal Penugasan *", new DateTimeField(DateTimeField.Mode.DATE, false));
        tanggal.setDate(baru ? java.time.LocalDate.now() : existing.getTanggalPenugasan());
        JComboBox<String> status = d.addCombo("Status Akun *", Options.STATUS_AKTIF, baru ? "aktif" : existing.getStatusAkun());

        // detail penugasan hanya relevan untuk petugas; labelnya mengikuti peran yang dipilih
        Runnable sinkron = () -> {
            String r = (String) role.getSelectedItem();
            boolean admin = Options.ROLE_ADMIN.equals(r);
            detail.setEnabled(!admin);
            detailLabel.setText(admin ? "Detail Penugasan" : labelDetail(r) + " *");
        };
        role.addActionListener(e -> sinkron.run());
        sinkron.run();

        d.setSaveAction(() -> {
            String r = (String) role.getSelectedItem();
            String pass = new String(password.getPassword());
            if (baru) {
                controller.tambahAkun(nama.getText(), username.getText(), pass,
                        (String) status.getSelectedItem(), r, detail.getText(), tanggal.getDate());
            } else {
                controller.ubahAkun(existing, nama.getText(), username.getText(), pass,
                        (String) status.getSelectedItem(), r, detail.getText(), tanggal.getDate());
            }
        });
        return d.showDialog();
    }
}
