package kongruenz.util;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Communication class that handles the termination condition of a group of threads.
 * Threads can notify the Communicator if more work is starting or work is done, other threads can wait for completion of work.
 * @author Thomas
 *
 */
public class Communicator {
private int workCount;
	private ThreadPoolExecutor threads;
	
	public Communicator(ThreadPoolExecutor threads){
		workCount = 0;
		this.threads = threads;
	}
	
	synchronized public void moreWorkToDo(){
		workCount++;
	}
	
	synchronized public void lessWorkToDo(){
		workCount--;
		notifyAll();
	}
	
	/**
	 * Waits till all the workers are done and the queue for tasks is empty.
	 */
	synchronized public boolean waitForDone(){
		boolean ret = false;
		while(!(workCount == 0 && threads.getQueue().peek() == null)){
			try{
				wait();
			}
			catch(InterruptedException e){
				System.err.println(e.getStackTrace());
				ret = true;
			}
		}
		return ret;
	}
}
