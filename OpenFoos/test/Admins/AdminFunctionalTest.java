
package Admins;

import org.junit.Test;
import play.mvc.Http;
import play.test.FunctionalTest;

/**
 *
 * 
 */
public class AdminFunctionalTest extends FunctionalTest {

    @Test
    public void testThatAdminPageWorks() {
        Http.Response response = GET("/admin");
        assertStatus(302, response);
        assertHeaderEquals("Location", "/secure/login", response);

    }
}
