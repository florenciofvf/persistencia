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
import br.com.persist.data.Array;
import br.com.persist.data.ContainerDocument;
import br.com.persist.data.DataParser;
import br.com.persist.data.Filtro;
import br.com.persist.data.Objeto;
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
			BarraButton barraButton = criarToolbarPesquisa(textPane, null);
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
		if (styledDoc instanceof AbstractDocument && json != null) {
			AbstractDocument doc = (AbstractDocument) styledDoc;
			json.export(new ContainerDocument(doc), 0);
		}
	}

	private void config(BarraButton barraButton, Tipo json, JTextPane textPane) {
		Action totalElemAction = Action.acaoIcon(RequisicaoMensagens.getString("label.total_elementos"), Icones.INFO);
		Action comAtributoAction = Action.acaoMenu(RequisicaoMensagens.getString("label.com_atributos"), null);
		Action semAtributoAction = Action.acaoMenu(RequisicaoMensagens.getString("label.sem_atributos"), null);
		Action originalAction = Action.acaoMenu(RequisicaoMensagens.getString("label.original"), null);
		TextField txtComAtributo = new TextField(20);
		TextField txtSemAtributo = new TextField(20);

		comAtributoAction.setActionListener(e -> filtrarComAtributo(json, textPane, txtComAtributo));
		semAtributoAction.setActionListener(e -> filtrarSemAtributo(json, textPane, txtSemAtributo));
		totalElemAction.setActionListener(e -> totalElementos(textPane));
		originalAction.setActionListener(e -> retornar(json, textPane));

		barraButton.addButton(comAtributoAction);
		barraButton.add(txtComAtributo);
		barraButton.addButton(semAtributoAction);
		barraButton.add(txtSemAtributo);
		barraButton.addButton(originalAction);
		barraButton.addButton(totalElemAction);
	}

	private void retornar(Tipo json, JTextPane textPane) {
		setText(json, textPane);
	}

	private void totalElementos(JTextPane textPane) {
		if (!Util.isEmpty(textPane.getText())) {
			try {
				Tipo json = parser.parse(textPane.getText());
				if (json instanceof Array) {
					String msg = RequisicaoMensagens.getString("label.total_elementos");
					Util.mensagem(textPane, msg + " [" + ((Array) json).getElementos().size() + "]");
				} else {
					Util.mensagem(textPane, RequisicaoMensagens.getString("msg.objeto_principal_nao_array"));
				}
			} catch (Exception e) {
				Util.mensagem(textPane, e.getMessage());
			}
		}
	}

	private void filtrarComAtributo(Tipo json, JTextPane textPane, TextField textField) {
		if ((json instanceof Objeto || json instanceof Array) && !Util.isEmpty(textField.getText())) {
			String[] atributos = textField.getText().split(",");
			filtrarComAtributos(json.clonar(), atributos, textPane);
		}
	}

	private void filtrarSemAtributo(Tipo json, JTextPane textPane, TextField textField) {
		if ((json instanceof Objeto || json instanceof Array) && !Util.isEmpty(textField.getText())) {
			String[] atributos = textField.getText().split(",");
			filtrarSemAtributos(json.clonar(), atributos, textPane);
		}
	}

	private void filtrarComAtributos(Tipo json, String[] atributos, JTextPane textPane) {
		if (json instanceof Objeto) {
			json = Filtro.comAtributos((Objeto) json, atributos, null);
		} else if (json instanceof Array) {
			json = Filtro.comAtributos((Array) json, atributos, null);
		}
		setText(json, textPane);
	}

	private void filtrarSemAtributos(Tipo json, String[] atributos, JTextPane textPane) {
		if (json instanceof Objeto) {
			json = Filtro.semAtributos((Objeto) json, atributos);
		} else if (json instanceof Array) {
			json = Filtro.semAtributos((Array) json, atributos);
		}
		setText(json, textPane);
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