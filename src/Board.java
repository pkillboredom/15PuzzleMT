import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;


/**
 * This class represents the board for the fifteen-tile puzzle.
 * 
 * @author andrew
 *
 */
public class Board {

	// use a fixed random number seed, for reproducibility
	private static final Random random = new Random(11023);
	private static final int INITIALIZATION_MOVES = 55;
	
	private final int[] board;
	public Semaphore listRWSem = new Semaphore(1);

	public boolean boardMade = false;
	public static ExecutorService threadPoolRef;
	
	/**
	 * Create a randomized initial board
	 */
	public static Board createBoard() {
		Board b = new Board();
		for (int i = 0;i<INITIALIZATION_MOVES;i++) {
			List<Board> nextBoards = b.generateSuccessors();
			
			int indx = random.nextInt(nextBoards.size());
			b = nextBoards.get(indx);
		}
		
		return b;		
	}
	
	// initialize a solved board
	public Board () {
		board = new int[16] ;
	
		for (int i=0;i<16;i++)
			board[i]= i;
	}

	
	private Board (int [] b) {
		this.board = b;
	}
	
	private static final boolean isValidIndex(int i) {
		return ((i>=0) && (i < 16));
	}
	
	private int getEmptyIndex() {
		// find the empty square
		int emptyIndex = -1;
		for (int i=0;i<16;i++) {
			if (board[i] == 0) {
				emptyIndex = i;
				break;
			}
		}
		
		assert (isValidIndex(emptyIndex));
		return emptyIndex;
	}
	
	// generate all valid board configurations within one move of this board
	public synchronized List<Board> generateSuccessorsThreaded() {
		List<Board> list = new ArrayList<Board>();
		int emptyIndex = this.getEmptyIndex();
		Semaphore tryPermits = new Semaphore(4); // use this semaphore we only return a done list.
		
		// above
        // In this version of the method, we are running new anonymous threads to do the tryAddMove concurrently.
		Runnable tryMove1 = (new Runnable() {
			@Override
			public void run() {
				try {
					tryPermits.acquire(1);
				}
				catch (InterruptedException e){
					e.printStackTrace();
				}
				tryToAddMove(list,emptyIndex,emptyIndex - 4);
				tryPermits.release(1);
			}
		});
		threadPoolRef.execute(tryMove1);
		
		// below
		Runnable tryMove2 = (new Runnable() {
			@Override
			public void run() {
				try {
					tryPermits.acquire(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				tryToAddMove(list, emptyIndex, emptyIndex + 4);
				tryPermits.release(1);
			}
		});
		threadPoolRef.execute(tryMove2);
		
		// left
		Runnable tryMove3;
		if ( (emptyIndex % 4) != 0) {
			tryMove3 = (new Runnable() {
				@Override
				public void run() {
					try {
						tryPermits.acquire(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					tryToAddMove(list, emptyIndex, emptyIndex - 1);
					tryPermits.release(1);
				}
			});
			threadPoolRef.execute(tryMove3);
		}
		
		// right
		Runnable tryMove4;
		if ( (emptyIndex % 4) != 3) {
			tryMove4 = (new Runnable() {
				@Override
				public void run() {
					try {
						tryPermits.acquire(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					tryToAddMove(list, emptyIndex, emptyIndex + 1);
					tryPermits.release(1);
				}
			});
			threadPoolRef.execute(tryMove4);
		}

		try {
			tryPermits.acquire(4);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		tryPermits.release(4); // we immediately release cause we just want to block until above threads are done.

		return list;
	}

	public List<Board> generateSuccessors() {
		List<Board> list = new ArrayList<Board>();
		int emptyIndex = this.getEmptyIndex();

		// above
		tryToAddMove(list,emptyIndex,emptyIndex - 4);

		// below
		tryToAddMove(list,emptyIndex,emptyIndex + 4);

		// left
		if ( (emptyIndex % 4) != 0)
			tryToAddMove(list,emptyIndex,emptyIndex - 1);

		// right
		if ( (emptyIndex % 4) != 3)
			tryToAddMove(list,emptyIndex,emptyIndex  + 1);

		return list;
	}

	private  final void tryToAddMove(List<Board> list, int emptyIndex, int targetIndex) {

		if (! isValidIndex(targetIndex))
			return;
		int [] copy = new int[16];
		System.arraycopy (this.board,0,copy,0,16);
		
		copy[emptyIndex] = copy[targetIndex];
		copy[targetIndex] = 0;

		Board newBoardCopy = new Board(copy);
		try {
            listRWSem.acquire(1);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
		list.add(newBoardCopy);
		listRWSem.release(1);
	}
	
	public boolean isSolved() {
		for (int i=0;i<16;i++) {
			if (board[i] != i)
				return false;
		}
		
		return true;				
	}

	/**
	 * Returns the minimum number of moves that could result in a solution for this board
	 * We use simply "manhattan distance" for this.
	 */
	public int minimumSolutionDepth() {
		int emptyIndex = this.getEmptyIndex();
		int row = emptyIndex / 4;
		int col = emptyIndex % 4;
		
		return row + col;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i=0;i<board.length;i++) {
			sb.append(board[i] + " ");
			
			if ( (i % 4) == 3)
				sb.append("\n");
		}
		
		return sb.toString();
	}
}
