package br.com.persist.plugins.expressao.processador;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import br.com.persist.plugins.expressao.biblioteca.Biblioteca;
import br.com.persist.plugins.expressao.biblioteca.CacheBiblioteca;
import br.com.persist.plugins.expressao.biblioteca.LinkBiblioteca;
import br.com.persist.plugins.expressao.funcao.FuncaoConstantesContexto;
import br.com.persist.plugins.expressao.ExpressaoException;

public class Processador {
	private final CacheBiblioteca cacheBiblioteca = new CacheBiblioteca();
	private final PilhaOperando pilhaOperando = new PilhaOperando();
	private final PilhaFuncao pilhaFuncao = new PilhaFuncao();

	public List<Object> processar(String nomeBiblioAbsoluto, String nomeFuncao, Object... args)
			throws ExpressaoException {
		Biblioteca biblioteca = cacheBiblioteca.getBiblioteca(nomeBiblioAbsoluto);

		Funcao funcao = biblioteca.getFuncao(nomeFuncao).clonar();

		for (int i = 0; i < args.length; i++) {
			funcao.setValorParametro(i, args[i]);
		}

		Funcao funcaoConstantes = biblioteca.getFuncao(FuncaoConstantesContexto.NOME_FUNCAO_CONSTANTES).clonar();

		pilhaFuncao.clear();
		pilhaOperando.clear();
		pilhaFuncao.push(funcao);
		pilhaFuncao.push(funcaoConstantes);

		funcao = pilhaFuncao.peek();

		AtomicBoolean processar = new AtomicBoolean(false);

		while (funcao != null) {
			processar.set(false);
			Instrucao instrucao = funcao.proximaInstrucao();
			if (instrucao instanceof LinkBiblioteca) {
				funcao = processarLink(funcao, processar, instrucao);
			} else {
				processar.set(true);
			}
			if (processar.get()) {
				instrucao.processar(funcao, pilhaFuncao, pilhaOperando);
				funcao = pilhaFuncao.isEmpty() ? null : pilhaFuncao.peek();
			}
		}

		List<Object> resposta = new ArrayList<>();

		while (!pilhaOperando.isEmpty()) {
			resposta.add(pilhaOperando.pop());
		}

		return resposta;
	}

	private Funcao processarLink(Funcao funcao, AtomicBoolean processar, Instrucao instrucao)
			throws ExpressaoException {
		LinkBiblioteca link = (LinkBiblioteca) instrucao;
		if (link.isRefLocal()) {
			processar.set(true);
		} else {
			if (cacheBiblioteca.contem(link.getNomeBiblioAbsoluto())) {
				Biblioteca cacheada = cacheBiblioteca.getBiblioteca(link.getNomeBiblioAbsoluto());
				pilhaOperando.push(cacheada);
				processar.set(true);
			} else {
				Biblioteca novaCacheada = cacheBiblioteca.getBiblioteca(link.getNomeBiblioAbsoluto());
				Funcao fnConstantes = novaCacheada.getFuncao(FuncaoConstantesContexto.NOME_FUNCAO_CONSTANTES).clonar();
				funcao.setIndice(link.getIndice());
				pilhaFuncao.push(fnConstantes);
				funcao = pilhaFuncao.peek();
				processar.set(false);
			}
		}
		return funcao;
	}

	public CacheBiblioteca getCacheBiblioteca() {
		return cacheBiblioteca;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(cacheBiblioteca.toString() + "\n");
		sb.append(pilhaFuncao.toString() + "\n");
		sb.append(pilhaOperando.toString());
		return sb.toString();
	}
}