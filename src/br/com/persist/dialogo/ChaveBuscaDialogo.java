package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.event.ActionEvent;

import javax.swing.JToolBar;

import br.com.persist.Objeto;
import br.com.persist.comp.Button;
import br.com.persist.comp.TextArea;
import br.com.persist.util.Acao;
import br.com.persist.util.Icones;
import br.com.persist.util.Mensagens;

public class ChaveBuscaDialogo extends Dialogo {
	private static final long serialVersionUID = 1L;
	private final TextArea textArea = new TextArea();
	private final Toolbar toolbar = new Toolbar();
	private final Objeto objeto;
	private final Tipo tipo;

	public ChaveBuscaDialogo(Dialog dialog, Objeto objeto, Tipo tipo) {
		super(dialog, objeto.getId(), 600, 600, false);
		this.objeto = objeto;
		this.tipo = tipo;
		montarLayout();
		setVisible(true);
	}

	public enum Tipo {
		CHAVE, BUSCA
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, textArea);

		if (Tipo.CHAVE.equals(tipo)) {
			setTitle(Mensagens.getString("label.chaveamento") + " " + getTitle());
		} else if (Tipo.BUSCA.equals(tipo)) {
			setTitle(Mensagens.getString("label.buscaAuto") + " " + getTitle());
		}
	}

	protected void processar() {
	}

	private class Toolbar extends JToolBar {
		private static final long serialVersionUID = 1L;

		Toolbar() {
			add(new Button(new ConfigFragmentoAcao()));
		}

		class ConfigFragmentoAcao extends Acao {
			private static final long serialVersionUID = 1L;

			ConfigFragmentoAcao() {
				super(false, "label.aplicar", Icones.SUCESSO);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				if (Tipo.CHAVE.equals(tipo)) {

				} else if (Tipo.CHAVE.equals(tipo)) {

				}

				dispose();
			}
		}
	}
}