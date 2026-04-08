package instrucao;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class Simples2Test extends AbstratoTeste {
	public Simples2Test() {
		super("simples2");
	}

	@Test
	public void test() throws IOException, InstrucaoException {
		compilar();

		assertNotNull(bibliotecaContexto);
	}
}