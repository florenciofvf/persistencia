package expressao;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.biblionativo.Lista;
import br.com.persist.plugins.expressao.compilador.Compilacao;
import br.com.persist.plugins.expressao.processador.Processador;

public class ListaTest extends AbstratoTest {
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

	@Test
	public void listaAlgorit() throws IOException, ExpressaoException {
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(getFile(LISTA, "listaAlgorit"));

		Processador processador = new Processador();

		String biblio = "br.com.teste.listaAlgorit";

		List<Object> result;

		result = processador.processar(biblio, "getMapa");
		log(result);
		// assertEquals("[[]]", result.toString());
	}

	@Test
	public void lista() throws IOException, ExpressaoException {
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(getFile(LISTA, "__lista"));

		Processador processador = new Processador();

		String biblio = "br.com.teste.__lista";

		List<Object> result;

		result = processador.processar(biblio, "main0");
		assertEquals("[[Florêncio, Vieira, Filho]]", result.toString());

		result = processador.processar(biblio, "main");
		assertEquals("[[Florêncio, Vieira, Filho]]", result.toString());

		result = processador.processar(biblio, "comprimento");
		assertEquals("[3]", result.toString());

		result = processador.processar(biblio, "comprimentoRecursivo");
		assertEquals("[3]", result.toString());

		result = processador.processar(biblio, "cabeca");
		assertEquals("[Florêncio]", result.toString());

		result = processador.processar(biblio, "cauda");
		assertEquals("[[Vieira, Filho]]", result.toString());

		result = processador.processar(biblio, "concatenar");
		assertEquals("[[Florêncio, Vieira, Filho][Florêncio, Vieira, Filho]]", result.toString());

		result = processador.processar(biblio, "mainItemMaior", bi(5));
		assertEquals("[[6, 7]]", result.toString());

		result = processador.processar(biblio, "inverterLista");
		assertEquals("[[0, 1, 2, 3, 4, 5]]", result.toString());
	}

	@Test
	public void lista2() throws IOException, ExpressaoException {
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(getFile(LISTA, "__lista2"));

		Processador processador = new Processador();

		String biblio = "br.com.teste.__lista2";

		List<Object> result;

		result = processador.processar(biblio, "teste2");
		assertEquals("[[]]", result.toString());
	}

	@Test
	public void lista4() throws IOException, ExpressaoException {
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(getFile(LISTA, "__lista4"));

		Processador processador = new Processador();

		String biblio = "br.com.teste.__lista4";

		List<Object> result;

		result = processador.processar(biblio, "teste5", new Lista(), "escola");
		assertEquals("[[]escola]", result.toString());

		result = processador.processar(biblio, "teste6");
		assertEquals("[[][]]", result.toString());

		result = processador.processar(biblio, "teste7");
		assertEquals("[[]]", result.toString());
	}
}