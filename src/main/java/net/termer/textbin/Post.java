package net.termer.textbin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import net.termer.twister.utils.Writer;

/**
 * Class to get posts and interact with them
 * @author termer
 * @since 1.0
 */
public class Post {
	private static HashMap<String,Post> POSTS = new HashMap<String,Post>();
	
	/**
	 * Creates a new post
	 * @param id the post's ID
	 * @param content the content of the post
	 * @param expire the minutes before the post will be deleted
	 * @param type the Post.Type type of the post
	 * @throws IOException if writing to post file fails
	 * @since 1.0
	 */
	public static void create(String id, String content, int expire, int type) throws IOException {
		new File("textbin/posts/"+id).createNewFile();
		Post post = new Post(id, expire, type);
		post.setContent(content);
		POSTS.put(id, post);
	}
	
	public static Post get(String id) {
		return POSTS.get(id);
	}
	
	public static boolean exists(String id) {
		return POSTS.containsKey(id);
	}
	
	public static Post[] getAll() {
		return POSTS.values().toArray(new Post[0]);
	}
	
	// Non-static
	private String ID = null;
	private int EXPIRE = 0;
	private int TYPE = 0;
	
	private Post(String id, int expire, int type) {
		ID = id;
		EXPIRE = expire;
		TYPE = type;
	}
	
	public String getID() {
		return ID;
	}
	
	public String getContent() throws IOException {
		String content = "";
		
		FileInputStream fin = new FileInputStream("textbin/posts/"+ID);
		while(fin.available()>0) {
			content+=(char)fin.read();
		}
		fin.close();
		
		return content;
	}
	
	public int getExpireTime() {
		return EXPIRE;
	}
	
	public int getType() {
		return TYPE;
	}
	
	public void setContent(String content) throws IOException {
		Writer.print(content, new File("textbin/posts/"+ID));
	}
	
	public void setExpireTime(int time) {
		EXPIRE = time;
	}
	
	public void setType(int type) {
		TYPE = type;
	}
	
	public void delete() {
		new File("textbin/posts/"+ID).delete();
		POSTS.remove(ID);
	}
	
	public static class Type {
		public static int PLAIN = 0;
		public static int HTML = 1;
	}
}
