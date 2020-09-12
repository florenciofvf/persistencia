package br.com.persist.plugins.objeto.config;

import java.awt.BorderLayout;
import java.awt.Dialog;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.plugins.objeto.Objeto;
import br.com.persist.util.Mensagens;

public class MiscelaniaDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final MiscelaniaContainer container;

	private MiscelaniaDialogo(Dialog dialog, Objeto objeto, MiscelaniaContainer.Tipo tipo) {
		super(dialog, objeto.getId());
		container = new MiscelaniaContainer(this, objeto, tipo);
		montarLayout();

		if (MiscelaniaContainer.Tipo.CHAVE.equals(tipo)) {
			setTitle(Mensagens.getString("label.chaveamento") + " - " + getTitle());

		} else if (MiscelaniaContainer.Tipo.MAPA.equals(tipo)) {
			setTitle(Mensagens.getString("label.mapeamento") + " - " + getTitle());

		} else if (MiscelaniaContainer.Tipo.BUSCA.equals(tipo)) {
			setTitle(Mensagens.getString("label.buscaAuto") + " - " + getTitle());

		} else if (MiscelaniaContainer.Tipo.LINK.equals(tipo)) {
			setTitle(Mensagens.getString("label.linkAuto") + " - " + getTitle());
		}
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static MiscelaniaDialogo criar(Dialog dialog, Objeto objeto, MiscelaniaContainer.Tipo tipo) {
		return new MiscelaniaDialogo(dialog, objeto, tipo);
	}
}