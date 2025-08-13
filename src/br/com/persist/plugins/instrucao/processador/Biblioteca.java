package br.com.persist.plugins.instrucao.processador;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import br.com.persist.assistencia.MetaInfo;
import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;

public class Biblioteca {
	private final Map<String, Constante> constantes;
	public static final String EXTENSAO = ".fvf";
	private final Map<String, Importa> imports;
	private final Map<String, Funcao> funcoes;
	private String nomePackage;
	private final String nome;

	public Biblioteca(File file) {
		constantes = new LinkedHashMap<>();
		funcoes = new LinkedHashMap<>();
		this.nome = file.getName();
		imports = new HashMap<>();
	}

	public String getNome() {
		return nomePackage != null ? nomePackage + "." + nome : nome;
	}

	public String getNomeSimples() {
		return nome;
	}

	public String getNomePackage() {
		return nomePackage;
	}

	public String getNomeImport(String nome) {
		Importa obj = imports.get(nome);
		if (obj == null) {
			return nome;
		}
		return obj.biblio;
	}

	public void setNomePackage(String pacote) {
		this.nomePackage = pacote;
	}

	public void addImport(String string) {
		String[] strings = string.split(InstrucaoConstantes.ESPACO);
		Importa obj = new Importa(strings[0], strings[1]);
		imports.put(obj.alias, obj);
	}

	public void addConstante(Constante constante) {
		if (constante != null) {
			constantes.put(constante.getNome(), constante);
			constante.setBiblioteca(this);
		}
	}

	public void addFuncao(Funcao funcao) {
		if (funcao != null) {
			funcoes.put(funcao.getNome(), funcao);
			funcao.setBiblioteca(this);
		}
	}

	public boolean contemFuncao(String nome) {
		return funcoes.containsKey(nome);
	}

	public Constante getConstante(String nome) throws InstrucaoException {
		Constante constante = constantes.get(nome);
		if (constante == null) {
			throw new InstrucaoException("erro.constante_inexistente", nome, this.nome);
		}
		return constante;
	}

	public Funcao getFuncao(String nome) throws InstrucaoException {
		Funcao funcao = funcoes.get(nome);
		if (funcao == null) {
			throw new InstrucaoException("erro.funcao_inexistente", nome, this.nome);
		}
		return funcao;
	}

	public void initConstantes() throws InstrucaoException {
		for (Constante constante : constantes.values()) {
			constante.init();
		}
	}

	public List<MetaInfo> getNomeConstantes() {
		List<MetaInfo> lista = new ArrayList<>();
		for (String item : constantes.keySet()) {
			lista.add(new MetaInfo(item, item));
		}
		return lista;
	}

	public List<MetaInfo> getNomeFuncoes() {
		List<MetaInfo> lista = new ArrayList<>();
		for (Funcao item : funcoes.values()) {
			lista.add(new MetaInfo(item.getInterface(), item.getInterfaceInfo()));
		}
		return lista;
	}

	@Override
	public String toString() {
		return nome + " constantes:" + constantes;
	}
}

class Importa {
	final String biblio;
	final String alias;

	public Importa(String biblio, String alias) {
		this.biblio = Objects.requireNonNull(biblio);
		this.alias = Objects.requireNonNull(alias);
	}
}