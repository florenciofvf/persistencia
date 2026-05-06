package instrucao;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class Lambda1Test extends AbstratoTeste {
	public Lambda1Test() {
		super("lambda1");
	}

	@Test
	public void test() throws IOException, InstrucaoException {
		compilar();

		result = processador.processar(bibliotecaContexto.getNome(), "main");
		equals("[[impar -> 1, par -> 2, impar -> 3, par -> 4, impar -> 5]]", result.toString());
	}
}