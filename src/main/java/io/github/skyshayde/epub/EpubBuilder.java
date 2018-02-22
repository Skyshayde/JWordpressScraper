package io.github.skyshayde.epub;

import com.google.gson.Gson;
import io.github.skyshayde.Blog;
import nl.siegmann.epublib.domain.*;
import nl.siegmann.epublib.epub.EpubWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static nl.siegmann.epublib.epub.PackageDocumentBase.NAMESPACE_OPF;

public class EpubBuilder {
    private final Blog blog;
    private final Book book = new Book();
    private String epubName = null;
    public ByteArrayOutputStream bookStream = new ByteArrayOutputStream();

    public EpubBuilder(Blog blog) {
        this.blog = blog;
        this.epubName = blog.getTitle() + " - " + blog.getAuthor() + ".epub";
        build();
        try {
            new EpubWriter().write(book, bookStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public EpubBuilder(Blog blog, String fileName) {
        this.blog = blog;
        this.epubName = fileName.replace("${author}", blog.getAuthor()).replace("${title}", blog.getTitle());
        build();
        makeEpub();
    }

    public void build() {
        Metadata metadata = book.getMetadata();
        metadata.addTitle(blog.getTitle());
        metadata.addAuthor(new Author(blog.getAuthor()));

        Map<String, String> urls = new HashMap<>();
        urls.put("firstUrl", blog.firstUrl);
        urls.put("prevUrl", blog.prevUrl);

        Map<QName, String> props = new HashMap<>();
        props.put(new QName(NAMESPACE_OPF, "meta", "firstUrl"), blog.firstUrl);
        props.put(new QName(NAMESPACE_OPF, "meta", "prevUrl"), blog.prevUrl);
        metadata.setOtherProperties(props);

        Gson gson = new Gson();
        String json = gson.toJson(urls);
        Resource res = new Resource(json.getBytes(), "FeedToBookMeta.json");
        res.setMediaType(new MediaType("FeedToBookMeta", "json"));
        book.getResources().add(res);

        String titleHTML = "<h1 style=\"text-align:center\">" + blog.getTitle() + "</h1>";

        book.addSection("Title", new Resource(titleHTML.getBytes(), "title.html"));


        blog.getPosts().forEach(i -> book.addSection(i.title, new Resource(i.content.getBytes(), "chapters/" + i.title.replaceAll("[^a-zA-Z0-9\\.\\-]", "_") + ".html")));
//        buildInlineTOC(book);
    }

    public void makeEpub() {
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
