package controllers;

import java.lang.reflect.Constructor;
import java.util.Date;
import models.Player;
import models.Team;
import play.data.binding.Binder;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.libs.Crypto;
import play.mvc.With;

@With(Secure.class)
public class Players extends CRUD {

    public static void create() throws Exception {


        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        Model object = (Model) constructor.newInstance();
        Binder.bindBean(params.getRootParamNode(), "object", object);
        validation.valid(object);
        if (validation.hasErrors()) {
            renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
            try {
                render(request.controller.replace(".", "/") + "/blank.html", type, object);
            } catch (TemplateNotFoundException e) {
                render("CRUD/blank.html", type, object);
            }
        }

        /*
         * important
         * check if the username is uniq 
         * have to encrypt the playes password and give the player a team
         */
        Player player = (Player) object;
        player.password = Crypto.encryptAES(player.password);
        player.registered = new Date();


        //Players team
        Team team = new Team();
        team.registered = player.registered;
        team.team_name = player.username;
        team.player1 = player;


        object = player;
        Player chekPlayer = Player.find("byUsername", player.username).first();
        if (chekPlayer == null) {
            //Still a uniq name
            object._save();

            team.save();

            flash.success(play.i18n.Messages.get("crud.created", type.modelName));
        }
        if (chekPlayer != null) {
            flash.error(type.modelName + " Username have been used, and it's not uniq", "PROBLEM");
        }

        if (params.get("_save") != null) {
            redirect(request.controller + ".list");
        }
        if (params.get("_saveAndAddAnother") != null) {
            redirect(request.controller + ".blank");
        }
        redirect(request.controller + ".show", object._key());

    }

    public static void save(String id, String encryptedPassword, String oldUsername) throws Exception {

        ObjectType type = ObjectType.get(getControllerClass());
        notFoundIfNull(type);
        Model object = type.findById(id);
        notFoundIfNull(object);
        Binder.bindBean(params.getRootParamNode(), "object", object);
        validation.valid(object);
        if (validation.hasErrors()) {
            renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
            try {
                render(request.controller.replace(".", "/") + "/show.html", type, object);
            } catch (TemplateNotFoundException e) {
                render("CRUD/show.html", type, object);
            }
        }



        Player player = (Player) object;
        boolean usernameIsStillUniq = true;
        if (!player.username.equals(oldUsername)) {

            Player chekPlayer = Player.find("byUsername", player.username).first();
            if (chekPlayer != null) {
                usernameIsStillUniq = false;
                flash.error(type.modelName + " Username have been used, and it's not uniq", "PROBLEM");
            }
        }


        //Admin whants to change players password
        if (!encryptedPassword.equals(player.password)) {
            String temp = Crypto.encryptAES(player.password);
            player.password = temp;

        } else if (encryptedPassword.equals(player.password)) {
            player.password = Crypto.decryptAES(player.password);
            player.password = Crypto.encryptAES(player.password);
        }

        object = player;
        if (usernameIsStillUniq) {
            object._save();
            flash.success(play.i18n.Messages.get("crud.saved", type.modelName));
        }
        if (params.get("_save") != null && usernameIsStillUniq) {
            redirect(request.controller + ".list");
        }
        redirect(request.controller + ".show", object._key());
    }
}
