
package Players;

import models.Player;
import org.junit.Test;
import play.test.Fixtures;
import play.test.UnitTest;

/**
 *
 * 
 */
public class PlayersUnitTest extends UnitTest {

    @Test
    public void EmptyDatabase() {
        Fixtures.deleteDatabase();
    }

    @Test
    public void IsPlayerDatabseEmpty() {
        assertEquals(0, Player.count());
    }

    @Test
    public void RegisterTwoPlayers() {



        Player player = new Player();
        player.username = "Amir";
        player.password = "73507af67b22e1d948ef07e5de05502b";
        player.save();

        Player player2 = new Player();
        player2.username = "Neberd";
        player2.password = "73507af67b22e1d948ef07e5de05502b";
        player2.save();

    }

    @Test
    public void CountPlayersIsTwo() {

        assertEquals(2, Player.count());
    }

    @Test
    public void FindPlayer() {
        Player amir = Player.find("byUsername", "Amir").first();
        Player neberd = Player.find("byUsername", "Neberd").first();
        assertNotNull(amir);
        assertNotNull(neberd);
        assertEquals("Neberd", neberd.username);
        assertEquals("Amir", amir.username);

    }

    @Test
    public void ChangePlayer() {
        Player amir = Player.find("byUsername", "Amir").first();
        amir.username = "Rima";
        amir.save();
        Player rima = Player.find("byUsername", "Rima").first();
        assertNotNull(rima);
        assertEquals("Rima", rima.username);

    }
    
    @Test
    public void DeletePlayers(){
        Player rima = Player.find("byUsername", "Rima").first();
        rima.delete();
        Player neberd = Player.find("byUsername", "Neberd").first();
        neberd.delete();
        
    }
    
    @Test
    public void IsPlayerDatabseEmptyAfterDelete() {
        assertEquals(0, Player.count());
    }
}
