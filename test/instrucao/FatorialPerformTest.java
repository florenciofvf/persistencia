package instrucao;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class FatorialPerformTest extends AbstratoTeste {
	public FatorialPerformTest() {
		super("fatorial_perform");
	}

	@Test
	public void test() throws IOException, InstrucaoException {
		compilar();

		result = processador.processar(bibliotecaContexto.getNome(), "fatorial", bi(5));
		equals("[120]", result.toString());
	}
}