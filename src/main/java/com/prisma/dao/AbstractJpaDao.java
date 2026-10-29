package com.prisma.dao;

import com.prisma.config.DatabaseManager;
import com.prisma.util.DataAccessException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * DAO Pattern + JPA/Hibernate. Semua operasi CRUD generik ada di sini;
 * subclass hanya menentukan field pencarian (dan query khusus bila perlu).
 * Setiap operasi memakai EntityManager baru yang koneksinya diambil dari pool HikariCP.
 */
public abstract class AbstractJpaDao<T, ID> implements GenericDao<T, ID> {

    private final Class<T> entityClass;
    private final String idField;

    protected AbstractJpaDao(Class<T> entityClass, String idField) {
        this.entityClass = entityClass;
        this.idField = idField;
    }

    /** Path JPQL yang dipakai untuk pencarian keyword (alias entity = e). */
    protected abstract String[] searchPaths();

    /** Klausa join tambahan untuk pencarian, diawali spasi. Mis. " left join e.programTv p". */
    protected String joins() { return ""; }

    /** Urutan default hasil query. */
    protected String orderBy() { return "e." + idField; }

    protected String entityName() { return entityClass.getSimpleName(); }

    // ------------------------------------------------------------------ CRUD

    @Override
    public T save(T entity) {
        return write(em -> em.merge(entity));
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(read(em -> em.find(entityClass, id)));
    }

    @Override
    public List<T> findAll() {
        return read(em -> em.createQuery(
                "select e from " + entityName() + " e order by " + orderBy(), entityClass).getResultList());
    }

    @Override
    public List<T> search(String keyword) {
        if (keyword == null || keyword.isBlank()) return findAll();
        StringBuilder q = new StringBuilder("select e from ").append(entityName()).append(" e")
                .append(joins()).append(" where ");
        String[] paths = searchPaths();
        for (int i = 0; i < paths.length; i++) {
            if (i > 0) q.append(" or ");
            q.append("lower(coalesce(").append(paths[i]).append(", '')) like :kw");
        }
        q.append(" order by ").append(orderBy());
        String kw = "%" + keyword.trim().toLowerCase() + "%";
        return read(em -> em.createQuery(q.toString(), entityClass).setParameter("kw", kw).getResultList());
    }

    @Override
    public void delete(T entity) {
        write(em -> {
            em.remove(em.contains(entity) ? entity : em.merge(entity));
            return null;
        });
    }

    @Override
    public long count() {
        return read(em -> em.createQuery("select count(e) from " + entityName() + " e", Long.class).getSingleResult());
    }

    // ------------------------------------------------------- helper transaksi

    protected <R> R read(Function<EntityManager, R> work) {
        EntityManager em = DatabaseManager.getInstance().createEntityManager();
        try {
            return work.apply(em);
        } catch (RuntimeException ex) {
            throw translate(ex);
        } finally {
            em.close();
        }
    }

    protected <R> R write(Function<EntityManager, R> work) {
        EntityManager em = DatabaseManager.getInstance().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            R result = work.apply(em);
            tx.commit();
            return result;
        } catch (RuntimeException ex) {
            if (tx.isActive()) tx.rollback();
            throw translate(ex);
        } finally {
            em.close();
        }
    }

    /** Mengubah exception teknis menjadi pesan yang mudah dipahami pengguna. */
    protected DataAccessException translate(RuntimeException ex) {
        if (ex instanceof DataAccessException) return (DataAccessException) ex;
        Throwable t = ex;
        Throwable root = ex;
        while (t != null) {
            String m = t.getMessage() == null ? "" : t.getMessage();
            if (m.contains("Duplicate entry")) {
                return new DataAccessException(
                        "Data duplikat: nilai yang harus unik (mis. nama atau username) sudah digunakan.", ex);
            }
            if (m.contains("foreign key constraint")) {
                return new DataAccessException(
                        "Data tidak dapat diproses karena masih berelasi dengan data lain.", ex);
            }
            root = t;
            t = t.getCause();
        }
        return new DataAccessException("Terjadi kesalahan database: " + root.getMessage(), ex);
    }
}
