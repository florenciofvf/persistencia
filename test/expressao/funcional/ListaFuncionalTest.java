package expressao.funcional;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Compilacao;
import br.com.persist.plugins.expressao.processador.Processador;
import expressao.AbstratoTest;

public class ListaFuncionalTest extends AbstratoTest {
	private static final String LISTA = "lista";

	@Test
	public void teste1() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(LISTA, "lista6"));

		processador = new Processador();

		String biblio = "br.com.teste.lista6";

		result = processador.processar(biblio, "main");
		equals("[[7, 9]]", result.toString());
	}
}