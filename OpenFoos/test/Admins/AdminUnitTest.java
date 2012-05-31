
package Admins;

import models.Admin;
import org.junit.Test;
import play.test.Fixtures;
import play.test.UnitTest;

/**
 *
 * 
 */
public class AdminUnitTest extends UnitTest {

    @Test
    public void EmptyDatabase() {
        Fixtures.deleteDatabase();
    }

    @Test
    public void IsAdminDatabseEmpty() {
        assertEquals(0, Admin.count());
    }

    @Test
    public void AddTwoAdmins() {

        Admin admin = new Admin();
        admin.username = "Admin";
        admin.password = "c5abc7086cea5238aa8b53f36688ad05";
        admin.email = "hovedprosjekt.hioa@gmail.com";
        admin.active = true;
        admin.save();

        Admin admin2 = new Admin();
        admin2.username = "Admin2";
        admin2.password = "c5abc7086cea5238aa8b53f36688ad05";
        admin2.email = "hovedprosjekt2.hioa@gmail.com";
        admin2.active = true;
        admin2.save();
    }

    @Test
    public void CountAdminIsTwo() {

        assertEquals(2, Admin.count());
    }

    @Test
    public void FindAdmins() {

        Admin admin = Admin.find("byUsername", "Admin").first();
        Admin admin2 = Admin.find("byUsername", "Admin2").first();
        assertNotNull(admin);
        assertNotNull(admin2);
        assertEquals("Admin", admin.username);
        assertEquals("Admin2", admin2.username);
    }

    @Test
    public void DeactivateAdmins() {

        Admin admin = Admin.find("byUsername", "Admin").first();
        Admin admin2 = Admin.find("byUsername", "Admin2").first();
        admin.active = false;
        admin2.active = false;
        admin.save();
        admin2.save();

    }

    @Test
    public void IsAdminsDeactivated() {

        Admin admin = Admin.find("byUsername", "Admin").first();
        Admin admin2 = Admin.find("byUsername", "Admin2").first();
        assertFalse(admin.active);
        assertFalse(admin2.active);
    }

    @Test
    public void DeleteAdmins() {
        Admin admin = Admin.find("byUsername", "Admin").first();
        Admin admin2 = Admin.find("byUsername", "Admin2").first();
        admin.delete();
        admin2.delete();
    }
    @Test
    public void IsAdminsDeleted(){
        assertEquals(0, Admin.count());
    }
    @Test
    public void Clean(){
        Fixtures.deleteDatabase();
    }
}
