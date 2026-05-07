package expressao.funcional;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Compilacao;
import br.com.persist.plugins.expressao.processador.Processador;
import expressao.AbstratoTest;

public class FuncionalTest extends AbstratoTest {
	private static final String FUNCIONAL = "funcional";

	@Test
	public void teste1() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(FUNCIONAL, FUNCIONAL));

		processador = new Processador();
		biblio = "br.com.teste.funcional";

		result = processador.processar(biblio, "main");
		equals("[Minha Função]", result.toString());

		result = processador.processar(biblio, "main2");
		equals("[minhaFuncao()]", result.toString());
	}

	@Test
	public void teste2() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(FUNCIONAL, "funcional2"));

		processador = new Processador();
		biblio = "br.com.teste.funcional2";

		result = processador.processar(biblio, "testar");
		equals("[Java2]", result.toString());
	}

	@Test
	public void teste3() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(FUNCIONAL, "funcional3"));

		processador = new Processador();
		biblio = "br.com.teste.funcional3";

		result = processador.processar(biblio, "main");
		equals("[7]", result.toString());
	}
}