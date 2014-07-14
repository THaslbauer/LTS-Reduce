package afg10;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Barrier implements Runnable{
	private int num;
	private int n;
	private int threads;
	protected final Lock lock = new ReentrantLock();
	private final Condition notFull = lock.newCondition();
	private final Condition full = lock.newCondition();
	private final Condition exiting = lock.newCondition();
	
	public Barrier(int n) {
		this.num = 0;
		this.n = n;
		this.threads = 0;
	}
	public synchronized void arrive() {
		while(num == n) {
			try {
				notFull.await();
			}
			catch (Exception e){}
		}
		threads++;
		num++;
		while (num < n) {
			try {
				full.await();
			}
			catch (Exception e) {}
		}
		exiting.signalAll();
	}
	public synchronized void run() {
		while(true) {
			
		}
	}
}
