
package Goals;

import org.junit.Test;
import play.mvc.Http;
import play.test.FunctionalTest;

/**
 *
 * 
 */
public class GoalsFunctionalTest extends FunctionalTest {

    @Test
    public void testThatGoalsJsonWorks() {
        Http.Response response = GET("/application/goals");
        assertIsOk(response);
        assertContentType("application/json", response);
        assertCharset(play.Play.defaultWebEncoding, response);
    }
}
