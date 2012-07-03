package controllers;

import com.google.gson.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import models.Game;
import models.Goal;
import models.Player;
import models.Team;
import org.h2.util.StringUtils;
import play.libs.Crypto;
import play.mvc.Controller;
import play.mvc.Http;

public class Api extends Controller {

    /**
     * Performs a request to fetch usernames that begin with chars given in the
     * String 'username'
     *
     * @param username
     */
    public static void autocomplete(String username) {
        if (StringUtils.isNullOrEmpty(username)) {
            badRequest();
            return;
        }

        List<Player> players = Player.find("byUsernameLike", username + "%").fetch(10);
        ArrayList<String> usernames = new ArrayList<String>();
        for (Player player : players) {
            usernames.add(player.username);
        }

        renderJSON(usernames);
    }

    /**
     * Fetches a given player.
     *
     * @param body takes in a json representation of the player class
     */
    public static void getPlayer(JsonElement body) {
        Player player = construct(body, Player.class);

        if (StringUtils.isNullOrEmpty(player.username) || StringUtils.isNullOrEmpty(player.password)) {
            error(Http.StatusCode.BAD_REQUEST, "Username or password cannot be empty");
            return;
        }

        // Check if a player with the given username 
        // allready exist
        Player verifiedPlayer = Player.find("username = ? and password = ?", player.username, Crypto.encryptAES(player.password)).first();
        if (verifiedPlayer == null) {
            error(Http.StatusCode.BAD_REQUEST, "Ops! Seems like your username or password is wrong");
            return;
        } else {
            if (player.rfid != null && player.rfid != verifiedPlayer.rfid) {
                verifiedPlayer.rfid = player.rfid;
                verifiedPlayer.save();
            }

            renderJSON(verifiedPlayer);
        }

    }

    /**
     * Fetches a player by a given RFID
     *
     * @param rfid the RFID card number
     */
    public static void getPlayerByRFID(long rfid) {
        if (rfid < 1) {
            error(Http.StatusCode.BAD_REQUEST, "Please provide an non-empty RFID code");
            return;
        }

        Player player = Player.find("rfid = ?", rfid).first();

        if (player == null) {
            error(Http.StatusCode.BAD_REQUEST, "Could not find a user with the given RFID-code <br /><br /> Please sign-in, to connect this card to your account");
            return;
        }
        renderJSON(player);
    }

    /**
     * Registers a given player if he is not present
     *
     * @param body takes in a json representation of the player class
     */
    public static void registerPlayer(JsonElement body) {

        Player player = construct(body, Player.class);

        if (StringUtils.isNullOrEmpty(player.username) || StringUtils.isNullOrEmpty(player.password)) {
            error(Http.StatusCode.BAD_REQUEST, "Username or password cannot be empty");
            return;
        }

        // Check if a player with the given username 
        // allready exist
        Player existingPlayer = Player.find("username = ?", player.username).first();
        if (existingPlayer != null) {
            error(Http.StatusCode.BAD_REQUEST, "A player allready exsist with this username");
            return;
        } else {

            player.password = Crypto.encryptAES(player.password);
            player.registered = new Date();
            player.save();

            // Creates and saves a team for the player;
            Team team = new Team();
            team.player1 = player;
            team.player1 = player;
            team.team_name = player.username;
            team.save();

            renderJSON(player);
        }
    }

    /**
     * Fetches a given based on the player(s) provided. Creates the team if it
     * does not exists
     *
     * @param body takes in a json representation of the team class
     */
    public static void fetchTeam(JsonElement body) {
        Team team = construct(body, Team.class);

        if (team == null) {
            badRequest();
        }

        if (team.player1.id == null) {
            team.player1 = null;
        }

        if (team.player2.id == null) {
            team.player2 = null;
        }

        if (team.memberCount() == 0) {
            badRequest();
        }

        if (team.player1 == null && team.player2 != null) {
            team.player1 = team.player2;
            team.player2 = null;
        }

        //Make sure that the players are ordered in an
        //ascending order
        if (team.player2 != null) {
            if (team.player2.id < team.player1.id) {
                Player temp = team.player1;
                team.player1 = team.player2;
                team.player2 = temp;
            }
        }

        Team verifiedTeam = null;
        //Check if the team allready exsists
        if (team.memberCount() == 1) {
            verifiedTeam = Team.find("player1_id = ? and player2_id = null", team.player1.id).first();
        } else if (team.memberCount() == 2) {
            verifiedTeam = Team.find("player1_id = ? and player2_id = ?", team.player1.id, team.player2.id).first();
        }


        //We tried to look for the team specified,
        //specified, if we could not find it, then we will create it;
        if (verifiedTeam == null) {
            verifiedTeam = new Team();
            verifiedTeam.player1 = team.player1;
            if (team.memberCount() == 2) {
                verifiedTeam.player2 = team.player2;
            }

            String name = team.player1.username;
            if (team.memberCount() == 2) {
                name += " & " + team.player2.username;
            }

            verifiedTeam.team_name = name;
            verifiedTeam.save();
        }


        if (verifiedTeam == null) {
            error("Could not find or create the team");
        } else {
            verifiedTeam.arch_rival = null;
            renderJSON(verifiedTeam);
        }
    }

