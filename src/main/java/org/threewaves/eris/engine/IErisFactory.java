package org.threewaves.eris.engine;

import java.util.List;

import org.threewaves.eris.engine.IBuilder.Scope;

public interface IErisFactory {
	
	List<IBuilder> createBuilders(Scope scope);

	List<ICommand> createExternalCommands();
}
