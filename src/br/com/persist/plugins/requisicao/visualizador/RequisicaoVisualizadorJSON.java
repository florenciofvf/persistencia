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
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.TextField;
import br.com.persist.data.ContainerDocument;
import br.com.persist.data.DataParser;
import br.com.persist.data.Tipo;
import br.com.persist.plugins.requisicao.RequisicaoMensagens;

public class RequisicaoVisualizadorJSON extends RequisicaoVisualizadorHeader {
	private final DataParser parser = new DataParser();

	@Override
	public Component exibidor(Component parent, byte[] bytes, Tipo parametros) {
		try {
			JTextPane textPane = new JTextPane();
			String string = Util.getString(bytes);
			Tipo json = parser.parse(string);
			setText(json, textPane);
			String accessToken = getAccessToken(json);
			setAccesToken(accessToken);

			Panel panelTextPane = new Panel();
			panelTextPane.add(BorderLayout.CENTER, textPane);

			Panel panel = new Panel();
			BarraButton barraButton = criarToolbarPesquisa(textPane);
			config(barraButton, json, textPane);
			panel.add(BorderLayout.NORTH, barraButton);
			panel.add(BorderLayout.CENTER, new ScrollPane(panelTextPane));
			SwingUtilities.invokeLater(() -> textPane.scrollRectToVisible(new Rectangle()));

			return panel;
		} catch (Exception e) {
			Util.mensagem(parent, e.getMessage());
			return null;
		}
	}

	private void setText(Tipo json, JTextPane textPane) {
		textPane.setText(Constantes.VAZIO);
		StyledDocument styledDoc = textPane.getStyledDocument();
		if (styledDoc instanceof AbstractDocument) {
			AbstractDocument doc = (AbstractDocument) styledDoc;
			json.export(new ContainerDocument(doc), 0);
		}
	}

	private void config(BarraButton barraButton, Tipo json, JTextPane textPane) {
		Action retornarAction = Action.acaoMenu(RequisicaoMensagens.getString("label.retornar"), null);
		Action filtroAction = Action.acaoMenu(RequisicaoMensagens.getString("label.filtrar"), null);
		TextField txtComAtributo = new TextField(20);
		TextField txtSemAtributo = new TextField(20);
		txtComAtributo.setToolTipText(RequisicaoMensagens.getString("hint.com_atributo"));
		txtSemAtributo.setToolTipText(RequisicaoMensagens.getString("hint.sem_atributo"));

		filtroAction.setActionListener(e -> filtrar(json, txtComAtributo, txtSemAtributo));
		retornarAction.setActionListener(e -> retornar(json, textPane));

		barraButton.addButton(filtroAction);
		barraButton.add(txtComAtributo);
		barraButton.add(txtSemAtributo);
		barraButton.addButton(retornarAction);
	}

	private void retornar(Tipo json, JTextPane textPane) {
		setText(json, textPane);
	}

	private void filtrar(Tipo json, TextField txtComAtributo, TextField txtSemAtributo) {
	}

	@Override
	public String toString() {
		return "JSON";
	}

	@Override
	public Icon getIcone() {
		return Icones.CONFIG;
	}
}