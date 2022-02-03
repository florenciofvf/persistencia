package br.com.persist.plugins.requisicao.conteudo;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.Icon;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.parser.Tipo;

public class ConteudoTexto extends AbstratoRequisicaoConteudo {

	@Override
	public Component exibir(InputStream is, Tipo parametros) throws IOException {
		JTextPane textPane = new JTextPane();
		String string = Util.getString(is);
		textPane.setText(string);

		Panel panel = new Panel();
		panel.add(BorderLayout.NORTH, criarToolbarPesquisa(textPane));
		panel.add(BorderLayout.CENTER, new ScrollPane(textPane));
		SwingUtilities.invokeLater(() -> textPane.scrollRectToVisible(new Rectangle()));

		return panel;
	}

	@Override
	public String titulo() {
		return "Texto";
	}

	@Override
	public Icon icone() {
		return Icones.TEXTO;
	}
}