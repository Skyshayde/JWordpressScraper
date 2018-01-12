# JWordpressScraper
> Wordpress Scraping Library, built in Java


## Installing / Getting started

```java
WordpressScraper scraper = new WordpressScraper("URL of first post").scrape();
// ${title} and ${author} will be replaced by the actual title and author of the blog.
new EpubBuilder(scraper.blog, "${title} - ${author}");
```


## Features

* Scrape Wordpress Blog
* Create Epub file from Wordpress blogs
* Extract information from created epubs to allow for easy updating

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details