package br.com.persist.plugins.requisicao.conteudo;

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
import br.com.persist.componente.ScrollPane;
import br.com.persist.parser.Tipo;
import br.com.persist.plugins.requisicao.RequisicaoException;
import br.com.persist.plugins.requisicao.RequisicaoUtil;

public class ConteudoHTML extends RequisicaoHeader {

	@Override
	public Component exibir(InputStream is, Tipo parametros) throws RequisicaoException, IOException {
		JTextPane area = new JTextPane();
		area.setEditable(false);
		area.addHyperlinkListener(new Listener());
		area.setContentType("text/html");
		String string = Util.getString(is);
		area.setText(string);
		String varAuthToken = RequisicaoUtil.getAtributoVarAuthToken(parametros);
		setVarAuthToken(string, varAuthToken);
		return new ScrollPane(area);
	}

	private class Listener implements HyperlinkListener {

		public void hyperlinkUpdate(HyperlinkEvent e) {
			if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				JEditorPane pane = (JEditorPane) e.getSource();
				URL url = e.getURL();
				String desc = e.getDescription();
				if (url == null && !Util.estaVazio(desc)) {
					Object resp = Util.getValorInputDialog(null, "label.atencao", "Complete a URL", desc);
					if (resp != null && !Util.estaVazio(resp.toString())) {
						try {
							url = new URL(resp.toString().trim());
						} catch (Exception ex) {
							pane.setText("Erro: " + ex.getMessage());
						}
					}
				}
				executar(pane, url);
			}
		}

		private void executar(JEditorPane pane, URL url) {
			if (url != null) {
				try {
					pane.setPage(url);
				} catch (Exception ex) {
					pane.setText("Erro: " + ex.getMessage());
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