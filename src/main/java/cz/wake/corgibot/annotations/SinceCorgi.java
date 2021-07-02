package cz.wake.corgibot.annotations;

import java.lang.annotation.*;

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
