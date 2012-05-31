/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import net.sf.oval.constraint.MaxLength;
import play.data.validation.Max;
import play.data.validation.Min;
import play.db.jpa.Model;

/**
 * There is no need for getters and setters methods, play framework takes care of that while compiling the source code.
 */
@Entity
public class Game extends Model {


    
    public Date start_time = null;
    
    public Date end_time = null;

    @ManyToOne
    public Team home_team = null;
   
    @ManyToOne
    public Team visitor_team = null;
    
    @ManyToOne
    public Team winner = null;
    
    @MaxLength(2)
    @Max(10)
    @Min(0)
    public int home_score = 0;
    
    @MaxLength(2)
    @Max(10)
    @Min(0)
    public int visitor_score = 0;
    
    public String mode = null;
    
    @Override
    public String toString() {
        return home_team.team_name + " VS " + visitor_team.team_name;
    }
}
