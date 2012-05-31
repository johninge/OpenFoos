
package models;

import java.util.Date;
import javax.persistence.Entity;
import play.data.validation.Email;
import play.data.validation.Password;
import play.data.validation.Required;
import play.db.jpa.Model;

/**
 * There is no need for getters and setters methods, play framework takes care of that while compiling the source code.
 */
@Entity
public class Admin extends Model{
    
    @Required
    public String username;
    @Required
    @Email
    public String email;
    @Required
    @Password
    public String password; 
    public Date registered = new Date();
    @Required
    public boolean active = true;
    
    public String toString(){
        return username;
    }
    
}
