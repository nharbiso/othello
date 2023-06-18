import javax.swing.*;
import java.io.*;
import java.util.*;

/**
* OthelloDriver provides input from the player to customize the game, which is 
* sent to an instantiated OthelloPanel with a JFrame
* 
* @author Max White / Nathan Harbison
* @version 1.0
*/
public class OthelloDriver
{
   /**
   * Asks the game with be loaded from a saved one, if not it gives an 
   * option panel that asks how many players there are; if there is 
   * only one player, it gives another option panel that asks if the player 
   * would like to go first or not. Also instantiates a JFrame with an 
   * OthelloPanel to which this information is sent.
   * @param args the supplied command-line arguments represented as an
   * array of Strings
   */
   public static void main(String[] args)
   {
      int game = JOptionPane.showConfirmDialog(null, "Would you like to load a saved game?", "", JOptionPane.YES_NO_OPTION);
      boolean onePlayer = false, black = false;
      Scanner scan = null;
      if(game == 0)
      {
         String gameFrom = JOptionPane.showInputDialog(null, "What game would you like to load?");
         while(true)
         {
            try {
               scan = new Scanner(new File(gameFrom+".txt"));
               break;
            } catch(FileNotFoundException e) {
               if(gameFrom == null)
                  System.exit(0);
               gameFrom = JOptionPane.showInputDialog(null, "Game not found. Please enter a valid game.");
            }
         }
         black = scan.nextBoolean();
         onePlayer = scan.nextBoolean();
      }
      else
      {
         String[] options = {"One player", "Two players"};
         int x = JOptionPane.showOptionDialog(null, "How many players?", "", 
            JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, 
            null);
      
         int y = 0;
         if(x == 0)
         {
            options = new String[] {"Black", "White"};
            y = JOptionPane.showOptionDialog(null, 
               "What color would you like to play as?", "", JOptionPane.YES_NO_OPTION, 
               JOptionPane.INFORMATION_MESSAGE, null, options, null);
         }
         if(x == -1 || y == -1)
            System.exit(0);
         else
         {
            onePlayer = x == 1;
            black = y == 0;
         }
      }
      
      JFrame frame = new JFrame("Othello Final Project");
      frame.setSize(400, 500);
      frame.setLocation(200, 100);
      frame.getContentPane().add(new OthelloPanel(onePlayer, black, scan));
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.setVisible(true);
   }
}