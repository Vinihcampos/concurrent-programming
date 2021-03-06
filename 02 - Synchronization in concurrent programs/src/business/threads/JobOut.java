package business.threads;

import business.controller.BathroomManager;
import business.controller.BathroomManagerSemaphore;

public class JobOut extends Thread{
	private BathroomManager baManager;
	
	public JobOut(BathroomManager bathroomManager) {
		this.baManager = bathroomManager;
	}
	
	@Override
	public void run() {
		try {
			while(!baManager.isEmpty()) {
				Thread.sleep(1000);
				baManager.remove();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
