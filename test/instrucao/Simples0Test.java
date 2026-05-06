package instrucao;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class Simples0Test extends AbstratoTeste {
	public Simples0Test() {
		super("simples0");
	}

	@Test
	public void test() throws IOException, InstrucaoException {
		compilar();

		result = processador.processar(bibliotecaContexto.getNome(), "main1");
		equals("[-5]", result.toString());

		result = processador.processar(bibliotecaContexto.getNome(), "main2");
		equals("[2]", result.toString());
	}
}