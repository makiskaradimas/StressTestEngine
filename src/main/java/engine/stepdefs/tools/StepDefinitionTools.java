package engine.stepdefs.tools;

import cucumber.api.DataTable;
import org.springframework.stereotype.Service;

/**
 * @author ekaradimas
 * @since 13/2/2015
 */
@Service("StepDefinitionTools")
public class StepDefinitionTools {
	public int getPositionOfKeyInDataTable(DataTable dataTable, String key) {
		int i = 0;
		while (!dataTable.getGherkinRows().get(i).getCells().get(0).equals(key)) {
			i++;
		}
		return i;
	}
}
