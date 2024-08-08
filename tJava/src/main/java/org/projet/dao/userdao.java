package org.projet.dao;

import org.projet.jpa.user;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class userdao {
    private static final EntityManagerFactory emf = Persistence.createEntityManagerFactory("user");

    public void saveUser(user user) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        em.persist(user);
        em.getTransaction().commit();
        em.close();
    }

    public user getUserById(Long id) {
        EntityManager em = emf.createEntityManager();
        user user = em.find(user.class, id);
        em.close();
        return user;
    }

    public List<user> getAllUsers() {
        EntityManager em = emf.createEntityManager();
        List<user> users = em.createQuery("SELECT name FROM user", user.class).getResultList();
        em.close();
        return users;
    }

    public void close() {
        emf.close();
    }
}
