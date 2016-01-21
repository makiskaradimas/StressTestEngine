package stress.engine;


import cucumber.api.junit.Cucumber;
import engine.suites.StressSurefire;

import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

/**
 * @author ekaradimas
 * @since 9/2/2015
 */
@RunWith(Cucumber.class)
@Cucumber.Options(
        glue = {"engine"},
        features = {"src/test/resources/scenarios/"},
        format = {"json", "json:target/cucumber.json"}
)
@Category(StressSurefire.class)
public class CucumberExecutor {

}
