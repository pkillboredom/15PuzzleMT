import java.util.Random;



public class QueueTester  implements Runnable {

	private final SimpleQueue queue;
	private final String name;

	// used to generate random sleep intervals
	private final Random random = new Random();
	
	public static final long MAX_SLEEP = 2000; // milleseconds
	
	public QueueTester (SimpleQueue queue, String name) {
		this.name = name;
		this.queue = queue;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SimpleQueue queue = new ThreadSafeQueue();
		
		Thread t1 = new Thread(new QueueTester(queue,"Alpha"));
		Thread t2 = new Thread(new QueueTester(queue,"Bravo"));
		Thread t3 = new Thread(new QueueTester(queue,"Charlie"));

		t1.start();
		t2.start();
		t3.start();
	}

	public void run() {
		// loop around, enqueuing and dequeuing objects
		try {
			for (int i=0;i<8;i++) {
				long sleepTime = random.nextInt((int) MAX_SLEEP);
				Thread.sleep(sleepTime);
				
				synchronized (QueueTester.class) {
					System.out.println("Enqueue from: " + name);
					queue.enqueue("Hello from " + name);
				}
			}
			
			for (int i=0;i<8;i++) {
				long sleepTime = random.nextInt((int) MAX_SLEEP);
				Thread.sleep(sleepTime);
				
				synchronized (QueueTester.class) {
					System.out.println("Dequeue: " + queue.dequeue());
				}
			}
		}
		catch (InterruptedException ie) {}
	}


}
