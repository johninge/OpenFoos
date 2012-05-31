/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package securities;

import controllers.Secure;
import models.Admin;
import play.libs.Crypto;

public class Security extends Secure.Security {
                          
    public static boolean authenticate(String username, String password) {
        Admin admin = Admin.find("byUsernameAndPassword", username,Crypto.encryptAES(password)).first();
        return (admin != null && admin.active);
    }   
}
