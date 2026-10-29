package com.prisma.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/** Menyalin file lampiran ke folder uploads/ (sesuai kolom lokasi_file di database). */
public final class FileStorage {
    private FileStorage() { }

    private static final Path DIR = Paths.get("uploads");

    /** @return lokasi relatif, mis. "uploads/berita.jpg" */
    public static String store(File source) throws ValidationException {
        try {
            Files.createDirectories(DIR);
            Path target = DIR.resolve(source.getName());
            if (!target.toAbsolutePath().equals(source.toPath().toAbsolutePath())) {
                Files.copy(source.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
            }
            return "uploads/" + source.getName();
        } catch (IOException e) {
            throw new ValidationException("Gagal menyimpan file lampiran: " + e.getMessage());
        }
    }
}
