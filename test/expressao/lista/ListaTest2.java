package expressao.lista;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Compilacao;
import br.com.persist.plugins.expressao.processador.Processador;
import expressao.AbstratoTest;

public class ListaTest2 extends AbstratoTest {
	private static final String LISTA = "lista";

	@Test
	public void quicksort1() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(LISTA, "quicksort1"));

		processador = new Processador();

		String biblio = "br.com.teste.quicksort1";

		result = processador.processar(biblio, "testarQS");
		equals("[[-3, 0, 1, 2, 4, 6, 50]]", result.toString());
	}

	@Test
	public void quicksort2() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(LISTA, "quicksort2"));

		processador = new Processador();

		String biblio = "br.com.teste.quicksort2";

		result = processador.processar(biblio, "main");
		equals("[[-3, 0, 1, 2, 4, 6, 46, 50]]", result.toString());
	}

	@Test
	public void quicksort3() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(LISTA, "quicksort3"));

		processador = new Processador();

		String biblio = "br.com.teste.quicksort3";

		result = processador.processar(biblio, "main");
		equals("[[-3, 0, 1, 2, 4, 6, 46, 50]]", result.toString());
	}

	@Test
	public void quicksort4() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(LISTA, "quicksort4"));

		processador = new Processador();

		String biblio = "br.com.teste.quicksort4";

		result = processador.processar(biblio, "main");
		equals("[[-3, 0, 1, 2, 4, 6, 46, 50]]", result.toString());
	}
}