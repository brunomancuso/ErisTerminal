package org.threewaves.eris.terminal;

import java.util.ArrayList;
import java.util.List;

class MergeAutoComplete implements IAutoComplete {
	public final List<IAutoComplete> sources = new ArrayList<>();

	public MergeAutoComplete() {
	}

	public MergeAutoComplete add(IAutoComplete a) {
		sources.add(a);
		return this;
	}

	@Override
	public List<List<String>> complete(String text) {
		List<List<String>> ls = new ArrayList<>();
		for (IAutoComplete c : sources) {
			for (List<String> list : c.complete(text)) {
				if (list != null && list.size() > 0) {
					ls.add(list);
				}
			}
		}
		return ls;
	}
}
