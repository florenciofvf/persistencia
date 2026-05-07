package expressao.funcional;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Compilacao;
import br.com.persist.plugins.expressao.processador.Processador;
import expressao.AbstratoTest;

public class FuncionalTest extends AbstratoTest {

	@Test
	public void teste0() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile("funcional", "simples15"));

		processador = new Processador();

		String biblio = "br.com.teste.simples15";

		result = processador.processar(biblio, "main");
		equals("[7]", result.toString());
	}

	@Test
	public void teste3() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile("funcional", "teste3"));

		processador = new Processador();

		String biblio = "br.com.teste.teste3";

		result = processador.processar(biblio, "testar");
		equals("[Java2]", result.toString());
	}

	@Test
	public void funcional() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile("funcional", "funcional"));

		processador = new Processador();

		String biblio = "br.com.teste.funcional";

		result = processador.processar(biblio, "main");
		equals("[Minha Função]", result.toString());

		result = processador.processar(biblio, "main2");
		equals("[minhaFuncao()]", result.toString());
	}
}