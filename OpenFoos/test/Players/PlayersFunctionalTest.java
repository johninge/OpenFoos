
package Players;

import models.Player;
import models.Team;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import play.mvc.Http;
import play.test.Fixtures;
import play.test.FunctionalTest;

/**
 *
 * 
 */
public class PlayersFunctionalTest extends FunctionalTest {

    @Before
    public void RegisterOnePlayer() {

        Fixtures.deleteDatabase();

        Player guest = new Player();
        guest.username = "Guest";
        guest.password = "73507af67b22e1d948ef07e5de05502b";
        guest.save();

        Team guestTeam = new Team();
        guestTeam.player1 = guest;
        guestTeam.team_name = guest.username;
        guestTeam.save();

    }

    @Test
    public void testThatPlayerProfileWorks() {
        Http.Response response = GET("/players/profile/Guest");
        assertStatus(302, response);
        assertHeaderEquals("Location", "/error", response);
    }

    @After
    public void clean() {
        Fixtures.deleteDatabase();
    }
}
