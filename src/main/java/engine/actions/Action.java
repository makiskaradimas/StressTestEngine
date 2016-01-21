package engine.actions;

/**
 * @author ekaradimas
 * @since 9/2/2015
 */
public interface Action {
	public void doInit();

	public boolean execute();

	public void doFinalize();
}
