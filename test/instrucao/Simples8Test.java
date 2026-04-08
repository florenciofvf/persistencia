package instrucao;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class Simples8Test extends AbstratoTeste {
	public Simples8Test() {
		super("simples8");
	}

	@Test
	public void test() throws IOException, InstrucaoException {
		compilar();

		result = processador.processar(bibliotecaContexto.getNome(), "igual", bi(3), bi(3));
		assertEquals("[1]", result.toString());

		result = processador.processar(bibliotecaContexto.getNome(), "diff", bi(3), bi(3));
		assertEquals("[0]", result.toString());
	}
}