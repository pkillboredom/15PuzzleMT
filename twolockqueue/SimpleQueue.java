

public interface SimpleQueue { 
	public void enqueue(Object arg);
	public Object dequeue();
	public int getSize();
	public boolean isEmpty();
}
