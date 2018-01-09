package io.github.skyshayde;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubWriter;
import org.jsoup.select.Elements;

/**
 * Created by skysh on 12/30/2017.
 */

public class WordpressScraper {
    String startingUrl;
    int delay = 0;
    Map<String, String> meta = new HashMap<String, String>();
    List<List<String>> spine = new ArrayList<List<String>>();
    Blog blog = new Blog();

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

    public void fetchMeta(String url) {
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
                    meta.put("title", i.text());
                } catch (NullPointerException e) {
                    meta.put("title", "Unknown");
                }
            }
            //wip this author parsing bit
            if (i.attr("property").equals("author")) {
            }
        });
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

    public void fetchChapter(Document doc) {
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
