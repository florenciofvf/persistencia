package br.com.persist.ambiente;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

import br.com.persist.componente.BarraButton;
import br.com.persist.componente.TextArea;
import br.com.persist.container.AbstratoContainer;
import br.com.persist.fichario.Fichario;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Util;

public class AmbienteContainer extends AbstratoContainer implements Fichario.IFicharioSalvar {
	private static final long serialVersionUID = 1L;
	private static final String PAINEL_AMBIENTE = "PAINEL AMBIENTE";
	private final TextArea textArea = new TextArea();
	private final Toolbar toolbar = new Toolbar();
	private AmbienteFormulario ambienteFormulario;
	private final Ambiente ambiente;
	private final File file;

	public AmbienteContainer(IJanela janela, Formulario formulario, String conteudo, Ambiente ambiente) {
		super(formulario);
		file = new File("ambientes/" + ambiente.chave);
		this.ambiente = ambiente;
		toolbar.ini(janela);
		montarLayout();
		abrir(conteudo);
	}

	public Ambiente getAmbiente() {
		return ambiente;
	}

	public enum Ambiente {
		DESENVOLVIMENTO("desenv",
				Mensagens.getString("label.desenv")), TREINAMENTO1("treina1", Mensagens.getString(
						"label.treina1")), TREINAMENTO2("treina2", Mensagens.getString("label.treina2")), TREINAMENTO3(
								"treina3", Mensagens.getString("label.treina3")), HOLOMOGACAO("homolog",
										Mensagens.getString("label.homolog")), PRODUCAO("producao",
												Mensagens.getString("label.producao")), RASCUNHO("rascunho",
														Mensagens.getString("label.rascunho")), ESTUDO("estudo",
																Mensagens.getString("label.estudo")), TESTE("teste",
																		Mensagens.getString("label.teste")), BUGS(
																				"bugs",
																				Mensagens.getString("label.bugs"));
		private final String chaveLabelMin;
		private final String chaveLabel;
		private final String descricao;
		private final String chave;

		private Ambiente(String chave, String descricao) {
			chaveLabelMin = "label." + chave + "_min";
			chaveLabel = "label." + chave;
			this.chave = chave;
			this.descricao = descricao;
		}

		public String getChave() {
			return chave;
		}

		public String getDescricao() {
			return descricao;
		}

		public String getChaveLabelMin() {
			return chaveLabelMin;
		}

		public String getChaveLabel() {
			return chaveLabel;
		}

		public static Ambiente get(String nome) {
			for (Ambiente a : values()) {
				if (a.chave.equals(nome)) {
					return a;
				}
			}

			throw new IllegalArgumentException();
		}
	}

	public AmbienteFormulario getAmbienteFormulario() {
		return ambienteFormulario;
	}

	public void setAmbienteFormulario(AmbienteFormulario ambienteFormulario) {
		this.ambienteFormulario = ambienteFormulario;
	}

	@Override
	public File getFileSalvarAberto() {
		return new File(Constantes.III + getClass().getName() + "_" + ambiente.chave);
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
					textArea.append(linha + Constantes.QL2);
					linha = br.readLine();
				}
			} catch (Exception ex) {
				Util.stackTraceAndMessage(PAINEL_AMBIENTE, ex, AmbienteContainer.this);
			}
		}
	}

	@Override
	protected void destacarEmFormulario() {
		formulario.getFichario().getAmbientes().destacarEmFormulario(formulario, this);
	}

	@Override
	protected void clonarEmFormulario() {
		formulario.getFichario().getAmbientes().clonarEmFormulario(formulario, this);
	}

	@Override
	protected void abrirEmFormulario() {
		AmbienteFormulario.criar(formulario, Constantes.VAZIO, ambiente);
	}

	@Override
	protected void retornoAoFichario() {
		if (ambienteFormulario != null) {
			ambienteFormulario.retornoAoFichario();
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
			if (!Util.confirmaSalvar(AmbienteContainer.this, Constantes.TRES)) {
				return;
			}

			try (PrintWriter pw = new PrintWriter(file, StandardCharsets.UTF_8.name())) {
				pw.print(textArea.getText());
			} catch (Exception ex) {
				Util.stackTraceAndMessage(PAINEL_AMBIENTE, ex, AmbienteContainer.this);
			}
		}
	}
}