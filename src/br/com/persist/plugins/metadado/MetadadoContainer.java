package br.com.persist.plugins.metadado;

import static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO;
import static br.com.persist.componente.BarraButtonEnum.ATUALIZAR;
import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.DESTACAR_EM_FORMULARIO;
import static br.com.persist.componente.BarraButtonEnum.RETORNAR_AO_FICHARIO;
import static br.com.persist.componente.BarraButtonEnum.SALVAR;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JProgressBar;

import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.abstrato.PluginArquivo;
import br.com.persist.assistencia.ArgumentoException;
import br.com.persist.assistencia.AssistenciaException;
import br.com.persist.assistencia.Base64Util;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.ButtonPopup;
import br.com.persist.componente.Janela;
import br.com.persist.componente.ScrollPane;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;
import br.com.persist.marca.XML;
import br.com.persist.marca.XMLException;
import br.com.persist.marca.XMLUtil;
import br.com.persist.plugins.conexao.Conexao;
import br.com.persist.plugins.conexao.ConexaoEvento;
import br.com.persist.plugins.conexao.ConexaoProvedor;
import br.com.persist.plugins.objeto.Desktop;
import br.com.persist.plugins.objeto.Objeto;
import br.com.persist.plugins.objeto.internal.ExternalFormulario;
import br.com.persist.plugins.persistencia.Exportado;
import br.com.persist.plugins.persistencia.Importado;
import br.com.persist.plugins.persistencia.Persistencia;
import br.com.persist.plugins.persistencia.PersistenciaException;

public class MetadadoContainer extends AbstratoContainer implements MetadadoTreeListener, PluginArquivo {
	private static final File file = new File(MetadadoConstantes.METADADOS);
	private final JComboBox<Conexao> comboConexaoDireita;
	private static final long serialVersionUID = 1L;
	private MetadadoFormulario metadadoFormulario;
	private final Toolbar toolbar = new Toolbar();
	private final JComboBox<Conexao> comboConexao;
	private final MetadadoTree metadadoTree;

