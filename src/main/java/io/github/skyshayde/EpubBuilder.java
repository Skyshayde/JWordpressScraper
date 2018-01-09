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
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.List;

public class EpubBuilder {
    Blog blog;
    Book book = new Book();

    public EpubBuilder(WordpressScraper b) {
        b.scrape();
        this.blog = b.blog;
    }

    public void build() {
        Metadata metadata = book.getMetadata();
        metadata.addTitle(blog.getTitle());
        metadata.addAuthor(new Author(blog.getAuthor()));

        String titleHTML = "<h1>" + blog.getTitle() + "</h1>";

        book.addSection("Title", new Resource(titleHTML.getBytes(), "title.html"));

        blog.getPosts().forEach(i -> {
            book.addSection(i.title, new Resource(i.content.getBytes(), "chapters/" + i.title.replaceAll("[^a-zA-Z0-9\\.\\-]", "_") + ".html"));
        });
        buildInlineTOC(book);
        try {
            new EpubWriter().write(book, new FileOutputStream("test.epub"));
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
