package instrucao;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class Simples6Test extends AbstratoTeste {
	public Simples6Test() {
		super("simples6");
	}

	@Test
	public void test() throws IOException, InstrucaoException {
		compilar();

		result = processador.processar(bibliotecaContexto.getNome(), "expressao_01", bi(3), bi(4), bi(5));
		assertEquals("[23]", result.toString());

		result = processador.processar(bibliotecaContexto.getNome(), "expressao_02", bi(3), bi(4), bi(5));
		assertEquals("[35]", result.toString());

		result = processador.processar(bibliotecaContexto.getNome(), "expressao_03", bi(3), bi(4), bi(5));
		assertEquals("[-35]", result.toString());
	}
}