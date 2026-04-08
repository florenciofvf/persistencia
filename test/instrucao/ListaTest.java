package instrucao;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class ListaTest extends AbstratoTeste {
	public ListaTest() {
		super("lista");
	}

	@Test
	public void test() throws IOException, InstrucaoException {
		compilar();

		result = processador.processar(bibliotecaContexto.getNome(), "main0");
		assertEquals("[[Florêncio, Vieira, Filho]]", result.toString());

		result = processador.processar(bibliotecaContexto.getNome(), "main");
		assertEquals("[[Florêncio, Vieira, Filho]]", result.toString());

		result = processador.processar(bibliotecaContexto.getNome(), "comprimento");
		assertEquals("[3]", result.toString());

		result = processador.processar(bibliotecaContexto.getNome(), "comprimentoRecursivo");
		assertEquals("[3]", result.toString());

		result = processador.processar(bibliotecaContexto.getNome(), "cabeca");
		assertEquals("[Florêncio]", result.toString());

		result = processador.processar(bibliotecaContexto.getNome(), "cauda");
		assertEquals("[[Vieira, Filho]]", result.toString());

		result = processador.processar(bibliotecaContexto.getNome(), "concatenar");
		assertEquals("[[Florêncio, Vieira, Filho][Florêncio, Vieira, Filho]]", result.toString());

		result = processador.processar(bibliotecaContexto.getNome(), "mainItemMaior", bi(5));
		assertEquals("[[6, 7]]", result.toString());

		result = processador.processar(bibliotecaContexto.getNome(), "inverterLista");
		assertEquals("[[0, 1, 2, 3, 4, 5]]", result.toString());
	}
}