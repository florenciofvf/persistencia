package expressao;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Compilacao;
import br.com.persist.plugins.expressao.processador.Processador;

public class InternaTest extends AbstratoTest {

	@Test
	public void teste1() throws IOException, ExpressaoException {
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(getFile("internas", "teste1"));

		Processador processador = new Processador();

		String biblio = "br.com.teste.teste1";

		List<Object> result;

		result = processador.processar(biblio, "main");
		assertEquals("[4]", result.toString());
	}

}