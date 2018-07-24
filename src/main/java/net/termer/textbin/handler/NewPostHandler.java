package net.termer.textbin.handler;

import net.termer.textbin.Module;
import net.termer.textbin.Post;
import net.termer.textbin.captcha.Captcha;
import net.termer.twister.handler.RequestHandler;
import net.termer.twister.utils.StringFilter;
import spark.Request;
import spark.Response;

/**
 * Post handler for new post
 * @author termer
 * @since 1.0
 */
public class NewPostHandler implements RequestHandler {
	
	public String handle(Request req, Response res) {
		String r = "";
		try {
			String captcha = "";
			if(req.queryParams("captcha") != null) {
				captcha = req.queryParams("captcha");
			}
			int type = 0;
			if(req.queryParams("type") != null) {
				type = Integer.parseInt(req.queryParams("type"));
			}
			
			if(Captcha.isCorrect(captcha, req)) {
				String content = "";
				if(req.queryParams("content") != null) {
					content = req.queryParams("content");
				}
				
				if(content.length()>0) {
					String id = StringFilter.generateString(10);
					
					Post.create(id, content, Module.EXPIRE_TIME, type);
					
					res.redirect("/view/?id="+id);
				} else {
					res.redirect("?error=Post+must+be+at+least+1+character+long");
				}
			} else {
				res.redirect("?error=You+did+not+solve+the+captcha+correctly");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return r;
	}

}
