package io.github.skyshayde;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class RemoteBlog implements Iterable<Post> {

    private String nextUrl;

    public Blog blog;

    public RemoteBlog(String url) {
        blog = new Blog();
        blog.firstUrl = url;
        blog.prevUrl = url;
        nextUrl = url;
    }

    public RemoteBlog(Blog in) {
        blog = in;
    }


    public String getNextUrl(Document doc) {
        Element first = doc.selectFirst("link[rel=next]");
        if (first == null) {
            return blog.prevUrl;
        }
        return first.attr("href");
    }

    public String checkIfNewUrl() {
        try {
            String next = getNextUrl(Jsoup.connect(blog.prevUrl).get());
            return next;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void fetchMeta(Document doc) {

        String title = doc.selectFirst("meta[property=og:site_name]").attr("content");
        blog.setTitle((title != null) ? (title) : ("Unknown"));

        String author = doc.selectFirst("span.author").text();
        blog.setAuthor((author != null) ? (author) : ("Unknown"));

        nextUrl = getNextUrl(doc);

    }

    private Post fetchChapter(Document doc) {
        Element title = doc.selectFirst("h1.entry-title");
        System.out.println(title.text());
        doc.select("div.wpcnt").remove();
        doc.select("div.sharedaddy").remove();
        doc.select("div#jp-post-flair").remove();
        doc.select("a").remove();
        String chapterContent = title.outerHtml() + doc.select("div.entry-content").first().outerHtml();
        blog.prevUrl = nextUrl;
        nextUrl = getNextUrl(doc);
        return new Post(title.text(), chapterContent);
    }

    @Override
    public Iterator<Post> iterator() {
        Iterator<Post> it = new Iterator<Post>() {

            public boolean hasNext() {
                return checkIfNewUrl() != null ? true : false;
            }

            @Override
            public Post next() {
                if (nextUrl == null) {
                    throw new NoSuchElementException();
                }
                try {
                    return fetchChapter(Jsoup.connect(nextUrl).get());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
        return it;
    }
}
