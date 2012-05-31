
package repositories;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.Game;

/**
 *
 * 
 */
public class GameRepository {

    public static List<Game> getOngoingGames() {
        StringBuilder sqlToQuery =
                new StringBuilder("SELECT Game.id FROM Game, Team ");
        sqlToQuery.append("WHERE ((Game.home_team_id = Team.id OR Game.visitor_team_id = Team.id) ");
       sqlToQuery.append(")");
        //sqlToQuery.append("AND ( NOW() - Game.start_time < 36000 )) ");
        sqlToQuery.append("GROUP BY Game.id DESC LIMIT 15;");

        
        ResultSet resultset = OpenFoosDatabase.executeQueryToFoosBase(sqlToQuery.toString());
        List<Game> games = new ArrayList<Game>();
        try {
            while (resultset.next()) {
                Game game = Game.findById(resultset.getLong(1));
                games.add(game);
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return games;
    }

    public static void countAllTime() {
        StringBuilder sqlToQuery =
                new StringBuilder("select ((sum(start_time)) - (sum(end_time))) as total from Game where end_time != 0");
        ResultSet resultset = OpenFoosDatabase.executeQueryToFoosBase(sqlToQuery.toString());
        try {
            while (resultset.next()) {
                Long time = resultset.getLong(1);
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }
}
