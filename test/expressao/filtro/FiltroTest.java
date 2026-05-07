package expressao.filtro;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Compilacao;
import br.com.persist.plugins.expressao.processador.Processador;
import expressao.AbstratoTest;

public class FiltroTest extends AbstratoTest {
	private static final String FILTRO = "filtro";

	@Test
	public void teste1() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(FILTRO, "lamb_filtro"));

		processador = new Processador();
		biblio = "br.com.teste.lamb_filtro";

		result = processador.processar(biblio, "main");
		equals("[[1, 2, 3]]", result.toString());
	}

	@Test
	public void teste2() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(FILTRO, "lamb_filtro2"));

		processador = new Processador();
		biblio = "br.com.teste.lamb_filtro2";

		result = processador.processar(biblio, "main", bi(6));
		equals("[[50, 6]]", result.toString());
	}

	@Test
	public void teste3() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(FILTRO, "lista_filtro"));

		processador = new Processador();
		biblio = "br.com.teste.lista_filtro";

		result = processador.processar(biblio, "main");
		equals("[[2, 4, 50, 6]]", result.toString());
	}

	@Test
	public void teste4() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(FILTRO, "lista_filtro2"));

		processador = new Processador();
		biblio = "br.com.teste.lista_filtro2";

		result = processador.processar(biblio, "main");
		equals("[[2, 4, 50, 6]]", result.toString());
	}
}