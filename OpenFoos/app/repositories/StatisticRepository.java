
package repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.Statistic;
import models.Team;

/**
 *
 * 
 */
public class StatisticRepository {

    public static Statistic getStatistics(Long team_id) {

        StringBuilder sqlToQuery = 
                new StringBuilder("SELECT (SELECT SUM(home_score) from Game where home_team_id = ");
        sqlToQuery.append(team_id);
        sqlToQuery.append(" and end_time != 0) AS score_home_for, ");
        sqlToQuery.append("(SELECT SUM( visitor_score ) from Game where visitor_team_id = ");
        sqlToQuery.append(team_id);
        sqlToQuery.append(" and end_time != 0) AS score_away_for, ");
        sqlToQuery.append("(SELECT ( score_home_for + score_away_for )) AS score_for, ");
        sqlToQuery.append("(SELECT count(Game.id) FROM Game WHERE home_team_id = ");
        sqlToQuery.append(team_id);
        sqlToQuery.append(" and end_time != 0) AS home_games, ");
        sqlToQuery.append("(SELECT count(Game.id) FROM Game WHERE visitor_team_id = ");
        sqlToQuery.append(team_id);
        sqlToQuery.append(" and end_time != 0) AS away_games, ");
        sqlToQuery.append("(SELECT ( home_games + away_games )) AS games_playd, ");

        sqlToQuery.append("(SELECT count(Game.id) FROM Game WHERE winner_id = ");
        sqlToQuery.append(team_id);
        sqlToQuery.append(" and end_time != 0) AS winns, ");


        sqlToQuery.append("(SELECT count(Game.id) FROM Game WHERE winner_id = ");
        sqlToQuery.append(team_id);
        sqlToQuery.append(" and end_time != 0 and home_team_id = winner_id) AS home_wins, ");



        sqlToQuery.append("(SELECT count(Game.id) FROM Game WHERE winner_id = ");
        sqlToQuery.append(team_id);
        sqlToQuery.append(" and end_time != 0 and visitor_team_id = winner_id) AS away_wins, ");




        sqlToQuery.append("(SELECT (games_playd-winns)) as losts,  ");
        sqlToQuery.append(" (SELECT SUM(visitor_score) FROM Game WHERE home_team_id = ");
        sqlToQuery.append(team_id);
        sqlToQuery.append(" and end_time != 0) AS score_home_against, ");
        sqlToQuery.append("(SELECT SUM(home_score) FROM Game WHERE visitor_team_id = ");
        sqlToQuery.append(team_id);
        sqlToQuery.append(" and end_time != 0) as score_away_against, ");
        sqlToQuery.append("(select sum(score_home_against + score_away_against)) as score_against; ");

        ResultSet resultset = OpenFoosDatabase.executeQueryToFoosBase(sqlToQuery.toString());
        Statistic statistic = new Statistic();
        try {
            while (resultset.next()) {

                statistic.games_playd = resultset.getInt("games_playd");
                statistic.home_games = resultset.getInt("home_games");
                statistic.away_games = resultset.getInt("away_games");
                statistic.winns = resultset.getInt("winns");
                statistic.losts = resultset.getInt("losts");


                statistic.home_wins = resultset.getInt("home_wins");
                statistic.away_wins = resultset.getInt("away_wins");


                statistic.score_for = resultset.getInt("score_for");
                statistic.score_home_for = resultset.getInt("score_home_for");
                statistic.score_away_for = resultset.getInt("score_away_for");
                if (statistic.score_for == 0 && statistic.score_home_for != 0) {
                    statistic.score_for = statistic.score_home_for;
                }
                if (statistic.score_for == 0 && statistic.score_away_for != 0) {
                    statistic.score_for = statistic.score_away_for;
                }
                statistic.score_against = resultset.getInt("score_against");
                statistic.score_home_against = resultset.getInt("score_home_against");
                statistic.score_away_against = resultset.getInt("score_away_against");
               
                if (statistic.score_against == 0 && statistic.score_home_against != 0) {
                    statistic.score_against = statistic.score_home_against;
                }
                if (statistic.score_against == 0 && statistic.score_away_against != 0) {
                    statistic.score_against = statistic.score_away_against;
                }


                //If team have played any home game's? 
                if (statistic.home_games > 0) {

                    //if team have won at home, then home lost must be home games - home wins
                    if (statistic.home_wins > 0) {
                        statistic.home_lost = statistic.home_games - statistic.home_wins;
                    }
                    //if team have not won at home, but played at home then home lost must be count home games.
                    if (statistic.home_wins == 0) {
                        statistic.home_lost = statistic.home_games;
                    }
                }


               //The same for away
                if (statistic.away_games > 0) {

                  
                    if (statistic.away_wins > 0) {
                        statistic.away_lost = statistic.away_games - statistic.away_wins;
                    }
                   
                    if (statistic.away_wins == 0) {
                        statistic.away_lost = statistic.away_games;
                    }
                }







            }
            resultset.close();
        } catch (SQLException e) {
            System.out.println(e.toString());
        } catch (Exception e){
             System.out.println(e.toString());
        }

        if (statistic.games_playd >= 1) {


            statistic.average_score_for = (int)(statistic.score_for / statistic.games_playd);

            statistic.average_score_against = (int)(statistic.score_against / statistic.games_playd);
            
            statistic.average_score_difference = statistic.average_score_for - statistic.average_score_against;
            
            statistic.score_difference = statistic.score_for - statistic.score_against;
            
            statistic.win_prosent = (statistic.winns * 100) / statistic.games_playd;

            statistic.lost_prosent = (100 - statistic.win_prosent);
            
            

            statistic.last_three_games_played = getLastThreeGameResult(team_id);
        }

        return statistic;

    }

