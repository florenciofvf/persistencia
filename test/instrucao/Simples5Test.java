package instrucao;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class Simples5Test extends AbstratoTeste {
	public Simples5Test() {
		super("simples5");
	}

	@Test
	public void test() throws IOException, InstrucaoException {
		compilar();

		result = processador.processar(bibliotecaContexto.getNome(), "teste");
		assertEquals("[2]", result.toString());

		result = processador.processar(bibliotecaContexto.getNome(), "dividir", bi(30), bi(3));
		assertEquals("[0]", result.toString());
	}
}