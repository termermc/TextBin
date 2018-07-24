package net.termer.textbin;

public class Timebomb extends Thread {
	private boolean enabled = true;
	
	public void run() {
		while(enabled) {
			try {
				sleep(1000*60);
				
				// Decrement times
				for(Post post : Post.getAll()) {
					post.setExpireTime(post.getExpireTime()-1);
					
					// Delete if time is up
					if(post.getExpireTime()<1) {
						post.delete();
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Stops the timebomb
	 * @since 1.0
	 */
	public void disarm() {
		enabled = false;
	}
}
