package controllers;

import java.util.List;
import models.Game;
import play.mvc.Controller;

/**
 *
 *
 */
public class GamesController extends Controller {

    public static List<Game> getTeamGames(Long team_id, String order, int limit) {


        List<Game> games = Game.find("(home_team_id = ? or visitor_team_id = ?) and (end_time != 0 ) order by " + order, team_id, team_id).fetch(limit);

        for (int i = 0; i < games.size(); i++) {
            //For Security off application
             games.get(i).home_team.arch_rival = null;
             games.get(i).visitor_team.arch_rival = null;
            if (games.get(i).home_team.memberCount() >= 1) {
                games.get(i).home_team.player1.password = "sorry to disappoint you hacker";
                games.get(i).home_team.player1.email = null;
            }
            if (games.get(i).home_team.memberCount() == 2) {

                games.get(i).home_team.player2.password = "sorry to disappoint you hacker";
                games.get(i).home_team.player2.email = null;
            }
            if (games.get(i).visitor_team.memberCount() >= 1) {
                games.get(i).visitor_team.player1.password = "sorry to disappoint you hacker";
                games.get(i).visitor_team.player1.email = null;
            }
            if (games.get(i).visitor_team.memberCount() == 2) {

                games.get(i).visitor_team.player2.password = "sorry to disappoint you hacker";
                games.get(i).visitor_team.player2.email = null;
            }
        }
        return games;
    }

    public static void getTeamGamesForChart(Long team_id) {
        renderJSON(getTeamGames(team_id, "id asc", 30));
    }
}
