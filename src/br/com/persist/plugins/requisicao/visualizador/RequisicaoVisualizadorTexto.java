package br.com.persist.plugins.requisicao.visualizador;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.data.Tipo;

public class RequisicaoVisualizadorTexto extends AbstratoRequisicaoVisualizador {
	@Override
	public Component exibidor(Component parent, byte[] bytes, Tipo parametros) {
		try {
			JTextPane textPane = new JTextPane();
			textPane.setText(Util.getString(bytes));

			Panel panel = new Panel();
			panel.add(BorderLayout.NORTH, criarToolbarPesquisa(textPane, null));
			panel.add(BorderLayout.CENTER, new ScrollPane(textPane));
			SwingUtilities.invokeLater(() -> textPane.scrollRectToVisible(new Rectangle()));

			return panel;
		} catch (Exception e) {
			Util.mensagem(parent, e.getMessage());
			return null;
		}
	}

	@Override
	public String toString() {
		return "Texto";
	}

	@Override
	public Icon getIcone() {
		return Icones.TEXTO;
	}
}