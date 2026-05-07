package expressao.lambda;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Compilacao;
import br.com.persist.plugins.expressao.processador.Processador;
import expressao.AbstratoTest;

public class LambdaTest extends AbstratoTest {
	private static final String LAMBDA = "lambda";

	@Test
	public void lambda0() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(LAMBDA, "lambda0"));

		processador = new Processador();

		String biblio = "br.com.teste.lambda0";

		result = processador.processar(biblio, "main");
		equals("[[impar-1, PAR-2, impar-3, PAR-4, impar-5]]", result.toString());
	}

	@Test
	public void lambda1() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(LAMBDA, "lambda1"));

		processador = new Processador();

		String biblio = "br.com.teste.lambda1";

		result = processador.processar(biblio, "main");
		equals("[[impar -> 1, par -> 2, impar -> 3, par -> 4, impar -> 5]]", result.toString());
	}

	@Test
	public void lambda2() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(LAMBDA, "lambda2"));

		processador = new Processador();

		String biblio = "br.com.teste.lambda2";

		result = processador.processar(biblio, "main");
		equals("[[\nPAI: Florêncio Vieira Filho\n FILHO -> Amanda Vieira Freire, \nPAI: Florêncio Vieira Filho\n FILHO -> Julia Vieira Freire]]",
				result.toString());
	}

	@Test
	public void lambda3() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(LAMBDA, "lambda3"));

		processador = new Processador();

		String biblio = "br.com.teste.lambda3";

		result = processador.processar(biblio, "main");
		equals("[3]", result.toString());
	}
}