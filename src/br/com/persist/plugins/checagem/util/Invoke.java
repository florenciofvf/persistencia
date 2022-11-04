package br.com.persist.plugins.checagem.util;

import java.lang.reflect.Method;

import br.com.persist.assistencia.ListaEncadeada;
import br.com.persist.plugins.checagem.Bloco;
import br.com.persist.plugins.checagem.Checagem;
import br.com.persist.plugins.checagem.ChecagemException;
import br.com.persist.plugins.checagem.Contexto;
import br.com.persist.plugins.checagem.funcao.FuncaoBinariaOuMaior;

public class Invoke extends FuncaoBinariaOuMaior {
	private static final String ERRO = "Erro Invoke";

	public Invoke() {
		super(4);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object executar(Checagem checagem, Bloco bloco, Contexto ctx) throws ChecagemException {
		Object objeto = param0().executar(checagem, bloco, ctx);
		Object metodo = param1().executar(checagem, bloco, ctx);
		checkObrigatorioString(metodo, ERRO + " >>> metodo");
		Object classes = parametros.get(2).executar(checagem, bloco, ctx);
		Object objetos = parametros.get(3).executar(checagem, bloco, ctx);
		ListaEncadeada<Class<?>> classess = (ListaEncadeada<Class<?>>) classes;
		ListaEncadeada<Object> objetoss = (ListaEncadeada<Object>) objetos;
		try {
			Class<?> klass = objeto.getClass();
			Method method = klass.getMethod((String) metodo, classess.toArray(new Class[0]));
			return method.invoke(objeto, objetoss.toArray());
		} catch (Exception ex) {
			throw new ChecagemException(getClass(), ERRO + " >>> " + ex.getMessage());
		}
	}

	@Override
	public String getDoc() throws ChecagemException {
		return "invoke(Texto, Texto, [Texto, Objeto]) : Objeto";
	}
}