    /**
     * Persists a new game. Returning a JSON representation with imprinted start
     * time and an id
     *
     * @param body takes in a json representation of the game class
     */
    public static void initializeGame(JsonElement body) {
        Game game = construct(body, Game.class);
        if (game == null) {
            badRequest();
            return;
        }


        // Verifies the teams. 
        // We only want to operate on data that is retrieved from the server.
        game.home_team = Team.findById(game.home_team.getId());
        game.home_team.arch_rival = null;
        game.visitor_team = Team.findById(game.visitor_team.getId());
        game.visitor_team.arch_rival = null;
        if (game.home_team == null || game.visitor_team == null) {
            badRequest();
            return;
        }

        game.start_time = new Date();
        game.id = null;

        Game verifiedGame = game.save();
        renderJSON(verifiedGame);
    }

    /**
     * Sets and finishes a started game. Persists changes and scores. Calculates
     * ratings based on game mode
     *
     * @param body takes in a json representation of the game class
     */
    public static void setGame(JsonElement body) {
        Game game = construct(body, Game.class);

        if (game == null) {
            badRequest();
            return;
        }

        Game verified = Game.findById(game.getId());
        if (verified == null) {
            badRequest();
            return;
        }

        // Verifies the teams. 
        // We only want to operate on data that is retrieved from the server.
        verified.home_team = Team.findById(game.home_team.getId());
        verified.home_team.arch_rival = null;
        verified.visitor_team = Team.findById(game.visitor_team.getId());
        verified.visitor_team.arch_rival = null;

        if (verified.home_team == null || verified.visitor_team == null) {
            badRequest();
            return;
        } else {
            verified.home_score = game.home_score;
            verified.visitor_score = game.visitor_score;
            verified.winner = (verified.home_score > verified.visitor_score ? verified.home_team : verified.visitor_team);
            if (verified.home_team == verified.winner) {
                verified.home_team.won++;
                verified.visitor_team.lost++;
            } else {
                verified.visitor_team.won++;
                verified.home_team.lost++;
            }

            verified.end_time = new Date();

            //Calculates and sets team score if the game mode is ranked
            if (verified.mode.equals("ranked")) {
                calculateRating(
                        verified.home_team,
                        verified.visitor_team,
                        verified.home_score,
                        verified.visitor_score);
            }

            verified.save();
            renderJSON(verified);
        }
    }

    /**
     * Persists a collection of goals
     *
     * @param body the collection / array of goals to be persisted
     */
    public static void registerGoals(JsonElement body) {
        if (body == null) {
            badRequest();
            return;
        }

        JsonArray array = body.getAsJsonArray();
        if (array.size() <= 0) {
            badRequest();
            return;
        }

        for (int i = 0; i < array.size(); i++) {
            Goal goal = new Goal();
            JsonObject object = array.get(i).getAsJsonObject();
            goal.position = object.get("position").getAsInt();
            goal.game_id = object.get("game_id").getAsLong();
            goal.backfire = object.get("backfire").getAsBoolean();
            long timestamp = object.get("timestamp").getAsLong();
            goal.registered = new Date(timestamp);
            JsonObject player = object.get("player").getAsJsonObject();
            goal.player_id = player.get("id").getAsLong();

            goal.save();
        }

        ok();

    }

    /**
     * Helper method to bind and construct JSON objects to Java classes.
     *
     * @param <T> the class you want to bind to
     * @param json the json body representation of the object to be constructed
     * @param classOfT
     * @return
     */
    private static <T extends Object> T construct(JsonElement json, Class<T> classOfT) {
        if (json == null) {
            return null;
        }

        Gson gson = new Gson();

        try {
            return gson.fromJson(json, classOfT);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }

    /**
     * Private helper method to calculate rating based on the ELO algorithm.
     *
     * @param home_team
     * @param visitor_team
     * @param home_score
     * @param visitor_score
     */
    private static void calculateRating(Team home_team, Team visitor_team, int home_score, int visitor_score) {

        double current_home_rating = home_team.rating;
        double current_visitor_rating = visitor_team.rating;


        double E = 0;

        if (home_score != visitor_score) {
            if (home_score > visitor_score) {
                E = 120 - Math.round(1 / (1 + Math.pow(10, ((current_visitor_rating - current_home_rating) / 400))) * 120);
                home_team.rating = current_home_rating + E;
                visitor_team.rating = current_visitor_rating - E;
            } else {
                E = 120 - Math.round(1 / (1 + Math.pow(10, ((current_home_rating - current_visitor_rating) / 400))) * 120);
                home_team.rating = current_home_rating - E;
                visitor_team.rating = current_visitor_rating + E;
            }
        } else {
            if (current_home_rating == current_visitor_rating) {
                home_team.rating = current_home_rating;
                visitor_team.rating = current_visitor_rating;
            } else {
                if (current_home_rating > current_visitor_rating) {
                    E = (120 - Math.round(1 / (1 + Math.pow(10, ((current_home_rating - current_visitor_rating) / 400))) * 120)) - (120 - Math.round(1 / (1 + Math.pow(10, ((current_visitor_rating - current_home_rating) / 400))) * 120));
                    home_team.rating = current_home_rating - E;
                    visitor_team.rating = current_visitor_rating + E;
                } else {
                    E = (120 - Math.round(1 / (1 + Math.pow(10, ((current_visitor_rating - current_home_rating) / 400))) * 120)) - (120 - Math.round(1 / (1 + Math.pow(10, ((current_home_rating - current_visitor_rating) / 400))) * 120));
                    home_team.rating = current_home_rating + E;
                    visitor_team.rating = current_visitor_rating - E;
                }
            }
        }

        //Saves the calculated rating
        home_team.save();
        visitor_team.save();
    }
}
