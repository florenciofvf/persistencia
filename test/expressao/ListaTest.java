package expressao;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Compilacao;

public class ListaTest extends ExpressaoTest {
	private static final String LISTA = "lista";

	@Test
	public void teste0() throws IOException, ExpressaoException {
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(getFile(LISTA, "__lista0"));
	}
}