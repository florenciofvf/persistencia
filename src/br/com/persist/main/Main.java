package br.com.persist.main;

import br.com.persist.formulario.Formulario;
import br.com.persist.util.Util;

public class Main {
	public static void main(String[] args) throws Exception {
		try {
			// Conexao.getConnection();
		} catch (Exception ex) {
			Util.stackTraceMessageAndException("Main", ex);
		}

		Formulario formulario = new Formulario();
		formulario.setLocationRelativeTo(null);
		formulario.setVisible(true);
	}
}