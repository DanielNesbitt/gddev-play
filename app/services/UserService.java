package services;

import models.User;
import play.db.jpa.JPA;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Daniel Nesbitt
 */
@SuppressWarnings("SqlNoDataSourceInspection")
public final class UserService {

    // ------------- Public -------------

    public static List<User> list() {
        return transaction(em ->
            em.createQuery("select u from User u", User.class)
                .getResultList()
        );
    }

    public static User find(long id) {
        return transaction(em -> em.find(User.class, id));
    }

    public static User create(String username, String forename, String surname) {
        return transaction(em -> {
            User user = new User();
            user.setUsername(username);
            user.setForename(forename);
            user.setSurname(surname);

            em.persist(user);
            em.flush();

            return user;
        });
    }

    public static void delete(long id) {
        update(em -> {
            User user = em.find(User.class, id);
            em.remove(user);
        });
    }

    // ------------- Private -------------

    public static <T> T transaction(Function<EntityManager, T> f) {
        try {
            return JPA.withTransaction(() -> f.apply(JPA.em()));
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    public static void update(Consumer<EntityManager> c) {
        try {
            JPA.withTransaction(() -> c.accept(JPA.em()));
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

}
