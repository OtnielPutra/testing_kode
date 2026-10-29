package com.prisma.model;

import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * INHERITANCE + ABSTRACTION: induk semua entity.
 * equals/hashCode berdasarkan tipe dan ID sehingga object yang sama dari session
 * berbeda tetap dianggap sama (penting untuk pemilihan item di ComboBox).
 */
@MappedSuperclass
public abstract class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Primary key entity. */
    public abstract Object getId();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntity)) return false;
        BaseEntity other = (BaseEntity) o;
        Object id = getId();
        return id != null && getClass() == other.getClass() && id.equals(other.getId());
    }

    @Override
    public int hashCode() { return getClass().hashCode(); }
}
