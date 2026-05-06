package instrucao;

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
		equals("[ELSE]", result.toString());

		result = processador.processar(bibliotecaContexto.getNome(), "teste", bi(3), "A", "B");
		equals("[0]", result.toString());
	}
}