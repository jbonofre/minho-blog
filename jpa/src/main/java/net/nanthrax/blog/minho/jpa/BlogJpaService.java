package net.nanthrax.blog.minho.jpa;

import org.apache.karaf.minho.boot.service.LifeCycleService;
import org.apache.karaf.minho.boot.service.ServiceRegistry;
import org.apache.karaf.minho.boot.spi.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.List;

public class BlogJpaService implements Service {

    private EntityManagerFactory factory;
    private EntityManager entityManager;

    @Override
    public int priority() {
        return Service.DEFAULT_PRIORITY + 1;
    }

    @Override
    public void onRegister(ServiceRegistry serviceRegistry) {
        LifeCycleService lifeCycleService = serviceRegistry.get(LifeCycleService.class);
        lifeCycleService.onStart(() -> {
            factory = Persistence.createEntityManagerFactory("BlogEntity", System.getProperties());
            entityManager = factory.createEntityManager();
        });
        lifeCycleService.onShutdown(() -> {
            entityManager.close();
            factory.close();
        });
    }

    public void createBlogPost(BlogEntity entity) {
        entityManager.getTransaction().begin();
        entityManager.persist(entity);
        entityManager.getTransaction().commit();
    }

    public List<BlogEntity> listBlogPosts() {
        Query query = entityManager.createQuery("SELECT blog FROM BlogEntity blog");
        return query.getResultList();
    }

    public void deleteBlogPost(BlogEntity entity) {
        entityManager.getTransaction().begin();
        entityManager.remove(entity);
        entityManager.getTransaction().commit();
    }

}
