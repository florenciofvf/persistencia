package br.com.persist.plugins.metadado;

import static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO;
import static br.com.persist.componente.BarraButtonEnum.ATUALIZAR;
import static br.com.persist.componente.BarraButtonEnum.DESTACAR_EM_FORMULARIO;
import static br.com.persist.componente.BarraButtonEnum.RETORNAR_AO_FICHARIO;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import br.com.persist.componente.ButtonPopup;
import br.com.persist.componente.Janela;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.TextField;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;
import br.com.persist.plugins.conexao.Conexao;
import br.com.persist.plugins.conexao.ConexaoEvento;
import br.com.persist.plugins.conexao.ConexaoProvedor;
import br.com.persist.plugins.persistencia.Exportado;
import br.com.persist.plugins.persistencia.Importado;
import br.com.persist.plugins.persistencia.Persistencia;

public class MetadadoContainer extends AbstratoContainer implements MetadadoTreeListener {
	private static final long serialVersionUID = 1L;
	private final MetadadoTree metadadoTree = new MetadadoTree();
	private MetadadoFormulario metadadoFormulario;
	private final Toolbar toolbar = new Toolbar();
	private final JComboBox<Conexao> comboConexao;

	public MetadadoContainer(Janela janela, Formulario formulario, Conexao conexao) {
		super(formulario);
		comboConexao = ConexaoProvedor.criarComboConexao(conexao);
		toolbar.ini(janela);
		montarLayout();
		configurar();
	}

	public MetadadoFormulario getMetadadoFormulario() {
		return metadadoFormulario;
	}

	public void setMetadadoFormulario(MetadadoFormulario metadadoFormulario) {
		this.metadadoFormulario = metadadoFormulario;
	}

