package com.prisma.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Singleton pengelola koneksi database.
 * Hanya ada SATU EntityManagerFactory (Hibernate) untuk seluruh aplikasi,
 * dan koneksi JDBC diambil dari Connection Pool HikariCP.
 */
public final class DatabaseManager {

    private static volatile DatabaseManager instance;

    private final EntityManagerFactory emf;

    private DatabaseManager() {
        Properties p = loadProperties();
        Map<String, Object> cfg = new HashMap<>();
        cfg.put("jakarta.persistence.jdbc.driver", "com.mysql.cj.jdbc.Driver");
        cfg.put("jakarta.persistence.jdbc.url", p.getProperty("db.url"));
        cfg.put("jakarta.persistence.jdbc.user", p.getProperty("db.user", "root"));
        cfg.put("jakarta.persistence.jdbc.password", p.getProperty("db.password", ""));

        // Connection pooling (HikariCP)
        cfg.put("hibernate.connection.provider_class", "org.hibernate.hikaricp.internal.HikariCPConnectionProvider");
        cfg.put("hibernate.hikari.poolName", p.getProperty("db.pool.name", "PrismaTVPool"));
        cfg.put("hibernate.hikari.maximumPoolSize", p.getProperty("db.pool.maxSize", "10"));
        cfg.put("hibernate.hikari.minimumIdle", p.getProperty("db.pool.minIdle", "2"));
        cfg.put("hibernate.hikari.connectionTimeout", p.getProperty("db.pool.connectionTimeoutMs", "10000"));
        cfg.put("hibernate.hikari.idleTimeout", p.getProperty("db.pool.idleTimeoutMs", "300000"));

        this.emf = Persistence.createEntityManagerFactory("prismaPU", cfg);
    }

    public static DatabaseManager getInstance() {
        DatabaseManager local = instance;
        if (local == null) {
            synchronized (DatabaseManager.class) {
                local = instance;
                if (local == null) {
                    local = new DatabaseManager();
                    instance = local;
                }
            }
        }
        return local;
    }

    public EntityManager createEntityManager() {
        return emf.createEntityManager();
    }

    /** Menutup pool koneksi. Aman dipanggil berkali-kali. */
    public static synchronized void shutdown() {
        if (instance != null) {
            if (instance.emf.isOpen()) instance.emf.close();
            instance = null;
        }
    }

    private static Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream in = DatabaseManager.class.getResourceAsStream("/database.properties")) {
            if (in == null) throw new IllegalStateException("File database.properties tidak ditemukan di classpath.");
            props.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Gagal membaca database.properties", e);
        }
        return props;
    }
}
