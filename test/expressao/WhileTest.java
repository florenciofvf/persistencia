package expressao;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Compilacao;
import br.com.persist.plugins.expressao.processador.Processador;

public class WhileTest extends ExpressaoTest {

	@Test
	public void teste11() throws IOException, ExpressaoException {
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(getFile("loop", "__simples11"));

		Processador processador = new Processador();

		String biblio = "br.com.teste.__simples11";

		List<Object> result;

		result = processador.processar(biblio, "main");
		assertEquals("[5050]", result.toString());
	}

	@Test
	public void teste13() throws IOException, ExpressaoException {
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(getFile("loop", "__simples13"));

		Processador processador = new Processador();

		String biblio = "br.com.teste.__simples13";

		List<Object> result;

		result = processador.processar(biblio, "main");
		assertEquals("[-12345]", result.toString());
	}
}