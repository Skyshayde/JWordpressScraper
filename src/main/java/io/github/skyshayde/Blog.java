package io.github.skyshayde;

import java.util.ArrayList;
import java.util.List;

public class Blog {

    private List<Post> posts = new ArrayList<Post>();
    private String author = null;
    private String title = null;

    public void addPost(Post p){
        posts.add(p);
    }

    public List<Post> getPosts() {
        return this.posts;
    }

    public String getAuthor() {
        if(author == null) {
            return "Unknown";
        }
        return this.author;
    }
    public String getTitle() {
        if(title == null) {
            return "Unknown";
        }
        return this.title;
    }
}
