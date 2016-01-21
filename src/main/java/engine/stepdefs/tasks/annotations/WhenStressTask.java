package engine.stepdefs.tasks.annotations;

import java.lang.annotation.*;

/**
 * @author ekaradimas
 * @since 12/2/2015
 */
@Inherited
@Documented
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface WhenStressTask {
	String value();
}
