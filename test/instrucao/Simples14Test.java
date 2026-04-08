package instrucao;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class Simples14Test extends AbstratoTeste {
	public Simples14Test() {
		super("simples14");
	}

	@Test
	public void test() throws IOException, InstrucaoException {
		compilar();

		result = processador.processar(bibliotecaContexto.getNome(), "fatorial", bi(5));
		assertEquals("[120]", result.toString());
	}
}