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
	public void teste14() throws IOException, ExpressaoException {
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(getFile("funcao", "recursao14"));

		Processador processador = new Processador();

		String biblio = "br.com.teste.recursao14";

		List<Object> result;

		result = processador.processar(biblio, "fatorial", bi(5));
		assertEquals("[120]", result.toString());
	}
}