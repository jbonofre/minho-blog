package net.nanthrax.blog.minho.jpa;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class BlogEntity {

    @Id
    private String title;
    private String content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
