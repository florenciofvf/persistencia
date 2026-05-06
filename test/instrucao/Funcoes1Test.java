package instrucao;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class Funcoes1Test extends AbstratoTeste {
	public Funcoes1Test() {
		super("funcoes1");
	}

	@Test
	public void test() throws IOException, InstrucaoException {
		compilar();

		result = processador.processar(bibliotecaContexto.getNome(), "main");
		equals("[10\n4]", result.toString());
	}
}