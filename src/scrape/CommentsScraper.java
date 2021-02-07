package scrape;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class CommentsScraper {
	public static final String USER_SELECTOR = "a.nickname > b",
			BIG_USER_SELECTOR = ".content_more > .txt-name > .nickname > b",
			MESSAGE_SELECTOR = "div.content-comment > p.full_content",
			BIG_MESSAGE_SELECTOR = "div.content-comment > p.content_more",
			TIME_SELECTOR = "span.time-com",
			LOAD_SELECTOR = "p.count-reply > a.view_all_reply",
			SHRUNKEN_COMMENT = "div.content-comment > p.content_less > a.icon_show_full_comment";
	private List<Comment> comments;
	private int count = 0;
	
	public CommentsScraper(String url) {
		comments = new ArrayList<>();
		try (Browser browser = new Browser(true)) {
			WebElement commentsBox = browser
				.visit(url)	// load website
				.waitFor(By.cssSelector("div.box_comment_vne.width_common"));
			// Click on 'Xem them' first to load everything
			commentsBox.findElement(By.className("view_more_coment"))
				.click();
			// Get all comments
			commentsBox.findElements(By.cssSelector("div.comment_item.width_common"))
				.forEach(domComment -> comments.add(convertToComment(domComment)));
		}
		System.out.printf("Global comments : %d%n",comments.size());
		System.out.printf("Total comments : %d%n", count);
	}
	
	private Comment convertToComment(WebElement domComment) {
		++count;
		// Expand if big comment
		boolean shrunk;
		try {
			domComment.findElement(By.cssSelector(SHRUNKEN_COMMENT)).click();
			shrunk = true;  // expand button exists
		} catch (Exception e) { 
			shrunk = false; 
		}
		Comment.Builder builder = new Comment.Builder();
		String user = domComment.findElement(By.cssSelector(shrunk ? BIG_USER_SELECTOR : USER_SELECTOR))
					.getText();
		builder.setUser(user);
		String message = domComment.findElement(By.cssSelector(shrunk ? BIG_MESSAGE_SELECTOR : MESSAGE_SELECTOR))
				.getText()
				.substring(user.length());
		builder.setMessage(message);
		builder.setTimestamp(domComment.findElement(By.cssSelector(TIME_SELECTOR)).getText());
		Comment comment = builder.build();
		loadMore(comment, domComment);
		return comment;
	}
	
	private void loadMore(Comment parent, WebElement comment) {
		// Load all replies until load button disappears
		boolean stop = false;
		while (!stop) {
			try { 
				comment.findElement(By.cssSelector(LOAD_SELECTOR)).click();
			} catch (Exception e) {		// NoSuchElement
				stop = true;
			}
		}
		// Add replies to parent comment
		comment.findElements(By.cssSelector(".sub_comment_item.comment_item.width_common"))
			.stream()
			.map(this::convertToComment)
			.forEach(parent::addReply);
	}
	
	public void serialize(String filename) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filename), StandardCharsets.UTF_16)) {
			writer.append("[");
			for (int i=0; i<comments.size(); i++)
				writer.append(comments.get(i).toString()+(i == comments.size()-1 ? "" : ","));
			writer.append("]");
			writer.flush();
		}
	}
	
	@Override
	public String toString() {
		return Arrays.toString(comments.toArray());
	}
	
	public static class Comment {
		public final String user, message, timestamp;
		public final List<Comment> replies;
		
		private Comment(String user, String message, String timestamp) {
			this.user = user;
			this.message = message;
			this.timestamp = timestamp;
			this.replies = new ArrayList<>();
		}
		
		public Comment addReply(Comment reply) {
			replies.add(reply);
			return this;
		}
		
		private String jsonify(final int depth) {
			final String tab = tabs(depth);
			return  "\n"+
					tab + "{\n" +
					tab + "\t\"user\": \"" + user + "\",\n" +
					tab + "\t\"message\": \"" + message + "\",\n" +
					tab + "\t\"timestamp\": \"" + timestamp + "\",\n" +
					tab + "\t\"replies\": " + Arrays.deepToString(replies.stream()
						.map(comment -> comment.jsonify(depth+1))
						.collect(Collectors.toList())
						.toArray()) + "\n" +
					tab + "}";
		}
		
		@Override
		public String toString() {
			return jsonify(0);
		}
		
		// Static helper
		
		private static String tabs(int i) {
			StringBuilder sb = new StringBuilder();
			while (i-->0) sb.append("\t");
			return sb.toString();
		}
		
		public static class Builder {
			public String user, message, timestamp;
		
			public Builder setUser(String user) {
				this.user = user;
				return this;
			}
			
			public Builder setMessage(String message) {
				this.message = message;
				return this;
			}
			
			public Builder setTimestamp(String timestamp) {
				this.timestamp = timestamp;
				return this;
			}
			
			public Comment build() {
				if (user == null)
					throw new IllegalArgumentException("User cannot be null");
				if (message == null)
					throw new IllegalArgumentException("Message cannot be null");
				if (timestamp == null)
					throw new IllegalArgumentException("Timestamp cannot be null");
				return new Comment(user, message, timestamp);
			}
		}
	}
}