package startup;

import models.Admin;
import models.Board;
import models.Player;
import models.Team;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.libs.Crypto;

@OnApplicationStart
public class Bootstrap extends Job {

    @Override
    public void doJob() {
        
        
        if (Admin.count() == 0) {

            Admin admin = new Admin();
            admin.username = "Admin";
            admin.password = "admin";
            admin.password = Crypto.encryptAES(admin.password);
            admin.email = "hovedprosjekt.hioa@gmail.com";
            admin.active = true;
            admin.save();
            System.out.println("Admin added.");

        }
        
        if (Player.count() == 0) {

            Player guest = new Player();
            guest.username = "Guest";
            guest.password = "1";
            guest.password = Crypto.encryptAES(guest.password);
            guest.save();
            Team guestTeam = new Team();
            guestTeam.player1 = guest;
            guestTeam.team_name = guest.username;
            guestTeam.save();


            guest = new Player();
            guest.username = "Guest2";
            guest.password = "1";
            guest.password = Crypto.encryptAES(guest.password);
            guest.save();
            guestTeam = new Team();
            guestTeam.player1 = guest;
            guestTeam.team_name = guest.username;
            guestTeam.save();

            guest = new Player();
            guest.username = "Guest3";
            guest.password = "1";
            guest.password = Crypto.encryptAES(guest.password);
            guest.save();
            guestTeam = new Team();
            guestTeam.player1 = guest;
            guestTeam.team_name = guest.username;
            guestTeam.save();

            guest = new Player();
            guest.username = "Guest4";
            guest.password = "1";
            guest.password = Crypto.encryptAES(guest.password);
            guest.save();
            guestTeam = new Team();
            guestTeam.player1 = guest;
            guestTeam.team_name = guest.username;
            guestTeam.save();

            System.out.println("Four new guest players added.");
        }
        
        if (Board.count() == 0) {

            Board board = new Board();
            board.board_name = "Logica";
            board.latitude = 59.915817;
            board.longitude = 10.801041;
            board.address = "Helsfyr, Grenseveien 86";
            board.city = "Oslo";
            board.organization = "Logica, Norway";
            board.description = "Logica provides football board for fun";
            board.save();


            Board hioaBoard = new Board();
            hioaBoard.board_name = "Hioa";
            hioaBoard.latitude = 59.919525;
            hioaBoard.longitude = 10.735332;
            hioaBoard.address = "Høgskolen i Oslo og Akershus Postboks 4, St. Olavs plass";
            hioaBoard.city = "Oslo";
            hioaBoard.organization = "Hioa, Norway";
            hioaBoard.description = "Hioa provides football board for fun";
            hioaBoard.save();

            System.out.println("Two boards added.");
        }
    }
}
