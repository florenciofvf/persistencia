package expressao.loop;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Compilacao;
import br.com.persist.plugins.expressao.processador.Processador;
import expressao.AbstratoTest;

public class WhileTest extends AbstratoTest {
	@Test
	public void teste11() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile("loop", "__simples11"));

		processador = new Processador();

		String biblio = "br.com.teste.__simples11";

		result = processador.processar(biblio, "main");
		equals("[5050]", result.toString());
	}

	@Test
	public void teste13() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile("loop", "__simples13"));

		processador = new Processador();

		String biblio = "br.com.teste.__simples13";

		result = processador.processar(biblio, "main");
		equals("[-12345]", result.toString());
	}
}