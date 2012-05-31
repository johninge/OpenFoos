
package Goals;

import java.util.Date;
import java.util.List;
import models.Goal;
import org.junit.Test;
import play.test.Fixtures;
import play.test.UnitTest;

/**
 *
 * 
 */
public class GoalsUnitTest extends UnitTest{
    
    @Test
    public void EmptyDatabase() {
        Fixtures.deleteDatabase();
    }
    
    @Test
    public void Score(){
        Goal goal = new Goal();
        goal.game_id = new Long(1);
        goal.player_id = new Long(1);
        goal.position = 0;
        goal.registered = new Date();
        goal.save();
    }
    @Test 
    public void OwnGoal(){
        
        Goal goal = new Goal();
        goal.game_id = new Long(1);
        goal.player_id = new Long(1);
        goal.position = 0;
        goal.registered = new Date();
        goal.backfire = true;
        goal.save();
    }
    
    @Test
    public void CountGoal(){
        assertEquals(2, Goal.count());
    }
    @Test
    public void GetGoal(){
        Goal goal = Goal.findById(new Long(1));
        assertNotNull(goal);
    }
    @Test
    public void GetAllGoals(){
        List<Goal> goals = Goal.findAll();
        assertNotNull(goals);
    }
    @Test
    public void ChangePosition(){
        Goal goal = Goal.findById(new Long(1));
        goal.position = 1; 
        goal.save();
        assertEquals(1, goal.position);
        Fixtures.deleteDatabase();
    }
    
}
