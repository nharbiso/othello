import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.text.DecimalFormat;

/**
* The OthelloScoreboard receives and displays information  
* regarding the current game state and past game results	 
* 
* @author Max White / Nathan Harbison
* @version 1.0
*/

public class OthelloScoreboard extends JPanel
{
   /**
   * JLabel that indicates that it is player one’s turn by being highlighted
   */
   private JLabel p1;
   /**
   * JLabel that indicates that it is player two’s turn by being highlighted
   */
   private JLabel p2;
   /**
   * JLabel that shows the current amount of wins player one has
   */
   private JLabel p1Wins;
   /**
   * JLabel that shows the current amount of wins player two has
   */
   private JLabel p2Wins;
   /**
   * JLabel that shows the percentage of total games that player one has won
   */
   private JLabel p1Percent;
   /**
   * JLabel that shows the percentage of total games that player two has won
   */
   private JLabel p2Percent;
   /**
   * JLabel that shows the number of ties that have occurred
   */
   private JLabel ties;
   /**
   * JLabel that shows the current number of chips player one has
   */
   private JLabel p1Chips;
   /**
   * JLabel that shows the current number of chips player two has
   */
   private JLabel p2Chips;
   /**
   * JLabel that shows how many games have already occurred
   */
   private JLabel games;
   /**
   * An int that stores how many times player one has won a game
   */
   private int pOneWins;
   /**
   * An int that stores how many times player two has won a game
   */   
   private int pTwoWins;
   /**
   * An int that stores how many times has a tie occurred
   */
   private int tie;
   /**
   * An int that stores how many games have been played
   */
   private int gamesPlayed;
   /**
   * A DecimalFormat object used to format decimals to two places
   */
   private DecimalFormat d = new DecimalFormat("0.00");
   /**
   * Constructs an OthelloScoreboard that displays the information of the current game 
   * using JLabels and private fields based whether or not the game is two player and 
   * if not, whether or not the human player is first
   * @param twoPlayer whether or not the game is two player
   * @param black if the game is one player, whether or not the human player is first
   */
   public OthelloScoreboard(boolean twoPlayer, boolean black)
   {
      setLayout(new GridLayout(3, 3, 5, 5));
      pOneWins = pTwoWins = tie = gamesPlayed = 0;
      p1 = new JLabel("Player One", SwingConstants.CENTER);
      p1.setOpaque(true);
      add(p1);
      add(new JLabel());
      p2 = new JLabel("Player Two", SwingConstants.CENTER);
      p2.setOpaque(true);
      add(p2);
      if(!twoPlayer && black)
         p2.setText("AI");
      if(!twoPlayer && !black)
         p1.setText("AI");
      p1.setBackground(Color.YELLOW);
      
      
      p1Chips = new JLabel("Chips: 2", SwingConstants.CENTER);
      add(p1Chips);
      ties = new JLabel("Ties: 0", SwingConstants.CENTER);
      add(ties);
      p2Chips = new JLabel("Chips: 2", SwingConstants.CENTER);
      add(p2Chips);
      
      p1Wins = new JLabel("Wins: 0 (---%)", SwingConstants.CENTER);
      add(p1Wins);
      games = new JLabel("Games Played: 0", SwingConstants.CENTER);
      add(games);
      p2Wins = new JLabel("Wins: 0 (---%)", SwingConstants.CENTER);
      add(p2Wins);
   }
   /**
   * Updates the scoreboard to show whose turn it is and how many chips 
   * each player has
   * @param playerOne whether or not the current player is player one
   * @param chips1 how many chips player one has
   * @param chips2 how many chips player two has
   */
   public void update(boolean playerOne, int chips1, int chips2) 
   {
      p1Chips.setText("Chips: " + chips1);
      p2Chips.setText("Chips: " + chips2);
      if(playerOne)
      {
         p1.setBackground(Color.YELLOW);
         p2.setBackground(this.getBackground());
         
      }
      else
      {
         p2.setBackground(Color.YELLOW);
         p1.setBackground(this.getBackground());
      }
      repaint();
   }
   /**
   * Updates the scoreboard to show the win-loss record, games played and percentages, based  
   * on the number given and shows the player a message displaying who won
   * @param winner the outcome of the match (1 means player one won, 2 means player two 
   * won, 3 means a tie occurred)
   */
   public void winLoss(int winner)
   {
      gamesPlayed++;
      if(winner == 1)
      {
         JOptionPane.showMessageDialog(null, p1.getText()+" Wins!");
         pOneWins++;
         
      }
      else if(winner == 2)
      {
         JOptionPane.showMessageDialog(null, p2.getText()+" Wins!");
         pTwoWins++;
      }
      else
      {
         JOptionPane.showMessageDialog(null, "It's a Tie!");
         tie++;   
         ties.setText("Ties: "+tie);  
      }  
      p1Wins.setText("Wins: "+pOneWins+" ("+format((double)pOneWins / gamesPlayed)+"%)");
      p2Wins.setText("Wins: "+pTwoWins+" ("+format((double)pTwoWins / gamesPlayed)+"%)");
      games.setText("Games Played: "+gamesPlayed);
      p1Chips.setText("Chips: 2");
      p2Chips.setText("Chips: 2");
      p2.setBackground(this.getBackground());
      p1.setBackground(Color.YELLOW);
      repaint();
   }
   /**
   * Formats a double (a win percentage) into a decimal rounded to two places
   * @param db the double to be formatted
   * @return the String object containing the formatted double
   */
   private String format(double db)
   {
      db *= 100;
      if(db * 10 % 10 == 0)
         return (int)db+"";
      else if(db * 1000 % 10 == 0)
         return db+"";
      else
         return d.format(db);
   }
   /**
   * Resets all the JLabels to their original text and all ints to 0
   */
   public void reset()
   {
      pOneWins = pTwoWins = tie = gamesPlayed = 0;
      p1.setBackground(Color.YELLOW);
      p2.setBackground(this.getBackground());
      
      p1Chips.setText("Chips: 2");
      ties.setText("Ties: 0");
      p2Chips.setText("Chips: 2");
      
      p1Wins.setText("Wins: 0 (---%)");
      games.setText("Games Played: 0");
      p2Wins.setText("Wins: 0 (---%)");
   }
   /**
   * Returns the current game statistics in the form of a String object
   * @return the stats of the current game
   */
   public String getScore()
   {
      return pOneWins + ":" + pTwoWins + ":" + tie + ":" + gamesPlayed + ":" +
      p1Chips.getText().replaceAll("Chips: ", "") + ":" + p2Chips.getText().replaceAll("Chips: ", "");
   }
   /**
   * Sets the score of the current game based on the inputted string
   * @param s the statistics that the current game is being set to
   */
   public void setScore(String s)
   {
      String[] sArr = s.split(":");
      pOneWins = Integer.parseInt(sArr[0]);
      pTwoWins = Integer.parseInt(sArr[1]);
      tie = Integer.parseInt(sArr[2]);
      gamesPlayed = Integer.parseInt(sArr[3]);
      p1Chips.setText("Chips: "+sArr[4]);
      p2Chips.setText("Chips: "+sArr[5]);      
      
      ties.setText("Ties: "+tie);  
      String p1 = format((double)pOneWins / gamesPlayed);
      String p2 = format((double)pTwoWins / gamesPlayed);
      if(gamesPlayed == 0)
         p1 = p2 = "---";
      p1Wins.setText("Wins: "+pOneWins+" ("+p1+"%)");
      p2Wins.setText("Wins: "+pTwoWins+" ("+p2+"%)");
      games.setText("Games Played: "+gamesPlayed);
   }
}



