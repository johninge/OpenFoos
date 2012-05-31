/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.lang.reflect.Constructor;
import models.Admin;
import play.data.binding.Binder;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.libs.Crypto;
import play.mvc.With;

@With(Secure.class)
public class Admins extends CRUD {

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
        Admin admin = (Admin) object;
        //important to encrypt admins password here
        admin.password = Crypto.encryptAES(admin.password);
        object = admin;
        object._save();
        flash.success(play.i18n.Messages.get("crud.created", type.modelName));
        if (params.get("_save") != null) {
            redirect(request.controller + ".list");
        }
        if (params.get("_saveAndAddAnother") != null) {
            redirect(request.controller + ".blank");
        }
        redirect(request.controller + ".show", object._key());
    }

    public static void save(String id, String encryptedPassword) throws Exception {
      
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

        Admin admin = (Admin) object;
        //Admin whants to change admins password
        if (!encryptedPassword.equals(admin.password)) {

            //exchange temp to admins password
            String temp = Crypto.encryptAES(admin.password);
            admin.password = temp;

        } else if (encryptedPassword.equals(admin.password)) {
            admin.password = Crypto.decryptAES(admin.password);
            admin.password = Crypto.encryptAES(admin.password);
        }

        object = admin;
        object._save();
        
        flash.success(play.i18n.Messages.get("crud.saved", type.modelName));
        if (params.get("_save") != null) {
            redirect(request.controller + ".list");
        }
        redirect(request.controller + ".show", object._key());
    }

    static void onDisconnected() {
        CRUD.index();
    }

    static void onAuthenticated() {
        CRUD.index();
    }
}
