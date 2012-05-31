package Application;

import models.Player;
import models.Team;
import org.junit.Test;
import play.mvc.Http;
import play.test.Fixtures;
import play.test.FunctionalTest;

/**
 *
 *
 */
public class ApplicationFunctionalTest extends FunctionalTest {

    @Test
    public void EmptyDatabase() {
        Fixtures.deleteDatabase();
    }

    @Test
    public void required() {

        Player player = new Player();
        player.username = "guest";
        player.rfid = new Long(1);
        player.save();
        Team teamPlayer = new Team();
        teamPlayer.team_name = player.username;
        teamPlayer.player1 = player;
        teamPlayer.save();
    }

    @Test
    public void testThatErrorPageWorks() {
        Http.Response response = GET("/error");
        assertIsOk(response);
        assertContentType("text/html", response);
        assertCharset(play.Play.defaultWebEncoding, response);
    }

    @Test
    public void testThatMainPageWorks() {
        Http.Response response = GET("/");
        assertIsOk(response);
        assertContentType("text/html", response);
        assertCharset(play.Play.defaultWebEncoding, response);
    }

    @Test
    public void testThatGamePageWorks() {
        Http.Response response = GET("/game");
        assertIsOk(response);
        assertContentType("text/html", response);
        assertCharset(play.Play.defaultWebEncoding, response);
    }

    @Test
    public void testThatLoginPageWorks() {
        Http.Response response = GET("/login");
        assertIsOk(response);
        assertContentType("text/html", response);
        assertCharset(play.Play.defaultWebEncoding, response);
    }

    @Test
    public void testThatRegisterPageWorks() {
        Http.Response response = GET("/register");
        assertIsOk(response);
        assertContentType("text/html", response);
        assertCharset(play.Play.defaultWebEncoding, response);
    }

    @Test
    public void testThatAdminPageWorks() {
        Http.Response response = GET("/admin");
        assertStatus(302, response);
        assertHeaderEquals("Location", "/secure/login", response);

    }

    @Test
    public void testThatEverybodyJsonWorks() {
        Http.Response response = GET("/application/everybody");
        assertIsOk(response);
        assertContentType("application/json", response);
        assertCharset(play.Play.defaultWebEncoding, response);
    }

    @Test
    public void testThatAutocompleteWorks() {
        Http.Response response = GET("/api/player/autocomplete/guest");
        assertIsOk(response);
        assertContentType("application/json", response);
        assertCharset(play.Play.defaultWebEncoding, response);
        Fixtures.deleteDatabase();
    }
}
