package expressao;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Compilacao;
import br.com.persist.plugins.expressao.processador.Processador;

public class FuncaoNativaTest extends AbstratoTest {

	@Test
	public void teste1() throws IOException, ExpressaoException {
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(getFile("funcao_nativa", "__simples2"));

		Processador processador = new Processador();

		String biblio = "br.com.teste.__simples2";

		List<Object> result;

		result = processador.processar(biblio, "somar", bi(2), bi(2));
		assertEquals("[4JAVA]", result.toString());
	}
}