package br.com.persist.plugins.propriedade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.persist.marca.XMLUtil;

public class Raiz extends Container {
	private final Map<String, Config> mapConfig;

	public Raiz() {
		super(null);
		mapConfig = new HashMap<>();
	}

	public Map<String, Config> getMapConfig() {
		return mapConfig;
	}

	@Override
	public void adicionar(Container c) {
		if (c instanceof Config || c instanceof Bloco) {
			super.adicionar(c);
		} else {
			throw new IllegalStateException();
		}
	}

	@Override
	public void salvar(Container pai, XMLUtil util) {
		List<Config> configs = getConfigs();
		mapConfig.clear();
		for (Config config : configs) {
			mapConfig.put(config.getNome(), config);
		}
		for (Bloco bloco : getBlocos()) {
			bloco.salvar(this, util);
		}
	}

	private List<Config> getConfigs() {
		List<Config> resp = new ArrayList<>();
		for (Container c : getFilhos()) {
			if (c instanceof Config) {
				resp.add((Config) c);
			}
		}
		return resp;
	}

	private List<Bloco> getBlocos() {
		List<Bloco> resp = new ArrayList<>();
		for (Container c : getFilhos()) {
			if (c instanceof Bloco) {
				resp.add((Bloco) c);
			}
		}
		return resp;
	}
}