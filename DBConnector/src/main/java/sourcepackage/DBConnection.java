package sourcepackage;

import domain.WebserverDbEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

public class DBConnection {

    static EntityManagerFactory emf = Persistence.createEntityManagerFactory("PU");


    public static List<WebserverDbEntity> getPeopleFromDb() {

        EntityManager em = emf.createEntityManager();

        TypedQuery<WebserverDbEntity> query = em.createQuery("SELECT w FROM WebserverDbEntity w", WebserverDbEntity.class);
        List<WebserverDbEntity> list = query.getResultList();

        em.close();

        return list;
    }

    public static void main(String[] args) {

        List<WebserverDbEntity> list = getPeopleFromDb();

        System.out.println(list);
    }
}
