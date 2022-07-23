package com.jazzkuh.gunshell.utils.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Subcommand {
    String name();
    String usage() default "";
    String description() default "No description";
    boolean permission() default false;
    boolean playerOnly() default false;
    String aliases() default "";
}
