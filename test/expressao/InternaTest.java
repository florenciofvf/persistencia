package expressao;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Compilacao;
import br.com.persist.plugins.expressao.processador.Processador;

public class InternaTest extends AbstratoTest {
	@Test
	public void teste1() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile("internas", "teste1"));

		processador = new Processador();

		String biblio = "br.com.teste.teste1";

		result = processador.processar(biblio, "main");
		equals("[4]", result.toString());

		result = processador.processar(biblio, "fatorial", bi(5));
		equals("[120]", result.toString());
	}
}