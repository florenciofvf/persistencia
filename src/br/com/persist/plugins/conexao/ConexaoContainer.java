package br.com.persist.plugins.conexao;

import static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO;
import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.DESTACAR_EM_FORMULARIO;
import static br.com.persist.componente.BarraButtonEnum.NOVO;
import static br.com.persist.componente.BarraButtonEnum.RETORNAR_AO_FICHARIO;
import static br.com.persist.componente.BarraButtonEnum.SALVAR;
import static br.com.persist.componente.BarraButtonEnum.APLICAR;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTable;

import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.componente.ScrollPane;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.plugins.variaveis.VariavelProvedor;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.Icones;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Util;

public class ConexaoContainer extends AbstratoContainer {
	private static final long serialVersionUID = 1L;
	private final ConexaoModelo conexaoModelo = new ConexaoModelo();
	private final JTable tabela = new JTable(conexaoModelo);
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
		tabela.getColumnModel().getColumn(0).setCellRenderer(new ConexaoStatusRenderer());
		tabela.getColumnModel().getColumn(0).setCellEditor(new ConexaoStatusEditor());
		tabela.getColumnModel().getColumn(3).setCellEditor(new ConexaoEditor());
		tabela.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		toolbar.baixar();
	}

	@Override
	public void setJanela(Janela janela) {
		toolbar.setJanela(janela);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		private Action desconectaAcao = Action.actionIcon("label.final_conexoes", Icones.BANCO_DESCONECTA);
		private Action conectaAcao = Action.actionIcon("label.conectar", Icones.CONECTA);
		private Action descerAcao = Action.actionIcon("label.descer", Icones.BAIXAR2);
		private Action subirAcao = Action.actionIcon("label.subir", Icones.TOP);
		// private Action infoAcao = Action.actionIcon("label.info",
		// Icones.INFO);

		public void ini(Janela janela) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, ABRIR_EM_FORMULARO, NOVO, BAIXAR, SALVAR,
					COPIAR, APLICAR);

			addButton(true, descerAcao);
			addButton(subirAcao);
			addButton(true, conectaAcao);
			// addButton(true, infoAcao);
			addButton(true, desconectaAcao);

			eventos();
		}

		private void eventos() {
			// infoAcao.setActionListener(e ->
			// formulario.getFichario().infoConexao());
			conectaAcao.setActionListener(e -> conectar());
			descerAcao.setActionListener(e -> descer());
			subirAcao.setActionListener(e -> subir());

			desconectaAcao.setActionListener(e -> {
				try {
					ConexaoProvedor.fecharConexoes();
					tabela.repaint();
				} catch (Exception ex) {
					Util.stackTraceAndMessage(getClass().getName() + ".fechar()", ex, formulario);
				}
			});
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
		protected void novo() {
			Object resp = Util.getValorInputDialog(ConexaoContainer.this, "label.id",
					Mensagens.getString("label.nome_conexao"), Constantes.VAZIO);

			if (resp == null || Util.estaVazio(resp.toString())) {
				return;
			}

			String nome = resp.toString();

			if (VariavelProvedor.contem(nome)) {
				Util.mensagem(ConexaoContainer.this, Mensagens.getString("label.indentificador_ja_existente"));
				return;
			}

			ConexaoProvedor.adicionar(new Conexao(nome));
			conexaoModelo.fireTableDataChanged();
			ajustarTabela();
		}

		@Override
		protected void baixar() {
			ConexaoProvedor.inicializar();
			conexaoModelo.fireTableDataChanged();
			ajustarTabela();
		}

		@Override
		protected void salvar() {
			ConexaoProvedor.salvar();
		}

		@Override
		protected void copiar() {
			int[] linhas = tabela.getSelectedRows();

			if (linhas != null) {
				for (int i : linhas) {
					Conexao c = ConexaoProvedor.getConexao(i);
					String nome = c.getNome() + "_" + Constantes.TEMP;
					ConexaoProvedor.adicionar(new Conexao(nome));
				}

				conexaoModelo.fireTableDataChanged();
			}
		}

		@Override
		protected void aplicar() {
			int[] linhas = tabela.getSelectedRows();

			if (linhas != null && linhas.length == 1) {
				Conexao c = ConexaoProvedor.getConexao(linhas[0]);
				formulario.processar(Constantes.SELECIONAR_CONEXAO, c);
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
		toolbar.paginaVisivel();
		ajustarTabela();
	}

	public void formularioVisivel() {
		toolbar.formularioVisivel();
		ajustarTabela();
	}

	public void dialogoVisivel() {
		toolbar.dialogoVisivel();
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
		return new Titulo() {
			@Override
			public String getTituloMin() {
				return Mensagens.getString(Constantes.LABEL_CONEXAO_MIN);
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