package com.prisma.dao;

import com.prisma.model.ProgramTv;

public class ProgramTvDao extends AbstractJpaDao<ProgramTv, Integer> {

    public ProgramTvDao() { super(ProgramTv.class, "idProgram"); }

    @Override
    protected String[] searchPaths() {
        return new String[] {"e.namaProgram", "e.kategoriProgram", "e.urlProgram", "e.statusProgram"};
    }
}
