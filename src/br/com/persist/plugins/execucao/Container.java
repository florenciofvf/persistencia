package br.com.persist.plugins.execucao;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.plugins.variaveis.Variavel;
import br.com.persist.plugins.variaveis.VariavelProvedor;

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

	public void lerAtributos(String tag, Attributes attributes) {
		for (int i = 0; i < attributes.getLength(); i++) {
			string = attributes.getValue(i);
		}
		if (string == null) {
			throw new IllegalStateException("Nenhum atributo em: " + tag);
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

	public void processar(StringBuilder sb, boolean confirmar, Component comp, List<Variavel> variaveis) {
		if (filhos.isEmpty()) {
			executar(sb, confirmar, comp, variaveis);
		} else {
			for (Container c : filhos) {
				c.processar(sb, confirmar, comp, variaveis);
			}
		}
	}

	private void executar(StringBuilder sb, boolean confirmar, Component comp, List<Variavel> variaveis) {
		try {
			String comando = gerarComando(variaveis);
			if (sb != null) {
				if (sb.length() > 0) {
					sb.append(Constantes.QL);
				}
				sb.append("[" + comando + "]" + Constantes.QL2);
			}
			if (confirmar && !Util.confirmar(comp, comando, false)) {
				return;
			}
			Process process = Runtime.getRuntime().exec(comando);
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

	private String gerarComando(List<Variavel> variaveis) {
		StringBuilder sb = new StringBuilder(string);
		Container container = pai;
		while (container != null) {
			sb.insert(0, container.string);
			container = container.pai;
		}
		if (variaveis != null) {
			for (Variavel var : variaveis) {
				sb.append(" " + var.getValor());
			}
		}
		return VariavelProvedor.substituir(sb.toString());
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