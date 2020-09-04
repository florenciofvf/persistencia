package br.com.persist.plugins.ambiente;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import static br.com.persist.componente.BarraButtonEnum.*;

import javax.swing.Icon;

import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.componente.TextArea;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.Icones;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Util;

public class AmbienteContainer extends AbstratoContainer {
	private static final long serialVersionUID = 1L;
	private final TextArea textArea = new TextArea();
	private final Toolbar toolbar = new Toolbar();
	private AmbienteFormulario ambienteFormulario;
	private final Ambiente ambiente;
	private final File file;

	public AmbienteContainer(Janela janela, Formulario formulario, String conteudo, Ambiente ambiente) {
		super(formulario);
		file = new File("ambientes" + Constantes.SEPARADOR + ambiente.chave);
		this.ambiente = ambiente;
		toolbar.ini(janela);
		montarLayout();
		abrir(conteudo);
	}

	public Ambiente getAmbiente() {
		return ambiente;
	}

	public enum Ambiente {
		DESENVOLVIMENTO("desenv", "label.desenv"), TREINAMENTO1("treina1", "label.treina1"), TREINAMENTO2("treina2",
				"label.treina2"), TREINAMENTO3("treina3", "label.treina3"), HOLOMOGACAO("homolog",
						"label.homolog"), PRODUCAO("producao", "label.producao"), RASCUNHO("rascunho",
								"label.rascunho"), ESTUDO("estudo",
										"label.estudo"), TESTE("teste", "label.teste"), BUGS("bugs", "label.bugs");

		private final String chaveTitulo;
		private final String tituloMin;
		private final String descricao;
		private final String titulo;
		private final String chave;

		private Ambiente(String chave, String desc) {
			chaveTitulo = "label." + chave;
			tituloMin = Mensagens.getString(chaveTitulo + "_min");
			this.descricao = Mensagens.getString(desc);
			titulo = Mensagens.getString(chaveTitulo);
			this.chave = chave;
		}

		public String getChaveTitulo() {
			return chaveTitulo;
		}

		public String getDescricao() {
			return descricao;
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
				Util.stackTraceAndMessage(Constantes.PAINEL_AMBIENTE, ex, AmbienteContainer.this);
			}
		}
	}

	@Override
	public void setJanela(Janela janela) {
		toolbar.setJanela(janela);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;

		public void ini(Janela janela) {
			super.ini(janela, DESTAC_EM_FORM, RETORNAR_AO_FICHARIO, ABRIR_EM_F0RM, CLONAR_EM_FORM, LIMPAR, SALVAR,
					COPIAR, COLAR, BAIXAR);
		}

		@Override
		protected void destacarEmFormulario() {
			if (formulario.excluirPagina(AmbienteContainer.this)) {
				buttonDestacar.habilitar(true);
				AmbienteFormulario.criar(formulario, AmbienteContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (ambienteFormulario != null) {
				ambienteFormulario.retornoAoFichario();
				formulario.adicionarPagina(AmbienteContainer.this);
			}
		}

		@Override
		protected void clonarEmFormulario() {
			AmbienteFormulario.criar(formulario, getConteudo(), ambiente);
		}

		@Override
		protected void abrirEmFormulario() {
			AmbienteFormulario.criar(formulario, null, ambiente);
		}

		void adicionadoNoFichario() {
			buttonDestacar.habilitar(true, false, true, true);
		}

		@Override
		protected void baixar() {
			abrir(null);
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
				Util.stackTraceAndMessage(Constantes.PAINEL_AMBIENTE, ex, AmbienteContainer.this);
			}
		}

		@Override
		protected void copiar() {
			String string = Util.getString(textArea.getTextAreaInner());
			Util.setContentTransfered(string);
			copiarMensagem(string);
			textArea.requestFocus();
		}

		@Override
		protected void colar() {
			Util.getContentTransfered(textArea.getTextAreaInner());
		}
	}

	@Override
	public void adicionadoAoFichario(Fichario fichario) {
		toolbar.adicionadoNoFichario();
	}

	@Override
	public String getStringPersistencia() {
		return ambiente.chave;
	}

	@Override
	public Class<?> getClasseFabrica() {
		return AmbienteFabrica.class;
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public Titulo getTitulo() {
		return new Titulo() {
			@Override
			public String getTituloMin() {
				return ambiente.tituloMin;
			}

			@Override
			public String getTitulo() {
				return ambiente.titulo;
			}

			@Override
			public String getHint() {
				return ambiente.descricao;
			}

			@Override
			public Icon getIcone() {
				return Icones.BOLA_VERDE;
			}
		};
	}
}