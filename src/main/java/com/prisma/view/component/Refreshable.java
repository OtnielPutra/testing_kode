package com.prisma.view.component;

/** Halaman yang datanya dimuat ulang dari database setiap kali dibuka. */
public interface Refreshable {
    void refresh();
}
