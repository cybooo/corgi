package cz.wake.corgibot.annotations;

import cz.wake.corgibot.commands.CommandCategory;
import net.dv8tion.jda.api.Permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandInfo {
    String name();
    String[] aliases() default {};
    String help() default "";
    String description() default "";
    CommandCategory category() default CommandCategory.HIDDEN;
    Permission[] userPerms() default {};
    Permission[] botPerms() default {};
}
