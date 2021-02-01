package org.threewaves.eris.terminal;

import java.util.List;

interface IAutoComplete {
	public List<List<String>> complete(String text);
	
	public static int count(List<List<String>> completions) {
		if (completions == null)
			return 0;
		int c = 0;
		for (List<String> list : completions) {
			c += list.size();
		}
		return c;
	}
}