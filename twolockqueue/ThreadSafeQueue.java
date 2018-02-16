
/**
 * A thread-safe Queue implementation.  It grows without capacity, 
 * and returns null when the queue is empty.
 * 
 *  @author andrew
 */
public class ThreadSafeQueue implements SimpleQueue {

	private QueueNode head = null;
	private QueueNode tail = null;
	private int size = 0;
			
	private static class QueueNode {				
		private final Object data;
		private QueueNode next;

		public QueueNode (Object data) {
			this.data = data;
			next = null;
		}
		
		public QueueNode() {
			this.data = null;
		}		
	}
	
	// returns null if the queue is empty
	public synchronized Object dequeue() {
		if (head == null)
			return null; // empty
		
		Object retVal = head.data;
		head = head.next;
		
		size--;
		
		return retVal;	
	}

	public synchronized void enqueue(Object arg) {
		assert(arg != null);
	
		QueueNode newNode = new QueueNode(arg);
	
		if (head == null) {
			head = tail = newNode;
		}
		else {
			tail.next = newNode;
			tail = newNode;
		}
		size++;				
	}

	public synchronized int getSize() {
		return size;		
	}

	public synchronized boolean isEmpty() {
			return (head == null);
	}
}
