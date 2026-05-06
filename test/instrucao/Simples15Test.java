package instrucao;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class Simples15Test extends AbstratoTeste {
	public Simples15Test() {
		super("simples15");
	}

	@Test
	public void test() throws IOException, InstrucaoException {
		compilar();

		result = processador.processar(bibliotecaContexto.getNome(), "main");
		equals("[7]", result.toString());
	}
}