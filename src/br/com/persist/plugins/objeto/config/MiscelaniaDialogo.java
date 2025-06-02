package br.com.persist.plugins.objeto.config;

import java.awt.BorderLayout;
import java.awt.Dialog;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.marca.XMLException;
import br.com.persist.plugins.objeto.Objeto;

public class MiscelaniaDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final MiscelaniaContainer container;

	private MiscelaniaDialogo(Dialog dialog, Objeto objeto, MiscelaniaContainer.Tipo tipo) throws XMLException {
		super(dialog, objeto.getId());
		container = new MiscelaniaContainer(this, objeto, tipo);
		montarLayout();
		if (MiscelaniaContainer.Tipo.CHAVEAMENTO.equals(tipo)) {
			setTitle(Mensagens.getString("label.chaveamento") + " - " + getTitle());
		} else if (MiscelaniaContainer.Tipo.MAPEAMENTO.equals(tipo)) {
			setTitle(Mensagens.getString("label.mapeamento") + " - " + getTitle());
		} else if (MiscelaniaContainer.Tipo.SEQUENCIA.equals(tipo)) {
			setTitle(Mensagens.getString("label.sequencias") + " - " + getTitle());
		} else if (MiscelaniaContainer.Tipo.COMPLEMENTO.equals(tipo)) {
			setTitle(Mensagens.getString("label.complemento") + " - " + getTitle());
		} else if (MiscelaniaContainer.Tipo.CLASSBIBLIO.equals(tipo)) {
			setTitle(Mensagens.getString("label.class_biblio") + " - " + getTitle());
		} else if (MiscelaniaContainer.Tipo.DESTACAVEIS.equals(tipo)) {
			setTitle(Mensagens.getString("label.campos_destac") + " - " + getTitle());
		} else if (MiscelaniaContainer.Tipo.INSTRUCAO.equals(tipo)) {
			setTitle(Mensagens.getString("label.add_instrucao") + " - " + getTitle());
		} else if (MiscelaniaContainer.Tipo.FILTRO.equals(tipo)) {
			setTitle(Mensagens.getString("label.add_filtro") + " - " + getTitle());
		}
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public void setMiscelaniaListener(MiscelaniaListener listener) {
		container.setListener(listener);
	}

	public static MiscelaniaDialogo criar(Dialog dialog, Objeto objeto, MiscelaniaContainer.Tipo tipo)
			throws XMLException {
		return new MiscelaniaDialogo(dialog, objeto, tipo);
	}
}