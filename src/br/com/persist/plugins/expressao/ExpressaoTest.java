package br.com.persist.plugins.expressao;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ExpressaoTest {
	protected Logger logger = Logger.getLogger(getClass().getName());

	protected void log(Object object) {
		logger.log(Level.INFO, "{0}", object);
	}
}