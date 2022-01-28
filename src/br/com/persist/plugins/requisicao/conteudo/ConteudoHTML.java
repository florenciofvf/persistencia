package br.com.persist.plugins.requisicao.conteudo;

import java.awt.Component;
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

public class ConteudoHTML extends RequisicaoHeader {

	@Override
	public Component exibir(InputStream is) throws Exception {
		JTextPane area = new JTextPane();
		area.setEditable(false);
		area.addHyperlinkListener(new Listener());
		area.setContentType("text/html");
		String string = Util.getString(is);
		area.setText(string);
		setAuthToken(string);
		return new ScrollPane(area);
	}

	private class Listener implements HyperlinkListener {

		public void hyperlinkUpdate(HyperlinkEvent e) {
			if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				JEditorPane pane = (JEditorPane) e.getSource();
				URL url = e.getURL();
				String desc = e.getDescription();
				if (url == null && !Util.estaVazio(desc)) {
					Object obj = Util.getValorInputDialog(null, "label.atencao", "Complete a URL", desc);
					if (obj != null) {
						try {
							url = new URL(obj.toString());
						} catch (Exception ex) {
							pane.setText("Erro: " + ex.getMessage());
						}
					}
				}
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