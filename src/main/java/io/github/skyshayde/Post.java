package io.github.skyshayde;

public class Post {

    public String title;
    public final String content;

    public Post(String inTitle, String inContent) {
        this.title = inTitle;
        this.content = inContent;
    }
}
