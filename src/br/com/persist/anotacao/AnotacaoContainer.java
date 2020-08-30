package br.com.persist.anotacao;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import javax.swing.Icon;

import br.com.persist.componente.BarraButton;
import br.com.persist.componente.TextArea;
import br.com.persist.container.AbstratoContainer;
import br.com.persist.icone.Icones;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Util;

public class AnotacaoContainer extends AbstratoContainer {
	private static final long serialVersionUID = 1L;
	private final TextArea textArea = new TextArea();
	private final Toolbar toolbar = new Toolbar();
	private AnotacaoFormulario anotacaoFormulario;
	private final File file;

	public AnotacaoContainer(IJanela janela, Formulario formulario, String conteudo) {
		super(formulario);
		file = new File("anotacoes" + Constantes.SEPARADOR + "anotacoes");
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
			try (BufferedReader br = new BufferedReader(
					new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
				String linha = br.readLine();

				while (linha != null) {
					textArea.append(linha + Constantes.QL);
					linha = br.readLine();
				}
			} catch (Exception ex) {
				Util.stackTraceAndMessage(Constantes.PAINEL_ANOTACAO, ex, AnotacaoContainer.this);
			}
		}
	}

	@Override
	public void setJanela(IJanela janela) {
		toolbar.setJanela(janela);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;

		public void ini(IJanela janela) {
			super.ini(janela, true, true);
			configButtonDestacar(e -> destacarEmFormulario(), e -> abrirEmFormulario(), e -> retornoAoFichario(),
					e -> clonarEmFormulario());
			configBaixarAcao(e -> abrir(null));
			configCopiar1Acao(true);
		}

		@Override
		protected void copiar1() {
			String string = Util.getString(textArea.getTextAreaInner());
			Util.setContentTransfered(string);
			copiar1Mensagem(string);
			textArea.requestFocus();
		}

		@Override
		protected void colar1() {
			Util.getContentTransfered(textArea.getTextAreaInner());
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

			try (PrintWriter pw = new PrintWriter(file, StandardCharsets.UTF_8.name())) {
				pw.print(textArea.getText());
			} catch (Exception ex) {
				Util.stackTraceAndMessage(Constantes.PAINEL_ANOTACAO, ex, AnotacaoContainer.this);
			}
		}
	}

	@Override
	protected void destacarEmFormulario() {
		if (formulario.excluirFicharioAba(this)) {
			AnotacaoFormulario.criar(formulario, this);
		}
	}

	@Override
	protected void clonarEmFormulario() {
		if (formulario.excluirFicharioAba(this)) {
			AnotacaoFormulario.criar(formulario, getConteudo());
		}
	}

	@Override
	protected void abrirEmFormulario() {
		AnotacaoFormulario.criar(formulario, Constantes.VAZIO);
	}

	@Override
	protected void retornoAoFichario() {
		if (anotacaoFormulario != null) {
			anotacaoFormulario.retornoAoFichario();
			formulario.adicionarFicharioAba(this);
		}
	}

	@Override
	public File getFileSalvarAberto() {
		return new File(getClasseFabricaEContainerDetalhe());
	}

	@Override
	public String getClasseFabricaEContainerDetalhe() {
		return classeFabricaEContainer(AnotacaoFabrica.class, AnotacaoContainer.class);
	}

	@Override
	public String getChaveTituloMin() {
		return Constantes.LABEL_ANOTACOES_MIN;
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public String getChaveTitulo() {
		return Constantes.LABEL_ANOTACOES;
	}

	@Override
	public String getHintTitulo() {
		return Mensagens.getString(Constantes.LABEL_ANOTACOES);
	}

	@Override
	public Icon getIcone() {
		return Icones.PANEL4;
	}
}