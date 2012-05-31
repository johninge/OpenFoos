/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import java.util.Date;
import javax.persistence.Entity;
import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * There is no need for getters and setters methods, play framework takes care of that while compiling the source code.
 */
@Entity
public class Board extends Model {
    @Required
    public String board_name = null;
    @Required
    public double latitude;
    @Required
    public double longitude;
    @Required
    public String description;
    @Required
    public String address;
    @Required
    public String city;
    @Required
    public String organization;
    
    public int count_games_played = 0;
    
    public Date registered = new Date();
    
    public boolean active = true;
    public boolean inUse = false;
    @Override
    public String toString() {
        return board_name;
    }
}
