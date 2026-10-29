# PRISMA TV — Sistem Informasi Manajemen Konten (Java Swing + Hibernate)

## 1. Persiapan
1. **Java JDK 17+** dan **Maven 3.8+**.
2. **MySQL 8** (XAMPP/Laragon dsb). Buat database `prisma_tv`, lalu impor `database/Prisma_tv.sql`
   (phpMyAdmin → Import, atau `mysql -u root -p prisma_tv < database/Prisma_tv.sql`).
3. Sesuaikan `src/main/resources/database.properties` (default: `localhost:3308`, user `root`, password kosong —
   sesuai header dump SQL Anda; ubah ke `3306` bila MySQL Anda memakai port standar).

## 2. Menjalankan
```
mvn compile exec:java
```
atau buat jar: `mvn package` lalu `java -jar target/prisma-tv-1.0.0.jar`.
Di NetBeans/IntelliJ/Eclipse: *Open as Maven project*, jalankan class `com.prisma.Main`.

## 3. Akun (dari tabel `pengguna`)
| Peran | Username | Password |
|---|---|---|
| Admin | `admin` | `adminprisma1` |
| Petugas Jadwal | `petugasjadwal1` / `petugasjadwal2` | sama dengan username |
| Petugas Konten | `petugaskonten1` … `petugaskonten5` | sama dengan username |

## 4. Menu per peran (mengikuti flowchart)
- **Admin**: Dashboard, Program TV, Monitor Data, Data Kanal, Data Mitra, Kerja Sama, Akun Pengguna
- **Petugas Konten**: Dashboard, Manajemen Konten (+lampiran), Publikasi Konten, Lampiran Konten, Program TV (lihat saja), Monitor Data
- **Petugas Jadwal**: Dashboard, Jadwal Tayang (+Nonaktifkan), Program TV (lihat saja), Monitor Data

## 5. Struktur MVC
```
com.prisma
├── Main.java                      titik masuk (splash + inisialisasi pool)
├── config/   DatabaseManager (Singleton, Hibernate + HikariCP), AppSession (Singleton login)
├── model/    Entity JPA: BaseEntity ← Pengguna ← Admin / PetugasJadwal / PetugasKonten, ProgramTv, Kanal, Mitra,
│             KerjaSama, Konten, LampiranKonten, PublikasiKonten, JadwalTayang, Menu
├── dao/      GenericDao (interface) ← AbstractJpaDao ← XxxDao
├── controller/ BaseController ← XxxController (validasi + logika bisnis), AuthController, DashboardController
├── view/     LoginFrame, MainFrame, CrudPanel<T> ← XxxPanel, DashboardPanel, MonitorPanel, PanelFactory
│   └── component/ Theme, AnimatedButton, DataTable, FormDialog, DateTimeField, Toast, FadePanel, ...
└── util/     Options (nilai ENUM DB), Validator, Fmt, FileStorage, exception
```

## 6. Pemenuhan ketentuan
| Ketentuan | Implementasi |
|---|---|
| CRUD terhubung DB | Semua panel: tambah / cari / ubah / hapus lewat DAO → MySQL. Tidak ada data hardcode. |
| Encapsulation | Field `private` + getter/setter di semua entity. |
| Inheritance | `BaseEntity`; `Pengguna` → `Admin`/`PetugasJadwal`/`PetugasKonten` (JPA `JOINED`, sama dengan struktur tabel); `AbstractJpaDao`; `BaseController`; `CrudPanel`. |
| Polymorphism | `Pengguna.getRole()/getMenus()/getDetail()` dioverride tiap peran → menu sidebar berbeda; `validate()` berbeda per controller. |
| Abstraction | `GenericDao`, `AbstractJpaDao`, `BaseController`, `CrudPanel`, `Pengguna`, `BaseEntity`. |
| Swing | Login, Main, dashboard, tabel, form dialog, animasi (hover, fade, toast, hitung naik, getar login gagal). |
| MVC | `model` / `view` / `controller` terpisah; View tidak menyentuh DAO. |
| DAO Pattern | `dao/` |
| Singleton + Connection Pooling | `DatabaseManager` (Singleton) + HikariCP (`db.pool.*` di database.properties). |
| ORM | Hibernate 6 / JPA (`persistence.xml`, anotasi entity, JPQL). |

## 7. Penyesuaian input: SQL vs flowchart (SQL diprioritaskan)
- **Jadwal**: flowchart hanya Program/Tanggal/Mulai/Selesai; SQL mewajibkan juga **Kanal** dan **Status**; petugas diisi otomatis dari akun login.
- **Program TV**: flowchart "Jenis + Deskripsi" → SQL: **Kategori** (enum), **URL**, **Status**; admin otomatis dari login. (Tidak ada kolom deskripsi.)
- **Mitra**: flowchart ada "Logo Mitra" → tidak ada kolom di SQL, jadi **tidak dibuat**.
- **Konten**: ditambah **Ringkasan** & **Tanggal Pencatatan** (NOT NULL di SQL). Lampiran disimpan ke tabel `lampiran_konten`, file disalin ke folder `uploads/`.
- **Publikasi**: ditambah pilihan **Konten** & **Kanal**; status "Terbit" mewajibkan link.
- **Akun**: ditambah **Status**, serta **Spesialisasi/Cakupan** + **Tanggal Penugasan** sesuai tabel peran. Ganti peran = memindahkan baris antar tabel (satu transaksi).
- Pencarian Jadwal memakai Date Picker + 2 Time Picker (boleh kosong), sesuai flowchart.
- Validasi tambahan: jadwal tidak boleh bertabrakan pada kanal & tanggal yang sama (kecuali berstatus Dibatalkan).

## 8. Catatan
- Kolom `password` di SQL hanya `varchar(30)`, sehingga password disimpan apa adanya (tidak bisa hash). Untuk produksi, perbesar kolom dan gunakan BCrypt.
- Hapus data yang masih dipakai tabel lain (mis. program yang sudah dijadwalkan) ditolak database; aplikasi menampilkan pesan yang ramah.
