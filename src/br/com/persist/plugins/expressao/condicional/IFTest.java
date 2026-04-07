package br.com.persist.plugins.expressao.condicional;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.ExpressaoTest;
import br.com.persist.plugins.expressao.compilador.Compilacao;
import br.com.persist.plugins.expressao.processador.Processador;

public class IFTest extends ExpressaoTest {

	@Test
	public void teste10() throws IOException, ExpressaoException {
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(getFile("condicional", "__simples10"));

		Processador processador = new Processador();

		String biblio = "br.com.teste.__simples10";

		List<Object> result;

		result = processador.processar(biblio, "se", bi(3), "IF", "ELSE");
		assertEquals("[ELSE]", result.toString());

		result = processador.processar(biblio, "teste", bi(3), "A", "B");
		assertEquals("[0]", result.toString());
	}

	@Test
	public void teste12() throws IOException, ExpressaoException {
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(getFile("condicional", "__simples12"));

		Processador processador = new Processador();

		String biblio = "br.com.teste.__simples12";

		List<Object> result;

		result = processador.processar(biblio, "main", bi(1));
		assertEquals("[1]", result.toString());

		result = processador.processar(biblio, "main", bi(0));
		assertEquals("[100]", result.toString());
	}

	@Test
	public void teste17() throws IOException, ExpressaoException {
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(getFile("condicional", "__simples17"));

		Processador processador = new Processador();

		String biblio = "br.com.teste.__simples17";

		List<Object> result;

		String nomeFuncao = "diaDaSemana";

		result = processador.processar(biblio, nomeFuncao, bi(1));
		assertEquals("[FINAL DE SEMANA]", result.toString());

		result = processador.processar(biblio, nomeFuncao, bi(-1));
		assertEquals("[DIA INVÁLIDO]", result.toString());

		result = processador.processar(biblio, nomeFuncao, bi(6));
		assertEquals("[Meio da Semana]", result.toString());

		result = processador.processar(biblio, nomeFuncao, bi(7));
		assertEquals("[FINAL DE SEMANA]", result.toString());

		result = processador.processar(biblio, nomeFuncao, bi(8));
		assertEquals("[DIA INVÁLIDO]", result.toString());
	}
}