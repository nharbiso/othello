import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
* OthelloPanel acts a holder for all other parts of the Othello game, such as 
* the gameboard, scoreboard, and the resign, reset and quit buttons.
* 
* @author Max White / Nathan Harbison
* @version 1.0
*/
public class OthelloPanel extends JPanel
{
   /**
   * The gameboard used to display the current pieces and to provide 
   * interaction with the player
   */
   private OthelloGameboard gameboard;
   /**
   * The scoreboard used to display information regarding the current 
   * game on the gameboard and past games
   */
   private OthelloScoreboard scoreboard;
   /**
   * JButton used by the human player to resign their current game
   */
   private JButton resign;
   /**
   * JButton used by the human player to reset the scoreboard and 
   * current game
   */
   private JButton reset;
   /**
   * JButton used by the human player to save the current game
   */
   private JButton save;
   /**
   * JButton used by the human player to exit the current game
   */
   private JButton exit;
   /**
   * Constructs an OthelloPanel with an OthelloGameboard, OthelloScoreboard, 
   * and JButtons which all behave accordingly depending on whether or not the 
   * game is two player and if not, if the human player wishes to go first as 
   * black 
   * @param twoPlayers whether or not the game is to be played by two human 
   * players
   * @param black if the game is one player, whether or not the human player is 
   * playing as black
   * @param s Scanner object containing the information of a loaded game; null if not
   */
   public OthelloPanel(boolean twoPlayers, boolean black, Scanner scan)
   {
      setLayout(new BorderLayout());
      
      JPanel subpanel = new JPanel();
      reset = new JButton("Reset");
      reset.addActionListener(new Resetter());
      subpanel.add(reset);
      resign = new JButton("Resign");
      resign.addActionListener(new Resigner());
      subpanel.add(resign);
      save = new JButton("Save Game");
      save.addActionListener(new Saver());
      subpanel.add(save);
      exit = new JButton("Exit");
      exit.addActionListener(new Exiter());
      subpanel.add(exit);
      add(subpanel, BorderLayout.SOUTH);
      
      scoreboard = new OthelloScoreboard(twoPlayers, black);
      add(scoreboard, BorderLayout.NORTH);
      
      gameboard = new OthelloGameboard(this, scoreboard, twoPlayers, black, scan);
      add(gameboard, BorderLayout.CENTER);
     
   }
   /**
   * Sets the resign + save buttons as enabled as according to the given boolean
   * @param b whether or not the buttons are enabled
   */
   public void setButtons(boolean b)
   {
      resign.setEnabled(b);
      save.setEnabled(b);
   }
   /**
   * A Resetter is used by the reset button to reset the gameboard and 
   * scoreboard
   */
   private class Resetter implements ActionListener
   {
      /**
      * Resets the gameboard and scoreboard
      * @param e ActionEvent object the Action Listener interface needs
      */
      public void actionPerformed(ActionEvent e)
      {
         gameboard.reset();
         scoreboard.reset();
      }
   }
   /**
   * A Resigner is used by the resign button to resign the game for the 
   * current player
   */
   private class Resigner implements ActionListener
   {
      /**
      * Resigns the game for the current player
      * @param e ActionEvent object the Action Listener interface needs
      */
      public void actionPerformed(ActionEvent e)
      {
         gameboard.resignGame();
      }
   }
   /**
   * A Saver is used by the save game button to save the game at any point
   */
   private class Saver implements ActionListener
   {
      /**
      * Saves the game
      * @param e ActionEvent object the ActionListener interface needs
      */
      public void actionPerformed(ActionEvent e)
      {
         gameboard.saveGame();
      }
   }
   /**
   * An Exiter is used by the exit button to exit the game at any point
   */
   private class Exiter implements ActionListener
   {
      /**
      * Exits the game
      * @param e ActionEvent object the Action Listener interface needs
      */
      public void actionPerformed(ActionEvent e)
      {
         System.exit(0);
      }
   }
}