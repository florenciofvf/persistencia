package expressao.ordenacao;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Compilacao;
import br.com.persist.plugins.expressao.processador.Processador;
import expressao.AbstratoTest;

public class OutrosTest extends AbstratoTest {
	private static final String ORDENACAO = "ordenacao";

	@Test
	public void insercao() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(ORDENACAO, "insercao"));

		processador = new Processador();
		biblio = "br.com.teste.insercao";

		result = processador.processar(biblio, "main");
		equals("[[3, 4, 5, 23, 67]]", result.toString());
	}

	@Test
	public void mergesort() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(ORDENACAO, "mergesort"));

		processador = new Processador();
		biblio = "br.com.teste.mergesort";

		result = processador.processar(biblio, "main");
		equals("[[1, 2, 3, 4]]", result.toString());
	}

	@Test
	public void selecao() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(ORDENACAO, "selecao"));

		processador = new Processador();
		biblio = "br.com.teste.selecao";

		result = processador.processar(biblio, "main");
		equals("[[-3, 1, 7, 9]]", result.toString());
	}
}