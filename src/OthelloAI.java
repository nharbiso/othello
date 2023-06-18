import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
/**
 *  OthelloAI is a class that provides valuable support 
 *  to the gameboard. Most importantly, it finds all legal moves
 *  for a specified player based on the gameboard, and also
 *  conducts the AI's best possible move, based on the highest
 *  net chip gain, if needed.
 *
 *  @author Nathan Harbison / Max White
 *  @version 1.0
 **/
public class OthelloAI
{
   /**
   * An array containing all colors used by the JButtons on the gameboard
   */
   private static final Color[] colors = {Color.GREEN.darker(), Color.BLACK, 
   Color.WHITE};
   /**
   * Tells whether the AI's turn is before the player
   */
   private static boolean isFirst;
   /**
   * The index value of the color of the AI's piece
   */
   private static int myColor;
   /**
   * A matrix containing all the possible directions that pieces can be captured
   */
   private static final int[][] directions = {{-1, 0}, {-1, 1}, {0, 1}, {1, 1}, 
   {1, 0}, {1, -1}, {0, -1}, {-1, -1}};
   /**
   * Sets the color of the AI's piece
   * @param x the index value of the AI's piece color according to the 
   * array colors
   */
   public static void setColor(int x)
   {
      myColor = x;
   }
   /**
   * Returns the index value of the color of the AI's piece
   * @return the index value of the AI's color
   */
   public static int getColor()
   {
      return myColor;
   }
   /**
   * Sets the AI's turn in relation to the player
   * @param b whether or not the AI is first
   */
   public static void setFirst(boolean b)
   {
      isFirst = b;
   }
   /**
   * Returns the AI's turn in relation to the player
   * @return whether or not the AI is first
   */
   public static boolean isFirst()
   {
      return isFirst;
   }
   /**
   * Based on the current pieces on the board, conducts the AI's move
   * @param m an int matrix representing JButton of the gameboard, composed of 
   * the index values of the colors of the corresponding piece
   * @param board a matrix of JButtons that the gameboard is composed of
   * @param g the OthelloGameboard executing the command
   */
   public static void doAIMove(int[][] m, JButton[][] board, OthelloGameboard g)
   {
      int[][] moves = legalMoves(m, isFirst);
      if(moves.length == 0)
         return;
      if(moves.length == 1)
      {
         g.executeMove(moves[0]);
         return;
      }
      int[] netGain = new int[moves.length];
      for(int x = 0; x < moves.length; x++)
      {
         if((moves[x][0] == 0 && moves[x][1] == 0) || (moves[x][0] == 0 && 
         moves[x][1] == 7) || (moves[x][0] == 7 && moves[x][1] == 0) || 
         (moves[x][0] == 7 && moves[x][1] == 7))
         {
            g.executeMove(moves[x]);
            return;
         }
         netGain[x] = netChipGain(moves[x], m);
      }
      int maxPos = 0;
      for(int x = 1; x < netGain.length; x++)
         if(netGain[x] > netGain[maxPos])
            maxPos = x;
      int tmp = maxPos;
      boolean allCorners = false;
      while(isCorner(moves[maxPos], m))
      {
         allCorners = true;
         netGain[maxPos] = Integer.MIN_VALUE;
         maxPos = 0;
         for(int x = 1; x < netGain.length; x++)
            if(netGain[x] > netGain[maxPos])
               maxPos = x;
         for(int x = 0; x < netGain.length; x++)
            if(netGain[x] != Integer.MIN_VALUE)
               allCorners = false;
         if(allCorners)
            break;
      }
      if(allCorners)
         g.executeMove(moves[tmp]);
      else
         g.executeMove(moves[maxPos]);
   }
   /**
   * For a certain move, finds the net gain of a move, calculated from the gain 
   * of chips and loss of chips from the opponents next best move
   * @param move an array containing information about a possible move,
   * @param board an int matrix representing the current buttons on the gameboard
   * @return the net gain of chips for a certain move
   */
   private static int netChipGain(int[] move, int[][] board)
   {
      int[][] m = makeCopyOf(board);
      int chipsGained = chipsGained(move, m, true);
      //m should be changed now
      
      int[][] moves = legalMoves(m, !isFirst);
      if(moves.length == 0)
         return chipsGained;
      
      int[] netGains = new int[moves.length];
      for(int x = 0; x < moves.length; x++)
         netGains[x] = chipsGained(moves[x], makeCopyOf(m), false);
      int maxPos = 0;
      for(int x = 1; x < netGains.length; x++)
         if(netGains[x] > netGains[maxPos])
            maxPos = x;
      return chipsGained - netGains[maxPos];
   }
   /**
   * Creates a copy of a specified matrix
   * @param s the matrix to be copied
   * @return a copy of the matrix
   */
   private static int[][] makeCopyOf(int[][] s)
   {
      int[][] m = new int[s.length][s[0].length];
      for(int x = 0; x < s.length; x++)
         m[x] = Arrays.copyOf(s[x], m[0].length);
      return m;
   }
   /**
   * Finds the number of chips that are gained from a certain move
   * @param move an array containing information about a possible move
   * @param m an int matrix representing the gameboard
   * @param me whether or not the moves is the AI's or not
   * @return the number of chips captured with the specified move
   */
   private static int chipsGained(int[] move, int[][] m, boolean me)
   {
      int color;
      if(me)
         color = myColor;
      else
      {
         if(myColor == 1)
            color = 2;
         else
            color = 1;
      }
      int chipsGained = 1;
      for(int x = 2; x < move.length; x += 4)
      {
         if(move[x+2] == -1)
            break;
         int row = move[0], col = move[1];
         m[row][col] = color;
         while(row != move[x+2] || col != move[x+3])
         {
            row += move[x];
            col += move[x+1];
            if(m[row][col] != color)
               chipsGained++;
            m[row][col] = color;
         }
      }
      return chipsGained;
   }
   /**
   * Checks if a certain move would allow the other player to play in the corner
   * @param move the move to be checked
   * @param board an int matrix representing the current Othello gameboard
   * @return whether or not the given move allows the other player to player 
   * in the corner
   */
   private static boolean isCorner(int[] move, int[][] board)
   {
      if(move[0] <= 1 && move[1] <= 1 && board[0][0] == 0)
         return true;
      if(move[0] >= 6 && move[1] <= 1 && board[7][0] == 0)
         return true;
      if(move[0] <= 1 && move[1] >= 6 && board[0][7] == 0)
         return true;
      if(move[0] >= 6 && move[1] >= 6 && board[7][7] == 0)
         return true;
      
      return false;
   }
   
