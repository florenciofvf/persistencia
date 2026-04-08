package instrucao;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class LambFiltro0Test extends AbstratoTeste {
	public LambFiltro0Test() {
		super("lamb_filtro0");
	}

	@Test
	public void test() throws IOException, InstrucaoException {
		compilar();

		result = processador.processar(bibliotecaContexto.getNome(), "main");
		assertEquals("[[1, 2, 3]]", result.toString());
	}
}