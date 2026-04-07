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
	public void test() throws IOException, ExpressaoException {
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
	public void teste2() throws IOException, ExpressaoException {
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
}