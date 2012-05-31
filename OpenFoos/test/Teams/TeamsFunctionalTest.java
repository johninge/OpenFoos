
package Teams;

import models.Player;
import models.Team;
import org.junit.Before;
import org.junit.Test;
import play.mvc.Http;
import play.test.Fixtures;
import play.test.FunctionalTest;

/**
 *
 * 
 */
public class TeamsFunctionalTest extends FunctionalTest {

    @Before
    public void RegisterOnePlayer() {

        Fixtures.deleteDatabase();

        Player guest = new Player();
        guest.username = "Guest";
        guest.save();
        Team gTeam = new Team();
        gTeam.player1 = guest;

        gTeam.team_name = "Guest";
        gTeam.save();


        Player guest2 = new Player();
        guest2.username = "Guest2";
        guest2.save();
        Team gTeam2 = new Team();
        gTeam2.player1 = guest2;

        gTeam2.team_name = "Guest2";
        gTeam2.save();

        Team guestTeam = new Team();
        guestTeam.player1 = guest;
        guestTeam.player2 = guest2;
        guestTeam.team_name = "Star";
        guestTeam.save();

    }

    @Test
    public void testThatTeamProfileWorks() {
        Http.Response response = GET("/teamcontroller/profile?teamname=star");
        assertStatus(302, response);
        assertNotNull(response);
    }

    @Test
    public void Clean() {
        
        Fixtures.deleteDatabase();
    }
}
