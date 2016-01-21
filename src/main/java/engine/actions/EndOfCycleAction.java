package engine.actions;

import org.springframework.stereotype.Service;

/**
 * @author ekaradimas
 * @since 12/2/2015
 */
@Service("EndOfCycleAction")
public final class EndOfCycleAction implements Action {
	public void doInit() {
	}

	public boolean execute() {
		// System.out.println("Computing statistics");
		return true;
	}

	public void doFinalize() {
	}
}
