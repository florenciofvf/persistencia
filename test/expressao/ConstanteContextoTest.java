package expressao;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Compilacao;

public class ConstanteContextoTest extends AbstratoTest {

	@Test
	public void teste1() throws IOException, ExpressaoException {
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(getFile("constante", "constante"));
	}

}