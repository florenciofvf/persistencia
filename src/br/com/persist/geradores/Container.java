package br.com.persist.geradores;

import java.util.ArrayList;
import java.util.List;

import br.com.persist.assistencia.StringPool;
import br.com.persist.assistencia.Util;

public abstract class Container extends Objeto {
	protected final List<Objeto> objetos;

	protected Container(String id) {
		super(id);
		objetos = new ArrayList<>();
	}

	protected Container add(Objeto o) {
		if (o == null) {
			return this;
		}
		if (o.parent != null) {
			o.parent.remover(o);
		}
		if (addInvalido(o)) {
			return this;
		}
		o.parent = this;
		objetos.add(o);
		return this;
	}

	private boolean addInvalido(Objeto o) {
		Objeto obj = this;
		while (obj != null) {
			if (obj == o) {
				return true;
			}
			obj = obj.parent;
		}
		return false;
	}

	public Container remover(Objeto o) {
		if (o.parent == this) {
			objetos.remove(o);
			o.parent = null;
		}
		return this;
	}

	public Objeto get(int indice) {
		if (indice >= 0 && indice < objetos.size()) {
			return objetos.get(indice);
		}
		return null;
	}

	@Override
	public void gerar(int tab, StringPool pool) {
		for (Objeto o : objetos) {
			o.gerar(tab + 1, pool);
		}
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

	public FuncaoPublica criarFuncaoPublica(String retorno, String nome, Parametros param) {
		FuncaoPublica funcao = new FuncaoPublica(retorno, nome, param);
		add(funcao);
		return funcao;
	}

	public FuncaoPublica criarFuncaoPublica(String retorno, String nome) {
		return criarFuncaoPublica(retorno, nome, new Parametros());
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

	public ConstrutorPrivado criarConstrutorPrivado(String nome, Parametros param) {
		ConstrutorPrivado construtor = new ConstrutorPrivado(nome, param);
		add(construtor);
		return construtor;
	}

	public ConstrutorPublico criarConstrutorPublico(String nome) {
		return criarConstrutorPublico(nome, new Parametros());
	}

	public Try criarTry(Catch catche) {
		Try tre = new Try(catche);
		add(tre);
		return tre;
	}

	public If criarIf(String condicao, Else elsee) {
		If se = new If(condicao, elsee);
		add(se);
		return se;
	}

	public Container newLine() {
		add(new NewLine());
		return this;
	}

	public Container addString(String string) {
		add(new Sequence(string));
		return this;
	}

	public Container addCampoConstanteString(String nome, String valor) {
		return addCampoConstante(new Variavel("String", nome), Util.citar2(valor));
	}

	public Container addCampoConstante(Variavel var, String valor) {
		add(new CampoConstante(var, valor));
		return this;
	}

	public Container addInstrucao(String string) {
		add(new Instrucao(string));
		return this;
	}

	public Container addComentario(String string) {
		add(new Comentario(string));
		return this;
	}

	public Container addOverride() {
		add(new Anotacao("Override", true));
		return this;
	}

	public Container addImport(String string) {
		add(new Importar(string));
		return this;
	}

	public Container addVarJS(String string) {
		add(new VarJS(string));
		return this;
	}

	public Container addReturn(String string) {
		add(new Retornar(string));
		return this;
	}
}