
package controllers;

import models.Player;
import models.Statistic;
import models.Team;
import play.mvc.Controller;
import repositories.StatisticRepository;

/**
 *This class handles the information sent via the repository
 * 
 */
public class StatisticController extends Controller {
    
    public static void data(Long id) {

        Statistic statistic = StatisticRepository.getStatistics(id);
        renderJSON(statistic);
    }
    
    //renders teamobject or a playerobject in JSONformat
    public static void statisticForWho(String name){
        
        Player player = Player.find("byUsername", name).first();
        if(player!= null){
            Team team = statisticTeamForPlayer(player.getId());
            if ( team != null){
                 team.arch_rival = null;
                 if ( team.memberCount() >= 1){
                     team.player1.password = "sorry to disappoint you hacker";
                     team.player1.email = null;
                 }
                 if (team.memberCount() == 2 ){
                     team.player2.password = "sorry to disappoint you hacker";
                     team.player2.email = null;
                 }
                 renderJSON(team);
            }
           
        }else //name is a team_name
        {
            Team team = Team.find("byTeam_name", name).first();
            if ( team != null){
                team.arch_rival = null;
                 if ( team.memberCount() >= 1){
                     team.player1.password = "sorry to disappoint you hacker";
                     team.player1.email = null;
                 }
                 if (team.memberCount() == 2 ){
                     team.player2.password = "sorry to disappoint you hacker";
                     team.player2.email = null;
                 }
                renderJSON(team);
            }
        }  
    }
    
    public static Team statisticTeamForPlayer(Long player1_id){
        return controllers.TeamController.getTeam(player1_id);
    }
}
