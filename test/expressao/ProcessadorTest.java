package expressao;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Compilacao;
import br.com.persist.plugins.expressao.processador.Processador;

public class ProcessadorTest extends AbstratoTest {
	@Test
	public void teste1() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile("processador", "processador"));

		processador = new Processador();
		List<Object> result = processador.processar("br.com.teste.processador", "get");
		equals("[Hello, World!]", result.toString());
	}
}