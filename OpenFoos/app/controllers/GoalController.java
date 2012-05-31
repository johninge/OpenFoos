
package controllers;

import java.util.ArrayList;
import java.util.List;
import models.Goal;
import play.mvc.Controller;

/*
 *This class shows information about all registered goals that have been scored by players. 
 * The methods under have been designed with two options in this 
 * way can the methods run against SQL  and Postgres databases
 * 
 */
public class GoalController extends Controller {

    public static int getIndividualGoalScoreds(Long player_id) {
       
        //ALT ONE  
        //List<Goal> goal = Goal.find("player_id = ? AND backfire != 1", player_id).fetch();
        //return goal.size();

        //ALT TWO
        List<Goal> allGoals = Goal.find("player_id = ?", player_id).fetch();
        List<Goal> goals = new ArrayList<Goal>();
        for (int i = 0; i < allGoals.size(); i++) {
            if (!allGoals.get(i).backfire) {
                goals.add(allGoals.get(i));
            }
        }
        return goals.size();

    }

    public static int getIndividualOwnGoal(Long player_id) {
          
        //ALT ONE
        //List<Goal> goal = Goal.find("player_id = ? AND backfire = 1", player_id).fetch();
        //return goal.size();
       
        //ALT TWO      
        List<Goal> allGoals = Goal.find("player_id = ?", player_id).fetch();
        List<Goal> goals = new ArrayList<Goal>();
        for (int i = 0; i < allGoals.size(); i++) {
            if (allGoals.get(i).backfire) {
                goals.add(allGoals.get(i));
            }
        }
        return goals.size();

    }
    
    public static void getAllGoals(){
        //show all goas in JS-format
        List<Goal> goals = Goal.findAll();
        renderJSON(goals);
    }
}
