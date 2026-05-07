package expressao.cond;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Compilacao;
import br.com.persist.plugins.expressao.processador.Processador;
import expressao.AbstratoTest;

public class IFTest extends AbstratoTest {
	private static final String CONDICIONAL = "condicional";

	@Test
	public void teste1() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(CONDICIONAL, "cond2"));

		processador = new Processador();
		String biblio = "br.com.teste.cond2";

		result = processador.processar(biblio, "se", bi(3), "IF", "ELSE");
		equals("[ELSE]", result.toString());

		result = processador.processar(biblio, "teste", bi(3), "A", "B");
		equals("[0]", result.toString());
	}

	@Test
	public void teste2() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(CONDICIONAL, "cond3"));

		processador = new Processador();
		String biblio = "br.com.teste.cond3";

		result = processador.processar(biblio, "main", bi(1));
		equals("[1]", result.toString());

		result = processador.processar(biblio, "main", bi(0));
		equals("[100]", result.toString());
	}

	@Test
	public void teste3() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(CONDICIONAL, "cond4"));

		processador = new Processador();
		String biblio = "br.com.teste.cond4";

		String nomeFuncao = "diaDaSemana";

		result = processador.processar(biblio, nomeFuncao, bi(1));
		equals("[FINAL DE SEMANA]", result.toString());

		result = processador.processar(biblio, nomeFuncao, bi(-1));
		equals("[DIA INVÁLIDO]", result.toString());

		result = processador.processar(biblio, nomeFuncao, bi(6));
		equals("[Meio da Semana]", result.toString());

		result = processador.processar(biblio, nomeFuncao, bi(7));
		equals("[FINAL DE SEMANA]", result.toString());

		result = processador.processar(biblio, nomeFuncao, bi(8));
		equals("[DIA INVÁLIDO]", result.toString());
	}
}