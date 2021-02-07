package main;

import java.io.IOException;

import scrape.CommentsScraper;

public class Run {
	public static void main(String[] args) throws IOException {
		String url = "https://vnexpress.net/5-gio-nha-trang-don-dep-don-tan-tong-thong-4223047.html";
		CommentsScraper scraper = new CommentsScraper(url);
		scraper.serialize("comments.json");
	}
}
