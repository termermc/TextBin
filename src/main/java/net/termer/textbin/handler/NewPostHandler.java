package net.termer.textbin.handler;

import java.io.File;

import net.termer.textbin.Module;
import net.termer.textbin.captcha.Captcha;
import net.termer.twister.handler.RequestHandler;
import net.termer.twister.utils.StringFilter;
import net.termer.twister.utils.Writer;
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
			
			if(Captcha.isCorrect(captcha, req)) {
				String content = "";
				if(req.queryParams("content") != null) {
					content = req.queryParams("content");
				}
				
				if(content.length()>0) {
					String id = StringFilter.generateString(10);
					
					File postFile = new File("textbin/posts/"+id);
					postFile.createNewFile();
					Writer.print(content, postFile);
					
					Module.TIMEBOMB.register(id, Module.EXPIRE_TIME);
					
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
