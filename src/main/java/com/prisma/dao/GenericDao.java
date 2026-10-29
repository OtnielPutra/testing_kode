package com.prisma.dao;

import java.util.List;
import java.util.Optional;

/** ABSTRACTION: kontrak operasi CRUD untuk semua entity. */
public interface GenericDao<T, ID> {
    T save(T entity);                    // Create + Update
    Optional<T> findById(ID id);         // Read
    List<T> findAll();                   // Read
    List<T> search(String keyword);      // Read (pencarian)
    void delete(T entity);               // Delete
    long count();
}
