import io.github.skyshayde.RemoteBlog;

/**
 * Created by skysh on 12/30/2017.
 */

class EpubTest {
    public static void main(String[] args) {
        // A wordpress serial I read, using it to test.
//        EpubLoader e = new EpubLoader("A Practical Guide to Evil - Erraticerrata.epub");
//        return;
//        WordpressScraper scraper = new WordpressScraper("https://practicalguidetoevil.wordpress.com/2015/03/25/prologue/").scrape();
//        WordpressScraper scraper = new WordpressScraper("https://tiraas.wordpress.com/2014/08/20/book-1-prologue/").scrape();
        // ${title} and ${author} will be replaced by the actual title and author of the blog.
//        new EpubBuilder(scraper.blog, "${title} - ${author}.epub");
//
//        Epub epub = new Epub("A Practical Guide to Evil - Erraticerrata.epub");
////        return;
//        epub.wp.scrape(epub.wp.blog.prevUrl);
//        new EpubBuilder(epub.wp.blog, "${title} - ${author}.epub");
        RemoteBlog test = new RemoteBlog("https://practicalguidetoevil.wordpress.com/2015/03/25/prologue/");
        test.forEach(i -> {
            System.out.println(i.title);
        });


    }
}