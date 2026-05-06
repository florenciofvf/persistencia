package instrucao;

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
		equals("[[Florêncio, Vieira, Filho]]", result.toString());

		result = processador.processar(bibliotecaContexto.getNome(), "main");
		equals("[[Florêncio, Vieira, Filho]]", result.toString());

		result = processador.processar(bibliotecaContexto.getNome(), "comprimento");
		equals("[3]", result.toString());

		result = processador.processar(bibliotecaContexto.getNome(), "comprimentoRecursivo");
		equals("[3]", result.toString());

		result = processador.processar(bibliotecaContexto.getNome(), "cabeca");
		equals("[Florêncio]", result.toString());

		result = processador.processar(bibliotecaContexto.getNome(), "cauda");
		equals("[[Vieira, Filho]]", result.toString());

		result = processador.processar(bibliotecaContexto.getNome(), "concatenar");
		equals("[[Florêncio, Vieira, Filho][Florêncio, Vieira, Filho]]", result.toString());

		result = processador.processar(bibliotecaContexto.getNome(), "mainItemMaior", bi(5));
		equals("[[6, 7]]", result.toString());

		result = processador.processar(bibliotecaContexto.getNome(), "inverterLista");
		equals("[[0, 1, 2, 3, 4, 5]]", result.toString());
	}
}