package io.github.skyshayde;

import java.util.ArrayList;
import java.util.List;

public class Blog {


    private final List<Post> posts = new ArrayList<>();
    public String author = "Unknown";
    public String title = "Unknown";
    public String firstUrl = "";
    public String prevUrl = "";

    public void addPost(Post p) {
        posts.forEach(i -> {
            // add underscore if the title is a duplicate and then call the function again
            if (i.title.equals(p.title)) {
                p.title = p.title + "_";
            }
        });
        posts.add(p);
    }

    public List<Post> getPosts() {
        return this.posts;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String in) {
        this.author = in.substring(0, 1).toUpperCase() + in.substring(1);
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String in) {
        this.title = in;
    }

}
