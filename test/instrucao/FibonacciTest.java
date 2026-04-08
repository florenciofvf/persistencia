package instrucao;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class FibonacciTest extends AbstratoTeste {
	public FibonacciTest() {
		super("fibonacci");
	}

	@Test
	public void test() throws IOException, InstrucaoException {
		compilar();

		result = processador.processar(bibliotecaContexto.getNome(), "fibonacci_1", bi(8));
		assertEquals("[21]", result.toString());

		result = processador.processar(bibliotecaContexto.getNome(), "fibonacci_2", bi(9));
		assertEquals("[34]", result.toString());
	}
}