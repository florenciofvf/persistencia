package expressao;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Compilacao;
import br.com.persist.plugins.expressao.processador.Processador;

public class RetornoInstrucaoTest extends ExpressaoTest {

	@Test
	public void teste1() throws IOException, ExpressaoException {
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(getFile("retorno", "__simples1"));

		Processador processador = new Processador();

		String biblio = "br.com.teste.__simples1";

		List<Object> result;

		result = processador.processar(biblio, "getString");
		assertEquals("[Olá Mundo!]", result.toString());

		result = processador.processar(biblio, "pi");
		assertEquals("[3.1415169]", result.toString());

		result = processador.processar(biblio, "mil");
		assertEquals("[1000]", result.toString());

		result = processador.processar(biblio, "somar", bi(2), bi(2));
		assertEquals("[4]", result.toString());
	}
}