package instrucao;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class Lista1Test extends AbstratoTeste {
	public Lista1Test() {
		super("lista1");
	}

	@Test
	public void test() throws IOException, InstrucaoException {
		compilar();

		result = processador.processar(bibliotecaContexto.getNome(), "teste1");
		equals("[[]]", result.toString());
	}
}