package tutorium;

public class BoxedInt {
	int i;
	public synchronized int get() {
		return i;
	}
	
	public synchronized void set(int i){
		this.i = i;
	}

	public static void increment(BoxedInt c) {
		synchronized (c) {
			int i = c.get();
			c.set(++i);
		}
	}
	public static void main(String [] args) {
		BoxedInt x = new BoxedInt();
		x.set(5);
		System.out.println(x.get());
		BoxedInt.increment(x);
		System.out.println(x.get());
	}
}
