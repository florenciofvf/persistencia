package br.com.persist.plugins.expressao.instrucoes;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Compilacao;
import br.com.persist.plugins.expressao.processador.Processador;

public class ExpressaoTest extends br.com.persist.plugins.expressao.ExpressaoTest {

	@Test
	public void teste3() throws IOException, ExpressaoException {
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(getFile("expressao", "__simples3"));

		Processador processador = new Processador();

		String biblio = "br.com.teste.__simples3";

		List<Object> result;

		result = processador.processar(biblio, "mesmo", bi(30));
		assertEquals("[30]", result.toString());

		result = processador.processar(biblio, "mesmoNegado", bi(30));
		assertEquals("[-30]", result.toString());

		result = processador.processar(biblio, "mesmoNegado2", bi(30));
		assertEquals("[-30]", result.toString());
	}

	@Test
	public void teste4() throws IOException, ExpressaoException {
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(getFile("expressao", "__simples4"));

		Processador processador = new Processador();

		String biblio = "br.com.teste.__simples4";

		List<Object> result;

		result = processador.processar(biblio, "dobrar", bi(30));
		assertEquals("[60]", result.toString());

		result = processador.processar(biblio, "quadrado", bi(30));
		assertEquals("[900]", result.toString());

		result = processador.processar(biblio, "somar", bi(30), bi(3));
		assertEquals("[-33]", result.toString());

		result = processador.processar(biblio, "area", bi(30));
		assertEquals("[2827.4057100]", result.toString());
	}

	@Test
	public void teste5() throws IOException, ExpressaoException {
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(getFile("expressao", "__simples5"));

		Processador processador = new Processador();

		String biblio = "br.com.teste.__simples5";

		List<Object> result;

		result = processador.processar(biblio, "teste");
		assertEquals("[2]", result.toString());

		result = processador.processar(biblio, "dividir", bi(30), bi(3));
		assertEquals("[0]", result.toString());
	}

	@Test
	public void teste6() throws IOException, ExpressaoException {
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(getFile("expressao", "__simples6"));

		Processador processador = new Processador();

		String biblio = "br.com.teste.__simples6";

		List<Object> result;

		result = processador.processar(biblio, "expressao_01", bi(3), bi(4), bi(5));
		assertEquals("[23]", result.toString());

		result = processador.processar(biblio, "expressao_02", bi(3), bi(4), bi(5));
		assertEquals("[35]", result.toString());

		result = processador.processar(biblio, "expressao_03", bi(3), bi(4), bi(5));
		assertEquals("[-35]", result.toString());
	}

	@Test
	public void teste7() throws IOException, ExpressaoException {
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(getFile("expressao", "__simples7"));

		Processador processador = new Processador();

		String biblio = "br.com.teste.__simples7";

		List<Object> result;

		result = processador.processar(biblio, "expressao");
		assertEquals("[14]", result.toString());
	}

	@Test
	public void teste8() throws IOException, ExpressaoException {
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(getFile("expressao", "__simples8"));

		Processador processador = new Processador();

		String biblio = "br.com.teste.__simples8";

		List<Object> result;

		result = processador.processar(biblio, "igual", bi(3), bi(3));
		assertEquals("[1]", result.toString());

		result = processador.processar(biblio, "diff", bi(3), bi(3));
		assertEquals("[0]", result.toString());
	}

	@Test
	public void teste9() throws IOException, ExpressaoException {
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(getFile("expressao", "__simples9"));

		Processador processador = new Processador();

		String biblio = "br.com.teste.__simples9";

		List<Object> result;

		result = processador.processar(biblio, "main");
		assertEquals("[1000]", result.toString());
	}
}