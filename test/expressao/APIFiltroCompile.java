package expressao;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Compilacao;

public class APIFiltroCompile extends AbstratoTest {
	@Test
	public void compilarAPIFiltro() throws IOException, ExpressaoException {
		File raiz = new File("expressoes");
		File filtro = new File(raiz, "filtro");
		compilacao = new Compilacao();
		compilacao.compilar(new File(filtro, "_"));
	}
}