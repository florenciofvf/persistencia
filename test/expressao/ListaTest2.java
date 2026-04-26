package expressao;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Compilacao;
import br.com.persist.plugins.expressao.processador.Processador;

public class ListaTest2 extends AbstratoTest {
	private static final String LISTA = "lista";

	@Test
	public void quicksort1() throws IOException, ExpressaoException {
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(getFile(LISTA, "quicksort1"));

		Processador processador = new Processador();

		String biblio = "br.com.teste.quicksort1";

		List<Object> result;

		result = processador.processar(biblio, "testarQS");
		assertEquals("[[-3, 0, 1, 2, 4, 6, 50]]", result.toString());
	}

	@Test
	public void quicksort2() throws IOException, ExpressaoException {
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(getFile(LISTA, "quicksort2"));

		Processador processador = new Processador();

		String biblio = "br.com.teste.quicksort2";

		List<Object> result;

		result = processador.processar(biblio, "main");
		assertEquals("[[-3, 0, 1, 2, 4, 6, 46, 50]]", result.toString());
	}

	@Test
	public void quicksort3() throws IOException, ExpressaoException {
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(getFile(LISTA, "quicksort3"));

		Processador processador = new Processador();

		String biblio = "br.com.teste.quicksort3";

		List<Object> result;

		result = processador.processar(biblio, "main");
		assertEquals("[[-3, 0, 1, 2, 4, 6, 46, 50]]", result.toString());
	}

	@Test
	public void quicksort4() throws IOException, ExpressaoException {
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(getFile(LISTA, "quicksort4"));

		Processador processador = new Processador();

		String biblio = "br.com.teste.quicksort2";

		List<Object> result;

		result = processador.processar(biblio, "main");
		assertEquals("[[-3, 0, 1, 2, 4, 6, 46, 50]]", result.toString());
	}
}