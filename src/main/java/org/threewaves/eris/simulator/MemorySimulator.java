package org.threewaves.eris.simulator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class MemorySimulator implements ISimulator {
	private final Map<String, Map<String, Queue<String>>> simulators = new HashMap<>();

	public MemorySimulator() {
	}

	@Override
	public void put(String simulator, String key, Object response) {
		put(simulator, key, ISimulator.gson().toJson(response));
	}

	@Override
	public void put(String simulator, String key, String response) {
		Queue<String> rs;
		Map<String, Queue<String>> responses = this.simulators.get(simulator);
		if (responses == null) {
			responses = new HashMap<>();
			this.simulators.put(simulator, responses);
		}
		if (!responses.containsKey(key)) {
			rs = new LinkedList<>();
			responses.put(key, rs);
		} else {
			rs = responses.get(key);
		}
		rs.add(response);
	}

	@Override
	public String pop(String simulator, String key) {
		if (simulators.get(simulator) == null) {
			return null;
		}
		Queue<String> rs = simulators.get(simulator).get(key);
		if (rs != null && rs.size() > 0) {
			return rs.remove();
		}
		return null;
	}

	@Override
	public <T> T pop(Class<T> class1, String simulator, String key) {
		String r = pop(simulator, key);
		if (r == null) {
			return null;
		}
		return ISimulator.gson().fromJson(r, class1);
	}

	@Override
	public String get(String simulator, String key) {
		if (simulators.get(simulator) == null) {
			return null;
		}
		Queue<String> rs = simulators.get(simulator).get(key);
		if (rs != null && rs.size() > 0) {
			return rs.element();
		}
		return null;
	}

	@Override
	public void clear(String simulator) {
		Map<String, Queue<String>> sim = simulators.get(simulator);
		if (sim != null) {
			sim.clear();
		}
	}

}
