package io.github.skyshayde.epub;

import com.google.gson.Gson;
import io.github.skyshayde.Blog;
import io.github.skyshayde.Post;
import io.github.skyshayde.RemoteBlog;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Map;

public class Epub {
    public RemoteBlog wp;
    public Blog blog;

    public Epub(String path) {
        this.blog = blogFromEpub(path);
        wp = new RemoteBlog(blog);
    }

    public Blog blogFromEpub(String path) {
        Blog blog = new Blog();
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
                String contents = readerToString(reader);
                blog.addPost(getPostFromString(contents));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return blog;
    }

    private Post getPostFromString(String s) {
        Document doc = Jsoup.parse(s);
        Elements e = doc.getAllElements();
        String title = e.get(0).outerHtml();
        String author = e.get(1).outerHtml();
        return new Post(title, author);
    }

    public String readerToString(Reader reader) throws IOException {
        char[] arr = new char[8 * 1024];
        StringBuilder buffer = new StringBuilder();
        int numCharsRead;
        while ((numCharsRead = reader.read(arr, 0, arr.length)) != -1) {
            buffer.append(arr, 0, numCharsRead);
        }
        reader.close();
        return buffer.toString();
    }
}
