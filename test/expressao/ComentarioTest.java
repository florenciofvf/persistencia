package expressao;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Compilacao;

public class ComentarioTest extends AbstratoTest {

	@Test
	public void teste1() throws IOException, ExpressaoException {
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(getFile("comentario", "comentario"));
	}

	@Test
	public void compilarAPIFiltro() throws IOException, ExpressaoException {
		File raiz = new File("expressoes");
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(new File(raiz, "_"));
	}

}