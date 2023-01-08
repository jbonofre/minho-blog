package net.nanthrax.blog.minho.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.nanthrax.blog.minho.jpa.BlogEntity;
import net.nanthrax.blog.minho.jpa.BlogJpaService;
import org.apache.karaf.minho.boot.Minho;

import java.util.List;
import java.util.Objects;

@Path("/")
public class BlogApi {

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createBlog(BlogEntity blog) {
        getBlogJpaService().createBlogPost(blog);
        return Response.ok().build();
    }

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<BlogEntity> listBlogs() {
        return getBlogJpaService().listBlogPosts();
    }

    private BlogJpaService getBlogJpaService() {
        Minho minho = Minho.getInstance();
        BlogJpaService blogJpaService = minho.getServiceRegistry().get(BlogJpaService.class);
        if (Objects.isNull(blogJpaService)) {
            throw new IllegalStateException("Blog JPA service is not available");
        }
        return blogJpaService;
    }

}
