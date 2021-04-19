package br.com.persist.plugins.ambiente;

import static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO;
import static br.com.persist.componente.BarraButtonEnum.BACKUP;
import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.CLONAR_EM_FORMULARIO;
import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.DESTACAR_EM_FORMULARIO;
import static br.com.persist.componente.BarraButtonEnum.LIMPAR;
import static br.com.persist.componente.BarraButtonEnum.RETORNAR_AO_FICHARIO;
import static br.com.persist.componente.BarraButtonEnum.SALVAR;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.swing.Icon;

import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.componente.SetLista;
import br.com.persist.componente.TextArea;
import br.com.persist.componente.SetLista.Coletor;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;

public class AmbienteContainer extends AbstratoContainer {
	private static final long serialVersionUID = 1L;
	private final TextArea textArea = new TextArea();
	private final Toolbar toolbar = new Toolbar();
	private AmbienteFormulario ambienteFormulario;
	private AmbienteDialogo ambienteDialogo;
	private final Ambiente ambiente;
	private final File fileParent;
	private final File file;

	public AmbienteContainer(Janela janela, Formulario formulario, String conteudo, Ambiente ambiente) {
		super(formulario);
		file = new File(Constantes.AMBIENTES + Constantes.SEPARADOR + ambiente.chave);
		fileParent = new File(Constantes.AMBIENTES);
		this.ambiente = ambiente;
		toolbar.ini(janela);
		montarLayout();
		abrir(conteudo);
	}

	public Ambiente getAmbiente() {
		return ambiente;
	}

	public enum Ambiente {
		DESENVOLVIMENTO("desenv", "label.desenv"), HOLOMOGACAO("homolog", "label.homolog"), TREINAMENTO("treina",
				"label.treina"), PRODUCAO("producao", "label.producao"), RASCUNHO("rascunho",
						"label.rascunho"), LEMBRETE("lembrete", "label.lembrete"), SCRIPTS("scripts",
								"label.scripts"), ESTUDO("estudo",
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

	public AmbienteDialogo getAmbienteDialogo() {
		return ambienteDialogo;
	}

	public void setAmbienteDialogo(AmbienteDialogo ambienteDialogo) {
		this.ambienteDialogo = ambienteDialogo;
		if (ambienteDialogo != null) {
			ambienteFormulario = null;
		}
	}

	public AmbienteFormulario getAmbienteFormulario() {
		return ambienteFormulario;
	}

	public void setAmbienteFormulario(AmbienteFormulario ambienteFormulario) {
		this.ambienteFormulario = ambienteFormulario;
		if (ambienteFormulario != null) {
			ambienteDialogo = null;
		}
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, textArea);
	}

	public String getConteudo() {
		return textArea.getText();
	}

	private void abrir(String conteudo) {
		if (!Util.estaVazio(conteudo)) {
			textArea.setText(conteudo);
			return;
		}
		abrirArquivo(file);
	}

	private void abrirArquivo(File file) {
		toolbar.limparNomeBackup();
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
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, CLONAR_EM_FORMULARIO, ABRIR_EM_FORMULARO,
					BAIXAR, LIMPAR, SALVAR, COPIAR, COLAR, BACKUP);
		}

		@Override
		protected void destacarEmFormulario() {
			if (formulario.excluirPagina(AmbienteContainer.this)) {
				AmbienteFormulario.criar(formulario, AmbienteContainer.this);
			} else if (ambienteDialogo != null) {
				ambienteDialogo.excluirContainer();
				AmbienteFormulario.criar(formulario, AmbienteContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (ambienteFormulario != null) {
				ambienteFormulario.excluirContainer();
				formulario.adicionarPagina(AmbienteContainer.this);
			} else if (ambienteDialogo != null) {
				ambienteDialogo.excluirContainer();
				formulario.adicionarPagina(AmbienteContainer.this);
			}
		}

		@Override
		protected void clonarEmFormulario() {
			if (ambienteDialogo != null) {
				ambienteDialogo.excluirContainer();
			}
			AmbienteFormulario.criar(formulario, getConteudo(), ambiente);
		}

		@Override
		protected void abrirEmFormulario() {
			if (ambienteDialogo != null) {
				ambienteDialogo.excluirContainer();
			}
			AmbienteFormulario.criar(formulario, null, ambiente);
		}

		void formularioVisivel() {
			buttonDestacar.estadoFormulario();
		}

		void paginaVisivel() {
			buttonDestacar.estadoFichario();
		}

		void dialogoVisivel() {
			buttonDestacar.estadoDialogo();
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
			if (Util.confirmaSalvar(AmbienteContainer.this, Constantes.TRES)) {
				salvarArquivo(file);
			}
		}

		private void salvarArquivo(File file) {
			try (PrintWriter pw = new PrintWriter(file, StandardCharsets.UTF_8.name())) {
				pw.print(textArea.getText());
				salvoMensagem();
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
		protected void colar(boolean numeros, boolean letras) {
			Util.getContentTransfered(textArea.getTextAreaInner(), numeros, letras);
		}

		@Override
		protected void criarBackup() {
			if (Util.confirmar(AmbienteContainer.this, "label.confirma_criar_backup")) {
				String nome = Util.gerarNomeBackup(fileParent, ambiente.chave);
				salvarArquivo(new File(fileParent, nome));
			}
		}

		@Override
		protected void abrirBackup() {
			List<String> arquivos = Util.listarNomeBackup(fileParent, ambiente.chave);
			if (arquivos.isEmpty()) {
				Util.mensagem(AmbienteContainer.this, Mensagens.getString("msg.sem_arq_backup"));
				return;
			}
			Coletor coletor = new Coletor();
			SetLista.view(Constantes.AMBIENTES, arquivos, coletor, AmbienteContainer.this, true);
			if (coletor.size() == 1) {
				abrirArquivo(new File(fileParent, coletor.get(0)));
				setNomeBackup(coletor.get(0));
			}
		}
	}

	@Override
	public void adicionadoAoFichario(Fichario fichario) {
		toolbar.paginaVisivel();
	}

	public void formularioVisivel() {
		toolbar.formularioVisivel();
	}

	public void dialogoVisivel() {
		toolbar.dialogoVisivel();
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
		return new AbstratoTitulo() {
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