   /**
   * Finds all the possible legal moves for a specified player
   * @param board an int matrix representing the gameboard
   * @param playerOne whether or not the current player is player one
   * @return all legal moves for a specified player
   */
   public static int[][] legalMoves(int[][] board, boolean playerOne)
   {
      int[][] z = new int[64][34];
      int rlength = 0;
      int clength = 0;
      for(int x = 0; x < board.length; x++)
         for(int y = 0; y < board[0].length; y++)
            if(board[x][y] == 0)
            {
               int[] startingPos = new int[32];
               for(int s = 0; s < 32; s++)
                  startingPos[s] = -1;
               int l = 0;
               for(int d = 0; d < 8; d++)
               {
                  int[] dir = findPossibleDirection(board, directions[d], x, 
                  y, playerOne);
                  if(dir[0] != -1)
                  {
                     startingPos[l] = directions[d][0];
                     startingPos[l+1] = directions[d][1];
                     startingPos[l+2] = dir[0];
                     startingPos[l+3] = dir[1];
                     l += 4;
                  } 
               }
               if(l > 0)
               {
                  if(l+2 > clength)
                     clength = l+2;
                  int[] arr = new int[34];
                  arr[0] = x;
                  arr[1] = y;
                  System.arraycopy(startingPos, 0, arr, 2, startingPos.length);
                  z[rlength] = arr;
                  rlength++;
               }             
            }
      int[][] fin = new int[rlength][clength];
      for(int x = 0; x < fin.length; x++)
         for(int y = 0; y < fin[0].length; y++)
            fin[x][y] = z[x][y];
      return fin;
   }
   /**
   * Determines if a direction for a certain blank piece is in a straight line 
   * with one of the current player's pieces, indicating a possible legal move
   * @param board an int matrix representing the gameboard
   * @param dir an array specifying the direction to be checked
   * @param r the row of the starting piece
   * @param c the column of the starting piece
   * @param playerOne whether or not the current player is player one
   * @return an int array of the end position of a move if legal
   */
   private static int[] findPossibleDirection(int[][] board, int[] dir, int r, 
   int c, boolean playerOne)
   {
      int you, opponent;
      if(playerOne)
      {
         you = 1;
         opponent = 2;
      }
      else
      {
         you = 2;
         opponent = 1;
      }
      int[] loc = {-1};
      try {
         if(board[r+dir[0]][c+dir[1]] == opponent)
         {
            while(board[r+dir[0]][c+dir[1]] != 0)
            {
               r += dir[0];
               c += dir[1];
               if(board[r][c] == you)
               {
                  loc = new int[] {r, c};
                  break;
               }
            }
         }
      } 
      catch(ArrayIndexOutOfBoundsException e) {
         return loc;}
      if(loc[0] == -1)
         return loc;
      return loc;
   }
}
