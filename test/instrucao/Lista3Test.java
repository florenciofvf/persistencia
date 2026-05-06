package instrucao;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class Lista3Test extends AbstratoTeste {
	public Lista3Test() {
		super("lista3");
	}

	@Test
	public void test() throws IOException, InstrucaoException {
		compilar();

		notNull(bibliotecaContexto);
	}
}