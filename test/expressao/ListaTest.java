package expressao;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.biblionativo.Lista;
import br.com.persist.plugins.expressao.compilador.Compilacao;
import br.com.persist.plugins.expressao.processador.Processador;

public class ListaTest extends AbstratoTest {
	private static final String LISTA = "lista";

	@Test
	public void teste0() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(LISTA, "__lista0"));
	}

	@Test
	public void teste1() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(LISTA, "__lista1"));

		processador = new Processador();

		String biblio = "br.com.teste.__lista1";

		result = processador.processar(biblio, "teste1");
		equals("[[]]", result.toString());
	}

	@Test
	public void teste3() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(LISTA, "__lista3"));
	}

	@Test
	public void teste5() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(LISTA, "__lista5"));
	}

	@Test
	public void listaAlgorit() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(LISTA, "listaAlgorit"));

		processador = new Processador();

		String biblio = "br.com.teste.listaAlgorit";

		result = processador.processar(biblio, "getMapa");
		equals("[{nome=Teste, valores=[1000, 30000, {valor=1.2}]}]", result.toString());
	}

	@Test
	public void lista() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(LISTA, "__lista"));

		processador = new Processador();

		String biblio = "br.com.teste.__lista";

		result = processador.processar(biblio, "main0");
		equals("[[Florêncio, Vieira, Filho]]", result.toString());

		result = processador.processar(biblio, "main");
		equals("[[Florêncio, Vieira, Filho]]", result.toString());

		result = processador.processar(biblio, "comprimento");
		equals("[3]", result.toString());

		result = processador.processar(biblio, "comprimentoRecursivo");
		equals("[3]", result.toString());

		result = processador.processar(biblio, "cabeca");
		equals("[Florêncio]", result.toString());

		result = processador.processar(biblio, "cauda");
		equals("[[Vieira, Filho]]", result.toString());

		result = processador.processar(biblio, "concatenar");
		equals("[[Florêncio, Vieira, Filho][Florêncio, Vieira, Filho]]", result.toString());

		result = processador.processar(biblio, "mainItemMaior", bi(5));
		equals("[[6, 7]]", result.toString());

		result = processador.processar(biblio, "inverterLista");
		equals("[[0, 1, 2, 3, 4, 5]]", result.toString());
	}

	@Test
	public void lista2() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(LISTA, "__lista2"));

		processador = new Processador();

		String biblio = "br.com.teste.__lista2";

		result = processador.processar(biblio, "teste2");
		equals("[[]]", result.toString());
	}

	@Test
	public void lista4() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(LISTA, "__lista4"));

		processador = new Processador();

		String biblio = "br.com.teste.__lista4";

		result = processador.processar(biblio, "teste5", new Lista(), "escola");
		equals("[[]escola]", result.toString());

		result = processador.processar(biblio, "teste6");
		equals("[[][]]", result.toString());

		result = processador.processar(biblio, "teste7");
		equals("[[]]", result.toString());
	}
}