
package models;

/**
 * There is no need for getters and setters methods, play framework takes care of that while compiling the source code.
 */
public class Statistic {
   
    public int games_playd;
    
    public int home_games;
    public int away_games;
    
    public int home_wins;
    public int home_lost;
    
    
    public int away_wins;
    public int away_lost;
    
    public int winns;
    public int losts;
    
    
    public int score_for;   
    public int score_home_for;   
    public int score_away_for;
    
    
    
    public int score_against;
    public int score_home_against;   
    public int score_away_against;
    
    public int score_difference;
    
    
    public int average_score_for;
    public int average_score_against;
    public int average_score_difference;
    
    public double win_prosent;
    public double lost_prosent;
    
    
    
    public String last_three_games_played;
    
    public Team target_team = null;
    
    
    
    
    public int count_most_played_against;
    public int count_most_lost_against;
    public int count_most_won_against;  
    public int count_most_regular_appearances;

    @Override
    public String toString() {
        return "Statistic{ games_playd=" + games_playd + ", home_games=" + home_games + ", away_games=" + away_games + ", winns=" + winns + ", losts=" + losts + ", score_for=" + score_for + ", score_home_for=" + score_home_for + ", score_away_for=" + score_away_for + ", score_against=" + score_against + ", score_home_against=" + score_home_against + ", score_away_against=" + score_away_against + ", average_score_for=" + average_score_for + ", average_score_against=" + average_score_against + ", win_prosent=" + win_prosent + ", lost_prosent=" + lost_prosent + ", last_three_games_played=" + last_three_games_played + ", target_team=" + target_team + ", count_most_played_against=" + count_most_played_against + ", count_most_lost_against=" + count_most_lost_against + ", count_most_won_against=" + count_most_won_against + ", count_most_regular_appearances=" + count_most_regular_appearances + '}';
    }
    
    
    
    
    
    
    
}
