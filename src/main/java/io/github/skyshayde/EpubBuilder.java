package io.github.skyshayde;

import com.google.gson.Gson;
import nl.siegmann.epublib.domain.*;
import nl.siegmann.epublib.epub.EpubWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EpubBuilder {
    private final Blog blog;
    private final Book book = new Book();
    private String epubName = null;

    public EpubBuilder(Blog blog) {
        this.blog = blog;
        this.epubName = blog.getTitle() + " - " + blog.getAuthor() + ".epub";
        build();
    }

    public EpubBuilder(Blog blog, String fileName) {
        this.blog = blog;
        this.epubName = fileName.replace("${author}", blog.getAuthor()).replace("${title}", blog.getTitle());
        build();
    }

    public void build() {
        Metadata metadata = book.getMetadata();
        metadata.addTitle(blog.getTitle());
        metadata.addAuthor(new Author(blog.getAuthor()));

        Map<String, String> urls = new HashMap<>();
        urls.put("firstUrl", blog.firstUrl);
        urls.put("prevUrl", blog.prevUrl);

        Gson gson = new Gson();
        String json = gson.toJson(urls);
        Resource res = new Resource(json.getBytes(), "FeedToBookMeta.json");
        res.setMediaType(new MediaType("FeedToBookMeta", "json"));
        book.getResources().add(res);

        String titleHTML = "<h1 style=\"text-align:center\">" + blog.getTitle() + "</h1>";

        book.addSection("Title", new Resource(titleHTML.getBytes(), "title.html"));

        blog.getPosts().forEach(i -> book.addSection(i.title, new Resource(i.content.getBytes(), "chapters/" + i.title.replaceAll("[^a-zA-Z0-9\\.\\-]", "_") + ".html")));
//        buildInlineTOC(book);
        try {
            new EpubWriter().write(book, new FileOutputStream(this.epubName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String buildInlineTOC(Book b) {
        List<TOCReference> l = b.getTableOfContents().getTocReferences();
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        Document doc = docBuilder.newDocument();
        Element root = doc.createElement("body");
        doc.appendChild(root);

        l.forEach(i -> {
            Element toc = doc.createElement("a");
            toc.setAttribute("href", i.getCompleteHref());
            root.appendChild(toc);
        });
        System.out.println(doc.toString());
        return "";
    }
}
