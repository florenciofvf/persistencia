package instrucao;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class Simples3Test extends AbstratoTeste {
	public Simples3Test() {
		super("simples3");
	}

	@Test
	public void test() throws IOException, InstrucaoException {
		compilar();

		result = processador.processar(bibliotecaContexto.getNome(), "mesmo", bi(30));
		assertEquals("[30]", result.toString());

		result = processador.processar(bibliotecaContexto.getNome(), "mesmoNegado", bi(30));
		assertEquals("[-30]", result.toString());

		result = processador.processar(bibliotecaContexto.getNome(), "mesmoNegado2", bi(30));
		assertEquals("[-30]", result.toString());
	}
}