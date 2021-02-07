package main;

import java.io.IOException;

import scrape.CommentsScraper;

public class Run {
	public static void main(String[] args) throws IOException {
		String url = "https://vnexpress.net/buoi-tong-duyet-le-nham-chuc-cua-biden-4223129.html";
		CommentsScraper scraper = new CommentsScraper(url);
		scraper.serialize("comments.json");
	}
}
