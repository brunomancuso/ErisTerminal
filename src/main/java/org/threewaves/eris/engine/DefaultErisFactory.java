package org.threewaves.eris.engine;

import java.util.Collections;
import java.util.List;

import org.threewaves.eris.engine.IBuilder.Scope;

public class DefaultErisFactory implements IErisFactory {

	@Override
	public List<IBuilder> createBuilders(Scope scope) {
		return Collections.emptyList();
	}

	@Override
	public List<ICommand> createExternalCommands() {
		return Collections.emptyList();
	}

}
