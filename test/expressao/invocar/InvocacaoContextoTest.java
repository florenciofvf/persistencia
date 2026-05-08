package expressao.invocar;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Compilacao;
import br.com.persist.plugins.expressao.processador.Processador;
import expressao.AbstratoTest;

public class InvocacaoContextoTest extends AbstratoTest {
	private static final String INVOCACAO = "invocacao";

	@Test
	public void teste1() throws IOException, ExpressaoException {
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(getFile(INVOCACAO, INVOCACAO));
		compilacao.compilar(getFile(INVOCACAO, "invocacao2"));

		processador = new Processador();
		biblio = "br.com.teste.invocacao.invocacao2";

		result = processador.processar(biblio, "concatenar");
		equals("[[]Exemplo-001]", result.toString());
	}
}