package instrucao;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class ConstanteTest extends AbstratoTeste {
	public ConstanteTest() {
		super("constante");
	}

	@Test
	public void test() throws IOException, InstrucaoException {
		compilar();

		result = processador.processar(bibliotecaContexto.getNome(), "getIdade");
		assertEquals("[1050]", result.toString());
	}
}