package expressao;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Compilacao;
import br.com.persist.plugins.expressao.processador.Processador;

public class FuncaoTest extends AbstratoTest {
	@Test
	public void teste2() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile("funcao", "__teste2"));
	}

	@Test
	public void teste0() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile("funcao", "__simples0"));

		processador = new Processador();

		String biblio = "br.com.teste.__simples0";

		result = processador.processar(biblio, "main1");
		equals("[-5]", result.toString());

		result = processador.processar(biblio, "main2");
		equals("[2]", result.toString());
	}

	@Test
	public void teste14() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile("funcao", "recursao14"));

		processador = new Processador();

		String biblio = "br.com.teste.recursao14";

		result = processador.processar(biblio, "fatorial", bi(5));
		equals("[120]", result.toString());
	}

	@Test
	public void recursaoPerform() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile("funcao", "recursao_perform"));

		processador = new Processador();

		String biblio = "br.com.teste.recursao_perform";

		result = processador.processar(biblio, "fatorial", bi(5));
		equals("[120]", result.toString());
	}

	@Test
	public void fibonacci() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile("funcao", "__fibonacci"));

		processador = new Processador();

		String biblio = "br.com.teste.__fibonacci";

		result = processador.processar(biblio, "fibonacci_1", bi(8));
		equals("[21]", result.toString());

		// result = processador.processar(biblio, "fibonacci_2", bi(9));
		// assertEquals("[34]", result.toString());
	}

	@Test
	public void funcoes1() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile("funcao", "funcoes1"));

		processador = new Processador();

		String biblio = "br.com.teste.funcoes1";

		result = processador.processar(biblio, "main");
		equals("[10\n4]", result.toString());
	}
}