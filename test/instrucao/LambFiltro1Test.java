package instrucao;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class LambFiltro1Test extends AbstratoTeste {
	public LambFiltro1Test() {
		super("lamb_filtro1");
	}

	@Test
	public void test() throws IOException, InstrucaoException {
		compilar();

		result = processador.processar(bibliotecaContexto.getNome(), "main", bi(6));
		assertEquals("[[50, 6]]", result.toString());
	}
}