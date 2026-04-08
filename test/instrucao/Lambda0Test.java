package instrucao;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class Lambda0Test extends AbstratoTeste {
	public Lambda0Test() {
		super("lambda0");
	}

	@Test
	public void test() throws IOException, InstrucaoException {
		compilar();

		result = processador.processar(bibliotecaContexto.getNome(), "main");
		assertEquals("[[impar-1, PAR-2, impar-3, PAR-4, impar-5]]", result.toString());
	}
}