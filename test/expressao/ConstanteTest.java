package expressao;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Compilacao;
import br.com.persist.plugins.expressao.processador.Processador;

public class ConstanteTest extends AbstratoTest {

	@Test
	public void teste16() throws IOException, ExpressaoException {
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(getFile("constante", "__simples16"));

		Processador processador = new Processador();

		String biblio = "br.com.teste.__simples16";

		List<Object> result;

		result = processador.processar(biblio, "main");
		assertEquals("[90.7]", result.toString());
	}

	@Test
	public void testeConstante() throws IOException, ExpressaoException {
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(getFile("constante", "__constante"));

		Processador processador = new Processador();

		String biblio = "br.com.teste.__constante";

		List<Object> result;

		result = processador.processar(biblio, "getIdade");
		assertEquals("[1050]", result.toString());
	}
}