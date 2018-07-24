package net.termer.textbin.handler;

import java.io.File;
import java.io.IOException;

import net.termer.twister.document.DocumentBuilder;
import net.termer.twister.handler.RequestHandler;
import spark.Request;
import spark.Response;

public class RawPageHandler implements RequestHandler {
	public String handle(Request req, Response res) {
		// Set type to text/plain
		res.type("text/plain");
		
		String r = "Unknown post ID";
		
		if(req.queryParams("id") != null) {
			String id = req.queryParams("id");
			File postFile = new File("textbin/posts/"+id);
			if(postFile.exists()) {
				try {
					r = DocumentBuilder.readFile("textbin/posts/"+id);
				} catch (IOException e) {
					e.printStackTrace();
					r+="Error reading post";
				}
			} else {
				r = "Invalid post ID";
			}
		}
		
		return r;
	}
}
