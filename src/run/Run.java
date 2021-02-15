package run;

import java.io.IOException;
import java.util.List;

import scrape.CommentsScraper;
import scrape.Page;

/*  https://jsonformatter.curiousconcept.com/#
 *  for JSON pretty-printing since GSON makes it ugly */
@SuppressWarnings("unused")
public class Run {
	public static void main(String[] args) throws IOException {
		String filename = "comments.json";
		String[] urls = {
			"https://vnexpress.net/5-gio-nha-trang-don-dep-don-tan-tong-thong-4223047.html",
			"https://vnexpress.net/them-mot-nguoi-nhat-o-ha-noi-duong-tinh-ncov-4235646.html"
		};
		CommentsScraper scraper = new CommentsScraper(urls);
		scraper.serialize(filename);
		// Deserialization doesn't work yet
//		List<Page> pages = CommentsScraper.deserialize(filename);
//		pages.forEach(page -> System.out.println(page.getCount()));
	}
}
