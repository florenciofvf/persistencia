package br.com.persist.dialogo;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;

import javax.swing.JToolBar;

import br.com.persist.Objeto;
import br.com.persist.comp.Button;
import br.com.persist.comp.TextArea;
import br.com.persist.util.Action;
import br.com.persist.util.BuscaAuto;
import br.com.persist.util.Constantes;
import br.com.persist.util.Icones;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Util;
import br.com.persist.util.BuscaAuto.Grupo;

public class ChaveBuscaDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final TextArea textArea = new TextArea();
	private final Toolbar toolbar = new Toolbar();
	private final Objeto objeto;
	private final Tipo tipo;

	public ChaveBuscaDialogo(Dialog dialog, Objeto objeto, Tipo tipo) {
		super(dialog, objeto.getId(), false);
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

		StringBuilder builder = new StringBuilder();

		if (Tipo.CHAVE.equals(tipo)) {
			setTitle(Mensagens.getString("label.chaveamento") + " - " + getTitle());

			Map<String, List<String>> campoNomes = Util.criarMapaCampoNomes(objeto.getChaveamento());

			int tamanho = campoNomes.size();
			int i = 0;

			for (String chave : campoNomes.keySet()) {
				List<String> nomes = campoNomes.get(chave);
				builder.append(campoDetalhe(chave, nomes));

				if (i + 1 < tamanho) {
					builder.append(";");
				}

				builder.append(Constantes.QL);
				i++;
			}
		} else if (Tipo.BUSCA.equals(tipo)) {
			setTitle(Mensagens.getString("label.buscaAuto") + " - " + getTitle());

			List<Grupo> listaGrupo = BuscaAuto.criarGruposAuto(objeto.getBuscaAutomatica());

			for (int i = 0; i < listaGrupo.size(); i++) {
				Grupo grupo = listaGrupo.get(i);
				builder.append(grupo.getDetalhe());

				if (i + 1 < listaGrupo.size()) {
					builder.append(";");
				}

				builder.append(Constantes.QL);
			}
		}

		textArea.setText(builder.toString().trim());
	}

	private String campoDetalhe(String chave, List<String> lista) {
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

	private class Toolbar extends JToolBar implements ActionListener {
		private static final long serialVersionUID = 1L;

		Toolbar() {
			add(new Button(Action.actionIcon("label.aplicar", Icones.SUCESSO, this)));
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (Tipo.BUSCA.equals(tipo)) {
				objeto.setBuscaAutomatica(Util.normalizar(textArea.getText(), false));

			} else if (Tipo.CHAVE.equals(tipo)) {
				objeto.setChaveamento(Util.normalizar(textArea.getText(), false));
			}

			dispose();
		}
	}
}