	public Conexao getConexaoPadrao() {
		return (Conexao) comboConexao.getSelectedItem();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, new ScrollPane(metadadoTree));
		add(BorderLayout.NORTH, toolbar);
	}

	private void configurar() {
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), Constantes.EXEC);
		getActionMap().put(Constantes.EXEC, toolbar.getAtualizarAcao());
		metadadoTree.adicionarOuvinte(this);
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
		comboConexao.setSelectedItem(conexao);
	}

	private class Toolbar extends BarraButton implements ActionListener {
		private static final long serialVersionUID = 1L;
		private final TextField txtMetadado = new TextField(35);
		private ButtonInfo buttonInfo = new ButtonInfo();

		public void ini(Janela janela) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, ABRIR_EM_FORMULARO, ATUALIZAR);

			add(buttonInfo);
			add(true, comboConexao);
			add(txtMetadado);

			eventos();
		}

		private void eventos() {
			txtMetadado.addActionListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (Util.estaVazio(txtMetadado.getText())) {
				return;
			}

			metadadoTree.selecionar(txtMetadado.getText().trim());
		}

		@Override
		protected void destacarEmFormulario() {
			if (formulario.excluirPagina(MetadadoContainer.this)) {
				MetadadoFormulario.criar(formulario, MetadadoContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (metadadoFormulario != null) {
				metadadoFormulario.excluirContainer();
				formulario.adicionarPagina(MetadadoContainer.this);
			}
		}

		@Override
		protected void abrirEmFormulario() {
			MetadadoFormulario.criar(formulario, (Conexao) null);
		}

		void formularioVisivel() {
			buttonDestacar.estadoFormulario();
		}

		void paginaVisivel() {
			buttonDestacar.estadoFichario();
		}

		private class ButtonInfo extends ButtonPopup {
			private static final long serialVersionUID = 1L;
			private Action queExportamAcao = Action.actionMenu("label.tabelas_que_exportam", null);
			private Action naoExportamAcao = Action.actionMenu("label.tabelas_nao_exportam", null);
			private Action ordemExportAcao = Action.actionMenu("label.ordenado_exportacao", null);
			private Action ordemImportAcao = Action.actionMenu("label.ordenado_importacao", null);
			private Action pksMultiplaAcao = Action.actionMenu("label.pks_multiplas", null);
			private Action pksAusentesAcao = Action.actionMenu("label.pks_ausente", null);

			private ButtonInfo() {
				super("label.funcoes", Icones.INFO);

				addMenuItem(pksMultiplaAcao);
				addMenuItem(true, naoExportamAcao);
				addMenuItem(true, queExportamAcao);
				addMenuItem(true, pksAusentesAcao);
				addMenuItem(true, ordemExportAcao);
				addMenuItem(true, ordemImportAcao);

				queExportamAcao
						.setActionListener(e -> Util.mensagem(MetadadoContainer.this, metadadoTree.queExportam()));
				naoExportamAcao
						.setActionListener(e -> Util.mensagem(MetadadoContainer.this, metadadoTree.naoExportam()));
				pksMultiplaAcao
						.setActionListener(e -> Util.mensagem(MetadadoContainer.this, metadadoTree.pksMultipla()));
				pksAusentesAcao
						.setActionListener(e -> Util.mensagem(MetadadoContainer.this, metadadoTree.pksAusente()));
				ordemImportAcao
						.setActionListener(e -> Util.mensagem(MetadadoContainer.this, metadadoTree.ordemExpImp(false)));
				ordemExportAcao
						.setActionListener(e -> Util.mensagem(MetadadoContainer.this, metadadoTree.ordemExpImp(true)));
			}
		}

		@Override
		protected void atualizar() {
			Conexao conexao = (Conexao) comboConexao.getSelectedItem();

			if (conexao == null) {
				return;
			}

			try {
				Connection conn = ConexaoProvedor.getConnection(conexao);
				List<Metadado> tabelas = converterLista(Persistencia.listarNomeTabelas(conn, conexao));
				Metadado raiz = new Metadado(Mensagens.getString(Constantes.LABEL_TABELAS) + " - " + tabelas.size());
				raiz.setEhRaiz(true);

				for (Metadado tabela : tabelas) {
					tabela.setTabela(true);

					List<Metadado> camposImportados = converterImportados(
							Persistencia.listarCamposImportados(conn, conexao, tabela.getDescricao()));
					List<Metadado> camposExportados = converterExportados(
							Persistencia.listarCamposExportados(conn, conexao, tabela.getDescricao()));
					List<Metadado> chavesPrimarias = converterLista(
							Persistencia.listarChavesPrimarias(conn, conexao, tabela.getDescricao()));

					adicionarLista(tabela, chavesPrimarias, Constantes.CHAVES_PRIMARIAS, Constantes.CHAVE_PRIMARIA,
							' ');
					adicionarLista(tabela, camposImportados, Constantes.CAMPOS_IMPORTADOS, Constantes.CAMPO_IMPORTADO,
							'I');
					adicionarLista(tabela, camposExportados, Constantes.CAMPOS_EXPORTADOS, Constantes.CAMPO_EXPORTADO,
							'E');

					raiz.add(tabela);
				}

				raiz.montarOrdenacoes();
				metadadoTree.setModel(new MetadadoModelo(raiz));
			} catch (Exception ex) {
				Util.stackTraceAndMessage("META-DADOS", ex, this);
			}
		}

		public List<Metadado> converterImportados(List<Importado> lista) {
			List<Metadado> resposta = new ArrayList<>();
			for (Importado imp : lista) {
				Metadado campo = new Metadado(imp.getCampo());
				resposta.add(campo);

				Metadado ref = new Metadado(imp.getTabelaOrigem() + "(" + imp.getCampoOrigem() + ")");
				campo.add(ref);
			}
			return resposta;
		}

		public List<Metadado> converterExportados(List<Exportado> lista) {
			List<Metadado> resposta = new ArrayList<>();
			for (Exportado imp : lista) {
				Metadado campo = new Metadado(imp.getCampo());
				resposta.add(campo);

				Metadado ref = new Metadado(imp.getTabelaDestino() + "(" + imp.getCampoDestino() + ")");
				campo.add(ref);
			}
			return resposta;
		}

		private List<Metadado> converterLista(List<String> lista) {
			List<Metadado> resposta = new ArrayList<>();
			for (String string : lista) {
				resposta.add(new Metadado(string));
			}
			return resposta;
		}

		private void adicionarLista(Metadado objeto, List<Metadado> filhos, String rotuloPlural, String rotuloSingular,
				char chave) {
			if (filhos.isEmpty()) {
				return;
			}

			Metadado rotulo = new Metadado(filhos.size() > 1 ? rotuloPlural : rotuloSingular);

			for (Metadado obj : filhos) {
				rotulo.add(obj);

				if (chave == 'E') {
					objeto.setTotalExportados(objeto.getTotalExportados() + 1);
				} else if (chave == 'I') {
					objeto.setTotalImportados(objeto.getTotalImportados() + 1);
				}
			}

			objeto.add(rotulo);
		}
	}

	private Map<String, Object> criarArgs(Metadado metadado, String metodo) {
		Map<String, Object> args = new HashMap<>();
		args.put(MetadadoEvento.ABRIR_METADADO, metadado);
		args.put(MetadadoEvento.METODO, metodo);
		return args;
	}

	@Override
	public void abrirExportacaoFormArquivo(MetadadoTree metadados, boolean circular) {
		Metadado metadado = metadados.getObjetoSelecionado();

		if (metadado != null) {
			Map<String, Object> args = criarArgs(metadado, "abrirExportacaoMetadadoForm");
			args.put(MetadadoEvento.CIRCULAR, circular);
			formulario.processar(args);
		}
	}

	@Override
	public void abrirExportacaoFichArquivo(MetadadoTree metadados, boolean circular) {
		Metadado metadado = metadados.getObjetoSelecionado();

		if (metadado != null) {
			Map<String, Object> args = criarArgs(metadado, "abrirExportacaoMetadadoFich");
			args.put(MetadadoEvento.CIRCULAR, circular);
			formulario.processar(args);
		}
	}

	@Override
	public void abrirImportacaoFormArquivo(MetadadoTree metadados, boolean circular) {
		Metadado metadado = metadados.getObjetoSelecionado();

		if (metadado != null) {
			Map<String, Object> args = criarArgs(metadado, "abrirImportacaoMetadadoForm");
			args.put(MetadadoEvento.CIRCULAR, circular);
			formulario.processar(args);
		}
	}

	@Override
	public void abrirImportacaoFichArquivo(MetadadoTree metadados, boolean circular) {
		Metadado metadado = metadados.getObjetoSelecionado();

		if (metadado != null) {
			Map<String, Object> args = criarArgs(metadado, "abrirImportacaoMetadadoFich");
			args.put(MetadadoEvento.CIRCULAR, circular);
			formulario.processar(args);
		}
	}

	@Override
	public void exportarFormArquivo(MetadadoTree metadados) {
		Metadado metadado = metadados.getObjetoSelecionado();

		if (metadado != null) {
			formulario.processar(criarArgs(metadado, "exportarMetadadoRaizForm"));
		}
	}

	@Override
	public void exportarFichArquivo(MetadadoTree metadados) {
		Metadado metadado = metadados.getObjetoSelecionado();

		if (metadado != null) {
			formulario.processar(criarArgs(metadado, "exportarMetadadoRaizFich"));
		}
	}

	@Override
	public void adicionadoAoFichario(Fichario fichario) {
		toolbar.paginaVisivel();
	}

	public void formularioVisivel() {
		toolbar.formularioVisivel();
	}

	@Override
	public String getStringPersistencia() {
		return Constantes.VAZIO;
	}

	@Override
	public Class<?> getClasseFabrica() {
		return MetadadoFabrica.class;
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
				return Mensagens.getString(Constantes.LABEL_METADADOS_MIN);
			}

			@Override
			public String getTitulo() {
				return Mensagens.getString(Constantes.LABEL_METADADOS);
			}

			@Override
			public String getHint() {
				return Mensagens.getString(Constantes.LABEL_METADADOS);
			}

			@Override
			public Icon getIcone() {
				return Icones.CAMPOS;
			}
		};
	}
}