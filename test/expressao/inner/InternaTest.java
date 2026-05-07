package expressao.inner;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Compilacao;
import br.com.persist.plugins.expressao.processador.Processador;
import expressao.AbstratoTest;

public class InternaTest extends AbstratoTest {
	@Test
	public void teste1() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile("internas", "inner"));

		processador = new Processador();
		biblio = "br.com.teste.inner";

		result = processador.processar(biblio, "main");
		equals("[4]", result.toString());
	}

	@Test
	public void teste2() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile("internas", "inner2"));

		processador = new Processador();
		biblio = "br.com.teste.inner2";

		result = processador.processar(biblio, "fatorial", bi(5));
		equals("[120]", result.toString());
	}
}