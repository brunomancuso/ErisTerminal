/**
 * Copyright (C) 2012 Ben Navetta <ben.navetta@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
