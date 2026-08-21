package br.com.persist.plugins.requisicao.visualizador;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.SwingUtilities;

import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.TextEditor;
import br.com.persist.componente.TextEditorLine;
import br.com.persist.componente.ToolbarPesquisa;
import br.com.persist.data.Tipo;

public class RequisicaoVisualizadorTexto extends AbstratoRequisicaoVisualizador {
	@Override
	public Component exibidor(Component parent, byte[] bytes, Tipo parametros) {
		try {
			TextEditor textEditor = new TextEditor();
			textEditor.setText(Util.getString(bytes));

			ToolbarPesquisa toolbarPesquisa = new ToolbarPesquisa(textEditor);
			textEditor.setListener(TextEditor.newTextEditorAdapter(toolbarPesquisa::focusInputPesquisar));

			Panel panel = new Panel();
			panel.add(BorderLayout.NORTH, toolbarPesquisa);
			ScrollPane scrollPane2 = new ScrollPane(textEditor);
			TextEditorLine editorLine = new TextEditorLine(textEditor);
			textEditor.setFontListener(editorLine);
			scrollPane2.setRowHeaderView(editorLine);

			Panel panelScroll = new Panel();
			panelScroll.add(BorderLayout.CENTER, scrollPane2);
			panel.add(BorderLayout.CENTER, new ScrollPane(panelScroll));

			SwingUtilities.invokeLater(() -> textEditor.scrollRectToVisible(new Rectangle()));

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