package br.com.persist.container;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import br.com.persist.comp.BarraButton;
import br.com.persist.comp.Panel;
import br.com.persist.comp.TextArea;
import br.com.persist.fichario.Fichario;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Util;

public class AnotacaoContainer extends Panel implements Fichario.IFicharioSalvar {
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

	@Override
	public File getFileSalvarAberto() {
		return new File(Constantes.III + getClass().getName());
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, textArea);
		add(BorderLayout.NORTH, toolbar);
	}

	private void abrir() {
		textArea.limpar();

		if (file.exists()) {
			try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
				String linha = br.readLine();

				while (linha != null) {
					textArea.append(linha + Constantes.QL2);
					linha = br.readLine();
				}
			} catch (Exception ex) {
				Util.stackTraceAndMessage(PAINEL_ANOTACAO, ex, AnotacaoContainer.this);
			}
		}
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;

		public void ini(IJanela janela) {
			super.ini(janela, true, true);
			configAbrirAutoFichario(Constantes.ABRIR_AUTO_FICHARIO_ANOTACAO);
			configBaixarAcao(e -> abrir());
		}

		@Override
		protected void limpar() {
			textArea.limpar();
		}

		@Override
		protected void salvar() {
			if (!Util.confirmaSalvar(AnotacaoContainer.this, Constantes.TRES)) {
				return;
			}

			try (PrintWriter pw = new PrintWriter(file)) {
				pw.print(textArea.getText());
			} catch (Exception ex) {
				Util.stackTraceAndMessage(PAINEL_ANOTACAO, ex, AnotacaoContainer.this);
			}
		}
	}
}