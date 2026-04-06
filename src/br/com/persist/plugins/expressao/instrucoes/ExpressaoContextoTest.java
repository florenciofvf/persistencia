package br.com.persist.plugins.expressao.instrucoes;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.ExpressaoTest;
import br.com.persist.plugins.expressao.compilador.Compilacao;

public class ExpressaoContextoTest extends ExpressaoTest {

	@Test
	public void teste1() throws IOException, ExpressaoException {
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(getFile("expressao", "expressao"));
	}

}