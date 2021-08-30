package br.com.persist.componente;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JTable;
import javax.swing.SwingConstants;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Util;

public class SeparadorDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final SeparadorContainer container;

	private SeparadorDialogo(Frame frame, String titulo, JTable table, int indiceColuna, boolean comAspas) {
		super(frame, titulo);
		container = new SeparadorContainer(this, table, indiceColuna, comAspas);
		montarLayout();
	}

	private SeparadorDialogo(Dialog dialog, String titulo, JTable table, int indiceColuna, boolean comAspas) {
		super(dialog, titulo);
		container = new SeparadorContainer(this, table, indiceColuna, comAspas);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Component c, String titulo, JTable table, int indiceColuna, boolean comAspas) {
		Component comp = Util.getViewParent(c);
		SeparadorDialogo form = null;
		if (comp instanceof Frame) {
			form = new SeparadorDialogo((Frame) comp, titulo, table, indiceColuna, comAspas);
		} else if (comp instanceof Dialog) {
			form = new SeparadorDialogo((Dialog) comp, titulo, table, indiceColuna, comAspas);
		} else {
			form = new SeparadorDialogo((Frame) null, titulo, table, indiceColuna, comAspas);
		}
		form.pack();
		form.setLocationRelativeTo(comp != null ? comp : c);
		form.setVisible(true);
	}

	@Override
	public void dialogOpenedHandler(Dialog dialog) {
		container.checarCopias();
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
		Label label = new Label("label.separador");
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		setLayout(new GridLayout(1, 0));
		btnCopiar.setIcon(Icones.COPIA);
		label.setIcon(Icones.SEPARADOR);
		add(label);
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

	public void checarCopias() {
		List<String> lista = Util.getValoresLinha(table, indiceColuna);
		if (lista.size() == 1) {
			String string = Util.getStringLista(lista, Constantes.VAZIO, false, comAspas);
			Util.setContentTransfered(string);
			dialogo.dispose();
		}
	}
}