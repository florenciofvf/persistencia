package br.com.persist.plugins.expressao.retorno;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.ExpressaoTest;
import br.com.persist.plugins.expressao.compilador.Compilacao;

public class RetornoContextoTest extends ExpressaoTest {

	@Test
	public void teste1() throws IOException, ExpressaoException {
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(getFile("retorno", "retorno"));
	}

}