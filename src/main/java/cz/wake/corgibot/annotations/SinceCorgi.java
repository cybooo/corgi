package cz.wake.corgibot.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.TYPE)
@Inherited
@Retention(RetentionPolicy.SOURCE)
public @interface SinceCorgi {

    /*
        This annotation is used for determinating what release added given function.
     */

    String version();
}
