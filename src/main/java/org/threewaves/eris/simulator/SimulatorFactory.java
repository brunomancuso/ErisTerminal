package org.threewaves.eris.simulator;

public final class SimulatorFactory {
	private static final MemorySimulator memory = new MemorySimulator();

	private SimulatorFactory() {

	}

	public static ISimulator create(String name) {
		return memory;
	}
}
