package instrucao;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class Simples12Test extends AbstratoTeste {
	public Simples12Test() {
		super("simples12");
	}

	@Test
	public void test() throws IOException, InstrucaoException {
		compilar();

		result = processador.processar(bibliotecaContexto.getNome(), "main", bi(1));
		assertEquals("[1]", result.toString());

		result = processador.processar(bibliotecaContexto.getNome(), "main", bi(0));
		assertEquals("[100]", result.toString());
	}
}