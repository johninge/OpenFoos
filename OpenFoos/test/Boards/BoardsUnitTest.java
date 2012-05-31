
package Boards;

import models.Board;
import org.junit.Test;
import play.test.Fixtures;
import play.test.UnitTest;

/**
 *
 * 
 */
public class BoardsUnitTest extends UnitTest {

    @Test
    public void EmptyDatabase() {
        Fixtures.deleteDatabase();
    }

    @Test
    public void IsBoardDatabseEmpty() {
        assertEquals(0, Board.count());
    }

    @Test
    public void AddBords() {

        Board logicaBoard = new Board();
        logicaBoard.board_name = "Logica";
        logicaBoard.latitude = 59.915817;
        logicaBoard.longitude = 10.801041;
        logicaBoard.address = "Helsfyr, Grenseveien 86";
        logicaBoard.city = "Oslo";
        logicaBoard.organization = "Logica, Norway";
        logicaBoard.description = "Logica provides football board for fun";
        logicaBoard.save();

        Board hioaBoard = new Board();
        hioaBoard.board_name = "Hioa";
        hioaBoard.latitude = 59.919525;
        hioaBoard.longitude = 10.735332;
        hioaBoard.address = "Høgskolen i Oslo og Akershus Postboks 4, St. Olavs plass";
        hioaBoard.city = "Oslo";
        hioaBoard.organization = "Hioa, Norway";
        hioaBoard.description = "Hioa provides football board for fun";
        hioaBoard.save();
    }

    @Test
    public void CountBoardIsTwo() {

        assertEquals(2, Board.count());
    }

    @Test
    public void FindBoards() {

        Board logicaBoard = Board.find("byBoard_name", "Logica").first();
        Board hioaBoard = Board.find("byBoard_name", "Hioa").first();
        assertNotNull(logicaBoard);
        assertNotNull(hioaBoard);
        assertEquals("Logica", logicaBoard.board_name);
        assertEquals("Hioa", hioaBoard.board_name);
    }

    @Test
    public void SetBoardsInUse() {

        Board logicaBoard = Board.find("byBoard_name", "Logica").first();
        Board hioaBoard = Board.find("byBoard_name", "Hioa").first();
        logicaBoard.inUse = true;
        hioaBoard.inUse = true;
        logicaBoard.save();
        hioaBoard.save();
    }

    @Test
    public void IsAllBordsInUse() {

        Board logicaBoard = Board.find("byBoard_name", "Logica").first();
        Board hioaBoard = Board.find("byBoard_name", "Hioa").first();
        assertTrue(logicaBoard.inUse);
        assertTrue(hioaBoard.inUse);
    }

    @Test
    public void RemoveBoards() {

        Board logicaBoard = Board.find("byBoard_name", "Logica").first();
        Board hioaBoard = Board.find("byBoard_name", "Hioa").first();
        logicaBoard.delete();
        hioaBoard.delete();
    }

    @Test
    public void IsAllBoardsRemoved() {

        assertEquals(0, Board.count());
    }

    @Test
    public void Clean() {
        Fixtures.deleteDatabase();
    }
}
