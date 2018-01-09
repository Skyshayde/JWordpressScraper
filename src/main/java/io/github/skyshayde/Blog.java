package io.github.skyshayde;

import java.util.ArrayList;
import java.util.List;

class Blog {

    private final List<Post> posts = new ArrayList<>();
    private String author = null;
    private String title = null;

    public void addPost(Post p) {
        posts.add(p);
    }

    public List<Post> getPosts() {
        return this.posts;
    }

    public String getAuthor() {
        if (author == null) {
            return "Unknown";
        }
        return this.author;
    }

    public void setAuthor(String in) {
        this.author = in.substring(0, 1).toUpperCase() + in.substring(1);
    }

    public String getTitle() {
        if (title == null) {
            return "Unknown";
        }
        return this.title;
    }

    public void setTitle(String in) {
        this.title = in;
    }

}
