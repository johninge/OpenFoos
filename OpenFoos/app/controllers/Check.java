/*
 * THIS IS module.secure=${play.path}/modules/secure/Check
 * This interface is been used inside secure class
 */
package controllers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Check {

    String[] value();
}
