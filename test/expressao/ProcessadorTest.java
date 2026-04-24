package expressao;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Compilacao;
import br.com.persist.plugins.expressao.processador.Processador;

public class ProcessadorTest extends AbstratoTest {

	@Test
	public void teste1() throws IOException, ExpressaoException {
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(getFile("processador", "processador"));

		Processador processador = new Processador();
		List<Object> result = processador.processar("br.com.teste.processador", "get");
		assertEquals("[Hello, World!]", result.toString());
	}

}