    private static String getLastThreeGameResult(Long team_id) {

        StringBuilder sqlToQuery = new StringBuilder("SELECT CASE winner_id ");
        sqlToQuery.append("WHEN ");
        sqlToQuery.append(team_id);
        sqlToQuery.append(" THEN 'W' ");
        sqlToQuery.append(" ELSE 'L' ");
        sqlToQuery.append(" END AS 'status' FROM Game WHERE ( Game.end_time != 0 ) AND ( home_team_id = ");
        sqlToQuery.append(team_id);
        sqlToQuery.append(" OR visitor_team_id = ");
        sqlToQuery.append(team_id);
        sqlToQuery.append(" ) ORDER BY id DESC LIMIT 3;");



        StringBuilder last_three_games_played = new StringBuilder();
        ResultSet resultset = OpenFoosDatabase.executeQueryToFoosBase(sqlToQuery.toString());

        try {

            while (resultset.next()) {

                last_three_games_played.append(resultset.getString(1));
                last_three_games_played.append(" / ");
            }
            resultset.close();

        } catch (SQLException e) {
            System.out.println(e.toString());
        } 
        last_three_games_played.replace(last_three_games_played.toString().length() - 2, last_three_games_played.toString().length(), "");
        last_three_games_played.reverse();
        return last_three_games_played.toString();
    }

