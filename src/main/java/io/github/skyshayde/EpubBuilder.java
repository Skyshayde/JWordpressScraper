package io.github.skyshayde;

import nl.siegmann.epublib.domain.*;
import nl.siegmann.epublib.epub.EpubWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class EpubBuilder {
    private final Blog blog;
    private final Book book = new Book();
    private String epubName = null;

    public EpubBuilder(WordpressScraper b) {
        b.scrape();
        this.blog = b.blog;
        this.epubName = blog.getTitle() + " - " + blog.getAuthor() + ".epub";
    }

    public EpubBuilder(WordpressScraper b, String fileName) {
        b.scrape();
        this.blog = b.blog;
        this.epubName = fileName.replace("${author}", blog.getAuthor()).replace("${title}", blog.getTitle());
    }

    public void build() {
        Metadata metadata = book.getMetadata();
        metadata.addTitle(blog.getTitle());
        metadata.addAuthor(new Author(blog.getAuthor()));

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
