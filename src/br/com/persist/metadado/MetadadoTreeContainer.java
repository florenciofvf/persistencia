package br.com.persist.metadado;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.sql.Connection;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.KeyStroke;

import br.com.persist.banco.Conexao;
import br.com.persist.banco.ConexaoProvedor;
import br.com.persist.banco.Persistencia;
import br.com.persist.comp.BarraButton;
import br.com.persist.comp.ScrollPane;
import br.com.persist.comp.TextField;
import br.com.persist.container.AbstratoContainer;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Fichario.InfoConexao;
import br.com.persist.icone.Icones;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Action;
import br.com.persist.util.ButtonPopup;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Util;

public class MetadadoTreeContainer extends AbstratoContainer
		implements MetadadoTreeListener, Fichario.IFicharioSalvar, Fichario.IFicharioConexao {
	private static final long serialVersionUID = 1L;
	private MetadadoTree metadadoTree = new MetadadoTree();
	private final Toolbar toolbar = new Toolbar();
	private MetadadoTreeFormulario metadadoFormulario;
	private final JComboBox<Conexao> cmbConexao;

	public MetadadoTreeContainer(IJanela janela, Formulario formulario, ConexaoProvedor provedor, Conexao padrao) {
		super(formulario);
		cmbConexao = Util.criarComboConexao(provedor, padrao);
		toolbar.ini(janela);
		montarLayout();
		config();
	}

	public MetadadoTreeFormulario getMetadadoTreeFormulario() {
		return metadadoFormulario;
	}

	public void setMetadadoTreeFormulario(MetadadoTreeFormulario metadadoTreeFormulario) {
		this.metadadoFormulario = metadadoTreeFormulario;
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

	@Override
	public File getFileSalvarAberto() {
		return new File(Constantes.III + getClass().getName());
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, new ScrollPane(metadadoTree));
		add(BorderLayout.NORTH, toolbar);
		metadadoTree.adicionarOuvinte(this);
	}

	@Override
	protected void destacarEmFormulario() {
		formulario.getFichario().getMetadadoTree().destacarEmFormulario(formulario, this);
	}

	@Override
	protected void clonarEmFormulario() {
		formulario.getFichario().getMetadadoTree().clonarEmFormulario(formulario, this);
	}

	@Override
	protected void abrirEmFormulario() {
		MetadadoTreeFormulario.criar(formulario, formulario, getConexaoPadrao());
	}

	@Override
	protected void retornoAoFichario() {
		if (metadadoFormulario != null) {
			metadadoFormulario.retornoAoFichario();
		}
	}

	public void setJanela(IJanela janela) {
		toolbar.setJanela(janela);
	}

	private class Toolbar extends BarraButton implements ActionListener {
		private static final long serialVersionUID = 1L;
		private Action atualizarAcao = Action.actionIconAtualizar();
		private final TextField txtMetadado = new TextField(35);

		public void ini(IJanela janela) {
			super.ini(janela, false, false);
			configButtonDestacar(e -> destacarEmFormulario(), e -> abrirEmFormulario(), e -> retornoAoFichario(),
					e -> clonarEmFormulario(), false);
			configAbrirAutoFichario(Constantes.ABRIR_AUTO_FICHARIO_METADADO);

			addButton(atualizarAcao);
			add(true, cmbConexao);
			add(true, new ButtonInfo());
			add(txtMetadado);

			eventos();
		}

		private void eventos() {
			atualizarAcao.setActionListener(e -> atualizar());
			txtMetadado.addActionListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (Util.estaVazio(txtMetadado.getText())) {
				return;
			}

			metadadoTree.selecionar(txtMetadado.getText().trim());
		}

		class ButtonInfo extends ButtonPopup {
			private static final long serialVersionUID = 1L;
			private Action queExportamAcao = Action.actionMenu("label.tabelas_que_exportam", null);
			private Action naoExportamAcao = Action.actionMenu("label.tabelas_nao_exportam", null);
			private Action ordemExportAcao = Action.actionMenu("label.ordenado_exportacao", null);
			private Action ordemImportAcao = Action.actionMenu("label.ordenado_importacao", null);
			private Action pksMultiplaAcao = Action.actionMenu("label.pks_multiplas", null);
			private Action pksAusentesAcao = Action.actionMenu("label.pks_ausente", null);

			ButtonInfo() {
				super("label.funcoes", Icones.INFO);

				addMenuItem(pksMultiplaAcao);
				addMenuItem(true, naoExportamAcao);
				addMenuItem(true, queExportamAcao);
				addMenuItem(true, pksAusentesAcao);
				addMenuItem(true, ordemExportAcao);
				addMenuItem(true, ordemImportAcao);

				queExportamAcao
						.setActionListener(e -> Util.mensagem(MetadadoTreeContainer.this, metadadoTree.queExportam()));
				naoExportamAcao
						.setActionListener(e -> Util.mensagem(MetadadoTreeContainer.this, metadadoTree.naoExportam()));
				pksMultiplaAcao
						.setActionListener(e -> Util.mensagem(MetadadoTreeContainer.this, metadadoTree.pksMultipla()));
				pksAusentesAcao
						.setActionListener(e -> Util.mensagem(MetadadoTreeContainer.this, metadadoTree.pksAusente()));
				ordemImportAcao.setActionListener(
						e -> Util.mensagem(MetadadoTreeContainer.this, metadadoTree.ordemExpImp(false)));
				ordemExportAcao.setActionListener(
						e -> Util.mensagem(MetadadoTreeContainer.this, metadadoTree.ordemExpImp(true)));
			}
		}
	}

	public void atualizar() {
		Conexao conexao = (Conexao) cmbConexao.getSelectedItem();

		if (conexao == null) {
			return;
		}

		try {
			Connection conn = Conexao.getConnection(conexao);
			List<Metadado> lista = Persistencia.listarMetadados(conn, conexao);
			Metadado raiz = new Metadado(Mensagens.getString(Constantes.LABEL_METADADOS) + " - " + lista.size());
			raiz.setEhRaiz(true);

			for (Metadado metadado : lista) {
				metadado.setTabela(true);

				List<Metadado> fks = Persistencia.listarImportados(conn, conexao, metadado);
				List<Metadado> eks = Persistencia.listarExportados(conn, conexao, metadado);
				List<Metadado> pks = Persistencia.listarPrimarias(conn, conexao, metadado);

				criarAtributoMetadado(metadado, pks, Constantes.PKS, Constantes.PK, ' ');
				criarAtributoMetadado(metadado, fks, Constantes.FKS, Constantes.FK, 'I');
				criarAtributoMetadado(metadado, eks, Constantes.EKS, Constantes.EK, 'E');

				raiz.add(metadado);
			}

			raiz.montarOrdenacoes();
			metadadoTree.setModel(new MetadadoModelo(raiz));
		} catch (Exception ex) {
			Util.stackTraceAndMessage("META-DADOS", ex, this);
		}
	}

	private void criarAtributoMetadado(Metadado metadado, List<Metadado> listaMetadado, String plural, String singular,
			char chave) {
		if (listaMetadado.isEmpty()) {
			return;
		}

		Metadado titulo = new Metadado(listaMetadado.size() > 1 ? plural : singular);
		metadado.add(titulo);

		for (Metadado meta : listaMetadado) {
			titulo.add(meta);

			if (chave == 'E') {
				metadado.setTotalExportados(metadado.getTotalExportados() + 1);
			} else if (chave == 'I') {
				metadado.setTotalImportados(metadado.getTotalImportados() + 1);
			}
		}
	}

	@Override
	public void abrirExportacaoFormArquivo(MetadadoTree metadados, boolean circular) {
		Metadado metadado = metadados.getObjetoSelecionado();

		if (metadado != null) {
			formulario.getConteiner().abrirExportacaoMetadado(metadado, circular);
		}
	}

	@Override
	public void exportarFormArquivo(MetadadoTree metadados) {
		Metadado metadado = metadados.getObjetoSelecionado();

		if (metadado != null) {
			formulario.getConteiner().exportarMetadadoRaiz(metadado);
		}
	}

	@Override
	public void abrirExportacaoFichArquivo(MetadadoTree metadados, boolean circular) {
		Metadado metadado = metadados.getObjetoSelecionado();

		if (metadado != null) {
			formulario.getFichario().getConteiner().abrirExportacaoMetadado(formulario, metadado, circular);
		}
	}

	@Override
	public void exportarFichArquivo(MetadadoTree metadados) {
		Metadado metadado = metadados.getObjetoSelecionado();

		if (metadado != null) {
			formulario.getFichario().getConteiner().exportarMetadadoRaiz(formulario, metadado);
		}
	}

	@Override
	public void abrirImportacaoFormArquivo(MetadadoTree metadados, boolean circular) {
		Metadado metadado = metadados.getObjetoSelecionado();

		if (metadado != null) {
			formulario.getConteiner().abrirImportacaoMetadado(metadado, circular);
		}
	}

	@Override
	public void abrirImportacaoFichArquivo(MetadadoTree metadados, boolean circular) {
		Metadado metadado = metadados.getObjetoSelecionado();

		if (metadado != null) {
			formulario.getFichario().getConteiner().abrirImportacaoMetadado(formulario, metadado, circular);
		}
	}
}