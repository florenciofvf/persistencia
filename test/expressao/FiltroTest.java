package expressao;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Compilacao;
import br.com.persist.plugins.expressao.processador.Processador;

public class FiltroTest extends AbstratoTest {
	private static final String FILTRO = "filtro";

	@Test
	public void teste1() throws IOException, ExpressaoException {
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(getFile(FILTRO, "lista_filtro"));

		Processador processador = new Processador();

		String biblio = "br.com.teste.lista_filtro";

		List<Object> result;

		result = processador.processar(biblio, "main");
		assertEquals("[[2, 4, 50, 6]]", result.toString());
	}

}