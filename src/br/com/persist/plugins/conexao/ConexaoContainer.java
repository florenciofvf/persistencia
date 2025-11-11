package br.com.persist.plugins.conexao;

import static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO;
import static br.com.persist.componente.BarraButtonEnum.APLICAR;
import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.DESTACAR_EM_FORMULARIO;
import static br.com.persist.componente.BarraButtonEnum.EXCLUIR;
import static br.com.persist.componente.BarraButtonEnum.NOVO;
import static br.com.persist.componente.BarraButtonEnum.RETORNAR_AO_FICHARIO;
import static br.com.persist.componente.BarraButtonEnum.SALVAR;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;

import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.abstrato.PluginTabela;
import br.com.persist.assistencia.ArgumentoException;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.TabelaPesquisa;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.componente.ScrollPane;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;
import br.com.persist.formulario.FormularioEvento;

public class ConexaoContainer extends AbstratoContainer implements PluginTabela {
	private final ConexaoModelo conexaoModelo = new ConexaoModelo();
	private final JTable tabela = new JTable(conexaoModelo);
	private static final Logger LOG = Logger.getGlobal();
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private ConexaoFormulario conexaoFormulario;
	private ConexaoDialogo conexaoDialogo;

	public ConexaoContainer(Janela janela, Formulario formulario) {
		super(formulario);
		toolbar.ini(janela);
		montarLayout();
		configurar();
	}

	public ConexaoDialogo getConexaoDialogo() {
		return conexaoDialogo;
	}

	public void setConexaoDialogo(ConexaoDialogo conexaoDialogo) {
		this.conexaoDialogo = conexaoDialogo;
		if (conexaoDialogo != null) {
			conexaoFormulario = null;
		}
	}

	public ConexaoFormulario getConexaoFormulario() {
		return conexaoFormulario;
	}

