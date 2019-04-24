package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.Dialog;

import br.com.persist.container.ChaveBuscaContainer;
import br.com.persist.container.ChaveBuscaContainer.Tipo;
import br.com.persist.desktop.Objeto;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;

public class ChaveBuscaDialogo extends AbstratoDialogo implements IJanela {
	private static final long serialVersionUID = 1L;
	private final ChaveBuscaContainer container;

	public ChaveBuscaDialogo(Dialog dialog, Objeto objeto, Tipo tipo) {
		super(dialog, objeto.getId());
		container = new ChaveBuscaContainer(this, objeto, tipo);
		montarLayout();

		if (Tipo.CHAVE.equals(tipo)) {
			setTitle(Mensagens.getString("label.chaveamento") + " - " + getTitle());
		} else if (Tipo.BUSCA.equals(tipo)) {
			setTitle(Mensagens.getString("label.buscaAuto") + " - " + getTitle());
		} else if (Tipo.LINK.equals(tipo)) {
			setTitle(Mensagens.getString("label.linkAuto") + " - " + getTitle());
		}
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	@Override
	public void fechar() {
		dispose();
	}
}