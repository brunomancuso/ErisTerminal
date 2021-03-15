package org.threewaves.eris.engine;

import java.util.Collections;
import java.util.List;

import org.threewaves.eris.engine.IBuilder.Scope;

/**
 * Creates a default eris factory, with no builders or external commands 
 * @author Bruno Mancuso
 *
 */
public class DefaultErisFactory implements IErisFactory {

	/**
	 * @param scope
	 * @return List of builders with Scope scope
	 */
	@Override
	public List<IBuilder> createBuilders(Scope scope) {
		return Collections.emptyList();
	}

	/**
	 * @return List of external commands
	 */
	@Override
	public List<ICommand> createExternalCommands() {
		return Collections.emptyList();
	}

}
