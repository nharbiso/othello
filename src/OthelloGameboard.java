import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Scanner;
/**
 * OthelloGameboard isa JPanel that knows information regarding the current 
 * Othello game such as the number of chips each player has and who's turn 
 * is currently is. It displays the current pieces for each player, updates 
 * the scoreboard as needed, conducts moves as specified by the human player 
 * or OthelloAI, and resets the gameboard as needed.
 * 
 * @author Nathan Harbison / Max White
 * @version 1.0
*/
public class OthelloGameboard extends JPanel
{
   /**
   * An array containing all colors used by the JButtons on the gameboard
   */
   private final Color[] colors = {Color.GREEN.darker(), Color.BLACK, 
   Color.WHITE};
   /**
   * The index value of the current player's color
   */
   private int currColorIndex;
   /**
   * The amount of turns that have been skipped consecutively
   */
   private int countNoTurns = 0;
   /**
   * A matrix of JButtons that each represent a certain piece on the gameboard
   */
   private JButton [][] board = new JButton[8][8];
   /**
   * An int matrix that represents the JButton matrix, with each index the index 
   * value of the corresponding JButton's color
   */
   private int[][] matrix = new int[8][8];
   /**
   * An int matrix that consists of all possible moves for the current player
   */
   private int[][] currPosMoves = new int[0][0];
   /**
   * An int matrix that mimics the JButton matrix and contains the index value 
   * of a move located the index of starting position of that move
   */
   private int[][] currMoves = new int[8][8];
   /**
   * Whether or not the game is being played by two human players
   */
   private boolean twoPlayers;
   /**
   * The current number of chips player one has 
   */
   private int p1Chips;
   /**
   * The number of chips player two has
   */
   private int p2Chips;
   /**
   * The scoreboard adjacently contained within the OthelloPanel
   */
   private OthelloScoreboard s;
   /**
   * The OthelloPanel the gameboard is contained in
   */
   private OthelloPanel p;
   /**
   * Executes the AI's move after a certain specified delay
   */
   private Timer AImover = new Timer(3000, new AIMover());
   /**
   * Executes a move with a delay between each piece being captured
   */
   private Timer mover = new Timer(500, new Mover());
   /**
   * The current move to be executed
   */
   private int[] move;
   /**
   * Contains the game to be loaded
   */
   private Scanner scan;
   
