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
        doc.getElementsByTag("meta").forEach(i -> {
            if (i.attr("property").equals("og:site_name")) {
                try {
                    blog.setTitle(i.attr("content"));
                } catch (NullPointerException e) {
                    blog.setTitle("Unknown");
                }
            }
            //wip this author parsing bit
        });
        try {
            blog.setAuthor(doc.selectFirst("span.author").text());
        } catch (NullPointerException e) {
            blog.setAuthor("Unknown");
        }
        doc.getElementsByTag("link").forEach(i -> {
            if (i.attr("rel").equals("next")) {
                try {
                    fetchChapter(Jsoup.connect(i.attr("href")).get());
                } catch (IOException e) {
                    System.out.println("No more pages.  ");
                }
            }
        });
    }

    private void fetchChapter(Document doc) {
        Document docClone = doc.clone();
        Element title = doc.select("h1.entry-title").first();
        System.out.println(title.text());
        docClone.select("div.wpcnt").remove();
        docClone.select("div.sharedaddy").remove();
        docClone.select("div#jp-post-flair").remove();
        String content = title.outerHtml() + docClone.select("div.entry-content").first().outerHtml();
        blog.addPost(new Post(title.text(), content));
        doc.getElementsByTag("link").forEach(i -> {
            if (i.attr("rel").equals("next")) {
                try {
                    fetchChapter(Jsoup.connect(i.attr("href")).get());
                } catch (IOException e) {
                    System.out.println("No more pages.  ");
                }
            }
        });
    }


}
