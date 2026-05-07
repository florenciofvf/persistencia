package expressao.loop;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Compilacao;
import br.com.persist.plugins.expressao.processador.Processador;
import expressao.AbstratoTest;

public class WhileTest extends AbstratoTest {
	@Test
	public void teste1() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile("loop", "loop2"));

		processador = new Processador();
		biblio = "br.com.teste.loop2";

		result = processador.processar(biblio, "main");
		equals("[5050]", result.toString());
	}

	@Test
	public void teste2() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile("loop", "loop3"));

		processador = new Processador();
		biblio = "br.com.teste.loop3";

		result = processador.processar(biblio, "main");
		equals("[-12345]", result.toString());
	}
}