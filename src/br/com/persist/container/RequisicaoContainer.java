package br.com.persist.container;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.swing.JSplitPane;

import br.com.persist.comp.BarraButton;
import br.com.persist.comp.CheckBox;
import br.com.persist.comp.Panel;
import br.com.persist.comp.ScrollPane;
import br.com.persist.comp.TextArea;
import br.com.persist.fmt.Parser;
import br.com.persist.fmt.Tipo;
import br.com.persist.util.Action;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Icones;
import br.com.persist.util.Util;

public class RequisicaoContainer extends Panel {
	private static final long serialVersionUID = 1L;
	private static final File file = new File("requisicao/requisicoes");
	private static final String PAINEL_REQUISICAO = "PAINEL REQUISICAO";
	private final TextArea areaParametros = new TextArea();
	private final TextArea areaResultados = new TextArea();
	private final Toolbar toolbar = new Toolbar();

	public RequisicaoContainer(IJanela janela) {
		toolbar.ini(janela);
		montarLayout();
		abrir();
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);

		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new ScrollPane(areaParametros),
				new ScrollPane(areaResultados));
		split.setDividerLocation(200);

		add(BorderLayout.CENTER, split);
	}

	private void abrir() {
		areaParametros.limpar();

		if (file.exists()) {
			try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
				String linha = br.readLine();

				while (linha != null) {
					areaParametros.append(linha + Constantes.QL);
					linha = br.readLine();
				}
			} catch (Exception ex) {
				Util.stackTraceAndMessage(PAINEL_REQUISICAO, ex, RequisicaoContainer.this);
			}
		}
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		private Action atualizarAcao = Action.actionIcon("label.requisicao", Icones.URL);
		private CheckBox chkRespostaJson = new CheckBox("label.resposta_json");
		private Action salvarAcao = Action.actionIconSalvar();

		@Override
		public void ini(IJanela janela) {
			super.ini(janela);

			add(chkRespostaJson);
			addButton(atualizarAcao);
			addButton(true, salvarAcao);

			eventos();
		}

		private void eventos() {
			atualizarAcao.setActionListener(e -> atualizar());

			salvarAcao.setActionListener(e -> {
				int total = 0;

				while (total < 3 && Util.confirmar(RequisicaoContainer.this, "label.confirma_salvar")) {
					total++;
				}

				if (total < 3) {
					return;
				}

				try {
					PrintWriter pw = new PrintWriter(file);
					pw.print(areaParametros.getText());
					pw.close();
				} catch (Exception ex) {
					Util.stackTraceAndMessage(PAINEL_REQUISICAO, ex, RequisicaoContainer.this);
				}
			});
		}
	}

	public void atualizar() {
		if (Util.estaVazio(areaParametros.getText())) {
			return;
		}

		String string = areaParametros.getSelectedText();

		if (Util.estaVazio(string)) {
			return;
		}

		try {
			Parser parser = new Parser();
			Tipo parametros = parser.parse(string);
			String resposta = Util.requisicao(parametros);

			if (!Util.estaVazio(resposta) && toolbar.chkRespostaJson.isSelected()) {
				Tipo json = parser.parse(resposta);
				StringBuilder sb = new StringBuilder();
				json.toString(sb, false, 0);
				resposta = sb.toString();
			}

			areaResultados.setText(resposta);
		} catch (Exception ex) {
			Util.stackTraceAndMessage(PAINEL_REQUISICAO, ex, this);
		}
	}
}