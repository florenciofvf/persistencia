package instrucao;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class Lambda2Test extends AbstratoTeste {
	public Lambda2Test() {
		super("lambda2");
	}

	@Test
	public void test() throws IOException, InstrucaoException {
		compilar();

		result = processador.processar(bibliotecaContexto.getNome(), "main");
		assertEquals(
				"[[\nPAI: Florêncio Vieira Filho\n FILHO -> Amanda Vieira Freire, \nPAI: Florêncio Vieira Filho\n FILHO -> Julia Vieira Freire]]",
				result.toString());
	}
}