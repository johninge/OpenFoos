package controllers;

import java.util.ArrayList;
import java.util.List;
import models.Game;
import models.Player;
import models.Statistic;
import models.Team;
import play.mvc.Controller;
import repositories.GameRepository;
import repositories.StatisticRepository;
import repositories.TeamRepository;

/*
 * This class takes care of Openfoos its main pages and rendering of data to
 * them. In addition, the main autocompelete gets its objects from methods that
 * are in this class as well as logging in and out of the players.For more
 * detailed explanation see the methods below
 */
public class Application extends Controller {

    public static void index() {

        render();
    }

    public static void game() {
        render();
    }

    public static void main_page() {


        //player is online or offline
        //If plyer is online we show them, their information. 
        Player onlinePlayer = null;
        Team team = null;
        Statistic statistic = null;
        if (session.get("login") != null && session.get("pid") != null) {
            boolean login = Boolean.parseBoolean(session.get("login"));
            if (login) {
                Long id = Long.parseLong(session.get("pid"));
                onlinePlayer = Player.findById(id);
                if (onlinePlayer != null) {
                    team = TeamController.getTeam(onlinePlayer.getId());
                    statistic = StatisticRepository.getStatistics(team.getId());

                }
            }
        }
        //The 156 last plyed games
        List<Game> onGoingGames = GameRepository.getOngoingGames();
        //Top rank, players and teams
        List<Team> topRanked = TeamController.getTopRanked(6);
        //Topp rank teams
        List<Team> topTeams = TeamController.getTopRankedTeams(6);
        //Top rank playes 
        List<Team> topPlayer = TeamController.getTopRankedPlayers(6);
        //Biggest winners
        List<Team> biggestWinner = TeamRepository.getBiggestWinner();
        //Biggest losers
        List<Team> biggestLoser = TeamRepository.getBiggestLoser();

        //Next 3objects its for List<Teams> above, we have to check it they are not NULL
        List<Statistic> topRankedStatistics = null;
        List<Statistic> topTeamsStatistics = null;
        List<Statistic> topPlayerStatistics = null;


        if (topRanked != null && topRanked.size() > 0) {
            topRankedStatistics = StatisticRepository.getMoreInfoForTeams(topRanked);

        }
        if (topTeams != null && topTeams.size() > 0) {
            topTeamsStatistics = StatisticRepository.getMoreInfoForTeams(topTeams);
        }
        if (topPlayer != null && topPlayer.size() > 0) {
            topPlayerStatistics = StatisticRepository.getMoreInfoForTeams(topPlayer);
        }
        //Render allof the objects to the page, some of them can bee equal null
        render(topRanked,
                onGoingGames,
                biggestWinner,
                biggestLoser,
                onlinePlayer,
                statistic, team,
                topTeams,
                topPlayer,
                topTeamsStatistics,
                topRankedStatistics,
                topPlayerStatistics);
    }

    public static void login() {
        render();
    }

    public static void register() {
        render();
    }

    public static void ofError() {
        render();
    }

    //Player is login
    public static boolean afterLogin(Player exist) {

        //So here we keep playes id and username and a session for login and AuthenticityToken 
        session.put("login", true);
        session.put("pid", exist.id);
        session.put("pname", exist.username);
        String token = session.getAuthenticityToken();
        session.put("token", token);
        return true;

    }
    //Use this method for your tests

    public static boolean isOnline() {

        boolean login = false;
        Long id = null;
        String username = null;
        if (session.get("login") == null || session.get("pid") == null || session.get("pname") == null) {
            return false;
        }
        if (session.get("login") != null) {
            login = Boolean.parseBoolean(session.get("login"));
        }
        if (session.get("pid") != null) {
            id = Long.parseLong(session.get("pid"));
        }
        if (session.get("pname") != null) {
            username = session.get("pname");
        }
        if (id > 0 && login && username != null) {
            return true;
        }
        return false;

    }

    public static boolean afterLogout() {
        //session.clear();
        //Best to use the code under, else if admin i online he/she wil be logd out...
        //Clearing session data 
        session.remove("login");
        session.remove("pid");
        session.remove("pname");
        session.remove("token");
        return true;
    }

    public static List<Object> teamsAndPlayers() {

        //First we get all the playes and the teams
        List<Object> teamsAndPlayers = new ArrayList<Object>();
        List<Team> teams = Team.findAll();
        List<Player> players = Player.findAll();
        for (int i = 0; i < teams.size(); i++) {

            if (teams.get(i) != null && teams.get(i).memberCount() == 2) {
                teams.get(i).arch_rival = null;//prevent from circular reference error
                teamsAndPlayers.add(teams.get(i));
            } else if (teams.get(i) != null && teams.get(i).memberCount() == 1) {
                if (!teams.get(i).team_name.equals(teams.get(i).player1.username)) {
                    teams.get(i).arch_rival = null;//prevent from circular reference error
                    teamsAndPlayers.add(teams.get(i));
                }
            }


        }
        for (int i = 0; i < players.size(); i++) {
            //For Security off application
            players.get(i).password = "sorry to disappoint you hacker";
            players.get(i).email = null;

            teamsAndPlayers.add(players.get(i));
        }
        return teamsAndPlayers;
    }

    public static void autocomplete() {

        List<Object> list = teamsAndPlayers();
        renderJSON(list);
    }

    public static String redirectToProfile(String who) {

        //Finds out if who is a player or a team. 
        //and sends redirection the controller. The controller will take care of the rest
        Player player = Player.find("byUsername", who).first();
        if (player != null) {
            return "/players/profile/" + who;
        }
        Team team = Team.find("byTeam_name", who).first();
        if (team != null) {
            return "/teams/profile/" + who;
        }
        if (team == null && player == null) {
            return "/error";
        }
        return "/";
    }
}