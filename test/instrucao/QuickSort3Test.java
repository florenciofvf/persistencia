package instrucao;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class QuickSort3Test extends AbstratoTeste {
	public QuickSort3Test() {
		super("quicksort3");
	}

	@Test
	public void test() throws IOException, InstrucaoException {
		compilar();

		result = processador.processar(bibliotecaContexto.getNome(), "main");
		equals("[[-3, 0, 1, 2, 4, 6, 46, 50]]", result.toString());
	}
}