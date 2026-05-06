package instrucao;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class QuickSort1Test extends AbstratoTeste {
	public QuickSort1Test() {
		super("quicksort1");
	}

	@Test
	public void test() throws IOException, InstrucaoException {
		compilar();

		result = processador.processar(bibliotecaContexto.getNome(), "testarQS");
		equals("[[-3, 0, 1, 2, 4, 6, 50]]", result.toString());
	}
}