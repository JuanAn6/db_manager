package com.manager.controller;

import com.manager.model.ConnectionSql;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Stream;

public class PersistController {

    private static final DateTimeFormatter FILE_TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private final Path connectionsDirectory;

    public PersistController() {
        this(Path.of(System.getProperty("user.home"), ".db-manager", "connections"));
    }

    public PersistController(Path connectionsDirectory) {
        this.connectionsDirectory = connectionsDirectory;
    }

    public Path saveConnection(ConnectionSql connection) throws IOException {
        Files.createDirectories(connectionsDirectory);

        String safeName = sanitizeFileName(connection.getName());
        if (safeName.isBlank()) {
            safeName = "connection";
        }

        String timestamp = LocalDateTime.now().format(FILE_TIMESTAMP_FORMAT);
        Path filePath = connectionsDirectory.resolve(safeName + "_" + timestamp + ".properties");

        Properties properties = new Properties();
        properties.setProperty("name", notNull(connection.getName()));
        properties.setProperty("url", notNull(connection.getUrl()));
        properties.setProperty("user", notNull(connection.getUser()));
        properties.setProperty("password", notNull(connection.getPassword()));
        properties.setProperty("savedAt", LocalDateTime.now().toString());

        try (OutputStream outputStream = Files.newOutputStream(
                filePath,
                StandardOpenOption.CREATE_NEW,
                StandardOpenOption.WRITE
        )) {
            properties.store(outputStream, "DB Manager connection");
        }

        return filePath;
    }

    public List<ConnectionSql> loadConnections() {
        List<ConnectionSql> connections = new ArrayList<>();
        if (!Files.isDirectory(connectionsDirectory)) {
            return connections;
        }

        try (Stream<Path> files = Files.list(connectionsDirectory)) {
            files.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".properties"))
                    .sorted(Comparator.comparing(path -> path.getFileName().toString()))
                    .map(this::readConnection)
                    .filter(Objects::nonNull)
                    .forEach(connections::add);
        } catch (IOException ex) {
            System.err.println("Error loading saved connections: " + ex.getMessage());
        }

        return connections;
    }

    private ConnectionSql readConnection(Path filePath) {
        Properties properties = new Properties();
        try (InputStream inputStream = Files.newInputStream(filePath)) {
            properties.load(inputStream);
        } catch (IOException ex) {
            System.err.println("Error reading connection file " + filePath + ": " + ex.getMessage());
            return null;
        }

        String url = notNull(properties.getProperty("url"));
        String user = notNull(properties.getProperty("user"));
        if (url.isBlank() || user.isBlank()) {
            return null;
        }

        String name = notNull(properties.getProperty("name"));
        String password = notNull(properties.getProperty("password"));
        return new ConnectionSql(name, url, user, password);
    }

    private String sanitizeFileName(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private String notNull(String value) {
        return value == null ? "" : value;
    }
}
