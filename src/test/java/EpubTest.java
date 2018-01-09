import io.github.skyshayde.EpubBuilder;
import io.github.skyshayde.WordpressScraper;

/**
 * Created by skysh on 12/30/2017.
 */

class EpubTest {
    public static void main(String[] args) {
        // A wordpress serial I read, using it to test.
        WordpressScraper scraper = new WordpressScraper("https://practicalguidetoevil.wordpress.com/2015/03/25/prologue/");
        new EpubBuilder(scraper).build();
    }
}
