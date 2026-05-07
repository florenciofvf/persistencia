package expressao.constante;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Compilacao;
import br.com.persist.plugins.expressao.processador.Processador;
import expressao.AbstratoTest;

public class ConstanteTest extends AbstratoTest {
	@Test
	public void teste1() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile("constante", "constante2"));

		processador = new Processador();
		biblio = "br.com.teste.constante2";

		result = processador.processar(biblio, "getIdade");
		equals("[1050]", result.toString());
	}

	@Test
	public void teste2() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile("constante", "constante3"));

		processador = new Processador();
		biblio = "br.com.teste.constante3";

		result = processador.processar(biblio, "main");
		equals("[90.7]", result.toString());
	}
}