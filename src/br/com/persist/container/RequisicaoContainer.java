package br.com.persist.container;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.text.AbstractDocument;
import javax.swing.text.StyledDocument;

import br.com.persist.comp.BarraButton;
import br.com.persist.comp.CheckBox;
import br.com.persist.comp.Panel;
import br.com.persist.comp.ScrollPane;
import br.com.persist.fichario.Fichario;
import br.com.persist.fmt.Parser;
import br.com.persist.fmt.Tipo;
import br.com.persist.util.Action;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Icones;
import br.com.persist.util.Preferencias;
import br.com.persist.util.Util;

public class RequisicaoContainer extends Panel implements Fichario.IFicharioSalvar {
	private static final long serialVersionUID = 1L;
	private static final File file = new File("requisicao/requisicoes");
	private static final String PAINEL_REQUISICAO = "PAINEL REQUISICAO";
	private final JTextPane areaResultados = new JTextPane();
	private final JTextPane areaParametros = new JTextPane();
	private final Toolbar toolbar = new Toolbar();

	public RequisicaoContainer(IJanela janela) {
		toolbar.ini(janela);
		montarLayout();
		abrir();
	}

	@Override
	public File getFileSalvarAberto() {
		return new File(getClass().getName());
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);

		Panel panelParametros = new Panel();
		panelParametros.add(areaParametros);

		Panel panelResultados = new Panel();
		panelResultados.add(areaResultados);

		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new ScrollPane(panelParametros),
				new ScrollPane(panelResultados));
		split.setDividerLocation(200);

		add(BorderLayout.CENTER, split);
	}

	private void abrir() {
		areaParametros.setText("");

		if (file.exists()) {
			try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
				StringBuilder sb = new StringBuilder();
				String linha = br.readLine();

				while (linha != null) {
					sb.append(linha + Constantes.QL2);
					linha = br.readLine();
				}

				areaParametros.setText(sb.toString());
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
			configAbrirAutoFichario(Constantes.ABRIR_AUTO_FICHARIO_REQUISICAO);

			add(chkRespostaJson);
			addButton(true, atualizarAcao);
			addButton(true, salvarAcao);

			eventos();
		}

		private void eventos() {
			chkRespostaJson.setSelected(Preferencias.getBoolean("requisicao_response_json"));
			chkRespostaJson.addActionListener(
					e -> Preferencias.setBoolean("requisicao_response_json", chkRespostaJson.isSelected()));

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
			areaResultados.setText("PARAMETROS VAZIO.");
			return;
		}

		String string = areaParametros.getSelectedText();

		if (Util.estaVazio(string)) {
			areaResultados.setText("NENHUM FRAGMENTO SELECIONADO.");
			return;
		}

		areaResultados.setText("");

		try {
			Parser parser = new Parser();
			Tipo parametros = parser.parse(string);
			String resposta = Util.requisicao(parametros);

			if (!Util.estaVazio(resposta) && toolbar.chkRespostaJson.isSelected()) {
				StyledDocument styledDoc = areaResultados.getStyledDocument();
				Tipo json = parser.parse(resposta);

				if (styledDoc instanceof AbstractDocument) {
					AbstractDocument doc = (AbstractDocument) styledDoc;
					json.toString(doc, false, 0);
				}
			} else {
				areaResultados.setText(resposta);
			}
		} catch (Exception ex) {
			Util.stackTraceAndMessage(PAINEL_REQUISICAO, ex, this);
		}
	}
}