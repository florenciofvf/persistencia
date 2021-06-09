package br.com.persist.plugins.persistencia.tabela;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JTable;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Button;
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.Label;
import br.com.persist.componente.Panel;
import br.com.persist.componente.TextField;

public class SeparadorDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final SeparadorContainer container;

	private SeparadorDialogo(String titulo, JTable table, int indiceColuna, boolean comAspas) {
		super((Frame) null, titulo);
		container = new SeparadorContainer(this, table, indiceColuna, comAspas);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Component component, String titulo, JTable table, int indiceColuna, boolean comAspas) {
		SeparadorDialogo form = new SeparadorDialogo(titulo, table, indiceColuna, comAspas);
		form.pack();
		form.setLocationRelativeTo(component);
		form.setVisible(true);
	}
}

class SeparadorContainer extends Panel {
	private static final long serialVersionUID = 1L;
	private final CheckBox chkQuebraLinha = new CheckBox("label.quebrarLinha");
	private final Button btnCopiar = new Button("label.copiar");
	private final TextField txtSeparador = new TextField();
	private final boolean comAspas;
	private final int indiceColuna;
	private final Dialog dialogo;
	private final JTable table;

	public SeparadorContainer(Dialog dialogo, JTable table, int indiceColuna, boolean comAspas) {
		this.indiceColuna = indiceColuna;
		this.comAspas = comAspas;
		this.dialogo = dialogo;
		this.table = table;
		montarLayout();
		btnCopiar.addActionListener(e -> copiar());
		txtSeparador.setText(", ");
	}

	private void montarLayout() {
		setLayout(new GridLayout(1, 0));
		add(new Label("label.separador"));
		add(txtSeparador);
		add(chkQuebraLinha);
		add(btnCopiar);
	}

	private void copiar() {
		List<String> lista = Util.getValoresLinha(table, indiceColuna);
		String string = Util.getStringLista(lista, txtSeparador.getText(), chkQuebraLinha.isSelected(), comAspas);
		Util.setContentTransfered(string);
		dialogo.dispose();
	}
}