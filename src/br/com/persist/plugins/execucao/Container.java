package br.com.persist.plugins.execucao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;

import br.com.persist.assistencia.Constantes;

public class Container {
	private final List<Container> filhos;
	private String string;
	private Container pai;

	public Container() {
		filhos = new ArrayList<>();
	}

	public Container getPai() {
		return pai;
	}

	public void lerAtributos(Attributes attributes) {
		for (int i = 0; i < attributes.getLength(); i++) {
			string = attributes.getValue(i);
		}
	}

	public List<Container> getFilhos() {
		return filhos;
	}

	public Container get(int i) {
		return filhos.get(i);
	}

	public int getIndice(Container container) {
		for (int i = 0; i < filhos.size(); i++) {
			if (filhos.get(i) == container) {
				return i;
			}
		}
		return -1;
	}

	public void adicionar(Container container) {
		if (container != null) {
			if (container.getPai() != null) {
				container.getPai().excluir(container);
			}
			filhos.add(container);
			container.pai = this;
		}
	}

	public void excluir(Container container) {
		if (container.getPai() == this) {
			filhos.remove(container);
		}
	}

	@Override
	public String toString() {
		return string;
	}

	public void processar(StringBuilder sb) {
		if (filhos.isEmpty()) {
			executar(sb);
		} else {
			for (Container c : filhos) {
				c.processar(sb);
			}
		}
	}

	private void executar(StringBuilder sb) {
		try {
			Process process = Runtime.getRuntime().exec(gerarComando());
			if (sb != null) {
				imprimir(process.getErrorStream(), sb);
				imprimir(process.getInputStream(), sb);
			}
		} catch (IOException e) {
			if (sb != null) {
				sb.append(e.getMessage());
			}
		}
	}

	private String gerarComando() {
		StringBuilder sb = new StringBuilder(string);
		Container container = pai;
		while (container != null) {
			sb.insert(0, container.string);
			container = container.pai;
		}
		return sb.toString();
	}

	private void imprimir(InputStream is, StringBuilder sb) throws IOException {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
			String linha = br.readLine();
			while (linha != null) {
				sb.append(linha + Constantes.QL);
				linha = br.readLine();
			}
		}
	}
}