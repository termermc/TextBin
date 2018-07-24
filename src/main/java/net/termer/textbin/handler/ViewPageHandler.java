package net.termer.textbin.handler;

import java.io.IOException;

import net.termer.textbin.Post;
import net.termer.twister.handler.RequestHandler;
import spark.Request;
import spark.Response;

public class ViewPageHandler implements RequestHandler {
	public String handle(Request req, Response res) {
		// Set type to text/html
		res.type("text/html");
		
		String r = "Unknown post ID";
		
		if(req.queryParams("id") != null) {
			String id = req.queryParams("id");
			if(Post.exists(id)) {
				if(Post.get(id).getType() == Post.Type.HTML) {
					try {
						r = Post.get(id).getContent();
					} catch (IOException e) {
						e.printStackTrace();
						r ="Error reading post";
					}
				} else {
					r = "Post is not HTML";
				}
			} else {
				r = "Invalid post ID";
			}
		}
		
		return r;
	}
}
