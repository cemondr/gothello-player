/**Base Code From: https://github.com/pdx-cs-ai/gothello-grossthello */
/** Cem Onder */

import java.util.Vector;
import java.lang.*;

public class ycoWorkBoard extends Board {
    static final int INF = 5 * 5 + 1;
    Move best_move = null;

    public ycoWorkBoard() {
    }

    public ycoWorkBoard(ycoWorkBoard w) {
	super(w);
    }

    int heval() {
	int nstones = 0;
	int ostones = 0;
	for (int i = 0; i < 5; i++)
	    for (int j = 0; j < 5; j++)
		if (square[i][j] == checker_of(to_move))
		    nstones++;
	        else if (square[i][j] == checker_of(opponent(to_move)))
		    ostones++;
	return nstones - ostones;
    }

    static java.util.Random prng = new java.util.Random();

    static int randint(int n) {
	return Math.abs(prng.nextInt()) % n;
    }


    /** Minimax with alpha beta prunning, given a move, depth and player status, it returns a value for the given
	 * move */
    int minimaxWithAlphaBetaPruning(Move move, int depth, int alpha, int beta, boolean isMax){
    	if (depth <= 0){
    		return heval();
		}

		ycoWorkBoard scratch = new ycoWorkBoard(this);
		int status = scratch.try_move(move);
		if (status == ILLEGAL_MOVE){
			throw new Error ("Unexpected Illegal Move");
		}
		if(status == GAME_OVER){
			throw new Error ("unexpectedly game over");
		}

		Vector<Move> childMoves = scratch.genMoves();

		if(isMax){
			int maxEval = -INF;
			for (Move childM : childMoves){
				int eval = -scratch.minimaxWithAlphaBetaPruning(childM,depth-1,alpha,beta,false);
				 maxEval = Math.max(maxEval,eval);
				alpha= Math.max(alpha,eval);
				if(beta <= alpha){
					break;
				}
			}
			return maxEval;
		}else{
			int minEval = INF;
			for(Move childM: childMoves){
				int eval = -scratch.minimaxWithAlphaBetaPruning(childM,depth-1,alpha,beta,true);
				minEval= Math.min(minEval,eval);
				beta = Math.min(beta,eval);
				if(beta<=alpha){
					break;
				}
			}
			return minEval;
		}

	}

	/** Minimax Wrapper first gets all the available positions from the  board
	 * then gets a value for each of those moves
	 * the ideal result is then assigned to the best_move field*/
	void minimax_wrapper(int depth,boolean find_move){
    	int bestValBlack = -INF;
    	int bestValWhite = INF;
    	if(!find_move){
    		return;
		}
    	Move bestOne = new Move();
    	Vector<Move> moves = genMoves();
    	int nmoves = moves.size();
		if (nmoves == 0) {
			best_move = new Move(); /**Create a new Move to (-1,-1) */
			ycoWorkBoard scratch = new ycoWorkBoard(this); /**Create a new scratch-Board exactly like the current state */
			int status = scratch.try_move(best_move);
			if (status != GAME_OVER){
				return;
			}
			int result = scratch.referee();

		}

		Vector<Move> equallyBestMoves = new Vector<Move>();

		for(Move m : moves){

			if(this.to_move == PLAYER_BLACK){
				int moveVal = minimaxWithAlphaBetaPruning(m,depth,-INF,INF,false);
				if (moveVal >= bestValBlack){
					if (moveVal > bestValBlack){
						equallyBestMoves = new Vector<Move>();
						bestOne = m;
						bestValBlack = moveVal;

					}else{
						equallyBestMoves.add(m);
					}
				}
			}else {
				int moveVal = minimaxWithAlphaBetaPruning(m,depth,-INF,INF,true);
				if (moveVal <= bestValWhite){
					if (moveVal < bestValBlack){
						equallyBestMoves = new Vector<Move>();
					}

					if(moveVal==bestValWhite){
						equallyBestMoves.add(m);
					}
					bestValWhite = moveVal;
					bestOne = m;
				}
			}
		}

		if(equallyBestMoves.size() != 0){
			bestOne = equallyBestMoves.get(randint(equallyBestMoves.size()));
		}
		best_move = bestOne;
	}


    void bestMove(int depth) {
    	minimax_wrapper(depth,true);
    }
}
