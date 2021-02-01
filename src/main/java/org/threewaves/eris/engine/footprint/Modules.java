package org.threewaves.eris.engine.footprint;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.threewaves.eris.engine.ConfigException;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Modules {
	private final List<Module> modules = new ArrayList<>();
	
	public void mark() {
		this.modules.forEach(m -> m.mark());
	}
	
	public void refresh() throws ConfigException {
		try {
			if (Files.exists(Paths.get("modules.json"))) {
				TypeToken<List<Module>> t = new TypeToken<List<Module>>() {};
				List<Module> modules = new ArrayList<>();
				modules.addAll(new Gson().fromJson(new String(Files.readAllBytes(Paths.get("modules.json")),Charset.defaultCharset()), t.getType()));
				for (Module m : modules) {
					m.compile();
				}				
				this.modules.clear();
				this.modules.addAll(modules);
				mark();
			}
		} catch (IOException e) {
			throw new ConfigException(e);
		}
	}

	public List<Module> getList() {
		return Collections.unmodifiableList(modules);
	}
	
	public void mark(String module) {
		Module m = Module.find(modules, module);
		if (m != null) {
			m.mark();
		}
	}

	public Optional<Module> find(String o) {
		return modules.stream().filter(m -> m.getName().equals(o)).findFirst();
	}

}
