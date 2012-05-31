
package models;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * There is no need for getters and setters methods, play framework takes care of that while compiling the source code.
 */
@Entity
public class Team extends Model {

    @Required
    public String team_name = null;
    public String bio = "";
    public Date registered = new Date();
    public String image = "team.png";
    public String organization = "";
    public int won = 0;
    public int lost = 0;
    @Required
    @ManyToOne
    public Player player1 = null;
    @ManyToOne
    public Player player2 = null;
    @OneToOne
    public Team arch_rival = null;
    
    public double rating = 1500;

    public int memberCount() {
        int count = 0;
        if (this.player1 != null) {
            count++;
        }
        if (this.player2 != null) {
            count++;
        }
        return count;
    }

    @Override
    public String toString() {
        return team_name;
    }
}
