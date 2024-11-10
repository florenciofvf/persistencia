package br.com.persist.geradores;

import br.com.persist.assistencia.Util;

public class ContainerJV extends Container {
	protected ContainerJV(String id) {
		super(id);
	}

	public InterfacePublica criarInterfacePublica(String string) {
		InterfacePublica interfacee = new InterfacePublica(string);
		add(interfacee);
		return interfacee;
	}

	public RetornoClasseAnonima criarRetornoClasseAnonima(String string) {
		RetornoClasseAnonima classe = new RetornoClasseAnonima(string);
		add(classe);
		return classe;
	}

	public ClassePublica criarClassePublica(String string) {
		ClassePublica classe = new ClassePublica(string);
		add(classe);
		return classe;
	}

	public ClassePrivada criarClassePrivada(String string) {
		ClassePrivada classe = new ClassePrivada(string);
		add(classe);
		return classe;
	}

	public FuncaoAbstrata criarFuncaoAbstrata(String retorno, String nome, Parametros param) {
		FuncaoAbstrata funcao = new FuncaoAbstrata(retorno, nome, param);
		add(funcao);
		return funcao;
	}

	public FuncaoAbstrata criarFuncaoAbstrata(String retorno, String nome) {
		return criarFuncaoAbstrata(retorno, nome, new Parametros());
	}

	public FuncaoPublica criarFuncaoPublica(String retorno, String nome, Parametros param) {
		FuncaoPublica funcao = new FuncaoPublica(retorno, nome, param);
		add(funcao);
		return funcao;
	}

	public FuncaoPublica criarFuncaoPublica(String retorno, String nome) {
		return criarFuncaoPublica(retorno, nome, new Parametros());
	}

	public FuncaoPublicaEstatica criarFuncaoPublicaEstatica(String retorno, String nome, Parametros param) {
		FuncaoPublicaEstatica funcao = new FuncaoPublicaEstatica(retorno, nome, param);
		add(funcao);
		return funcao;
	}

	public FuncaoPublicaEstatica criarFuncaoPublicaEstatica(String retorno, String nome) {
		return criarFuncaoPublicaEstatica(retorno, nome, new Parametros());
	}

	public FuncaoProtegida criarFuncaoProtegida(String retorno, String nome, Parametros param) {
		FuncaoProtegida funcao = new FuncaoProtegida(retorno, nome, param);
		add(funcao);
		return funcao;
	}

	public FuncaoProtegida criarFuncaoProtegida(String retorno, String nome) {
		return criarFuncaoProtegida(retorno, nome, new Parametros());
	}

	public FuncaoDefault criarFuncaoDefault(String retorno, String nome, Parametros param) {
		FuncaoDefault funcao = new FuncaoDefault(retorno, nome, param);
		add(funcao);
		return funcao;
	}

	public FuncaoDefault criarFuncaoDefault(String retorno, String nome) {
		return criarFuncaoDefault(retorno, nome, new Parametros());
	}

	public FuncaoPrivada criarFuncaoPrivada(String retorno, String nome, Parametros param) {
		FuncaoPrivada funcao = new FuncaoPrivada(retorno, nome, param);
		add(funcao);
		return funcao;
	}

	public FuncaoPrivada criarFuncaoPrivada(String retorno, String nome) {
		return criarFuncaoPrivada(retorno, nome, new Parametros());
	}

	public ConstrutorPublico criarConstrutorPublico(String nome, Parametros param) {
		ConstrutorPublico construtor = new ConstrutorPublico(nome, param);
		add(construtor);
		return construtor;
	}

	public ConstrutorPublico criarConstrutorPublico(String nome) {
		return criarConstrutorPublico(nome, new Parametros());
	}

	public ConstrutorPrivado criarConstrutorPrivado(String nome, Parametros param) {
		ConstrutorPrivado construtor = new ConstrutorPrivado(nome, param);
		add(construtor);
		return construtor;
	}

	public ConstrutorPrivado criarConstrutorPrivado(String nome) {
		return criarConstrutorPrivado(nome, new Parametros());
	}

	public MetodoGet criarMetodoGet(Variavel variavel) {
		MetodoGet get = new MetodoGet(variavel);
		add(get);
		return get;
	}

	public MetodoSet criarMetodoSet(Variavel variavel) {
		MetodoSet set = new MetodoSet(variavel);
		add(set);
		return set;
	}

	public Container addCampoConstanteString(String nome, String valor) {
		return addCampoConstante(new Variavel("String", nome), Util.citar2(valor));
	}

	public Container addCampoConstante(Variavel variavel, String valor) {
		add(new CampoConstante(variavel, valor));
		return this;
	}

	public Container addCampoPrivado(Variavel variavel) {
		add(new CampoPrivado(variavel));
		return this;
	}

	public Container addVariavel(Variavel variavel) {
		add(variavel);
		return this;
	}

	public Container addAnotacao(String string, boolean ql) {
		add(new Anotacao(string, ql));
		return this;
	}

	public Container addAnotacaoPath(String string) {
		add(new AnotacaoPath(string));
		return this;
	}

	public Container addAnotacao(String string) {
		return addAnotacao(string, true);
	}

	public Container addOverride(boolean newLine) {
		if (newLine) {
			newLine();
		}
		return addAnotacao("Override");
	}

	public Container addImport(String string) {
		add(new Importar(string));
		return this;
	}
}