package br.com.persist.plugins.anotacao;

import static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO;
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

import javax.swing.Icon;

import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.componente.TextArea;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;

public class AnotacaoContainer extends AbstratoContainer {
	private static final long serialVersionUID = 1L;
	private final TextArea textArea = new TextArea();
	private final Toolbar toolbar = new Toolbar();
	private AnotacaoFormulario anotacaoFormulario;
	private AnotacaoDialogo anotacaoDialogo;
	private final File file;

	public AnotacaoContainer(Janela janela, Formulario formulario, String conteudo) {
		super(formulario);
		file = new File("anotacoes" + Constantes.SEPARADOR + "anotacoes");
		toolbar.ini(janela);
		montarLayout();
		abrir(conteudo);
	}

	public AnotacaoDialogo getAnotacaoDialogo() {
		return anotacaoDialogo;
	}

	public void setAnotacaoDialogo(AnotacaoDialogo anotacaoDialogo) {
		this.anotacaoDialogo = anotacaoDialogo;
		if (anotacaoDialogo != null) {
			anotacaoFormulario = null;
		}
	}

	public AnotacaoFormulario getAnotacaoFormulario() {
		return anotacaoFormulario;
	}

	public void setAnotacaoFormulario(AnotacaoFormulario anotacaoFormulario) {
		this.anotacaoFormulario = anotacaoFormulario;
		if (anotacaoFormulario != null) {
			anotacaoDialogo = null;
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
		textArea.limpar();
		abrirArquivo();
	}

	private void abrirArquivo() {
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
	public void setJanela(Janela janela) {
		toolbar.setJanela(janela);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;

		public void ini(Janela janela) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, CLONAR_EM_FORMULARIO, ABRIR_EM_FORMULARO,
					BAIXAR, LIMPAR, SALVAR, COPIAR, COLAR);
		}

		@Override
		protected void destacarEmFormulario() {
			if (formulario.excluirPagina(AnotacaoContainer.this)) {
				AnotacaoFormulario.criar(formulario, AnotacaoContainer.this);
			} else if (anotacaoDialogo != null) {
				anotacaoDialogo.excluirContainer();
				AnotacaoFormulario.criar(formulario, AnotacaoContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (anotacaoFormulario != null) {
				anotacaoFormulario.excluirContainer();
				formulario.adicionarPagina(AnotacaoContainer.this);
			} else if (anotacaoDialogo != null) {
				anotacaoDialogo.excluirContainer();
				formulario.adicionarPagina(AnotacaoContainer.this);
			}
		}

		@Override
		protected void clonarEmFormulario() {
			if (anotacaoDialogo != null) {
				anotacaoDialogo.excluirContainer();
			}
			AnotacaoFormulario.criar(formulario, getConteudo());
		}

		@Override
		protected void abrirEmFormulario() {
			if (anotacaoDialogo != null) {
				anotacaoDialogo.excluirContainer();
			}
			AnotacaoFormulario.criar(formulario, Constantes.VAZIO);
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
			if (Util.confirmaSalvar(AnotacaoContainer.this, Constantes.TRES)) {
				salvarArquivo();
			}
		}

		private void salvarArquivo() {
			try (PrintWriter pw = new PrintWriter(file, StandardCharsets.UTF_8.name())) {
				pw.print(textArea.getText());
			} catch (Exception ex) {
				Util.stackTraceAndMessage(Constantes.PAINEL_ANOTACAO, ex, AnotacaoContainer.this);
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
		return Constantes.VAZIO;
	}

	@Override
	public Class<?> getClasseFabrica() {
		return AnotacaoFabrica.class;
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
				return Mensagens.getString(Constantes.LABEL_ANOTACOES_MIN);
			}

			@Override
			public String getTitulo() {
				return Mensagens.getString(Constantes.LABEL_ANOTACOES);
			}

			@Override
			public String getHint() {
				return Mensagens.getString(Constantes.LABEL_ANOTACOES);
			}

			@Override
			public Icon getIcone() {
				return Icones.PANEL4;
			}
		};
	}
}