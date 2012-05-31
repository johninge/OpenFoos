
package Application;

import models.Player;
import org.junit.Test;
import play.libs.Crypto;
import play.test.Fixtures;
import play.test.UnitTest;

/**
 *
 * 
 */
public class ApplicationUnitTest extends UnitTest {
    
    @Test
    public void EmptyDatabase() {
        Fixtures.deleteDatabase();
    }

    
    @Test
    public void PasswordCryption(){
        
        String password = "123456";
        String cryptet = Crypto.encryptAES(password);       
        String decrypt = Crypto.decryptAES(cryptet);
        assertEquals(password, decrypt);
    }
    
    @Test
    public void LoginPlayer(){
        
        Player player = new Player();
        player.username = "Player";
        player.id = new Long(1);
        controllers.Application.afterLogin(player); 
    }
    
    @Test
    public void IsPlayerLogdIn(){
        
       boolean online = controllers.Application.isOnline();
       assertTrue(online);    
    }
    
    @Test 
    public void LogOut(){
        controllers.Application.afterLogout();
    }
    
    @Test
    public void IsPlayerLogdOut(){
        boolean logOut = controllers.Application.isOnline();
        assertFalse(logOut);
    }
    
    @Test
    public void IsEmpty(){
        int count = controllers.Application.teamsAndPlayers().size();
        assertEquals(0,count);
    }
    
    
    
}
