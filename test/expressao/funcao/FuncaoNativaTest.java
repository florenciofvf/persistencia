package expressao.funcao;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Compilacao;
import br.com.persist.plugins.expressao.processador.Processador;
import expressao.AbstratoTest;

public class FuncaoNativaTest extends AbstratoTest {
	@Test
	public void teste1() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile("funcao_nativa", "__simples2"));

		processador = new Processador();

		String biblio = "br.com.teste.__simples2";

		result = processador.processar(biblio, "somar", bi(2), bi(2));
		equals("[4JAVA]", result.toString());
	}
}