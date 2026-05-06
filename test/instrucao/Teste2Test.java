package instrucao;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class Teste2Test extends AbstratoTeste {
	public Teste2Test() {
		super("teste2");
	}

	@Test
	public void test() throws IOException, InstrucaoException {
		compilar();

		notNull(bibliotecaContexto);
	}
}