   /**
   * Constructs an OthelloScoreboard that responds accordingly depending on 
   * whether the game is two player, and if not, if a player wishes to play 
   * as black or white
   * @param pa the OthelloPanel the gameboard is contained in
   * @param sc the scoreboard adjacently contained within the OthelloPanel
   * @param twoPlayer whether or not the game is to be played by two human 
   * players
   * @param black if two player, whether or not the human player's color is 
   * black or white
   * @param sca Scanner object containing the information of a loaded game; null if not
   */
   public OthelloGameboard (OthelloPanel pa, OthelloScoreboard sc, 
   boolean twoPlayer, boolean black, Scanner sca)
   {
      scan = sca;
      s = sc;
      p = pa;
      p1Chips = p2Chips = 2;
      setLayout(new GridLayout(8, 8, 2, 2));
      for(int x = 0; x < board.length; x++)
         for(int y = 0; y < board[0].length; y++)
         {
            board[x][y] = new JButton();
            board[x][y].setBackground(colors[0]);
            board[x][y].addActionListener(new Piece(x, y));
            add(board[x][y]);
         }
      setUpCenter();
      if(scan != null)
         loadGame();
      else
      { 
         currColorIndex = 2;
         twoPlayers = twoPlayer;
      }
      if(!twoPlayer)
      {
         OthelloAI.setFirst(!black);
         if(black)
            OthelloAI.setColor(2);
         else
            OthelloAI.setColor(1);
      }
      setUpCurrMoves();
   }
   /**
   * Removes all pieces from the gameboard, placing the starting
   * four pieces in the center
   */
   public void reset()
   {
      mover.stop();
      AImover.stop();
      for(int x = 0; x < board.length; x++)
         for(int y = 0; y < board[0].length; y++)
         {
            board[x][y].setEnabled(false);
            matrix[x][y] = 0;
            board[x][y].setBackground(colors[0]);
         }
      setUpCenter();
      currColorIndex = 2;
      p1Chips = p2Chips = 2;
      setUpCurrMoves();
      if(!OthelloAI.isFirst() && !twoPlayers)
         p.setButtons(true);
   }
   /**
   * Resigns the game for the current player, updating the scoreboard 
   * appropriately and resetting the board
   */
   public void resignGame()
   {
      if(currColorIndex == 2)
         s.winLoss(1);
      else
         s.winLoss(2);
      reset();
   }
   /**
   * Sets up the center pieces as black and white accordingly, as needed to 
   * start a new game
   */
   private void setUpCenter()
   {
      for(int x = 3; x <= 4; x++)
         for(int y = 3; y <= 4; y++)
         {
            if(x == y)
            {
               board[x][y].setBackground(colors[2]);
               matrix[x][y] = 2;
            }
            else
            {
               board[x][y].setBackground(colors[1]);
               matrix[x][y] = 1;
            }
            board[x][y].setEnabled(false);
         }
   }
   /**
   * Finds all possible moves for the current player, and if they have none, 
   * skips to the next player and displays a message. If this happens twice, 
   * the game ends. Otherwise, it makes the JButton for each of the current 
   * possible moves for the player a darker color, and if it is a human 
   * player's turn, makes these JButtons clickable.
   */
   private void setUpCurrMoves()
   {
      currPosMoves = OthelloAI.legalMoves(matrix, currColorIndex == 2);
      if(currPosMoves.length == 0)
      {
         String s = "";
         if(twoPlayers)
         {
            if(currColorIndex == 1)
               s = "Player two has no valid moves. Their turn has been skipped.";
            else
               s = "Player one has no valid moves. Their turn has been skipped.";
         }
         else
         {
            if(currColorIndex == OthelloAI.getColor())
               s = "You have no valid moves. Your turn has been skipped.";
            else
               s = "The AI has no valid moves. Its turn has been skipped.";
         }
         if(p1Chips + p2Chips == 64)
         {
            endGame();
            return;
         }
         else
            JOptionPane.showMessageDialog(null, s);
         countNoTurns++;
         if(countNoTurns == 2)
         {
            endGame();
            return;
         }
         switchColor();
         setUpCurrMoves();
         return;
      } 
      countNoTurns = 0;
      for(int x = 0; x < board.length; x++)
         for(int y = 0; y < board[0].length; y++)
         {
            board[x][y].setEnabled(false);
            currMoves[x][y] = -1;
         }
      for(int x = 0; x < currPosMoves.length; x++)
      {
         if(twoPlayers || currColorIndex == OthelloAI.getColor())
            board[currPosMoves[x][0]][currPosMoves[x][1]].setEnabled(true);
         board[currPosMoves[x][0]][currPosMoves[x][1]].setBackground(
            colors[0].darker());
         currMoves[currPosMoves[x][0]][currPosMoves[x][1]] = x;
      }
      switchColor();
      if(OthelloAI.isFirst() && !twoPlayers && currColorIndex == 
      OthelloAI.getColor())
      {
         AImover.start();
         p.setButtons(false);
      }
   }
   /**
   * Switches the color of the current player pieces to the color of the other 
   * player's pieces
   */
   private void switchColor()
   {
      if(currColorIndex == 1)
         currColorIndex = 2;
      else
         currColorIndex = 1;
   }
   /**
   * Ends the game, updating the scoreboard depending on the number of chips
   * each player has
   */
   private void endGame()
   {
      int whoWon;
      if(p1Chips == p2Chips)
         whoWon = 3;
      else if(p1Chips > p2Chips)
         whoWon = 1;
      else
         whoWon = 2;
      s.winLoss(whoWon);
      mover.stop();
      reset();
   }
   /**
   * A Piece knows the row and column of its corresponding button
   * and executes the move associated with this button
   */
   private class Piece implements ActionListener
   {
      /**
      * Its corresponding button's row
      */
      private int r;
      /**
      * Its corresponding button's column
      */
      private int c;
      /**
      * Constructs a Piece with a specified row and column value
      * @param x the row value
      * @param y the column value
      */
      public Piece(int x, int y)
      {
         r = x;
         c = y;
      }
      /**
      * Executes the move found by the index value (as specified by the
      * corresponding index currMoves) of all current possible moves
      * @param e ActionEvent object the Action Listener interface needs
      */
      public void actionPerformed(ActionEvent e)
      {
         for(int x = 0; x < board.length; x++)
            for(int y = 0; y < board[0].length; y++)
               board[x][y].setEnabled(false);
         executeMove(currPosMoves[currMoves[r][c]]);
      }
   }
   /**
   * A AIMover executes the AI's best move after a certain delay
   */
   private class AIMover implements ActionListener
   {
      /**
      * Executes the AI's best move based on its net chip gain
      * @param e ActionEvent object the Action Listener interface needs
      */
      public void actionPerformed(ActionEvent e)
      {
         OthelloAI.doAIMove(matrix, board, OthelloGameboard.this);
         AImover.stop();
      }
   }
   /**
   * Executes a specified move
   * @param m the move to be executed
   */
   public void executeMove(int[] m)
   {
      move = m;
      if(currColorIndex == 1)
         p1Chips++;
      else
         p2Chips++;
      s.update(currColorIndex == 1, p1Chips, p2Chips);
      board[move[0]][move[1]].setBackground(colors[currColorIndex]);
      board[move[0]][move[1]].setEnabled(false);
      matrix[move[0]][move[1]] = currColorIndex;
      mover.start();
   }
   /**
   * A Mover executes a given move, with a slight delay between
   * each piece being captured
   */
   private class Mover implements ActionListener
   {
      /**
      * The current index value of the array that specifies the move that 
      * is being looked at
      */
      private int x = 2;
      /**
      * The row of the starting position of a move
      */
      private int row;
      /**
      * The column of the starting position of a move
      */
      private int col;
      /**
      * The current row of the gameboard the Mover object is looking at
      */
      private int mrow;
      /**
      * The current column of the gameboard the Mover object is looking at
      */
      private int mcol;
      /**
      * Whether or not this is the first piece to be captured
      */
      private boolean first = true;
      /**
      * Finds the next piece to be captured and changes it to the current
      * player's color; if there are no more pieces, the move is ended
      * @param e ActionEvent object the Action Listener interface needs
      */
      public void actionPerformed(ActionEvent e)
      {
         if(first)
         {
            first = false;
            mrow = row = move[0];
            mcol = col = move[1];
            matrix[row][col] = currColorIndex;
         }
         while(matrix[mrow][mcol] == currColorIndex)
         {
            mrow += move[x];
            mcol += move[x+1];
         }
         if(currColorIndex == 1)
         {
            p1Chips++;
            p2Chips--;
         }
         else
         {
            p1Chips--;
            p2Chips++;
         }
         s.update(currColorIndex == 1, p1Chips, p2Chips);
         board[mrow][mcol].setBackground(colors[currColorIndex]);
         board[mrow][mcol].setEnabled(false);
         matrix[mrow][mcol] = currColorIndex;  
         try {
            if(matrix[mrow+move[x]][mcol+move[x+1]] == currColorIndex)
            {
               x += 4;  
               mrow = move[0];
               mcol = move[1];
               if(move[x+2] == -1 || x >= move.length)
                  throw new ArrayIndexOutOfBoundsException();
            }
         } catch(ArrayIndexOutOfBoundsException ex) {
            s.update(currColorIndex == 2, p1Chips, p2Chips);
            end();
         } 
      }
      /**
      * Ends the move, setting up the current moves for the next player, and if
      * that player is the AI, executes its move after a delay
      */
      private void end()
      {
         first = true;
         x = 2;
         for(int x = 0; x < currPosMoves.length; x++)
            if(board[currPosMoves[x][0]][currPosMoves[x][1]].getBackground()
            .getRGB() == colors[0].darker().getRGB())
               board[currPosMoves[x][0]][currPosMoves[x][1]].setBackground(
                  colors[0]);
         setUpCurrMoves();
         if(!OthelloAI.isFirst() && !twoPlayers && currColorIndex == 
         OthelloAI.getColor())
         {
            AImover.start();
            p.setButtons(false);
         }
         else if(!twoPlayers && currColorIndex != OthelloAI.getColor())
            p.setButtons(true);
         mover.stop();
      }
   }
   /**
   * Saves the current game under a certain name, so that it can be accessed later
   */
   public void saveGame()
   {
      String name = JOptionPane.showInputDialog(null, "What would you like to name the saved game?");
      PrintWriter outfile = null;
      try {
         outfile = new PrintWriter(new FileWriter(name+".txt"));
      } catch(IOException e) {
         e.printStackTrace();
         System.exit(0);
      }
      outfile.println(!OthelloAI.isFirst());
      outfile.println(twoPlayers+"");
      outfile.println(s.getScore());
      outfile.println(currColorIndex % 2 + 1); //1 % 2 = 1 + 1 = 2    2 % 2 = 0 + 1 = 1
      for(int x = 0; x < matrix.length; x++)
         for(int y = 0; y < matrix[0].length; y++)
            outfile.println(matrix[x][y]);
      outfile.close();
   }
   /**
   * Loads a game, using the name specified when the game is started
   */
   public void loadGame()
   {
      try {
         s.setScore(scan.next());
         currColorIndex = scan.nextInt();
         for(int x = 0; x < matrix.length; x++)
            for(int y = 0; y < matrix[0].length; y++)
            {
               matrix[x][y] = scan.nextInt();
               board[x][y].setBackground(colors[matrix[x][y]]);
               if(matrix[x][y] != 0)
                  board[x][y].setEnabled(false);
            }
      } catch(Exception e) {
         System.out.println("Error: Could not load game.");
         System.exit(0);
      }
   }
}