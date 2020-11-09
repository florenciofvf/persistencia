package br.com.persist.plugins.update;

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
import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.KeyStroke;

import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.componente.Label;
import br.com.persist.componente.TextArea;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;
import br.com.persist.plugins.conexao.Conexao;
import br.com.persist.plugins.conexao.ConexaoEvento;
import br.com.persist.plugins.conexao.ConexaoProvedor;
import br.com.persist.plugins.persistencia.Persistencia;

public class UpdateContainer extends AbstratoContainer {
	private static final long serialVersionUID = 1L;
	private final TextArea textArea = new TextArea();
	private final Toolbar toolbar = new Toolbar();
	private final Label labelStatus = new Label();
	private final JComboBox<Conexao> comboConexao;
	private UpdateFormulario updateFormulario;
	private UpdateDialogo updateDialogo;
	private final File file;

	public UpdateContainer(Janela janela, Formulario formulario, Conexao conexao, String conteudo) {
		super(formulario);
		file = new File(Constantes.ATUALIZACOES + Constantes.SEPARADOR + Constantes.ATUALIZACOES);
		textArea.setText(conteudo == null ? Constantes.VAZIO : conteudo);
		comboConexao = ConexaoProvedor.criarComboConexao(conexao);
		toolbar.ini(janela);
		montarLayout();
		configurar();
		abrir(conteudo);
	}

	public UpdateDialogo getUpdateDialogo() {
		return updateDialogo;
	}

	public void setUpdateDialogo(UpdateDialogo updateDialogo) {
		this.updateDialogo = updateDialogo;
		if (updateDialogo != null) {
			updateFormulario = null;
		}
	}

	public UpdateFormulario getUpdateFormulario() {
		return updateFormulario;
	}

	public void setUpdateFormulario(UpdateFormulario updateFormulario) {
		this.updateFormulario = updateFormulario;
		if (updateFormulario != null) {
			updateDialogo = null;
		}
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, textArea);
		add(BorderLayout.SOUTH, labelStatus);
		labelStatus.setForeground(Color.BLUE);
	}

	private void configurar() {
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), Constantes.EXEC);
		getActionMap().put(Constantes.EXEC, toolbar.atualizarAcao);
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
				Util.stackTraceAndMessage(Constantes.PAINEL_UPDATE, ex, UpdateContainer.this);
			}
		}
	}

	@Override
	public void setJanela(Janela janela) {
		toolbar.setJanela(janela);
	}

	@Override
	public void processar(Formulario formulario, Map<String, Object> args) {
		checarSelecionarConexao(formulario, args);
	}

	private void checarSelecionarConexao(Formulario formulario, Map<String, Object> args) {
		Conexao conexao = (Conexao) args.get(ConexaoEvento.SELECIONAR_CONEXAO);
		if (conexao != null) {
			comboConexao.setSelectedItem(conexao);
		}
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		private Action atualizarAcao = Action.actionIconUpdate();

		protected void ini(Janela janela) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, CLONAR_EM_FORMULARIO, ABRIR_EM_FORMULARO,
					BAIXAR, LIMPAR, SALVAR, COPIAR, COLAR);
			addButton(atualizarAcao);
			add(true, comboConexao);
			eventos();
		}

		private void eventos() {
			atualizarAcao.setActionListener(e -> atualizar());
		}

		@Override
		protected void destacarEmFormulario() {
			if (formulario.excluirPagina(UpdateContainer.this)) {
				UpdateFormulario.criar(formulario, UpdateContainer.this);
			} else if (updateDialogo != null) {
				updateDialogo.excluirContainer();
				UpdateFormulario.criar(formulario, UpdateContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (updateFormulario != null) {
				updateFormulario.excluirContainer();
				formulario.adicionarPagina(UpdateContainer.this);
			} else if (updateDialogo != null) {
				updateDialogo.excluirContainer();
				formulario.adicionarPagina(UpdateContainer.this);
			}
		}

		@Override
		protected void clonarEmFormulario() {
			if (updateDialogo != null) {
				updateDialogo.excluirContainer();
			}
			UpdateFormulario.criar(formulario, (Conexao) comboConexao.getSelectedItem(), getConteudo());
		}

		@Override
		protected void abrirEmFormulario() {
			if (updateDialogo != null) {
				updateDialogo.excluirContainer();
			}
			UpdateFormulario.criar(formulario, null, null);
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
			if (Util.confirmaSalvar(UpdateContainer.this, Constantes.TRES)) {
				salvarArquivo();
			}
		}

		private void salvarArquivo() {
			try (PrintWriter pw = new PrintWriter(file, StandardCharsets.UTF_8.name())) {
				pw.print(textArea.getText());
			} catch (Exception ex) {
				Util.stackTraceAndMessage(Constantes.PAINEL_UPDATE, ex, UpdateContainer.this);
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

		@Override
		public void atualizar() {
			if (!Util.estaVazio(textArea.getText())) {
				Conexao conexao = (Conexao) comboConexao.getSelectedItem();
				if (conexao != null) {
					String instrucao = Util.getString(textArea.getTextAreaInner());
					atualizar(conexao, instrucao);
				}
			}
		}

		private void atualizar(Conexao conexao, String instrucao) {
			try {
				Connection conn = ConexaoProvedor.getConnection(conexao);
				int atualizados = Persistencia.executar(conn, instrucao);
				labelStatus.setText("ATUALIZADOS [" + atualizados + "]");
				textArea.requestFocus();
			} catch (Exception ex) {
				labelStatus.limpar();
				Util.stackTraceAndMessage(Constantes.PAINEL_UPDATE, ex, this);
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
		return Constantes.VAZIO;
	}

	@Override
	public Class<?> getClasseFabrica() {
		return UpdateFabrica.class;
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
				return Mensagens.getString(Constantes.LABEL_ATUALIZAR_MIN);
			}

			@Override
			public String getTitulo() {
				return Mensagens.getString(Constantes.LABEL_ATUALIZAR);
			}

			@Override
			public String getHint() {
				return Mensagens.getString(Constantes.LABEL_ATUALIZAR);
			}

			@Override
			public Icon getIcone() {
				return Icones.UPDATE;
			}
		};
	}
}