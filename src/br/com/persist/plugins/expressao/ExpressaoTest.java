package br.com.persist.plugins.expressao;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ExpressaoTest {
	protected Logger logger = Logger.getLogger(getClass().getName());
	private final File root = new File("expressao");

	protected File getFile(String funcionalidade, String arquivo) {
		File subDir = new File(root, funcionalidade);
		return new File(subDir, arquivo);
	}

	protected void log(Object object) {
		logger.log(Level.INFO, "{0}", object);
	}
}