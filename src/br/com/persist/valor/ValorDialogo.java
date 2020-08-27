package br.com.persist.valor;

import java.awt.BorderLayout;
import java.awt.Dialog;

import br.com.persist.dialogo.AbstratoDialogo;
import br.com.persist.objeto.Objeto;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;

public class ValorDialogo extends AbstratoDialogo implements IJanela {
	private static final long serialVersionUID = 1L;
	private final ValorContainer container;

	private ValorDialogo(Dialog dialog, Objeto objeto, ValorContainer.Tipo tipo) {
		super(dialog, objeto.getId());
		container = new ValorContainer(this, objeto, tipo);
		montarLayout();

		if (ValorContainer.Tipo.CHAVE.equals(tipo)) {
			setTitle(Mensagens.getString("label.chaveamento") + " - " + getTitle());

		} else if (ValorContainer.Tipo.MAPA.equals(tipo)) {
			setTitle(Mensagens.getString("label.mapeamento") + " - " + getTitle());

		} else if (ValorContainer.Tipo.BUSCA.equals(tipo)) {
			setTitle(Mensagens.getString("label.buscaAuto") + " - " + getTitle());

		} else if (ValorContainer.Tipo.LINK.equals(tipo)) {
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

	public static ValorDialogo criar(Dialog dialog, Objeto objeto, ValorContainer.Tipo tipo) {
		return new ValorDialogo(dialog, objeto, tipo);
	}
}