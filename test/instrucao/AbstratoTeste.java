package instrucao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.BibliotecaContexto;
import br.com.persist.plugins.instrucao.compilador.Compilador;
import br.com.persist.plugins.instrucao.processador.Processador;

public abstract class AbstratoTeste {
	private static final File ROOT = new File("instrucoes_test");
	protected BibliotecaContexto bibliotecaContexto;
	protected Processador processador;
	protected Compilador compilador;
	protected final File biblioteca;
	protected List<Object> result;

	protected AbstratoTeste(String biblio) {
		biblioteca = new File(ROOT, biblio);
		processador = new Processador();
		compilador = new Compilador();
	}

	public void compilar() throws IOException, InstrucaoException {
		bibliotecaContexto = compilador.compilar(biblioteca);
	}

	public static BigInteger bi(int i) {
		return new BigInteger("" + i);
	}

	protected void equals(String expected, String actual) {
		assertEquals(expected, actual);
	}

	protected void notNull(Object object) {
		assertNotNull(object);
	}
}