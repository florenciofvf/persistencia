package br.com.persist.plugins.expressao.organiza;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Compilacao;

public class PacoteContextoTest {

	@Test
	public void teste1() throws IOException, ExpressaoException {
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(new File("pacote_teste"));
	}

}