# JWordpressScraper

JWordpressScraper is a scraper for wordpress blogs, built in Java.
## Getting Started
```
WordpressScraper scraper = new WordpressScraper("URL of first wordpress post");
new EpubBuilder(scraper).build();

new EpubBuilder(scraper, "${title}.epub").build();

```

EPUB file will by default output into the working directory with a name of Title - Author.epub.  If you pass a string as the second parameter when creating the EpubBuilder object then it will use that as the name instead.  ${title} will be replaced by the title and ${author} will be replaced by the author name.

## Authors

* **Teddy Heinen** - *Primary Author* - [Skyshayde](https://github.com/Skyshayde)

See also the list of [contributors](https://github.com/Skyshayde/JWordpressScraper/graphs/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details
