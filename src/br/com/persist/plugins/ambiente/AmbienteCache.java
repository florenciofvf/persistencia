package br.com.persist.plugins.ambiente;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import br.com.persist.assistencia.ArgumentoException;

public class AmbienteCache {
	private final List<Ambiente> ambientes;

	public AmbienteCache() {
		ambientes = new ArrayList<>();
	}

	public void inicializar(String arquivoProperties) throws IOException {
		if (arquivoProperties != null) {
			ResourceBundle bundle = new PropertyResourceBundle(new FileInputStream(arquivoProperties));
			int indice = 0;
			while (bundle.containsKey("chave." + indice)) {
				processar(bundle, "chave.", indice);
				indice++;
			}
		}
	}

	private void processar(ResourceBundle bundle, String chave, int indice) {
		String chaveAmbiente = bundle.getString(chave + indice);
		String tituloMin = bundle.getString("tituloMin." + indice);
		String titulo = bundle.getString("titulo." + indice);
		ambientes.add(new Ambiente(chaveAmbiente, titulo, tituloMin));
	}

	public List<Ambiente> getAmbientes() {
		return ambientes;
	}

	public Ambiente get(String nome) throws ArgumentoException {
		for (Ambiente a : ambientes) {
			if (a.chave.equals(nome)) {
				return a;
			}
		}
		throw new ArgumentoException(nome);
	}
}