	public void setConexaoFormulario(ConexaoFormulario conexaoFormulario) {
		this.conexaoFormulario = conexaoFormulario;
		if (conexaoFormulario != null) {
			conexaoDialogo = null;
		}
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, new ScrollPane(tabela));
	}

	private void configurar() {
		JTableHeader header = tabela.getTableHeader();
		header.addMouseMotionListener(headerListenerInner);
		tabela.getColumnModel().getColumn(0).setCellRenderer(new ConexaoRendererStatus());
		tabela.getColumnModel().getColumn(0).setCellEditor(new ConexaoEditorStatus());
		tabela.getColumnModel().getColumn(3).setCellEditor(new ConexaoEditorURL());
		tabela.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		toolbar.baixar();
	}

	private transient MouseMotionListener headerListenerInner = new MouseAdapter() {
		@Override
		public void mouseMoved(MouseEvent e) {
			int tableColuna = tabela.columnAtPoint(e.getPoint());
			int modelColuna = tabela.convertColumnIndexToModel(tableColuna);
			if (modelColuna == 6) {
				tabela.getTableHeader().setToolTipText(ConexaoMensagens.getString("hint.coluna_select_constraint"));
			} else if (modelColuna == 11) {
				tabela.getTableHeader().setToolTipText(ConexaoMensagens.getString("hint.coluna_tipos_funcoes"));
			} else {
				tabela.getTableHeader().setToolTipText(null);
			}
		}
	};

	@Override
	public void setJanela(Janela janela) {
		toolbar.setJanela(janela);
	}

	static Action acaoIcon(String chave, Icon icon) {
		return Action.acaoIcon(ConexaoMensagens.getString(chave), icon);
	}

	static Action acaoIcon(String chave) {
		return acaoIcon(chave, null);
	}

	private class Toolbar extends BarraButton implements ActionListener {
		private Action desconectaAcao = acaoIcon("label.final_conexoes", Icones.BANCO_DESCONECTA);
		private Action conectaAcao = actionIcon("label.conectar", Icones.CONECTA);
		private Action descerAcao = actionIcon("label.descer", Icones.BAIXAR2);
		private Action subirAcao = actionIcon("label.subir", Icones.TOP);
		private Action infoAcao = actionIcon("label.info", Icones.INFO);
		private static final long serialVersionUID = 1L;
		private transient TabelaPesquisa pesquisa;

		public void ini(Janela janela) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, ABRIR_EM_FORMULARO, NOVO, BAIXAR, SALVAR,
					EXCLUIR, COPIAR, APLICAR);
			txtPesquisa.addActionListener(this);
			addButton(true, descerAcao);
			addButton(subirAcao);
			addButton(true, conectaAcao);
			addButton(true, infoAcao);
			addButton(true, desconectaAcao);
			add(txtPesquisa);
			add(chkPorParte);
			chkPsqConteudo.setTag(Constantes.TABELA);
			add(chkPsqConteudo);
			add(label);
			eventos();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!Util.isEmpty(txtPesquisa.getText())) {
				if (chkPsqConteudo.isSelected()) {
					Set<String> set = new LinkedHashSet<>();
					conexaoModelo.contemConteudo(set, txtPesquisa.getText(), chkPorParte.isSelected());
					Util.mensagem(ConexaoContainer.this, Util.getString(set));
				} else {
					pesquisa = Util.getTabelaPesquisa(tabela, pesquisa, 1, txtPesquisa.getText(),
							chkPorParte.isSelected());
					pesquisa.selecionar(label);
				}
			} else {
				label.limpar();
			}
		}

		private void eventos() {
			aplicarAcao.text(ConexaoMensagens.getString("label.aplicar_todas_abas"));
			infoAcao.setActionListener(e -> infoConexao());
			conectaAcao.setActionListener(e -> conectar());
			descerAcao.setActionListener(e -> descer());
			subirAcao.setActionListener(e -> subir());
			desconectaAcao.setActionListener(e -> {
				Map<String, Object> args = new HashMap<>();
				args.put(FormularioEvento.FECHAR_CONEXOES, true);
				formulario.processar(args);
				tabela.repaint();
			});
		}

		private void infoConexao() {
			List<ConexaoInfo> lista = new ArrayList<>();
			Map<String, Object> args = new HashMap<>();
			args.put(ConexaoEvento.COLETAR_INFO_CONEXAO, lista);
			formulario.processar(args);
			StringBuilder builder = new StringBuilder();
			for (ConexaoInfo info : lista) {
				builder.append("PAGINA: " + info.getNomeAba() + Constantes.QL);
				if (!Util.isEmpty(info.getConexaoFile())) {
					builder.append("ARQUIVO: " + info.getConexaoFile() + Constantes.QL);
				}
				builder.append("ATUAL: " + info.getConexaoAtual() + Constantes.QL);
				builder.append(Constantes.QL);
			}
			if (builder.length() > 0) {
				builder.insert(0, "TOTAL = " + lista.size() + Constantes.QL + Constantes.QL);
			}
			Util.mensagem(ConexaoContainer.this, builder.toString());
		}

		@Override
		protected void destacarEmFormulario() {
			if (formulario.excluirPagina(ConexaoContainer.this)) {
				ConexaoFormulario.criar(formulario, ConexaoContainer.this);
			} else if (conexaoDialogo != null) {
				conexaoDialogo.excluirContainer();
				ConexaoFormulario.criar(formulario, ConexaoContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (conexaoFormulario != null) {
				conexaoFormulario.excluirContainer();
				formulario.adicionarPagina(ConexaoContainer.this);
			} else if (conexaoDialogo != null) {
				conexaoDialogo.excluirContainer();
				formulario.adicionarPagina(ConexaoContainer.this);
			}
		}

		@Override
		protected void abrirEmFormulario() {
			if (conexaoDialogo != null) {
				conexaoDialogo.excluirContainer();
			}
			ConexaoFormulario.criar(formulario);
		}

		@Override
		public void windowOpenedHandler(Window window) {
			buttonDestacar.estadoFormulario();
		}

		@Override
		public void dialogOpenedHandler(Dialog dialog) {
			buttonDestacar.estadoDialogo();
		}

		void adicionadoAoFichario() {
			buttonDestacar.estadoFichario();
		}

		@Override
		protected void novo() {
			String nome = getValor(Constantes.VAZIO);
			if (nome != null) {
				try {
					adicionar(new Conexao(nome));
				} catch (ArgumentoException ex) {
					Util.mensagem(ConexaoContainer.this, ex.getMessage());
				}
			}
		}

		private void adicionar(Conexao con) {
			if (ConexaoProvedor.contem(con)) {
				Util.mensagem(ConexaoContainer.this,
						Mensagens.getString("label.indentificador_ja_existente") + " " + con.getNome());
				return;
			}
			ConexaoProvedor.adicionar(con);
			conexaoModelo.fireTableDataChanged();
			ajustarTabela();
		}

		private String getValor(String padrao) {
			Object resp = Util.getValorInputDialog(ConexaoContainer.this, "label.id",
					ConexaoMensagens.getString("label.nome_conexao"), padrao);
			if (resp == null || Util.isEmpty(resp.toString())) {
				return null;
			}
			return resp.toString();
		}

		@Override
		protected void baixar() {
			ConexaoProvedor.inicializar();
			conexaoModelo.fireTableDataChanged();
			ajustarTabela();
		}

		@Override
		protected void salvar() {
			if (Util.confirmaSalvar(ConexaoContainer.this)) {
				try {
					ConexaoProvedor.salvar();
					salvoMensagem();
				} catch (Exception e) {
					LOG.log(Level.SEVERE, Constantes.ERRO, e);
				}
			}
		}

		@Override
		protected void copiar() {
			int[] linhas = tabela.getSelectedRows();
			if (linhas != null) {
				for (int i : linhas) {
					Conexao c = ConexaoProvedor.getConexao(i);
					String nome = getValor(c.getNome());
					if (nome != null) {
						try {
							adicionar(c.clonar(nome));
						} catch (ArgumentoException ex) {
							Util.mensagem(ConexaoContainer.this, ex.getMessage());
						}
					}
				}
			}
		}

		@Override
		protected void aplicar() {
			int[] linhas = tabela.getSelectedRows();
			if (linhas != null && linhas.length == 1) {
				Conexao c = ConexaoProvedor.getConexao(linhas[0]);
				Map<String, Object> map = new HashMap<>();
				map.put(ConexaoEvento.SELECIONAR_CONEXAO, c);
				formulario.processar(map);
			}
		}

		@Override
		protected void excluir() {
			int[] linhas = tabela.getSelectedRows();
			if (linhas != null && linhas.length == 1 && Util.confirmaExclusao(ConexaoContainer.this, false)) {
				ConexaoProvedor.excluir(linhas[0]);
				conexaoModelo.fireTableDataChanged();
			}
		}

		private void subir() {
			int[] linhas = tabela.getSelectedRows();
			int registros = conexaoModelo.getRowCount();
			if (linhas != null && linhas.length == 1 && registros > 1 && linhas[0] > 0) {
				int i = ConexaoProvedor.anterior(linhas[0]);
				conexaoModelo.fireTableDataChanged();
				if (i != -1) {
					tabela.setRowSelectionInterval(i, i);
				}
			}
		}

		private void descer() {
			int[] linhas = tabela.getSelectedRows();
			int registros = conexaoModelo.getRowCount();
			if (linhas != null && linhas.length == 1 && registros > 1 && linhas[0] + 1 < registros) {
				int i = ConexaoProvedor.proximo(linhas[0]);
				conexaoModelo.fireTableDataChanged();
				if (i != -1) {
					tabela.setRowSelectionInterval(i, i);
				}
			}
		}

		private void conectar() {
			int[] linhas = tabela.getSelectedRows();
			if (linhas != null && linhas.length == 1) {
				try {
					Conexao conexao = ConexaoProvedor.getConexao(linhas[0]);
					ConexaoProvedor.getConnection2(conexao);
					Util.mensagem(ConexaoContainer.this, "SUCESSO");
					tabela.repaint();
				} catch (Exception ex) {
					Util.stackTraceAndMessage(Constantes.ERRO, ex, ConexaoContainer.this);
				}
			}
		}
	}

	@Override
	public void adicionadoAoFichario(Fichario fichario) {
		toolbar.adicionadoAoFichario();
		ajustarTabela();
	}

	@Override
	public void windowOpenedHandler(Window window) {
		toolbar.windowOpenedHandler(window);
		ajustarTabela();
	}

	@Override
	public void dialogOpenedHandler(Dialog dialog) {
		toolbar.dialogOpenedHandler(dialog);
		ajustarTabela();
	}

	@Override
	public String getStringPersistencia() {
		return Constantes.VAZIO;
	}

	@Override
	public Class<?> getClasseFabrica() {
		return ConexaoFabrica.class;
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
				return ConexaoMensagens.getString(ConexaoConstantes.LABEL_CONEXAO_MIN);
			}

			@Override
			public String getTitulo() {
				return Mensagens.getString(Constantes.LABEL_CONEXAO);
			}

			@Override
			public String getHint() {
				return Mensagens.getString(Constantes.LABEL_CONEXAO);
			}

			@Override
			public Icon getIcone() {
				return Icones.BANCO;
			}
		};
	}

	private void ajustarTabela() {
		Util.ajustar(tabela, getGraphics());
	}
}