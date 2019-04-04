package br.com.persist.anotacao;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import br.com.persist.comp.BarraButton;
import br.com.persist.comp.Panel;
import br.com.persist.comp.ScrollPane;
import br.com.persist.comp.TextArea;
import br.com.persist.util.Action;
import br.com.persist.util.Constantes;
import br.com.persist.util.Icones;
import br.com.persist.util.IJanela;
import br.com.persist.util.Util;

public class AnotacaoContainer extends Panel {
	private static final long serialVersionUID = 1L;
	private static final String PAINEL_ANOTACAO = "PAINEL ANOTACAO";
	private static final File file = new File("anotacoes/anotacoes");
	private final TextArea textArea = new TextArea();
	private final Toolbar toolbar = new Toolbar();

	public AnotacaoContainer(IJanela janela) {
		toolbar.ini(janela);
		montarLayout();
		abrir();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, new ScrollPane(textArea));
		add(BorderLayout.NORTH, toolbar);
	}

	private void abrir() {
		textArea.setText("");

		if (file.exists()) {
			try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
				String linha = br.readLine();

				while (linha != null) {
					textArea.append(linha + Constantes.QL);
					linha = br.readLine();
				}
			} catch (Exception ex) {
				Util.stackTraceAndMessage(PAINEL_ANOTACAO, ex, AnotacaoContainer.this);
			}
		}
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		private Action atualizarAcao = Action.actionIcon("label.atualizar", Icones.BAIXAR);
		private Action salvarAcao = Action.actionIcon("label.salvar", Icones.SALVAR);

		@Override
		protected void ini(IJanela janela) {
			super.ini(janela);

			addButton(atualizarAcao);
			addSeparator();
			addButton(salvarAcao);

			eventos();
		}

		private void eventos() {
			atualizarAcao.setActionListener(e -> abrir());

			salvarAcao.setActionListener(e -> {
				try {
					PrintWriter pw = new PrintWriter(file);
					pw.print(textArea.getText());
					pw.close();
				} catch (Exception ex) {
					Util.stackTraceAndMessage(PAINEL_ANOTACAO, ex, AnotacaoContainer.this);
				}
			});
		}
	}
}