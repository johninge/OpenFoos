
package Teams;

import models.Player;
import models.Team;
import org.junit.Test;
import play.test.Fixtures;
import play.test.UnitTest;

/**
 *
 * 
 */
public class TeamsUnitTest extends UnitTest {

    @Test
    public void EmptyDatabase() {
        Fixtures.deleteDatabase();
    }

    @Test
    public void CreateTwoTeams() {

        Player one = new Player();
        one.username = "One";
        one.password = "1";
        Player two = new Player();
        two.username = "Two";
        two.password = "2";
        one.save();
        two.save();

        Team star = new Team();
        star.team_name = one.username + " & " + two.username;
        star.player1 = one;
        star.player2 = two;
        star.save();

        Player three = new Player();
        three.username = "Three";
        three.password = "3";
        Player fire = new Player();
        fire.username = "Fire";
        fire.password = "4";
        three.save();
        fire.save();

        Team moon = new Team();
        moon.team_name = three.username + " & " + fire.username;
        moon.player1 = three;
        moon.player2 = fire;
        moon.save();


    }

    @Test
    public void IsTeamCreated() {
        assertEquals(2, Team.count());
    }

    @Test
    public void FindTeams() {

        Team star = Team.find("byTeam_name", "One & Two").first();
        Team moon = Team.find("byTeam_name", "Three & Fire").first();
        assertNotNull(star);
        assertNotNull(moon);
    }

    @Test
    public void ChangeTeam() {

        Team star = Team.find("byTeam_name", "One & Two").first();
        Player one = star.player1;
        Player two = star.player2;
        star.team_name = "Star";
        Team moon = Team.find("byTeam_name", "Three & Fire").first();
        Player three = moon.player1;
        Player fire = moon.player2;
        moon.team_name = "Moon";
        moon.player1 = one;
        moon.player2 = two;
        star.player1 = three;
        star.player2 = fire;
        star.save();
        moon.save();
    }

    @Test
    public void IsChanged() {

        Team star = Team.find("byTeam_name", "Star").first();
        Team moon = Team.find("byTeam_name", "Moon").first();
        assertNotNull(star);
        assertNotNull(moon);
        assertEquals("Star", star.team_name);
        assertEquals("Moon", moon.team_name);

    }

    @Test
    public void SetArchRival() {
        Team star = Team.find("byTeam_name", "Star").first();
        Team moon = Team.find("byTeam_name", "Moon").first();
        moon.arch_rival = star;
        star.arch_rival = moon;
        moon.save();
        star.save();

        assertEquals("Star", moon.arch_rival.team_name);
        assertEquals("Moon", star.arch_rival.team_name);

    }

    @Test
    public void DeleteTeams() {

        Team star = Team.find("byTeam_name", "Star").first();
        Team moon = Team.find("byTeam_name", "Moon").first();
        star.player1 = null;
        star.player2 = null;
        star.arch_rival = null;
        
        moon.player1 = null;
        moon.player2 = null;
        moon.arch_rival = null;
        
        star.save();
        moon.save();
        
        star.delete();
        moon.delete();
    }
    
     @Test
    public void IsTeamDatabseEmptyAfterDelete() {
        assertEquals(0, Team.count());
    }
}
