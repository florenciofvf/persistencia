package br.com.persist.plugins.propriedade;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.persist.marca.XMLUtil;

public class Bloco extends Container {
	private final Map<String, String> mapString;

	public Bloco(String nome) {
		super(nome);
		mapString = new HashMap<>();
	}

	public Map<String, String> getMapString() {
		return mapString;
	}

	@Override
	public void adicionar(Container c) {
		if (c instanceof Param || c instanceof Propriedade) {
			super.adicionar(c);
		} else {
			throw new IllegalStateException();
		}
	}

	@Override
	public void salvar(Container pai, XMLUtil util) {
		Map<String, Config> mapConfig = ((Raiz) pai).getMapConfig();
		List<Param> params = getParams();
		mapString.clear();
		for (Param param : params) {
			Config config = mapConfig.get(param.getValor());
			if (config != null) {
				put(param, config);
			}
		}
		for (Propriedade prop : getPropriedades()) {
			prop.salvar(this, util);
		}
	}

	private void put(Param param, Config config) {
		for (Atributo att : config.getAtributos()) {
			mapString.put(param.getNome() + "." + att.getNome(), att.getValor());
		}
	}

	private List<Param> getParams() {
		List<Param> resp = new ArrayList<>();
		for (Container c : getFilhos()) {
			if (c instanceof Param) {
				resp.add((Param) c);
			}
		}
		return resp;
	}

	private List<Propriedade> getPropriedades() {
		List<Propriedade> resp = new ArrayList<>();
		for (Container c : getFilhos()) {
			if (c instanceof Propriedade) {
				resp.add((Propriedade) c);
			}
		}
		return resp;
	}
}