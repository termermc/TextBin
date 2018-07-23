package net.termer.textbin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import net.termer.textbin.captcha.Captcha;
import net.termer.textbin.handler.NewPostHandler;
import net.termer.textbin.handler.RawPageHandler;
import net.termer.twister.Twister;
import net.termer.twister.document.DocumentProcessor;
import net.termer.twister.document.HTMLDocumentResponse;
import net.termer.twister.module.ModulePriority;
import net.termer.twister.module.TwisterModule;
import net.termer.twister.utils.Config;
import net.termer.twister.utils.Method;
import net.termer.twister.utils.Writer;
import spark.Request;
import spark.Response;

public class Module implements TwisterModule {
	// Config values
	public static int EXPIRE_TIME = 60;
	public static String DOMAIN = "localhost";
	
	// Timebomb
	public static Timebomb TIMEBOMB = null;
	
	// Document processor
	private DocumentProcessor docPros = new DocumentProcessor() {
		public void process(HTMLDocumentResponse doc, Request req, Response res) {
			try {
				if(doc.getText().contains("%captcha")) {
					doc.replace("%captcha", Captcha.getCaptchaImageURL(req));
				}
				
				if(doc.getText().contains("%post")) {
					String post = "Unknown post ID";
					String postId = "";
					
					if(req.queryParams("id") != null) {
						postId = req.queryParams("id");
						File postFile = new File("textbin/posts/"+postId);
						if(postFile.exists()) {
							post = "";
							FileInputStream fin = new FileInputStream(postFile);
							while(fin.available()>0) {
								post+=(char)fin.read();
							}
							fin.close();
						} else {
							post = "Invalid post ID";
						}
					}
					
					String error = "";
					if(req.queryParams("error") != null) {
						error = req.queryParams("error");
					}
					
					doc.replace("%error", error);
					doc.replace("%postid", postId);
					doc.replace("%post", post);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	public String moduleName() {
		return "TextBin";
	}

	public double twiserVersion() {
		return 0.2;
	}

	public int modulePriority() {
		return ModulePriority.LOW;
	}

	public void initializeModule(Twister instance) {
		try {
			// Create config if it does not exist
			File conf = new File("textbin.ini");
			if(!conf.exists()) {
				HashMap<String,String> configMap = new HashMap<String,String>();
				configMap.put("expire-time", Double.toString(EXPIRE_TIME));
				configMap.put("domain", DOMAIN);
				Config.createConfig(conf, configMap, ":");
			}
			
			// Load values
			HashMap<String,String> loaded = Config.parseConfig(conf, ":", "#");
			EXPIRE_TIME = Integer.parseInt(loaded.get("expire-time"));
			DOMAIN = loaded.get("domain");
			
			// Create posts dir if it doesn't exist
			File postsDir = new File("textbin/posts/");
			if(!postsDir.exists()) {
				postsDir.mkdirs();
			}
			
			// Create page files if they don't exist
			File newPostDir = new File("domains/"+DOMAIN+"/new/");
			File viewPostDir = new File("domains/"+DOMAIN+"/view/");
			if(!newPostDir.exists()) {
				newPostDir.mkdirs();
			}
			if(!viewPostDir.exists()) {
				newPostDir.mkdirs();
			}
			
			File newPostPage = new File("domains/"+DOMAIN+"/new/index.html");
			File viewPostPage = new File("domains/"+DOMAIN+"/view/index.html");
			if(!newPostPage.exists()) {
				newPostPage.createNewFile();
				String page = "<span style=\"color:red\">%error</span>\n"
						+ "<br>\n"
						+ "<form method=\"POST\">\n"
						+ "<textarea name=\"content\">\n"
						+ "\n"
						+ "</textarea>\n"
						+ "<br>\n"
						+ "<img src=\"%captcha\" alt=\"captcha\"></img>\n"
						+ "<br>\n"
						+ "<label for=\"captcha\">Please enter the text above</label>\n"
						+ "<br>\n"
						+ "<input type=\"text\" name=\"captcha\">\n"
						+ "</form>";
				Writer.print(page, newPostPage);
			}
			if(!viewPostPage.exists()) {
				viewPostPage.createNewFile();
				String page = "<a href=\"/raw/?id=%postid\">View raw</a>\n"
						+ "<br>\n"
						+ "<textarea editable=\"false\">%post</textarea>";
				Writer.print(page, viewPostPage);
			}
			
		} catch (IOException e) {
			System.err.println("Failed to load TextBin configuration file, using default settings");
			e.printStackTrace();
		}
		
		// Start timebomb
		TIMEBOMB = new Timebomb();
		TIMEBOMB.start();
		
		// Register document processor
		instance.addDocumentProcessor(DOMAIN, docPros);
		
		// Register request handlers
		instance.addRequestHandler(DOMAIN, "/new/", new NewPostHandler(), Method.POST);
		instance.addRequestHandler(DOMAIN, "/raw/", new RawPageHandler(), Method.GET);
	}

	public void shutdownModule() {
		// Stop timebomb
		TIMEBOMB.disarm();
		
		// Unregister document processor
		Twister.current().removeDocumentProcessor(DOMAIN, docPros);
		
		// Unregister request handlers
		Twister.current().removeRequestHandler(DOMAIN, "/new/", Method.POST);
		Twister.current().removeRequestHandler(DOMAIN, "/raw/", Method.GET);
	}
	
}