package instrucao;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class MapaTest extends AbstratoTeste {
	public MapaTest() {
		super("mapa");
	}

	@Test
	public void test() throws IOException, InstrucaoException {
		compilar();

		notNull(bibliotecaContexto);
	}
}