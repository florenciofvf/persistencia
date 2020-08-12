package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.Dialog;

import br.com.persist.container.ValorContainer;
import br.com.persist.container.ValorContainer.Tipo;
import br.com.persist.objeto.Objeto;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;

public class ValorDialogo extends AbstratoDialogo implements IJanela {
	private static final long serialVersionUID = 1L;
	private final ValorContainer container;

	public ValorDialogo(Dialog dialog, Objeto objeto, Tipo tipo) {
		super(dialog, objeto.getId());
		container = new ValorContainer(this, objeto, tipo);
		montarLayout();

		if (Tipo.BUSCA_APOS.equals(tipo)) {
			setTitle(Mensagens.getString("label.buscaAutoApos") + " - " + getTitle());

		} else if (Tipo.CHAVE.equals(tipo)) {
			setTitle(Mensagens.getString("label.chaveamento") + " - " + getTitle());

		} else if (Tipo.MAPA.equals(tipo)) {
			setTitle(Mensagens.getString("label.mapeamento") + " - " + getTitle());

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