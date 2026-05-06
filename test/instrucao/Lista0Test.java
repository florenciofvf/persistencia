package instrucao;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class Lista0Test extends AbstratoTeste {
	public Lista0Test() {
		super("lista0");
	}

	@Test
	public void test() throws IOException, InstrucaoException {
		compilar();

		notNull(bibliotecaContexto);
	}
}