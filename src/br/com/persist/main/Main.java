package br.com.persist.main;

import br.com.persist.formulario.Formulario;
import br.com.persist.util.Imagens;

public class Main {
	public static void main(String[] args) throws Exception {
		Imagens.ini();
		Formulario formulario = new Formulario();
		formulario.setLocationRelativeTo(null);
		formulario.setVisible(true);
	}
}