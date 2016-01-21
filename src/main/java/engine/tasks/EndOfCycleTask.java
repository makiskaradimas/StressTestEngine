package engine.tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import engine.actions.EndOfCycleAction;

/**
 * @author ekaradimas
 * @since 12/2/2015
 */
@Service("EndOfCycleTask")
@Scope("prototype")
public class EndOfCycleTask implements Runnable {

	@Autowired
	EndOfCycleAction statisticsAction;

	public EndOfCycleTask() {
	}

	public void run() {
		statisticsAction.execute();
	}

}
