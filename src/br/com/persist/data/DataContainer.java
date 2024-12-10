package br.com.persist.data;

import static br.com.persist.componente.BarraButtonEnum.APLICAR;
import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.LIMPAR;

import java.awt.BorderLayout;

import javax.swing.JTextPane;
import javax.swing.text.AbstractDocument;
import javax.swing.text.StyledDocument;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.componente.Panel;
import br.com.persist.componente.TabbedPane;
import br.com.persist.componente.TextEditor;

public class DataContainer extends Panel {
	private final TextEditor areaModelo = new TextEditor();
	private final TextEditor areaEdicao = new TextEditor();
	private final TabbedPane fichario = new TabbedPane();
	private static final long serialVersionUID = 1L;
	private final transient DataListener listener;
	private final Toolbar toolbar = new Toolbar();

	public DataContainer(Janela janela, DataListener listener) {
		this.listener = listener;
		toolbar.ini(janela);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, fichario);
		if (!listener.somenteModelo()) {
			fichario.addTab("label.modelo", areaEdicao);
			setString(areaEdicao, listener.getModelo());
		}
		fichario.addTab("label.modelo", areaModelo);
		setString(areaModelo, listener.getModelo());
	}

	private void setString(JTextPane area, String string) {
		if (Util.isEmpty(string)) {
			return;
		}
		area.setText(Constantes.VAZIO);
		try {
			DataParser parser = new DataParser();
			Tipo objJSON = parser.parse(string);
			StyledDocument styledDoc = area.getStyledDocument();
			if (styledDoc instanceof AbstractDocument) {
				AbstractDocument doc = (AbstractDocument) styledDoc;
				objJSON.export(new ContainerDocument(doc), 0);
			}
			area.requestFocus();
		} catch (Exception ex) {
			Util.stackTraceAndMessage(DataConstantes.PAINEL_PARSER, ex, this);
		}
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;

		public void ini(Janela janela) {
			if (!listener.somenteModelo()) {
				super.ini(janela, LIMPAR, COPIAR, COLAR, APLICAR);
			} else {
				super.ini(janela, COPIAR);
			}
		}

		@Override
		protected void limpar() {
			areaEdicao.setText(Constantes.VAZIO);
		}

		@Override
		protected void copiar() {
			String string = null;
			if (!listener.somenteModelo()) {
				string = Util.getString(areaEdicao);
			} else {
				string = Util.getString(areaModelo);
			}
			Util.setContentTransfered(string);
			copiarMensagem(string);
			if (!listener.somenteModelo()) {
				areaEdicao.requestFocus();
			} else {
				areaModelo.requestFocus();
			}
		}

		@Override
		protected void colar(boolean numeros, boolean letras) {
			Util.getContentTransfered(areaEdicao, numeros, letras);
		}

		@Override
		protected void aplicar() {
			if (Util.isEmpty(areaEdicao.getText())) {
				return;
			}
			String string = Util.getString(areaEdicao);
			try {
				DataParser parser = new DataParser();
				Tipo json = parser.parse(string);
				listener.setParserTipo(json);
			} catch (Exception ex) {
				Util.stackTraceAndMessage(DataConstantes.PAINEL_PARSER, ex, this);
			}
			fechar();
		}
	}
}