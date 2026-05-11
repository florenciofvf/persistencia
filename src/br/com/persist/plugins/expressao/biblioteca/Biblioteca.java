package br.com.persist.plugins.expressao.biblioteca;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import br.com.persist.assistencia.MetaInfo;
import br.com.persist.plugins.expressao.constante.Constante;
import br.com.persist.plugins.expressao.funcao.FuncaoConstantesContexto;
import br.com.persist.plugins.expressao.processador.Funcao;
import br.com.persist.plugins.expressao.ExpressaoConstantes;
import br.com.persist.plugins.expressao.ExpressaoException;

public class Biblioteca {
	private final Map<String, Constante> mapaConstantes;
	private final Map<String, BiblioAlias> mapaAlias;
	private final Map<String, Funcao> mapaFuncoes;
	public static final String EXTENSAO = ".fvf2";
	private String nomePacote;
	private final String nome;

	public Biblioteca(File file) {
		this.mapaConstantes = new LinkedHashMap<>();
		this.mapaFuncoes = new LinkedHashMap<>();
		this.mapaAlias = new HashMap<>();
		this.nome = file.getName();
	}

	public String getNomeAbsoluto() {
		return nomePacote + "." + nome;
	}

	public String getNomeSimples() {
		return nome;
	}

	public String getNomePacote() {
		return nomePacote;
	}

	public void setNomePacote(String pacote) {
		this.nomePacote = pacote;
	}

	public String getBiblioteca(String alias) {
		BiblioAlias obj = mapaAlias.get(alias);
		if (obj != null) {
			return obj.biblio;
		}
		return null;
	}

	public void addAlias(String string) {
		String[] strings = string.split(ExpressaoConstantes.ESPACO);
		BiblioAlias obj = new BiblioAlias(strings[0], strings[1]);
		mapaAlias.put(obj.alias, obj);
	}

	public void addConstante(Constante constante) {
		if (constante != null) {
			mapaConstantes.put(constante.getNome(), constante);
			constante.setBiblioteca(this);
		}
	}

	public void addFuncao(Funcao funcao) {
		if (funcao != null && funcao.getBiblioteca() == this) {
			mapaFuncoes.put(funcao.getNome(), funcao);
		}
	}

	public boolean contemFuncao(String nome) {
		return mapaFuncoes.containsKey(nome);
	}

	public Constante getConstante(String nome) throws ExpressaoException {
		Constante constante = mapaConstantes.get(nome);
		if (constante == null) {
			throw new ExpressaoException("erro.constante_inexistente", nome, getNomeAbsoluto());
		}
		return constante;
	}

	public Funcao getFuncao(String nome) throws ExpressaoException {
		Funcao funcao = mapaFuncoes.get(nome);
		if (funcao == null) {
			throw new ExpressaoException("erro.funcao_inexistente", nome, getNomeAbsoluto());
		}
		return funcao;
	}

	public Funcao getFuncao2(String nome) {
		return mapaFuncoes.get(nome);
	}

	public List<MetaInfo> getNomeConstantes() {
		List<MetaInfo> lista = new ArrayList<>();
		for (String item : mapaConstantes.keySet()) {
			lista.add(new MetaInfo(item, item));
		}
		return lista;
	}

	public List<MetaInfo> getNomeFuncoes() {
		List<MetaInfo> lista = new ArrayList<>();
		for (Funcao item : mapaFuncoes.values()) {
			if (FuncaoConstantesContexto.NOME_FUNCAO_CONSTANTES.equals(item.getNome())) {
				continue;
			}
			lista.add(new MetaInfo(item.getInterface(), item.getInterfaceInfo()));
		}
		return lista;
	}

	@Override
	public String toString() {
		return nome;
	}
}

class BiblioAlias {
	final String biblio;
	final String alias;

	public BiblioAlias(String biblio, String alias) {
		this.biblio = Objects.requireNonNull(biblio);
		this.alias = Objects.requireNonNull(alias);
	}

	@Override
	public String toString() {
		return biblio + " " + alias;
	}
}