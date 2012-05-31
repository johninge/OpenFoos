
package Boards;

import org.junit.Test;
import play.mvc.Http;
import play.test.FunctionalTest;

/**
 *
 * 
 */
public class BoardsFunctionalTest extends FunctionalTest {

    @Test
    public void testThatBoardsJsonWorks() {
        Http.Response response = GET("/application/boards");
        assertIsOk(response);
        assertContentType("application/json", response);
        assertCharset(play.Play.defaultWebEncoding, response);
    }
}
