package instrucao;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class Simples10Test extends AbstratoTeste {
	public Simples10Test() {
		super("simples10");
	}

	@Test
	public void test() throws IOException, InstrucaoException {
		compilar();

		result = processador.processar(bibliotecaContexto.getNome(), "se", bi(3), "IF", "ELSE");
		assertEquals("[ELSE]", result.toString());

		result = processador.processar(bibliotecaContexto.getNome(), "teste", bi(3), "A", "B");
		assertEquals("[0]", result.toString());
	}
}