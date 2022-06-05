package br.com.persist.plugins.requisicao.visualizador;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.AbstractDocument;
import javax.swing.text.StyledDocument;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.parser.Parser;
import br.com.persist.parser.Tipo;

public class RequisicaoVisualizadorJSON extends RequisicaoVisualizadorHeader {
	private final Parser parser = new Parser();

	@Override
	public Component exibidor(Component parent, byte[] bytes, Tipo parametros) {
		try {
			JTextPane textPane = new JTextPane();
			textPane.setText(Constantes.VAZIO);
			String string = Util.getString(bytes);
			StyledDocument styledDoc = textPane.getStyledDocument();
			Tipo json = parser.parse(string);
			if (styledDoc instanceof AbstractDocument) {
				AbstractDocument doc = (AbstractDocument) styledDoc;
				json.toString(doc, false, 0);
			}
			String accessToken = getAccessToken(json);
			setAccesToken(accessToken);

			Panel panelTextPane = new Panel();
			panelTextPane.add(BorderLayout.CENTER, textPane);

			Panel panel = new Panel();
			panel.add(BorderLayout.NORTH, criarToolbarPesquisa(textPane));
			panel.add(BorderLayout.CENTER, new ScrollPane(panelTextPane));
			SwingUtilities.invokeLater(() -> textPane.scrollRectToVisible(new Rectangle()));

			return panel;
		} catch (Exception e) {
			Util.mensagem(parent, e.getMessage());
			return null;
		}
	}

	@Override
	public String toString() {
		return "Json";
	}

	@Override
	public Icon getIcone() {
		return Icones.CONFIG;
	}
}