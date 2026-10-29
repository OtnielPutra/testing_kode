package com.prisma.controller;

import com.prisma.dao.GenericDao;
import com.prisma.util.ValidationException;

import java.util.List;

/**
 * ABSTRACTION + POLYMORPHISM: alur CRUD sama untuk semua entity,
 * sedangkan aturan validasi (validate) berbeda di tiap subclass.
 */
public abstract class BaseController<T, ID> {

    protected final GenericDao<T, ID> dao;

    protected BaseController(GenericDao<T, ID> dao) { this.dao = dao; }

    /** Memeriksa dan merapikan (trim) isi entity. Lempar ValidationException bila salah. */
    protected abstract void validate(T entity) throws ValidationException;

    public List<T> findAll() { return dao.findAll(); }

    public List<T> search(String keyword) { return dao.search(keyword); }

    public long count() { return dao.count(); }

    public T save(T entity) throws ValidationException {
        validate(entity);
        return dao.save(entity);
    }

    public void delete(T entity) throws ValidationException { dao.delete(entity); }
}
