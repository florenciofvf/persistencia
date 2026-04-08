package instrucao;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class Lista2Test extends AbstratoTeste {
	public Lista2Test() {
		super("lista2");
	}

	@Test
	public void test() throws IOException, InstrucaoException {
		compilar();

		result = processador.processar(bibliotecaContexto.getNome(), "teste2");
		assertEquals("[[]]", result.toString());
	}
}