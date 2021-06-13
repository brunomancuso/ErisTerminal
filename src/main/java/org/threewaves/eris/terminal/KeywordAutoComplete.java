package org.threewaves.eris.terminal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

class KeywordAutoComplete implements IAutoComplete {
	private final List<String> terms = new ArrayList<>();

	public KeywordAutoComplete() {
	}

	public KeywordAutoComplete add(Collection<String> collection) {
		terms.addAll(collection);
		return this;
	}

	private List<String> find(String input) {
		List<String> matches = new ArrayList<String>();
		if (input == null) {
			return matches;
		}
		String[] tmp = input.split("\\s+");
		if (tmp != null && tmp.length > 0) {
			input = tmp[tmp.length - 1];
		}
		for (String term : terms) {
			if (term.toLowerCase().startsWith(input.toLowerCase())) {
				matches.add(term);
			}
		}
		Collections.sort(matches);
		return matches;
	}

	@Override
	public List<List<String>> complete(String text) {
		return Collections.singletonList(find(text));
	}

}
