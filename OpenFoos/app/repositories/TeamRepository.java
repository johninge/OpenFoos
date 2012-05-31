
package repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.Team;

/**
 *
 * 
 */
public class TeamRepository {

    public static List<Team> getBiggestWinner() {
        StringBuilder sqlToQuery = new StringBuilder("SELECT Team.id, team_name, ");
        sqlToQuery.append("SUM(Team.id = home_team_id) as home_games, ");
        sqlToQuery.append("SUM(Team.id = visitor_team_id) as away_games, ");
        sqlToQuery.append("SUM((Team.id = home_team_id or Team.id = visitor_team_id) and winner_id=Team.id) as win, ");
        sqlToQuery.append("SUM(Team.id = home_team_id or Team.id = visitor_team_id) as total ");
        sqlToQuery.append("FROM Game, Team where end_time != 0 GROUP BY Team.id ORDER BY win DESC LIMIT 15;");

        ResultSet resultset = OpenFoosDatabase.executeQueryToFoosBase(sqlToQuery.toString());
        List<Team> teams = new ArrayList<Team>();
        try {
            while (resultset.next()) {
                Team team = Team.findById(resultset.getLong(1));
                team.won = resultset.getInt("win");
                team.lost = resultset.getInt("total") - team.won;
                if (team.won != 0) {
                    teams.add(team);
                }

            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return teams;
    }

    public static List<Team> getBiggestLoser() {

        StringBuilder sqlToQuery = new StringBuilder("SELECT Team.id, team_name, ");
        sqlToQuery.append("sum(Team.id = home_team_id) as home_games, ");
        sqlToQuery.append("sum(Team.id = visitor_team_id) as away_games, ");
        sqlToQuery.append("sum((Team.id = home_team_id or Team.id = visitor_team_id) and winner_id!=Team.id) as lost, ");
        sqlToQuery.append("sum(Team.id = home_team_id or Team.id = visitor_team_id) as total ");
        sqlToQuery.append("FROM Game, Team Where end_time != 0 GROUP BY Team.id ORDER BY lost DESC LIMIT 15;");


        ResultSet resultset = OpenFoosDatabase.executeQueryToFoosBase(sqlToQuery.toString());
        List<Team> teams = new ArrayList<Team>();
        try {
            while (resultset.next()) {
              
                Team team = Team.findById(resultset.getLong("id"));
                team.lost = resultset.getInt("lost");
                team.won = resultset.getInt("total") - team.lost;
                
 
                if (team.lost != 0) {
                    teams.add(team);
                }


            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return teams;
    }
}
