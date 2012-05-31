/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Games;

import java.util.Date;
import models.Game;
import models.Player;
import models.Team;
import org.junit.Test;
import play.test.Fixtures;
import play.test.UnitTest;

/**
 *
 * @author Santonas
 */
public class GamesUnitTest extends UnitTest {

    @Test
    public void EmptyDatabase() {
        Fixtures.deleteDatabase();
    }

    @Test
    public void CreateAGame() {

        Player homePlayer = new Player();
        homePlayer.username = "HomePlayer";
        homePlayer.save();
        
        Team hometeam = new Team();
        hometeam.team_name = "Home";
        hometeam.player1 = homePlayer;
        hometeam.save();
        
        Player visitorPlayer = new Player();
        visitorPlayer.username = "VisitorPlayer";
        visitorPlayer.save();
        Team visitorTeam = new Team();
        visitorTeam.team_name = "Visitor";
        visitorTeam.player1 = visitorPlayer;
        visitorTeam.save();
        
        Game game = new Game();
        game.home_team = hometeam;
        game.visitor_team = visitorTeam;
        game.start_time = new Date();
        game.save();
    }
    @Test
    public void OnGoingGame(){
        Game game = Game.findById(new Long(1));
        assertNotNull(game);
        assertNotNull(game.start_time);
        assertEquals(null, game.end_time);
    }
    
    @Test
    public void EndGame(){
        Game game = Game.findById(new Long(1));
        game.end_time = new Date();
        game.save();
    }
    
    @Test
    public void IsGameEnded(){
        Game game = Game.findById(new Long(1));
        assertNotNull(game);
        assertNotNull(game.start_time);
        assertNotNull(game.end_time);
    }
    @Test
    public void RemoveGame(){
        Game game = Game.findById(new Long(1));
        game.delete();
    }
}
