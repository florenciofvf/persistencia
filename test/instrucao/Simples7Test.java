package instrucao;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class Simples7Test extends AbstratoTeste {
	public Simples7Test() {
		super("simples7");
	}

	@Test
	public void test() throws IOException, InstrucaoException {
		compilar();

		result = processador.processar(bibliotecaContexto.getNome(), "expressao");
		equals("[14]", result.toString());
	}
}