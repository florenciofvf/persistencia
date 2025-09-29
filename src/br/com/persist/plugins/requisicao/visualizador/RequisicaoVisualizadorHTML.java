package br.com.persist.plugins.requisicao.visualizador;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Rectangle;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.JEditorPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.data.Tipo;
import br.com.persist.plugins.requisicao.RequisicaoConstantes;
import br.com.persist.plugins.requisicao.RequisicaoUtil;

public class RequisicaoVisualizadorHTML extends RequisicaoVisualizadorHeader {
	@Override
	public Component exibidor(Component parent, byte[] bytes, Tipo parametros) {
		try {
			JTextPane textPane = new JTextPane();
			textPane.setEditable(false);
			textPane.addHyperlinkListener(new Listener());
			textPane.setContentType("text/html");
			String string = Util.getString(bytes);
			textPane.setText(string);
			String varAuthToken = RequisicaoUtil.getAtributoVarAuthToken(parametros);
			setVarAuthToken(varAuthToken, string);

			Panel panelTextPane = new Panel();
			panelTextPane.add(BorderLayout.CENTER, textPane);

			Panel panel = new Panel();
			panel.add(BorderLayout.NORTH, criarToolbarPesquisa(textPane, null));
			panel.add(BorderLayout.CENTER, new ScrollPane(panelTextPane));
			SwingUtilities.invokeLater(() -> textPane.scrollRectToVisible(new Rectangle()));

			return panel;
		} catch (Exception e) {
			Util.mensagem(parent, e.getMessage());
			return null;
		}
	}

	private class Listener implements HyperlinkListener {

		public void hyperlinkUpdate(HyperlinkEvent e) {
			if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				JEditorPane pane = (JEditorPane) e.getSource();
				URL url = e.getURL();
				if (url != null) {
					executar(pane, url);
					return;
				}
				String desc = e.getDescription();
				if (!Util.isEmpty(desc)) {
					String rota = getRequisicaoRota().getStringRota(desc);
					if (!Util.isEmpty(rota)) {
						getRequisicaoVisualizadorListener().processarRota(rota, desc);
						return;
					}
					processarLink(pane, desc);
				}
			}
		}

		private void processarLink(JEditorPane pane, String desc) {
			URL url = null;
			Object resp = Util.getValorInputDialog(null, "label.atencao", "Complete a URL", desc);
			if (resp != null && !Util.isEmpty(resp.toString())) {
				try {
					url = new URL(resp.toString().trim());
				} catch (Exception ex) {
					Util.stackTraceAndMessage(RequisicaoConstantes.PAINEL_REQUISICAO, ex, pane);
				}
			}
			executar(pane, url);
		}

		private void executar(JEditorPane pane, URL url) {
			if (url != null) {
				try {
					pane.setPage(url);
				} catch (Exception ex) {
					Util.stackTraceAndMessage(RequisicaoConstantes.PAINEL_REQUISICAO, ex, pane);
				}
			}
		}
	}

	@Override
	public String toString() {
		return "HTML";
	}

	@Override
	public Icon getIcone() {
		return Icones.URL;
	}
}