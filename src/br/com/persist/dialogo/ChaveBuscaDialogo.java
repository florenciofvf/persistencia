package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Map;

import javax.swing.JToolBar;

import br.com.persist.Objeto;
import br.com.persist.comp.Button;
import br.com.persist.comp.TextArea;
import br.com.persist.util.Acao;
import br.com.persist.util.BuscaAuto;
import br.com.persist.util.Constantes;
import br.com.persist.util.Icones;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Util;
import br.com.persist.util.BuscaAuto.Grupo;

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
			setTitle(Mensagens.getString("label.chaveamento") + " - " + getTitle());

			Map<String, List<String>> campoNomes = Util.criarMapaCampoNomes(objeto.getChaveamento());

			int tamanho = campoNomes.size();
			int i = 0;

			for (String chave : campoNomes.keySet()) {
				List<String> nomes = campoNomes.get(chave);
				textArea.append(get(chave, nomes));

				if (i + 1 < tamanho) {
					textArea.append(";");
				}

				textArea.append(Constantes.QL);
				i++;
			}
		} else if (Tipo.BUSCA.equals(tipo)) {
			setTitle(Mensagens.getString("label.buscaAuto") + " - " + getTitle());

			List<Grupo> listaGrupo = BuscaAuto.criarGruposAuto(objeto.getBuscaAutomatica());

			for (int i = 0; i < listaGrupo.size(); i++) {
				Grupo grupo = listaGrupo.get(i);
				textArea.append(grupo.getDetalhe());

				if (i + 1 < listaGrupo.size()) {
					textArea.append(";");
				}

				textArea.append(Constantes.QL);
			}
		}
	}

	private String get(String chave, List<String> lista) {
		StringBuilder sb = new StringBuilder(chave + "=" + Constantes.QL);

		for (int i = 0; i < lista.size(); i++) {
			String string = lista.get(i);
			sb.append(Constantes.TAB + string);

			if (i + 1 < lista.size()) {
				sb.append(",");
			}

			sb.append(Constantes.QL);
		}

		return sb.toString();
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