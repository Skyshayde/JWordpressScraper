package io.github.skyshayde;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

/**
 * Created by skysh on 12/30/2017.
 */

public class WordpressScraper {
    private final String startingUrl;
    private int delay = 0;
    final Blog blog = new Blog();

    public WordpressScraper(String url) {
        this.startingUrl = url;
    }

    public WordpressScraper(String url, int delayInMs) {
        this.startingUrl = url;
        this.delay = delayInMs;
    }

    public void scrape() {
        fetchMeta(this.startingUrl);
    }

    private void fetchMeta(String url) {
        Document doc;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            System.out.println("Could not connect to " + url);
            return;
        }
        try {
            blog.setTitle(doc.selectFirst("meta[property=og:site_name]").attr("content"));
        } catch (NullPointerException e) {
            blog.setTitle("Unknown");
        }
        try {
            blog.setAuthor(doc.selectFirst("span.author").text());
        } catch (NullPointerException e) {
            blog.setAuthor("Unknown");
        }
        fetchChapter(doc);
        String nextURL = doc.selectFirst("link[rel=next]").attr("href");
        while (true) {
            if (nextURL == null) {
                break;
            }
            try {
                nextURL = fetchChapter(Jsoup.connect(nextURL).get());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String fetchChapter(Document doc) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Element title = doc.selectFirst("h1.entry-title");
        System.out.println(title.text());
        doc.select("div.wpcnt").remove();
        doc.select("div.sharedaddy").remove();
        doc.select("div#jp-post-flair").remove();
        String chapterContent = title.outerHtml() + doc.select("div.entry-content").first().outerHtml();
        blog.addPost(new Post(title.text(), chapterContent));
        return doc.selectFirst("link[rel=next]").attr("href");
    }


}
