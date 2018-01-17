import io.github.skyshayde.epub.Epub;
import io.github.skyshayde.epub.EpubBuilder;
import io.github.skyshayde.WordpressScraper;

/**
 * Created by skysh on 12/30/2017.
 */

class EpubTest {
    public static void main(String[] args) {
        // A wordpress serial I read, using it to test.
//        EpubLoader e = new EpubLoader("A Practical Guide to Evil - Erraticerrata.epub");
//        return;
        WordpressScraper scraper = new WordpressScraper("https://practicalguidetoevil.wordpress.com/2015/03/25/prologue/").scrape();
        // ${title} and ${author} will be replaced by the actual title and author of the blog.
        new EpubBuilder(scraper.blog, "${title} - ${author}.epub");

//        Epub epub = new Epub("A Practical Guide to Evil - Erraticerrata.epub");
//        return;
//        epub.wp.scrape(epub.wp.blog.prevUrl);
//        new EpubBuilder(epub.wp.blog, "${title} - ${author}.epub");
    }
}