package expressao.ordenacao;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Compilacao;
import br.com.persist.plugins.expressao.processador.Processador;
import expressao.AbstratoTest;

public class OrdenacaoTest extends AbstratoTest {
	private static final String ORDENACAO = "ordenacao";

	@Test
	public void quicksort1() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(ORDENACAO, "quicksort1"));

		processador = new Processador();
		biblio = "br.com.teste.quicksort1";

		result = processador.processar(biblio, "testarQS");
		equals("[[-3, 0, 1, 2, 4, 6, 50]]", result.toString());
	}

	@Test
	public void quicksort2() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(ORDENACAO, "quicksort2"));

		processador = new Processador();
		biblio = "br.com.teste.quicksort2";

		result = processador.processar(biblio, "main");
		equals("[[-3, 0, 1, 2, 4, 6, 46, 50, 55]]", result.toString());
	}

	@Test
	public void quicksort32() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(ORDENACAO, "quicksort3_2"));

		processador = new Processador();
		biblio = "br.com.teste.quicksort3_2";

		result = processador.processar(biblio, "main");
		equals("[[-3, 0, 1, 2, 4, 5, 6, 46, 50]]", result.toString());
	}

	@Test
	public void quicksort33() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(ORDENACAO, "quicksort3_3"));

		processador = new Processador();
		biblio = "br.com.teste.quicksort3_3";

		result = processador.processar(biblio, "main");
		equals("[[-3, 0, 1, 2, 4, 6, 46, 50]]", result.toString());
	}

	@Test
	public void quicksort4() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(ORDENACAO, "quicksort4"));

		processador = new Processador();
		biblio = "br.com.teste.quicksort4";

		result = processador.processar(biblio, "main");
		equals("[[-3, 0, 1, 2, 4, 6, 46, 50]]", result.toString());
	}

	@Test
	public void quicksort5() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(ORDENACAO, "quicksort5"));

		processador = new Processador();
		biblio = "br.com.teste.quicksort5";

		result = processador.processar(biblio, "main");
		equals("[[-9, -3, 0, 1, 2, 4, 6, 46, 50]]", result.toString());
	}

	@Test
	public void quicksort6() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(ORDENACAO, "quicksort6"));

		processador = new Processador();
		biblio = "br.com.teste.quicksort6";

		result = processador.processar(biblio, "main");
		equals("[[-90, -3, 0, 1, 2, 4, 6, 46, 50]]", result.toString());
	}

	@Test
	public void quicksort7() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(ORDENACAO, "quicksort7"));

		processador = new Processador();
		biblio = "br.com.teste.quicksort7";

		result = processador.processar(biblio, "main");
		equals("[[-3, 0, 1, 2, 4, 6, 46, 50, 55]]", result.toString());
	}
}