	public MetadadoContainer(Janela janela, Formulario formulario, Conexao conexao) throws ArgumentoException {
		super(formulario);
		comboConexaoDireita = ConexaoProvedor.criarComboConexao(conexao);
		comboConexao = ConexaoProvedor.criarComboConexao(conexao);
		metadadoTree = new MetadadoTree();
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

	private void montarLayout() {
		add(BorderLayout.CENTER, new ScrollPane(metadadoTree));
		add(BorderLayout.NORTH, toolbar);
	}

	private void configurar() {
		metadadoTree.adicionarOuvinte(this);
	}

	@Override
	public void setJanela(Janela janela) {
		toolbar.setJanela(janela);
	}

	@Override
	public void processar(Formulario formulario, Map<String, Object> args) {
		checarSelecionarConexao(args);
		checarGetMetadado(args);
	}

	private void checarSelecionarConexao(Map<String, Object> args) {
		Conexao conexao = (Conexao) args.get(ConexaoEvento.SELECIONAR_CONEXAO);
		if (conexao != null) {
			comboConexao.setSelectedItem(conexao);
		}
	}

	private void checarGetMetadado(Map<String, Object> args) {
		String nome = (String) args.get(MetadadoEvento.GET_METADADO_OBJETO);
		if (!Util.isEmpty(nome)) {
			Metadado resposta = metadadoTree.getRaiz().getMetadado(nome);
			args.put(MetadadoConstantes.METADADO, resposta);
		}
	}

	private Conexao getConexao() {
		return (Conexao) comboConexao.getSelectedItem();
	}

	private class Toolbar extends BarraButton implements ActionListener {
		private ButtonInfoDiff buttonInfoDiff = new ButtonInfoDiff();
		private final JProgressBar progresso = new JProgressBar();
		private ButtonInfo buttonInfo = new ButtonInfo();
		private static final long serialVersionUID = 1L;
		private transient MetadadoPesquisa pesquisa;

		public void ini(Janela janela) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, ABRIR_EM_FORMULARO, ATUALIZAR, BAIXAR,
					SALVAR);
			add(buttonInfo);
			add(true, comboConexao);
			add(txtPesquisa);
			add(chkPorParte);
			add(label);
			add(progresso);
			add(true, comboConexaoDireita);
			add(buttonInfoDiff);
			txtPesquisa.addActionListener(this);
			progresso.setStringPainted(true);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!Util.isEmpty(txtPesquisa.getText())) {
				pesquisa = getPesquisa(metadadoTree, pesquisa, txtPesquisa.getText(), chkPorParte.isSelected());
				pesquisa.selecionar(label);
			} else {
				label.limpar();
			}
		}

		public MetadadoPesquisa getPesquisa(MetadadoTree metadadoTree, MetadadoPesquisa pesquisa, String string,
				boolean porParte) {
			if (pesquisa == null) {
				return new MetadadoPesquisa(metadadoTree, string, porParte);
			} else if (pesquisa.igual(string, porParte)) {
				return pesquisa;
			}
			return new MetadadoPesquisa(metadadoTree, string, porParte);
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
			try {
				MetadadoFormulario.criar(formulario, (Conexao) null);
			} catch (ArgumentoException ex) {
				Util.mensagem(formulario, ex.getMessage());
			}
		}

		@Override
		public void windowOpenedHandler(Window window) {
			buttonDestacar.estadoFormulario();
		}

		void adicionadoAoFichario() {
			buttonDestacar.estadoFichario();
		}

		private class ButtonInfoDiff extends ButtonPopup {
			private static final String ARQUIVO_ESQUERDO_INEXISTENTE = "ARQUIVO_ESQUERDO_INEXISTENTE";
			private static final String ARQUIVO_DIREITO_INEXISTENTE = "ARQUIVO_DIREITO_INEXISTENTE";
			private Action somenteEsquerdaAcao = acaoMenu("label.existe_somente_esquerda");
			private Action somenteDireitaAcao = acaoMenu("label.existe_somente_direita");
			private static final String CONEXAO_ESQUERDA_NULA = "CONEXAO_ESQUERDA_NULA";
			private static final String CONEXAO_DIREITA_NULA = "CONEXAO_DIREITA_NULA";
			private static final long serialVersionUID = 1L;

			private ButtonInfoDiff() {
				super("label.comparar", Icones.INFO);
				addMenuItem(somenteEsquerdaAcao);
				addMenuItem(somenteDireitaAcao);
				somenteEsquerdaAcao.setActionListener(e -> somenteEsquerda());
				somenteDireitaAcao.setActionListener(e -> somenteDireita());
			}

			private Conexao getConexaoDireita() {
				return (Conexao) comboConexaoDireita.getSelectedItem();
			}

			private void somenteEsquerda() {
				Conexao conexao = getConexao();
				if (conexao == null) {
					Util.mensagem(MetadadoContainer.this, CONEXAO_ESQUERDA_NULA);
					return;
				}
				Conexao conexaoDireita = getConexaoDireita();
				if (conexaoDireita == null) {
					Util.mensagem(MetadadoContainer.this, CONEXAO_DIREITA_NULA);
					return;
				}
				File fileE = new File(file, criarNomeArquivo(conexao));
				if (!fileE.isFile()) {
					Util.mensagem(MetadadoContainer.this, ARQUIVO_ESQUERDO_INEXISTENTE);
					return;
				}
				File fileD = new File(file, criarNomeArquivo(conexaoDireita));
				if (!fileD.isFile()) {
					Util.mensagem(MetadadoContainer.this, ARQUIVO_DIREITO_INEXISTENTE);
					return;
				}
				try {
					Metadado metadadoE = getMetadado(fileE);
					Metadado metadadoD = getMetadado(fileD);
					metadadoE.excluirTodos(metadadoD.getFilhos());
					Util.mensagemFormulario(MetadadoContainer.this, montarString(metadadoE.getFilhos()));
				} catch (Exception ex) {
					Util.stackTraceAndMessage("getMetadado: ", ex, MetadadoContainer.this);
				}
			}

			private Metadado getMetadado(File file) throws XMLException {
				MetadadoHandler handler = new MetadadoHandler();
				XML.processar(file, handler);
				return handler.getRaiz();
			}

			private String montarString(List<Metadado> metadados) {
				StringBuilder sb = new StringBuilder("TOTAL = " + metadados.size() + Constantes.QL);
				for (Metadado item : metadados) {
					sb.append(Constantes.QL);
					sb.append(item.getDescricao());
				}
				return sb.toString();
			}

			private void somenteDireita() {
				Conexao conexao = getConexao();
				if (conexao == null) {
					Util.mensagem(MetadadoContainer.this, CONEXAO_ESQUERDA_NULA);
					return;
				}
				Conexao conexaoDireita = getConexaoDireita();
				if (conexaoDireita == null) {
					Util.mensagem(MetadadoContainer.this, CONEXAO_DIREITA_NULA);
					return;
				}
				File fileE = new File(file, criarNomeArquivo(conexao));
				if (!fileE.isFile()) {
					Util.mensagem(MetadadoContainer.this, ARQUIVO_ESQUERDO_INEXISTENTE);
					return;
				}
				File fileD = new File(file, criarNomeArquivo(conexaoDireita));
				if (!fileD.isFile()) {
					Util.mensagem(MetadadoContainer.this, ARQUIVO_DIREITO_INEXISTENTE);
					return;
				}
				try {
					Metadado metadadoE = getMetadado(fileE);
					Metadado metadadoD = getMetadado(fileD);
					metadadoD.excluirTodos(metadadoE.getFilhos());
					Util.mensagemFormulario(MetadadoContainer.this, montarString(metadadoD.getFilhos()));
				} catch (Exception ex) {
					Util.stackTraceAndMessage("getMetadado: ", ex, MetadadoContainer.this);
				}
			}
		}

		Action acaoMenu(String chave) {
			return Action.acaoMenu(MetadadoMensagens.getString(chave), null);
		}

		private class ButtonInfo extends ButtonPopup {
			private Action pksMultiplaAcaoExport = acaoMenu("label.pks_multiplas_export");
			private Action queExportamAcao = acaoMenu("label.tabelas_que_exportam");
			private Action naoExportamAcao = acaoMenu("label.tabelas_nao_exportam");
			private Action ordemExportAcao = acaoMenu("label.ordenado_exportacao");
			private Action ordemImportAcao = acaoMenu("label.ordenado_importacao");
			private Action localizarCampoAcao = acaoMenu("label.localizar_campo");
			private Action ordemCamposAcao = acaoMenu("label.ordenado_campos");
			private Action pksMultiplaAcao = acaoMenu("label.pks_multiplas");
			private Action pksAusentesAcao = acaoMenu("label.pks_ausente");
			private static final long serialVersionUID = 1L;

			private ButtonInfo() {
				super("label.funcoes", Icones.INFO);
				addMenuItem(pksMultiplaAcaoExport);
				addMenuItem(pksMultiplaAcao);
				addMenuItem(true, naoExportamAcao);
				addMenuItem(true, queExportamAcao);
				addMenuItem(true, pksAusentesAcao);
				addMenuItem(true, ordemExportAcao);
				addMenuItem(true, ordemImportAcao);
				addMenuItem(true, ordemCamposAcao);
				addMenuItem(true, localizarCampoAcao);
				ordemCamposAcao.setActionListener(
						e -> Util.mensagemFormulario(MetadadoContainer.this, metadadoTree.getOrdenadosCampos()));
				pksMultiplaAcaoExport.setActionListener(
						e -> Util.mensagemFormulario(MetadadoContainer.this, metadadoTree.pksMultiplaExport()));
				queExportamAcao.setActionListener(
						e -> Util.mensagemFormulario(MetadadoContainer.this, metadadoTree.queExportam()));
				naoExportamAcao.setActionListener(
						e -> Util.mensagemFormulario(MetadadoContainer.this, metadadoTree.naoExportam()));
				pksMultiplaAcao.setActionListener(
						e -> Util.mensagemFormulario(MetadadoContainer.this, metadadoTree.pksMultipla()));
				pksAusentesAcao.setActionListener(
						e -> Util.mensagemFormulario(MetadadoContainer.this, metadadoTree.pksAusente()));
				ordemImportAcao.setActionListener(e -> Util.mensagemFormulario(MetadadoContainer.this,
						metadadoTree.getOrdenadosExportacaoImportacao(false)));
				ordemExportAcao.setActionListener(e -> Util.mensagemFormulario(MetadadoContainer.this,
						metadadoTree.getOrdenadosExportacaoImportacao(true)));
				localizarCampoAcao.setActionListener(e -> localizarCampo());
			}

			private void localizarCampo() {
				Object resp = Util.getValorInputDialog(MetadadoContainer.this, "label.atencao",
						MetadadoMensagens.getString("label.localizar_campo"), null);
				if (resp != null && !Util.isEmpty(resp.toString())) {
					Map<String, Set<String>> map = metadadoTree.localizarCampo(resp.toString().toUpperCase());
					Util.mensagemFormulario(MetadadoContainer.this, montarString(map));
				}
			}

			private String montarString(Map<String, Set<String>> map) {
				StringBuilder sb = new StringBuilder();
				Iterator<Map.Entry<String, Set<String>>> it = map.entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, Set<String>> entry = it.next();
					append(sb, entry.getKey(), entry.getValue());
				}
				return sb.toString();
			}

			private void append(StringBuilder sb, String tabela, Set<String> campos) {
				sb.append(tabela);
				sb.append(Constantes.QL + aux(tabela.length()));
				for (String campo : campos) {
					sb.append(Constantes.QL + campo);
				}
				sb.append(Constantes.QL);
				sb.append(Constantes.QL);
			}

			private String aux(int i) {
				StringBuilder sb = new StringBuilder();
				while (sb.length() < i) {
					sb.append('-');
				}
				return sb.toString();
			}
		}

		@Override
		protected void baixar() {
			Conexao conexao = getConexao();
			if (conexao != null) {
				abrir(criarNomeArquivo(conexao));
			} else {
				Util.mensagem(MetadadoContainer.this, Constantes.CONEXAO_NULA);
			}
			pesquisa = null;
			label.limpar();
		}

		private String criarNomeArquivo(Conexao conexao) {
			return Base64Util.criarNomeArquivo(conexao.getNome());
		}

		private void abrir(String nome) {
			File f = new File(file, nome);
			if (f.isFile()) {
				try {
					MetadadoHandler handler = new MetadadoHandler();
					XML.processar(f, handler);
					Metadado raiz = handler.getRaiz();
					raiz.setEhRaiz(true);
					metadadoTree.setModel(new MetadadoModelo(raiz));
				} catch (Exception ex) {
					Util.stackTraceAndMessage("ABRIR: " + f.getAbsolutePath(), ex, MetadadoContainer.this);
				}
			}
		}

		@Override
		protected void salvar() {
			Conexao conexao = getConexao();
			if (conexao != null && Util.confirmaSalvar(MetadadoContainer.this, Constantes.UM)) {
				salvarArquivo(criarNomeArquivo(conexao));
			}
		}

		public void salvarArquivo(String nome) {
			File f = new File(file, nome);
			try {
				XMLUtil util = new XMLUtil(f);
				util.prologo();
				util.abrirTag2(MetadadoConstantes.METADADOS);
				metadadoTree.getRaiz().salvar(util);
				util.finalizarTag(MetadadoConstantes.METADADOS);
				util.close();
				salvoMensagem();
			} catch (Exception ex) {
				Util.stackTraceAndMessage("ABRIR: " + f.getAbsolutePath(), ex, MetadadoContainer.this);
			}
		}

		@Override
		protected void atualizar() {
			Conexao conexao = getConexao();
			if (conexao != null) {
				new Thread(() -> atualizar(conexao)).start();
			} else {
				Util.mensagem(MetadadoContainer.this, Constantes.CONEXAO_NULA);
			}
		}

		private void atualizar(Conexao conexao) {
			try {
				Connection conn = ConexaoProvedor.getConnection(conexao);
				List<Metadado> tabelas = converterLista(Persistencia.listarNomeTabelas(conn, conexao));
				progresso.setMaximum(tabelas.size());
				progresso.setValue(0);
				Metadado raiz = new Metadado(Mensagens.getString(Constantes.LABEL_TABELAS), true);
				raiz.setEhRaiz(true);
				atualizar(raiz, tabelas, conexao, conn);
				metadadoTree.setModel(new MetadadoModelo(raiz));
			} catch (Exception ex) {
				Util.stackTraceAndMessage("META-DADOS", ex, this);
			}
		}

		private void atualizar(Metadado raiz, List<Metadado> tabelas, Conexao conexao, Connection conn)
				throws PersistenciaException, ArgumentoException {
			int contador = 0;
			for (Metadado tabela : tabelas) {
				tabela.setTabela(true);
				List<Metadado> campos = converterLista(Persistencia.listarCampos(conn, conexao, tabela.getDescricao()));
				preencher(tabela, campos, Constantes.CAMPOS, Constantes.CAMPO);
				List<Metadado> chavesPrimarias = converterLista(
						Persistencia.listarChavesPrimarias(conn, conexao, tabela.getDescricao()));
				preencher(tabela, chavesPrimarias, MetadadoConstantes.CHAVES_PRIMARIAS,
						MetadadoConstantes.CHAVE_PRIMARIA);
				List<Metadado> camposImportados = converterImportados(
						Persistencia.listarCamposImportados(conn, conexao, tabela.getDescricao()));
				preencher(tabela, camposImportados, MetadadoConstantes.CAMPOS_IMPORTADOS,
						MetadadoConstantes.CAMPO_IMPORTADO);
				List<Metadado> camposExportados = converterExportados(
						Persistencia.listarCamposExportados(conn, conexao, tabela.getDescricao()));
				preencher(tabela, camposExportados, MetadadoConstantes.CAMPOS_EXPORTADOS,
						MetadadoConstantes.CAMPO_EXPORTADO);
				if (!Util.isEmpty(conexao.getConstraint())) {
					List<Metadado> constraints = converterConstraint(
							Persistencia.listarConstraints(conn, conexao, tabela.getDescricao()));
					preencher(tabela, constraints, MetadadoConstantes.CONSTRAINTS, MetadadoConstantes.CONSTRAINT);
				}
				progresso.setValue(++contador);
				raiz.add(tabela);
			}
		}

		public List<Metadado> converterImportados(List<Importado> lista) throws ArgumentoException {
			List<Metadado> resposta = new ArrayList<>();
			for (Importado item : lista) {
				Metadado campo = new Metadado(item.getCampo(), false);
				resposta.add(campo);
				Metadado ref = new Metadado(item.getTabelaOrigem() + "(" + item.getCampoOrigem() + ")", false);
				campo.add(ref);
			}
			return resposta;
		}

		public List<Metadado> converterExportados(List<Exportado> lista) throws ArgumentoException {
			List<Metadado> resposta = new ArrayList<>();
			for (Exportado item : lista) {
				Metadado campo = new Metadado(item.getCampo(), false);
				resposta.add(campo);
				Metadado ref = new Metadado(item.getTabelaDestino() + "(" + item.getCampoDestino() + ")", false);
				campo.add(ref);
			}
			return resposta;
		}

		private List<Metadado> converterLista(List<String> lista) throws ArgumentoException {
			List<Metadado> resposta = new ArrayList<>();
			for (String string : lista) {
				resposta.add(new Metadado(string, false));
			}
			return resposta;
		}

		private List<Metadado> converterConstraint(List<List<String>> lista) throws ArgumentoException {
			List<Metadado> resposta = new ArrayList<>();
			for (List<String> listaString : lista) {
				Metadado m = new Metadado(listaString.get(0), false);
				m.setTag(ccAux(listaString));
				m.setConstraint(true);
				resposta.add(m);
			}
			return resposta;
		}

		private String ccAux(List<String> lista) {
			StringBuilder sb = new StringBuilder();
			for (int i = 1; i < lista.size(); i++) {
				if (sb.length() > 0) {
					sb.append("|");
				}
				sb.append(lista.get(i));
			}
			return sb.toString();
		}

		private void preencher(Metadado tabela, List<Metadado> campos, String tipoPlural, String tipoSingular)
				throws ArgumentoException {
			if (!campos.isEmpty()) {
				String descricao = campos.size() > 1 ? tipoPlural : tipoSingular;
				Metadado tipo = new Metadado(descricao, true);
				for (Metadado campo : campos) {
					tipo.add(campo);
				}
				tabela.add(tipo);
			}
		}
	}

	private boolean ehValido(Metadado metadado) {
		return metadado != null && metadado.isTabela();
	}

	private Map<String, Object> criarArgs(Metadado metadado, String metodo) {
		Map<String, Object> args = new HashMap<>();
		args.put(MetadadoEvento.ABRIR_METADADO, metadado);
		args.put(MetadadoEvento.METODO, metodo);
		Conexao conexao = getConexao();
		args.put(MetadadoEvento.CONEXAO, conexao);
		if (conexao != null) {
			Conexao conn = ConexaoProvedor.getConexao(conexao.getNome());
			if (conn != null) {
				args.put(MetadadoEvento.CONEXAO, conn);
			}
		}
		return args;
	}

	@Override
	public void focusInputPesquisar(MetadadoTree metadadoTree) {
		toolbar.focusInputPesquisar();
	}

	@Override
	public void atualizarArvore(MetadadoTree metadadoTree) {
		toolbar.getAtualizarAcao();
	}

	@Override
	public void abrirExportacaoFormArquivo(MetadadoTree metadadoTree, boolean circular) {
		Metadado metadado = metadadoTree.getObjetoSelecionado();
		if (ehValido(metadado)) {
			Map<String, Object> args = criarArgs(metadado, MetadadoEvento.ABRIR_EXPORTACAO_METADADO_FORM);
			args.put(MetadadoEvento.CIRCULAR, circular);
			formulario.processar(args);
		}
	}

	@Override
	public void abrirExportacaoFichArquivo(MetadadoTree metadadoTree, boolean circular) {
		Metadado metadado = metadadoTree.getObjetoSelecionado();
		if (ehValido(metadado)) {
			Map<String, Object> args = criarArgs(metadado, MetadadoEvento.ABRIR_EXPORTACAO_METADADO_FICH);
			args.put(MetadadoEvento.CIRCULAR, circular);
			formulario.processar(args);
		}
	}

	@Override
	public void abrirImportacaoFormArquivo(MetadadoTree metadadoTree, boolean circular) {
		Metadado metadado = metadadoTree.getObjetoSelecionado();
		if (ehValido(metadado)) {
			Map<String, Object> args = criarArgs(metadado, MetadadoEvento.ABRIR_IMPORTACAO_METADADO_FORM);
			args.put(MetadadoEvento.CIRCULAR, circular);
			formulario.processar(args);
		}
	}

	@Override
	public void abrirImportacaoFichArquivo(MetadadoTree metadadoTree, boolean circular) {
		Metadado metadado = metadadoTree.getObjetoSelecionado();
		if (ehValido(metadado)) {
			Map<String, Object> args = criarArgs(metadado, MetadadoEvento.ABRIR_IMPORTACAO_METADADO_FICH);
			args.put(MetadadoEvento.CIRCULAR, circular);
			formulario.processar(args);
		}
	}

	@Override
	public void exportarFormArquivo(MetadadoTree metadadoTree) {
		Metadado metadado = metadadoTree.getObjetoSelecionado();
		if (metadado != null) {
			formulario.processar(criarArgs(metadado, MetadadoEvento.EXPORTAR_METADADO_RAIZ_FORM));
		}
	}

	@Override
	public void exportarFichArquivo(MetadadoTree metadadoTree) {
		Metadado metadado = metadadoTree.getObjetoSelecionado();
		if (metadado != null) {
			formulario.processar(criarArgs(metadado, MetadadoEvento.EXPORTAR_METADADO_RAIZ_FICH));
		}
	}

	@Override
	public void registros(MetadadoTree metadadoTree) throws AssistenciaException {
		Metadado metadado = metadadoTree.getObjetoSelecionado();
		Conexao conexao = getConexao();
		if (conexao != null && metadado != null) {
			Objeto objeto = new Objeto(0, 0);
			objeto.setChaves(metadado.getChaves());
			objeto.setTabela(metadado.getDescricao());
			Desktop.setComplemento(conexao, objeto);
			ExternalFormulario.criar(formulario, conexao, objeto);
		}
	}

	@Override
	public void constraintInfo(MetadadoTree metadadoTree) {
		Metadado metadado = metadadoTree.getObjetoSelecionado();
		if (metadado != null && metadado.isConstraint()) {
			Util.mensagem(MetadadoContainer.this, metadado.getTag(), file);
		}
	}

	@Override
	public void adicionadoAoFichario(Fichario fichario) {
		toolbar.adicionadoAoFichario();
	}

	@Override
	public void windowOpenedHandler(Window window) {
		toolbar.windowOpenedHandler(window);
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
				return MetadadoMensagens.getString(MetadadoConstantes.LABEL_METADADOS_MIN);
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