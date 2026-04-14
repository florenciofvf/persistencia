package expressao;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Compilacao;
import br.com.persist.plugins.expressao.processador.Processador;

public class ListaTest extends ExpressaoTest {
	private static final String LISTA = "lista";

	@Test
	public void teste0() throws IOException, ExpressaoException {
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(getFile(LISTA, "__lista0"));
	}

	@Test
	public void teste1() throws IOException, ExpressaoException {
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(getFile(LISTA, "__lista1"));

		Processador processador = new Processador();

		String biblio = "br.com.teste.__lista1";

		List<Object> result;

		result = processador.processar(biblio, "teste1");
		assertEquals("[[]]", result.toString());
	}

	@Test
	public void teste3() throws IOException, ExpressaoException {
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(getFile(LISTA, "__lista3"));
	}

	@Test
	public void teste5() throws IOException, ExpressaoException {
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(getFile(LISTA, "__lista5"));
	}
}