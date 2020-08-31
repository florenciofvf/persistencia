package br.com.persist.fragmento;

import java.util.ArrayList;
import java.util.List;

public class FragmentoColetor {
	private final List<Fragmento> fragmentos;

	public FragmentoColetor() {
		fragmentos = new ArrayList<>();
	}

	public void init() {
		fragmentos.clear();
	}

	public List<Fragmento> getFragmentos() {
		return fragmentos;
	}
}