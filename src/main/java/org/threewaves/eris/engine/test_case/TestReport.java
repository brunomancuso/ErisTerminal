package org.threewaves.eris.engine.test_case;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.threewaves.eris.engine.footprint.NormalDiff;

public class TestReport {
	public static class Report {
		private final List<String> failedModules = new ArrayList<>();
		private final Map<String, List<NormalDiff>> diff = new HashMap<>();
		private long duration = 0;
		
		public List<String> failedModules() {
			return Collections.unmodifiableList(failedModules);
		}
				
		public long getDuration() {
			return duration;
		}

		public boolean isFailed() {
			return failedModules.size() > 0;
		}
	}
	private final Map<String, Report> reportByTestCase = new HashMap<>();
	private final TestRun testRun;

	private TestCase current;
	
	public TestReport() {
		this.testRun = new TestRun();
	}
	
	public void start(TestCase testCase) {
		current = testCase;
		reportByTestCase.put(current.getName(), new Report());
		testRun.start(testCase);
	}

	public static TestReport create() {
		return new TestReport();
	}

	public void end() {
		TestRun.Data data = testRun.end(current);
		currentReport().duration = data.duration;
	}

	public TestRun getTestRun() {
		return testRun;
	}

	public void diff(String name, List<NormalDiff> diff) {
		if (diff.size() > 0) {
			currentReport().failedModules.add(name);			
		}
		currentReport().diff.put(name, diff);
	}

	public Report currentReport() {
		return reportByTestCase.get(current.getName());
	}

	public void summary() {
		int count = countFailedTestCases();
		if (count == 0) {
			System.out.println("----- Passed OK");
		} else {
			System.err.println("----- Failed TestCases " + count + "/" + reportByTestCase.size());			
			Map<String, Integer> failedModules = failedModules();
			if (failedModules.size() > 0) {
				System.err.println("----- Modules Failed:");
				for (Entry<String, Integer> m : failedModules.entrySet()) {
					System.err.println("      " + m.getKey() + " in " + m.getValue() + " TC(s)");
				}
			}
		}
		
	}

	private Map<String, Integer> failedModules() {
		Map<String, Integer> map = new HashMap<>();
		for (Report r : reportByTestCase.values()) {
			for (String m : r.failedModules) {
				if (!map.containsKey(m)) {
					map.put(m, 0);
				}
				map.put(m, map.get(m) + 1);
			}
		}		
		return map;
	}

	private int countFailedTestCases() {
		int c = 0;
		for (Report r : reportByTestCase.values()) {
			if (r.failedModules.size() > 0) {
				c++;
			}
		}
		return c;
	}

	public TestCase current() {
		return current;
	}
}
