package instrucao;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class Simples4Test extends AbstratoTeste {
	public Simples4Test() {
		super("simples4");
	}

	@Test
	public void test() throws IOException, InstrucaoException {
		compilar();

		result = processador.processar(bibliotecaContexto.getNome(), "dobrar", bi(30));
		equals("[60]", result.toString());

		result = processador.processar(bibliotecaContexto.getNome(), "quadrado", bi(30));
		equals("[900]", result.toString());

		result = processador.processar(bibliotecaContexto.getNome(), "somar", bi(30), bi(3));
		equals("[-33]", result.toString());

		result = processador.processar(bibliotecaContexto.getNome(), "area", bi(30));
		equals("[2827.4057100]", result.toString());
	}
}