import com.google.gson.Gson;
import io.github.skyshayde.EpubBuilder;
import io.github.skyshayde.EpubLoader;
import io.github.skyshayde.WordpressScraper;
import io.github.skyshayde.Blog;

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
        new EpubBuilder(scraper.blog, "${title} - ${author}");
    }
}
