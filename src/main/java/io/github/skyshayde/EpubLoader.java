package io.github.skyshayde;

import com.google.gson.Gson;
import nl.siegmann.epublib.domain.*;
import nl.siegmann.epublib.epub.EpubReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import javax.xml.namespace.QName;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EpubLoader {
    public Blog blog = new Blog();

    public EpubLoader(String path) {
        EpubReader epubReader = new EpubReader();
        Book book = null;
        try {
            book = epubReader.readEpub(new FileInputStream(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Metadata metadata = book.getMetadata();
        blog.setTitle(metadata.getFirstTitle());
        blog.setAuthor(metadata.getAuthors().get(0).toString());
        try {
            Map<String, String> props = new Gson().fromJson(book.getResources().getByIdOrHref("FeedToBookMeta.json").getReader(), Map.class);
            blog.firstUrl = props.get("firstUrl");
            blog.prevUrl = props.get("prevUrl");
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<TOCReference> list = book.getTableOfContents().getTocReferences();
        list.remove(0);
        list.forEach(i -> {
            try {
                Reader reader = i.getResource().getReader();
                int intValueOfChar;
                String contents = "";
                while ((intValueOfChar = reader.read()) != -1) {
                    contents += (char) intValueOfChar;
                }
                blog.addPost(getPostFromString(contents));
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return;
    }

    public Post getPostFromString(String s) {
        Document doc = Jsoup.parse(s);
        Elements e = doc.getAllElements();
        String title = e.get(0).outerHtml();
        String author = e.get(1).outerHtml();
        return new Post(title, author);
    }
}
