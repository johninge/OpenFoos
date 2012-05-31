package models;

import java.util.Date;
import javax.persistence.Entity;
import play.db.jpa.Model;
/**
 * There is no need for getters and setters methods, play framework takes care of that while compiling the source code.
 */
@Entity
public class Goal extends Model{
    public Long player_id;
    public Long game_id;
    public int position;
    public boolean backfire = false;
    public Date registered;
    
}
