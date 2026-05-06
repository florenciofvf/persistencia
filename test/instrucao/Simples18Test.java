package instrucao;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class Simples18Test extends AbstratoTeste {
	public Simples18Test() {
		super("simples18");
	}

	@Test
	public void test() throws IOException, InstrucaoException {
		compilar();

		result = processador.processar(bibliotecaContexto.getNome(), "main");
		equals("[[2, 3, 4, 5, 6, 7]]", result.toString());
	}
}