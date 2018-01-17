package io.github.skyshayde;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

/**
 * Created by skysh on 12/30/2017.
 */

public class WordpressScraper {
    private String nextUrl;

    private int delay = 0;
    public Blog blog;

    public WordpressScraper(String url) {
        blog = new Blog();
        blog.firstUrl = url;
    }

    public WordpressScraper(Blog blog) {
        this.blog = blog;
    }

    public WordpressScraper(String url, int delayInMs) {
        blog.firstUrl = url;
        this.delay = delayInMs;
    }

    /**
     * Calls scrape(String) function with the URL passed when creating the object
     */
    public WordpressScraper scrape() {
        scrape(blog.firstUrl);
        return this;
    }

    /**
     * @param startUrl URL to start scraping at
     * @return the instance that the method belongs to
     */
    public WordpressScraper scrape(String startUrl) {
        if (blog.title.equals("Unknown") || blog.author.equals("Unknown")) {
            try {
                fetchMeta(Jsoup.connect(startUrl).get());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        nextUrl = startUrl;
        while (true) {
            // If nextUrl is equal to PrevUrl, we have reached the end of the blog
            if (nextUrl.equals(blog.prevUrl)) {
                break;
            }
            try {
                blog.addPost(fetchChapter(Jsoup.connect(nextUrl).get()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return this;
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

        nextUrl = getNextUrl(doc);

    }

    private Post fetchChapter(Document doc) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        blog.prevUrl = nextUrl;
        Element title = doc.selectFirst("h1.entry-title");
        System.out.println(title.text());
        doc.select("div.wpcnt").remove();
        doc.select("div.sharedaddy").remove();
        doc.select("div#jp-post-flair").remove();
        String chapterContent = title.outerHtml() + doc.select("div.entry-content").first().outerHtml();
        nextUrl = getNextUrl(doc);
        return new Post(title.text(), chapterContent);
    }

}
