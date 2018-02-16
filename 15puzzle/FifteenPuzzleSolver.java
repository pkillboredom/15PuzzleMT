import java.util.LinkedList;
import java.util.List;


/**
 * An implementation of a solver for the 15-puzzle.  This implementation uses an A* search strategy.
 * 
 * Unlike some pictures, I define a "solved" puzzle to have the empty slot in the upper-left corner.
 * 
 * TODO: concurrency support
 * 
 * @author andrew
 */
public class FifteenPuzzleSolver {

	public static void main(String [] args) {
		int threadCount = 1;
		
		if (args.length > 0)
			threadCount = Integer.parseInt(args[0]);
		
		FifteenPuzzleSolver fps = new FifteenPuzzleSolver(threadCount);
		Board board = Board.createBoard();
		
		System.out.println("Using " + threadCount + " threads to solve this board: \n" + board);
		System.out.println();
		
		long ts = System.currentTimeMillis();
		List<Board> solution = fps.solve(board);
		long elapsed = System.currentTimeMillis() - ts;
		
		System.out.println("Found a solution with " + solution.size() + " moves!");
		System.out.println("Elapsed time: " + ((double)elapsed) / 1000.0 + " seconds");
		for (int i=0;i<solution.size();i++) {
			System.out.println(solution.get(i));
		}
		
	}
	
	public FifteenPuzzleSolver(int threadCount) {
		if (threadCount > 1)
			throw new RuntimeException("No support for multiple threads :(");		
	}
	
	public List<Board> solve (Board board) {
				
		int maxDepth = board.minimumSolutionDepth();
		
		// note: program searches forever.  At each iteration, it searchers for solutions that
		// have an increasing number of maximum moves (as reflected in maxDepth).
		while (true) {
			List<Board> solution = doSolve(board,0,maxDepth);
			
			if (solution != null) {
				return solution;
			}
			else {
				maxDepth++; // search again, with a larger maxDepth
			}
		}
	}
	
	/**
	 * Look for solution with up to maxDepth moves
	 * 
	 * @param board: The board to be solved
	 * @param solution: The list of moves so far
	 * @param currentDepth: The number of moves so far
	 * @param maxDepth: The maximum number of moves before we quit.
	 * 
	 * @return A valid solution (sequence of boards) or null to indicate failure
	 */
	private List<Board> doSolve(Board board, int currentDepth, int maxDepth) {
		if (board.isSolved()) {
			List<Board> list = new LinkedList<Board>();
			list.add(board);
			return list;
		}
		
		// stop searching if we can't solve the puzzle within our maximum depth allotment
		if ((currentDepth + board.minimumSolutionDepth()) > maxDepth)
			return null;
		
		// search for neighboring moves...
		List<Board> nextMoves = board.generateSuccessors();
		for (Board nextBoard : nextMoves) {
			List<Board> solution = doSolve(nextBoard,currentDepth+1,maxDepth);
			if (solution != null) {
				// prepend this board to the solution, and return
				solution.add(0,board);
				return solution;
			}
		}
		
		// no successor moves were fruitful
		return null;
	}
}
