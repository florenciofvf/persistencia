package br.com.persist.container;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import br.com.persist.comp.BarraButton;
import br.com.persist.comp.TextArea;
import br.com.persist.fichario.Fichario;
import br.com.persist.formulario.AnotacaoFormulario;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Util;

public class AnotacaoContainer extends AbstratoContainer implements Fichario.IFicharioSalvar {
	private static final long serialVersionUID = 1L;
	private static final String PAINEL_ANOTACAO = "PAINEL ANOTACAO";
	private static final File file = new File("anotacoes/anotacoes");
	private final TextArea textArea = new TextArea();
	private final Toolbar toolbar = new Toolbar();
	private AnotacaoFormulario anotacaoFormulario;

	public AnotacaoContainer(IJanela janela, Formulario formulario, String conteudo) {
		super(formulario);
		toolbar.ini(janela);
		montarLayout();
		abrir(conteudo);
	}

	public AnotacaoFormulario getAnotacaoFormulario() {
		return anotacaoFormulario;
	}

	public void setAnotacaoFormulario(AnotacaoFormulario anotacaoFormulario) {
		this.anotacaoFormulario = anotacaoFormulario;
	}

	@Override
	public File getFileSalvarAberto() {
		return new File(Constantes.III + getClass().getName());
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, textArea);
		add(BorderLayout.NORTH, toolbar);
	}

	public String getConteudo() {
		return textArea.getText();
	}

	private void abrir(String conteudo) {
		if (!Util.estaVazio(conteudo)) {
			textArea.setText(conteudo);
			return;
		}

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

	@Override
	protected void destacarEmFormulario() {
		formulario.getFichario().anotacao.destacarEmFormulario(formulario, this);
	}

	@Override
	protected void clonarEmFormulario() {
		formulario.getFichario().anotacao.clonarEmFormulario(formulario, this);
	}

	@Override
	protected void abrirEmFormulario() {
		AnotacaoFormulario.criar(formulario, Constantes.VAZIO);
	}

	@Override
	protected void retornoAoFichario() {
		if (anotacaoFormulario != null) {
			anotacaoFormulario.retornoAoFichario();
		}
	}

	public void setJanela(IJanela janela) {
		toolbar.setJanela(janela);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;

		public void ini(IJanela janela) {
			super.ini(janela, true, true);
			configButtonDestacar(e -> destacarEmFormulario(), e -> abrirEmFormulario(), e -> retornoAoFichario(),
					e -> clonarEmFormulario());
			configAbrirAutoFichario(Constantes.ABRIR_AUTO_FICHARIO_ANOTACAO);
			configBaixarAcao(e -> abrir(null));
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