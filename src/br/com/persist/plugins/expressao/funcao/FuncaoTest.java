package br.com.persist.plugins.expressao.funcao;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.ExpressaoTest;
import br.com.persist.plugins.expressao.compilador.Compilacao;
import br.com.persist.plugins.expressao.processador.Processador;

public class FuncaoTest extends ExpressaoTest {

	@Test
	public void teste0() throws IOException, ExpressaoException {
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(getFile("funcao", "__simples0"));

		Processador processador = new Processador();

		String biblio = "br.com.teste.__simples0";

		List<Object> result;

		result = processador.processar(biblio, "main1");
		assertEquals("[-5]", result.toString());

		result = processador.processar(biblio, "main2");
		assertEquals("[2]", result.toString());
	}

	@Test
	public void teste14() throws IOException, ExpressaoException {
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(getFile("funcao", "recursao14"));

		Processador processador = new Processador();

		String biblio = "br.com.teste.recursao14";

		List<Object> result;

		result = processador.processar(biblio, "fatorial", bi(5));
		assertEquals("[120]", result.toString());
	}

	@Test
	public void recursaoPerform() throws IOException, ExpressaoException {
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(getFile("funcao", "recursao_perform"));

		Processador processador = new Processador();

		String biblio = "br.com.teste.recursao_perform";

		List<Object> result;

		result = processador.processar(biblio, "fatorial", bi(5));
		assertEquals("[120]", result.toString());
	}
}