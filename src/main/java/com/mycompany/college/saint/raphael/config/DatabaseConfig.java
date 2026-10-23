package com.mycompany.college.saint.raphael.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Configuration portable de MySQL/MariaDB.
 *
 * Ordre de configuration :
 * 1) options -Ddb.*
 * 2) variables d'environnement CSR_DB_*
 * 3) fichier config/database.properties
 * 4) valeurs par défaut
 *
 * Avec db.port=auto, l'application essaie 3306 puis 3307.
 */
public final class DatabaseConfig {
    private static final DatabaseConfig INSTANCE = new DatabaseConfig();

    private final Properties fileProperties = new Properties();
    private final String host;
    private final String database;
    private final String username;
    private final String password;
    private final List<Integer> candidatePorts;
    private volatile Integer workingPort;

    private DatabaseConfig() {
        loadPropertiesFile();
        host = value("db.host", "CSR_DB_HOST", "127.0.0.1");
        database = value("db.name", "CSR_DB_NAME", "college_sainte_famille");
        username = value("db.user", "CSR_DB_USER", "root");
        password = value("db.password", "CSR_DB_PASSWORD", "");
        candidatePorts = resolvePorts(value("db.port", "CSR_DB_PORT", "auto"));

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(
                "Pilote MySQL introuvable. Faites 'Clean and Build' avec Maven/NetBeans.", e);
        }
    }

    public static DatabaseConfig getInstance() {
        return INSTANCE;
    }

    public Connection getConnection() throws SQLException {
        if (workingPort != null) {
            try {
                return DriverManager.getConnection(url(workingPort), username, password);
            } catch (SQLException ignored) {
                workingPort = null;
            }
        }

        SQLException last = null;
        for (int port : candidatePorts) {
            try {
                Connection connection = DriverManager.getConnection(url(port), username, password);
                workingPort = port;
                return connection;
            } catch (SQLException e) {
                last = e;
            }
        }

        String ports = candidatePorts.toString();
        throw new SQLException(
            "Connexion impossible à la base '" + database + "' sur " + host
                + " (ports essayés : " + ports + "). Vérifiez XAMPP/MySQL et config/database.properties.",
            last);
    }

    public boolean testConnection() {
        try (Connection connection = getConnection()) {
            return connection.isValid(3);
        } catch (SQLException e) {
            return false;
        }
    }

    public String getConnectionDescription() {
        return host + ':' + (workingPort == null ? "auto(3306/3307)" : workingPort)
            + '/' + database;
    }

    private String url(int port) {
        return "jdbc:mysql://" + host + ':' + port + '/' + database
            + "?useSSL=false"
            + "&allowPublicKeyRetrieval=true"
            + "&serverTimezone=UTC"
            + "&useUnicode=true"
            + "&characterEncoding=UTF-8"
            + "&zeroDateTimeBehavior=CONVERT_TO_NULL";
    }

    private void loadPropertiesFile() {
        Path file = Path.of("config", "database.properties");
        if (!Files.isRegularFile(file)) return;
        try (InputStream in = Files.newInputStream(file)) {
            fileProperties.load(in);
        } catch (IOException ignored) {
            // Les valeurs par défaut restent utilisables.
        }
    }

    private String value(String property, String environment, String fallback) {
        String system = System.getProperty(property);
        if (system != null && !system.isBlank()) return system.trim();

        String env = System.getenv(environment);
        if (env != null && !env.isBlank()) return env.trim();

        String file = fileProperties.getProperty(property);
        if (file != null) return file.trim();

        return fallback;
    }

    private static List<Integer> resolvePorts(String configured) {
        Set<Integer> ports = new LinkedHashSet<>();
        if (configured != null && !configured.isBlank() && !"auto".equalsIgnoreCase(configured)) {
            try {
                ports.add(Integer.parseInt(configured.trim()));
            } catch (NumberFormatException ignored) {
                // On retombera sur les ports usuels.
            }
        }
        ports.add(3306);
        ports.add(3307);
        return new ArrayList<>(ports);
    }
}
