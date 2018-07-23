package net.termer.textbin;

import java.io.File;
import java.util.HashMap;

public class Timebomb extends Thread {
	private HashMap<String,Integer> posts = new HashMap<String,Integer>();
	private boolean enabled = true;
	
	public void run() {
		while(enabled) {
			try {
				sleep(1000*60);
				
				// Decrement times
				String[] keys = posts.keySet().toArray(new String[0]);
				Integer[] vals = posts.values().toArray(new Integer[0]);
				
				for(int i = 0; i < keys.length; i++) {
					posts.put(keys[i], new Integer(vals[i]-1));
					if(posts.get(keys[i]).intValue()<1) {
						System.out.println(new File("textbin/posts/"+keys[i]).delete());
						posts.remove(keys[i]);
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Register a post to be deleted
	 * @param id the post id
	 * @param time the time before it gets deleted
	 * @since 1.0
	 */
	public void register(String id, int time) {
		posts.put(id, new Integer(time));
	}
	
	/**
	 * Stops the timebomb
	 * @since 1.0
	 */
	public void disarm() {
		enabled = false;
	}
}
