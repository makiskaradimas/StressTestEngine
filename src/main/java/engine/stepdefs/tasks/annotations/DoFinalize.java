package engine.stepdefs.tasks.annotations;

import java.lang.annotation.*;

/**
 * @author ekaradimas
 * @since 12/2/2015
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DoFinalize {
}
