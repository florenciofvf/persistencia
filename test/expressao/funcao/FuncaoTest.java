package expressao.funcao;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Compilacao;
import br.com.persist.plugins.expressao.processador.Processador;
import expressao.AbstratoTest;

public class FuncaoTest extends AbstratoTest {
	private static final String FUNCAO = "funcao";

	@Test
	public void teste1() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(FUNCAO, "funcao2"));
	}

	@Test
	public void teste2() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(FUNCAO, "funcao3"));

		processador = new Processador();
		biblio = "br.com.teste.funcao3";

		result = processador.processar(biblio, "main1");
		equals("[-5]", result.toString());

		result = processador.processar(biblio, "main2");
		equals("[2]", result.toString());
	}

	@Test
	public void teste3() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(FUNCAO, "funcao4"));

		processador = new Processador();
		biblio = "br.com.teste.funcao4";

		result = processador.processar(biblio, "main");
		equals("[10\n4]", result.toString());
	}

	@Test
	public void teste4() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(FUNCAO, "recursao"));

		processador = new Processador();
		biblio = "br.com.teste.recursao";

		result = processador.processar(biblio, "fatorial", bi(5));
		equals("[120]", result.toString());
	}

	@Test
	public void teste5() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(FUNCAO, "recursao_perform"));

		processador = new Processador();
		biblio = "br.com.teste.recursao_perform";

		result = processador.processar(biblio, "fatorial", bi(5));
		equals("[120]", result.toString());
	}

	@Test
	public void teste6() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(FUNCAO, "fibonacci"));

		processador = new Processador();
		biblio = "br.com.teste.fibonacci";

		result = processador.processar(biblio, "fibonacci_1", bi(8));
		equals("[21]", result.toString());

		/**
		 * result = processador.processar(biblio, "fibonacci_2", bi(9));
		 * assertEquals("[34]", result.toString());
		 */
	}
}