package instrucao;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class Simples1Test extends AbstratoTeste {
	public Simples1Test() {
		super("simples1");
	}

	@Test
	public void test() throws IOException, InstrucaoException {
		compilar();

		result = processador.processar(bibliotecaContexto.getNome(), "getString");
		equals("[Olá Mundo!]", result.toString());

		result = processador.processar(bibliotecaContexto.getNome(), "pi");
		equals("[3.1415169]", result.toString());

		result = processador.processar(bibliotecaContexto.getNome(), "mil");
		equals("[1000]", result.toString());
	}
}