    private Statistic getMostPlayedAgainst(Long team_id) {

        StringBuilder sqlToQuery =
                new StringBuilder("SELECT DISTINCT Team.id, Team.team_name, Team.image, COUNT(*) AS count_matches from Team, Game ");
        sqlToQuery.append("WHERE (end_time != 0 AND Team.id = Game.home_team_id AND Game.visitor_team_id = ");
        sqlToQuery.append(team_id);
        sqlToQuery.append(" ) ");
        sqlToQuery.append("OR (end_time != 0 AND Game.home_team_id = ");
        sqlToQuery.append(team_id);
        sqlToQuery.append(" And Team.id = Game.visitor_team_id ) ");
        sqlToQuery.append("Group BY Team.id ORDER BY count_matches DESC LIMIT 1;");


        ResultSet resultset = OpenFoosDatabase.executeQueryToFoosBase(sqlToQuery.toString());
        Team team = new Team();
        Statistic statistic = new Statistic();
        try {
            while (resultset.next()) {

                team.id = resultset.getLong("id");
                team.team_name = resultset.getString("team_name");
                team.image = resultset.getString("image");
                statistic.count_most_played_against = resultset.getInt("count_matches");

            }
            resultset.close();
            if (team.id != null && team.team_name != null) {
                statistic.target_team = team;
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        } 

        return statistic;
    }

    private Statistic getMostLostAgainst(Long team_id) {


        StringBuilder sqlToQuery =
                new StringBuilder("SELECT DISTINCT Team.id, Team.team_name, Team.image, COUNT(*) AS count_matches from Team, Game ");
        sqlToQuery.append("WHERE ( winner_id != ");
        sqlToQuery.append(team_id);
        sqlToQuery.append(" AND end_time != 0 ) AND");
        sqlToQuery.append("(( Team.id = Game.home_team_id and Game.visitor_team_id = ");
        sqlToQuery.append(team_id);
        sqlToQuery.append(" ) ");
        sqlToQuery.append("OR ( Game.home_team_id = ");
        sqlToQuery.append(team_id);
        sqlToQuery.append(" AND Team.id = Game.visitor_team_id ))");
        sqlToQuery.append("AND winner_id != ");
        sqlToQuery.append(team_id);
        sqlToQuery.append(" GROUP BY Team.id ORDER BY count_matches DESC LIMIT 1;");


        ResultSet resultset = OpenFoosDatabase.executeQueryToFoosBase(sqlToQuery.toString());
        Team team = new Team();
        Statistic statistic = new Statistic();
        try {
            while (resultset.next()) {

                team.id = resultset.getLong("id");
                team.team_name = resultset.getString("team_name");
                team.image = resultset.getString("image");
                statistic.count_most_lost_against = resultset.getInt("count_matches");

            }
            resultset.close();
            if (team.id != null && team.team_name != null) {
                statistic.target_team = team;
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        } 
        return statistic;
    }

    private Statistic getMostWonAgainst(Long team_id) {

        StringBuilder sqlToQuery =
                new StringBuilder("SELECT DISTINCT Team.id, Team.team_name, Team.image,  count(*) AS count_matches FROM Team, Game ");
        sqlToQuery.append("WHERE ( winner_id = ");
        sqlToQuery.append(team_id);
        sqlToQuery.append(" ) ");
        sqlToQuery.append("AND (( Team.id = Game.home_team_id AND Game.visitor_team_id = ");
        sqlToQuery.append(team_id);
        sqlToQuery.append(" ) ");
        sqlToQuery.append("OR ( Game.home_team_id = ");
        sqlToQuery.append(team_id);
        sqlToQuery.append(" AND Team.id = Game.visitor_team_id ))");
        sqlToQuery.append("GROUP BY Team.id ORDER BY count_matches DESC LIMIT 1;");


        ResultSet resultset = OpenFoosDatabase.executeQueryToFoosBase(sqlToQuery.toString());
        Team team = new Team();
        Statistic statistic = new Statistic();
        try {
            while (resultset.next()) {

                team.id = resultset.getLong("id");
                team.team_name = resultset.getString("team_name");
                team.image = resultset.getString("image");
                statistic.count_most_won_against = resultset.getInt("count_matches");

            }
            resultset.close();
            if (team.id != null && team.team_name != null) {
                statistic.target_team = team;
            }
        } catch (SQLException e) {
        } finally {
        }
        return statistic;
    }

    //the difference is equal to 1. Matches that was played 10-9 or 9-10
    private Statistic getMostRegularAppearances(Long team_id) {

        StringBuilder sqlToQuery =
                new StringBuilder("SELECT DISTINCT Team.id, Team.team_name, Team.image, count(*) AS count_matches FROM Game, Team ");
        sqlToQuery.append("WHERE ( winner_id != ");
        sqlToQuery.append(team_id);
        sqlToQuery.append(" ) ");
        sqlToQuery.append("AND (( Team.id = Game.home_team_id AND Game.visitor_team_id = ");
        sqlToQuery.append(team_id);
        sqlToQuery.append(" ) ");
        sqlToQuery.append("OR ( Game.home_team_id = ");
        sqlToQuery.append(team_id);
        sqlToQuery.append(" AND Team.id = Game.visitor_team_id ))");
        sqlToQuery.append(" AND (( home_score=10 AND visitor_score=9 ) OR ( home_score=9 AND visitor_score=10 ))");
        sqlToQuery.append("GROUP BY Team.id ORDER BY count_matches DESC LIMIT 1;");


        ResultSet resultset = OpenFoosDatabase.executeQueryToFoosBase(sqlToQuery.toString());
        Team team = new Team();
        Statistic statistic = new Statistic();
        try {
            while (resultset.next()) {

                team.id = resultset.getLong("id");
                team.team_name = resultset.getString("team_name");
                team.image = resultset.getString("image");
                statistic.count_most_regular_appearances = resultset.getInt("count_matches");

            }
            resultset.close();
            if (team.id != null && team.team_name != null) {
                statistic.target_team = team;
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        } 
        return statistic;
    }
    
    public List<Statistic> getMoreInfoForOneTeam(Long team_id) {

        List<Statistic> statistics = new ArrayList<Statistic>();
        statistics.add(this.getMostPlayedAgainst(team_id));
        statistics.add(this.getMostWonAgainst(team_id));
        statistics.add(this.getMostLostAgainst(team_id));
        statistics.add(this.getMostRegularAppearances(team_id));
        return statistics;
    }

    public static List<Statistic> getMoreInfoForTeams(List<Team> teams) {

        List<Statistic> statistics = new ArrayList<Statistic>();
        for (int i = 0; i < teams.size(); i++) {
            if(teams.get(i)!= null ){
            statistics.add(getStatistics(teams.get(i).id));
            }
        }

      return statistics;
    }
}
