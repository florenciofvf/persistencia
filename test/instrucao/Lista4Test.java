package instrucao;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class Lista4Test extends AbstratoTeste {
	public Lista4Test() {
		super("lista4");
	}

	@Test
	public void test() throws IOException, InstrucaoException {
		compilar();

		result = processador.processar(bibliotecaContexto.getNome(), "teste6");
		equals("[[][]]", result.toString());

		result = processador.processar(bibliotecaContexto.getNome(), "teste7");
		equals("[[]]", result.toString());
	}
}