package main;

import java.io.IOException;

import scrape.CommentsScraper;

public class Run {
	public static void main(String[] args) throws IOException {
		String url = "https://vnexpress.net/thuong-nghi-si-my-hung-hau-qua-vi-ung-ho-trump-4222651.html";
		CommentsScraper scraper = new CommentsScraper(url);
		scraper.serialize("comments.txt");
	}
	
	// .count-reply > .view_all_reply

}
