package instrucao;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class QuickSort2Test extends AbstratoTeste {
	public QuickSort2Test() {
		super("quicksort2");
	}

	@Test
	public void test() throws IOException, InstrucaoException {
		compilar();

		result = processador.processar(bibliotecaContexto.getNome(), "main");
		assertEquals("[[-3, 0, 1, 2, 4, 6, 46, 50]]", result.toString());
	}
}