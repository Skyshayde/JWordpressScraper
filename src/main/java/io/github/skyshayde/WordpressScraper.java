package io.github.skyshayde;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

/**
 * Created by skysh on 12/30/2017.
 */

public class WordpressScraper {
    private final String firstUrl;
    private String nextUrl;
    private String prevUrl = "";

    private int delay = 0;
    public final Blog blog = new Blog();

    public WordpressScraper(String url) {
        this.firstUrl = url;
        blog.firstUrl = url;
    }

    public WordpressScraper(String url, int delayInMs) {
        this.firstUrl = url;
        blog.firstUrl = url;
        this.delay = delayInMs;
    }

    /**
     * Calls fetchMeta function with the URL passed when creating the object
     */
    public WordpressScraper scrape() {
        Document doc;
        try {
            doc = Jsoup.connect(firstUrl).get();
        } catch (IOException e) {
            System.out.println("Could not connect to " + firstUrl);
            return this;
        }
        fetchMeta(doc);
        while (true) {
            if (nextUrl.equals(prevUrl)) {
                break;
            }
            try {
                fetchChapter(Jsoup.connect(nextUrl).get());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    public WordpressScraper scrape(String startUrl) {
        nextUrl = startUrl;
        while (true) {
            if (nextUrl.equals(prevUrl)) {
                break;
            }
            try {
                fetchChapter(Jsoup.connect(nextUrl).get());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    public String getNextUrl(Document doc) {
        Element first = doc.selectFirst("link[rel=next]");
        if (first == null) {
            return null;
        }
        return first.attr("href");
    }

    public String checkIfNewUrl() {
        try {
            String next = getNextUrl(Jsoup.connect(prevUrl).get());
            return next;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This function will pull the metadata from a page and then call a function to fetch the chapters
     *
     * @param doc url of the first page on a blog
     */
    private void fetchMeta(Document doc) {

        String title = doc.selectFirst("meta[property=og:site_name]").attr("content");
        blog.setTitle((title != null) ? (title) : ("Unknown"));

        String author = doc.selectFirst("span.author").text();
        blog.setAuthor((author != null) ? (author) : ("Unknown"));

        fetchChapter(doc);
        this.nextUrl = doc.selectFirst("link[rel=next]").attr("href");

    }

    private void fetchChapter(Document doc) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        prevUrl = nextUrl;
        blog.prevUrl = nextUrl;
        Element title = doc.selectFirst("h1.entry-title");
        System.out.println(title.text());
        doc.select("div.wpcnt").remove();
        doc.select("div.sharedaddy").remove();
        doc.select("div#jp-post-flair").remove();
        String chapterContent = title.outerHtml() + doc.select("div.entry-content").first().outerHtml();
        blog.addPost(new Post(title.text(), chapterContent));
        Element first = doc.selectFirst("link[rel=next]");
        if (first == null) {
            return;
        }
//        nextUrl = first.attr("href");
    }

}
