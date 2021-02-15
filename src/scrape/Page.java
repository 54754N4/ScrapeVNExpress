package scrape;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Page {
	private int count;
	private String url;
	private LocalDateTime scraped;
	private List<Comment> comments;
	
	private Page(int count, String url, LocalDateTime scraped, List<Comment> comments) {
		this.url = url;
		this.count = count;
		this.scraped = scraped;
		this.comments = comments;
	}
	
	public int getCount() {
		return count;
	}
	
	public String getUrl() {
		return url;
	}

	public LocalDateTime getScraped() {
		return scraped;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public static class Builder {
		private static int count = 0;	// to number page instances
		private String url;
		private LocalDateTime scraped;
		private List<Comment> comments;
		
		public Builder() {
			comments = new ArrayList<>();
		}
		
		public Builder setUrl(String url) {
			this.url = url;
			return this;
		}
		
		public Builder setTime(LocalDateTime scraped) {
			this.scraped = scraped;
			return this;
		}
		
		public Builder setComments(Collection<Comment> comments) {
			this.comments = new ArrayList<>(comments);
			return this;
		}
		
		public Builder addComment(Comment comment) {
			comments.add(comment);
			return this;
		}
		
		public Builder addComments(Collection<Comment> comments) {
			this.comments.addAll(comments);
			return this;
		}
		
		public Page build() {
			if (url == null || url.equals(""))
				throw new IllegalArgumentException("Invalid page : empty url.");
			scraped = scraped == null ? LocalDateTime.now() : scraped;
			return new Page(count++, url, scraped, comments);
		}
	}
}
