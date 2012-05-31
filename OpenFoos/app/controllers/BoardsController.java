/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.util.List;
import models.Board;
import play.mvc.Controller;

/**
 * BoardsController,
 * This class displays information about all registered foosball board in the 
 * system, there can be called to see / retrieve them by id also
 * 
 */
public class BoardsController extends Controller {
    
    public static void getAllBoards(){
        List<Board> boards = Board.findAll();
        renderJSON(boards);
    }
    
    public static Board getBoard(Long board_id){
        return Board.findById(board_id);
    }
    public static void getBoardJS(Long board_id){
        renderJSON(getBoard(board_id));
    }
}
