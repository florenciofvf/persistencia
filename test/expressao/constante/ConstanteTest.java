package expressao.constante;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Compilacao;
import br.com.persist.plugins.expressao.processador.Processador;
import expressao.AbstratoTest;

public class ConstanteTest extends AbstratoTest {
	@Test
	public void teste16() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile("constante", "__simples16"));

		processador = new Processador();

		String biblio = "br.com.teste.__simples16";

		result = processador.processar(biblio, "main");
		equals("[90.7]", result.toString());
	}

	@Test
	public void testeConstante() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile("constante", "__constante"));

		processador = new Processador();

		String biblio = "br.com.teste.__constante";

		result = processador.processar(biblio, "getIdade");
		equals("[1050]", result.toString());
	}
}