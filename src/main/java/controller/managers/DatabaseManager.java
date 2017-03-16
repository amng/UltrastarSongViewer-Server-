package controller.managers;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

public class DatabaseManager {

    private final int version = 1;
    private static EntityManager factory;

    private static DatabaseManager instance = null;

    private DatabaseManager() {}

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
            factory = Persistence.createEntityManagerFactory("h2").createEntityManager();
        }
        return instance;
    }

    public EntityManager getFactory() {
        return factory;
    }
}
