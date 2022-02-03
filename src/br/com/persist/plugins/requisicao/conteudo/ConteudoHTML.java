package br.com.persist.plugins.requisicao.conteudo;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.JEditorPane;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.parser.Tipo;
import br.com.persist.plugins.requisicao.RequisicaoConstantes;
import br.com.persist.plugins.requisicao.RequisicaoException;
import br.com.persist.plugins.requisicao.RequisicaoUtil;

public class ConteudoHTML extends RequisicaoHeader {

	@Override
	public Component exibir(InputStream is, Tipo parametros) throws RequisicaoException, IOException {
		JTextPane textPane = new JTextPane();
		textPane.setEditable(false);
		textPane.addHyperlinkListener(new Listener());
		textPane.setContentType("text/html");
		String string = Util.getString(is);
		textPane.setText(string);
		String varAuthToken = RequisicaoUtil.getAtributoVarAuthToken(parametros);
		setVarAuthToken(string, varAuthToken);

		Panel panelTextPane = new Panel();
		panelTextPane.add(BorderLayout.CENTER, textPane);

		Panel panel = new Panel();
		panel.add(BorderLayout.NORTH, criarToolbarPesquisa(textPane));
		panel.add(BorderLayout.CENTER, new ScrollPane(panelTextPane));

		return panel;
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
				if (!Util.estaVazio(desc)) {
					String rota = getRequisicaoRota().getStringRota(desc);
					if (!Util.estaVazio(rota)) {
						getRequisicaoConteudoListener().processarRota(rota, desc);
						return;
					}
					processarLink(pane, desc);
				}
			}
		}

		private void processarLink(JEditorPane pane, String desc) {
			URL url = null;
			Object resp = Util.getValorInputDialog(null, "label.atencao", "Complete a URL", desc);
			if (resp != null && !Util.estaVazio(resp.toString())) {
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
	public String titulo() {
		return "Html";
	}

	@Override
	public Icon icone() {
		return Icones.URL;
	}
}