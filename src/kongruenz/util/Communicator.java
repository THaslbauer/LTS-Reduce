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
		//TODO remove
		System.out.println("more work");
		workCount++;
		//TODO remove
		System.out.println("work is now: "+workCount);
	}
	
	synchronized public void lessWorkToDo(){
		//TODO remove
		System.out.println("less work");
		workCount--;
		notifyAll();
		//TODO remove
		System.out.println("work is now: "+workCount);
	}
	
	/**
	 * Waits till all the workers are done and the queue for tasks is empty.
	 */
	synchronized public void waitForDone(){
		while(!(workCount == 0 && threads.getQueue().peek() == null)){
			try{
				wait();
			}
			catch(InterruptedException e){
				System.err.println(e.getStackTrace());
			}
		}
	}
}
