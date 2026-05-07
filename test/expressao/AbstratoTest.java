package expressao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.persist.plugins.expressao.compilador.Compilacao;
import br.com.persist.plugins.expressao.processador.Processador;

public abstract class AbstratoTest {
	protected Logger logger = Logger.getLogger(getClass().getName());
	private final File root = new File("expressao_test");
	protected Processador processador;
	protected Compilacao compilacao;
	protected List<Object> result;

	protected File getFile(String funcionalidade, String arquivo) {
		File subDir = new File(root, funcionalidade);
		return new File(subDir, arquivo);
	}

	protected void log(Object object) {
		logger.log(Level.INFO, "{0}", object);
	}

	protected BigInteger bi(int valor) {
		return BigInteger.valueOf(valor);
	}

	protected BigDecimal bd(double valor) {
		return BigDecimal.valueOf(valor);
	}

	protected void equals(String expected, String actual) {
		assertEquals(expected, actual);
	}

	protected void notNull(Object object) {
		assertNotNull(object);
	}
}