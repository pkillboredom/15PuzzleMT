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
	public List<Board> generateSuccessorsThreaded() {
		List<Board> list = new ArrayList<Board>();
		int emptyIndex = this.getEmptyIndex();
		
		// above
        // In this version of the method, we are running new anonymous threads to do the tryAddMove concurrently.
        Future<Board> board1 = null;
		Callable<Board> tryMove1 = (new Callable<Board>() {
			@Override
			public Board call() {
				Board b = tryToAddMove(emptyIndex,emptyIndex - 4);
				return b;
			}
		});
		board1 = threadPoolRef.submit(tryMove1);
		
		// below
        Future<Board> board2 = null;
		Callable<Board> tryMove2 = (new Callable<Board>() {
			@Override
			public Board call() {
				Board b = tryToAddMove(emptyIndex, emptyIndex + 4);
				return b;
			}
		});
		board2 = threadPoolRef.submit(tryMove2);
		
		// left
        Future<Board> board3 = null;
		Callable<Board> tryMove3;
		if ( (emptyIndex % 4) != 0) {
			tryMove3 = (new Callable<Board>() {
				@Override
				public Board call() {
					Board b = tryToAddMove(emptyIndex, emptyIndex - 1);
					return b;
				}
			});
			board3 = threadPoolRef.submit(tryMove3);
		}
		
		// right
		Callable<Board> tryMove4;
		Future<Board> board4 = null;
		if ( (emptyIndex % 4) != 3) {
			tryMove4 = (new Callable<Board>() {
				@Override
				public Board call() throws Exception{
					Board b = tryToAddMove(emptyIndex, emptyIndex + 1);
					return b;
				}
			});
			board4 = threadPoolRef.submit(tryMove4);
		}

		try {
            if (board1.get() != null) list.add(board1.get());
            if (board2.get() != null) list.add(board2.get());
            if (board3 != null && board3.get() != null) list.add(board3.get());
            if (board4 != null && board4.get() != null) list.add(board4.get());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

		return list;
	}

	public List<Board> generateSuccessors() {
		List<Board> list = new ArrayList<Board>();
		int emptyIndex = this.getEmptyIndex();

		// above
		Board board1 = tryToAddMove(emptyIndex,emptyIndex - 4);

		// below
		Board board2 = tryToAddMove(emptyIndex,emptyIndex + 4);

		// left
        Board board3 = null;
		if ( (emptyIndex % 4) != 0)
			board3 = tryToAddMove(emptyIndex,emptyIndex - 1);

		// right
        Board board4 = null;
		if ( (emptyIndex % 4) != 3)
			board4 = tryToAddMove(emptyIndex,emptyIndex  + 1);

		if(board1 != null) list.add(board1);
		if(board2 != null) list.add(board2);
		if(board3 != null) list.add(board3);
		if(board4 != null) list.add(board4);

		return list;
	}

	private  final Board tryToAddMove(int emptyIndex, int targetIndex) {

		if (! isValidIndex(targetIndex))
			return null;
		int [] copy = new int[16];
		System.arraycopy (this.board,0,copy,0,16);
		
		copy[emptyIndex] = copy[targetIndex];
		copy[targetIndex] = 0;

		Board newBoardCopy = new Board(copy);
		/*try {
		    if(listRWSem.availablePermits() < 1) System.out.println("listRWSem is blocked on thread " + Thread.currentThread().getId() +". Will acquire when free...");
            listRWSem.acquire(1);
            System.out.println("listRWSem Acquired by thread " + Thread.currentThread().getId());
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
		System.out.println("listRWSem released by thread " + Thread.currentThread().getId());
		listRWSem.release(1);*/
		return newBoardCopy;
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
