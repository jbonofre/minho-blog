package net.nanthrax.blog.minho.jpa;

import org.apache.karaf.minho.boot.Minho;
import org.apache.karaf.minho.boot.service.ConfigService;
import org.apache.karaf.minho.boot.service.LifeCycleService;
import org.apache.karaf.minho.jpa.openjpa.OpenJPAService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

public class BlogJpaServiceTest {

    @Test
    public void simpleUsage() throws Exception {
        System.setProperty("derby.system.home", "target/derby");
        System.setProperty("derby.stream.error.file", "target/derby.log");

        Minho minho = Minho.builder().loader(() -> Stream.of(new ConfigService(), new LifeCycleService(), new OpenJPAService(), new BlogJpaService())).build().start();

        BlogJpaService blogJpaService = minho.getServiceRegistry().get(BlogJpaService.class);

        BlogEntity blog = new BlogEntity();
        blog.setTitle("My First Blog");
        blog.setContent("Hello world!");
        blogJpaService.createBlogPost(blog);

        List<BlogEntity> list = blogJpaService.listBlogPosts();
        Assertions.assertEquals(1, list.size());

        blogJpaService.deleteBlogPost(blog);
        list = blogJpaService.listBlogPosts();
        Assertions.assertEquals(0, list.size());
    }

}
