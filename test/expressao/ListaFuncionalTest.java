package expressao;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Compilacao;
import br.com.persist.plugins.expressao.processador.Processador;

public class ListaFuncionalTest extends AbstratoTest {
	private static final String LISTA = "lista";

	@Test
	public void teste1() throws IOException, ExpressaoException {
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(getFile(LISTA, "lista6"));

		Processador processador = new Processador();

		String biblio = "br.com.teste.lista6";

		List<Object> result;

		result = processador.processar(biblio, "main");
		assertEquals("[[7, 9]]", result.toString());
	}

}