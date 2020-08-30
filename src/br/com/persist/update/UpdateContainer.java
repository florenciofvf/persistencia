package br.com.persist.update;

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

import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Label;
import br.com.persist.componente.TextArea;
import br.com.persist.conexao.Conexao;
import br.com.persist.conexao.ConexaoProvedor;
import br.com.persist.container.AbstratoContainer;
import br.com.persist.fichario.Fichario.InfoConexao;
import br.com.persist.fichario.IFicharioConexao;
import br.com.persist.fichario.IFicharioSalvar;
import br.com.persist.icone.Icones;
import br.com.persist.persistencia.Persistencia;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Action;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Util;

public class UpdateContainer extends AbstratoContainer implements IFicharioSalvar, IFicharioConexao {
	private static final long serialVersionUID = 1L;
	private final TextArea textArea = new TextArea();
	private final Toolbar toolbar = new Toolbar();
	private final Label labelStatus = new Label();
	private final JComboBox<Conexao> cmbConexao;
	private UpdateFormulario updateFormulario;
	private final File file;

	public UpdateContainer(IJanela janela, Formulario formulario, ConexaoProvedor provedor, Conexao padrao,
			String instrucao, Map<String, String> mapaChaveValor) {
		super(formulario);
		file = new File(Constantes.ATUALIZACOES + Constantes.SEPARADOR + Constantes.ATUALIZACOES);
		textArea.setText(Util.substituir(instrucao, mapaChaveValor));
		cmbConexao = Util.criarComboConexao(provedor, padrao);
		toolbar.ini(janela, mapaChaveValor);
		montarLayout();
		config();

		if (mapaChaveValor == null || mapaChaveValor.isEmpty()) {
			abrir();
		}
	}

	public UpdateContainer(IJanela janela, Formulario formulario, ConexaoProvedor provedor, Conexao padrao,
			String instrucao) {
		super(formulario);
		file = new File(Constantes.ATUALIZACOES + Constantes.SEPARADOR + Constantes.ATUALIZACOES);
		textArea.setText(instrucao);
		cmbConexao = Util.criarComboConexao(provedor, padrao);
		toolbar.ini(janela);
		montarLayout();
		config();
	}

	public UpdateFormulario getUpdateFormulario() {
		return updateFormulario;
	}

	public void setUpdateFormulario(UpdateFormulario updateFormulario) {
		this.updateFormulario = updateFormulario;
	}

	private void config() {
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), Constantes.EXEC);
		getActionMap().put(Constantes.EXEC, toolbar.atualizarAcao);
	}

	@Override
	public void selecionarConexao(Conexao conexao) {
		if (conexao != null) {
			cmbConexao.setSelectedItem(conexao);
		}
	}

	@Override
	public InfoConexao getInfoConexao() {
		Conexao conexao = getConexaoPadrao();
		String conexaoAtual = conexao == null ? "null" : conexao.getNome();
		String nomeAba = getFileSalvarAberto().getAbsolutePath();
		return new InfoConexao(conexaoAtual, null, nomeAba);
	}

	public Conexao getConexaoPadrao() {
		return (Conexao) cmbConexao.getSelectedItem();
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, textArea);
		add(BorderLayout.SOUTH, labelStatus);
		labelStatus.setForeground(Color.BLUE);
	}

	public String getConteudo() {
		return textArea.getText();
	}

	public void setConteudo(String conteudo) {
		if (!Util.estaVazio(conteudo)) {
			textArea.setText(conteudo);
		}
	}

	private void abrir() {
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
				Util.stackTraceAndMessage(Constantes.PAINEL_UPDATE, ex, UpdateContainer.this);
			}
		}
	}

	@Override
	public void setJanela(IJanela janela) {
		toolbar.setJanela(janela);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		private Action atualizarAcao = Action.actionIconUpdate();

		protected void ini(IJanela janela, Map<String, String> mapaChaveValor) {
			super.ini(janela, true, mapaChaveValor == null || mapaChaveValor.isEmpty());
			configButtonDestacar(e -> destacarEmFormulario(), e -> abrirEmFormulario(), e -> retornoAoFichario(),
					e -> clonarEmFormulario());
			configAbrirAutoFichario(Constantes.ABRIR_AUTO_FICHARIO_ATUALIZA);
			configBaixarAcao(e -> abrir());
			addButton(atualizarAcao);
			configCopiar1Acao(true);
			add(true, cmbConexao);

			eventos();
		}

		protected void ini(IJanela janela) {
			super.ini(janela, true, false);

			addButton(atualizarAcao);
			configCopiar1Acao(true);
			add(true, cmbConexao);

			eventos();
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
			if (!Util.confirmaSalvar(UpdateContainer.this, Constantes.TRES)) {
				return;
			}

			try (PrintWriter pw = new PrintWriter(file, StandardCharsets.UTF_8.name())) {
				pw.print(textArea.getText());
			} catch (Exception ex) {
				Util.stackTraceAndMessage(Constantes.PAINEL_UPDATE, ex, UpdateContainer.this);
			}
		}

		private void eventos() {
			atualizarAcao.setActionListener(e -> atualizar());
		}
	}

	public void atualizar() {
		if (Util.estaVazio(textArea.getText())) {
			return;
		}

		Conexao conexao = (Conexao) cmbConexao.getSelectedItem();

		if (conexao == null) {
			return;
		}

		String instrucao = Util.getString(textArea.getTextAreaInner());

		try {
			Connection conn = Conexao.getConnection(conexao);
			int atualizados = Persistencia.executar(instrucao, conn);
			labelStatus.setText("ATUALIZADOS [" + atualizados + "]");
			textArea.requestFocus();
		} catch (Exception ex) {
			labelStatus.limpar();
			Util.stackTraceAndMessage(Constantes.PAINEL_UPDATE, ex, this);
		}
	}

	@Override
	protected void destacarEmFormulario() {
		if (formulario.excluirFicharioAba(this)) {
			UpdateFormulario.criar(formulario, this);
		}
	}

	@Override
	protected void clonarEmFormulario() {
		if (formulario.excluirFicharioAba(this)) {
			UpdateFormulario.criar(formulario, formulario, getConexaoPadrao(), getConteudo());
		}
	}

	@Override
	protected void abrirEmFormulario() {
		UpdateFormulario.criar(formulario, formulario, getConexaoPadrao(), null);
	}

	@Override
	protected void retornoAoFichario() {
		if (updateFormulario != null) {
			updateFormulario.retornoAoFichario();
			formulario.adicionarFicharioAba(this);
		}
	}

	@Override
	public File getFileSalvarAberto() {
		return new File(getClasseFabricaEContainerDetalhe());
	}

	@Override
	public String getClasseFabricaEContainerDetalhe() {
		return classeFabricaEContainer(UpdateFabrica.class, UpdateContainer.class);
	}

	@Override
	public String getChaveTituloMin() {
		return Constantes.LABEL_ATUALIZAR_MIN;
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public String getChaveTitulo() {
		return Constantes.LABEL_ATUALIZAR;
	}

	@Override
	public String getHintTitulo() {
		return Mensagens.getString(Constantes.LABEL_ATUALIZAR);
	}

	@Override
	public Icon getIcone() {
		return Icones.UPDATE;
	}
}