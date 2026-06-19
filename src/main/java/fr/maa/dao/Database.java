package fr.maa.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Point d'accès à la connexion JDBC.
 * <p>
 * Les paramètres de connexion (URL, utilisateur, mot de passe) sont
 * externalisés dans le fichier {@code config.properties} du classpath afin de
 * ne pas coder en dur d'informations sensibles. Des valeurs par défaut (dev)
 * sont utilisées en secours si le fichier est absent.
 */
public class Database {

    private static final Logger LOGGER = Logger.getLogger(Database.class.getName());

    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/billetterie?useSSL=false&serverTimezone=UTC";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";

    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    private static Connection connection;

    static {
        Properties props = new Properties();
        try (InputStream in = Database.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in != null) {
                props.load(in);
            } else {
                LOGGER.warning("config.properties introuvable, utilisation des paramètres par défaut.");
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Lecture de config.properties impossible, paramètres par défaut utilisés.", e);
        }
        URL = props.getProperty("db.url", DEFAULT_URL);
        USER = props.getProperty("db.user", DEFAULT_USER);
        PASSWORD = props.getProperty("db.password", DEFAULT_PASSWORD);
    }

    private Database() {
    }

    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                LOGGER.info("Connexion MySQL OK");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Echec de la connexion MySQL", e);
            }
        }
        return connection;
    }
}
