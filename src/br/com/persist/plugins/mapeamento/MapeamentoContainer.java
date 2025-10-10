package br.com.persist.plugins.mapeamento;

import static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO;
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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.JTable;

import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.abstrato.PluginTabela;
import br.com.persist.assistencia.ArgumentoException;
import br.com.persist.assistencia.CellRenderer;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.SelecaoTabela;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.componente.ScrollPane;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;

public class MapeamentoContainer extends AbstratoContainer implements PluginTabela {
	private final MapeamentoModelo mapeamentoModelo = new MapeamentoModelo();
	private final JTable tabela = new JTable(mapeamentoModelo);
	private static final Logger LOG = Logger.getGlobal();
	private MapeamentoFormulario mapeamentoFormulario;
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private MapeamentoDialogo mapeamentoDialogo;

	public MapeamentoContainer(Janela janela, Formulario formulario) {
		super(formulario);
		toolbar.ini(janela);
		montarLayout();
		configurar();
	}

	public MapeamentoDialogo getMapeamentoDialogo() {
		return mapeamentoDialogo;
	}

	public void setMapeamentoDialogo(MapeamentoDialogo mapeamentoDialogo) {
		this.mapeamentoDialogo = mapeamentoDialogo;
		if (mapeamentoDialogo != null) {
			mapeamentoFormulario = null;
		}
	}

	public MapeamentoFormulario getMapeamentoFormulario() {
		return mapeamentoFormulario;
	}

	public void setMapeamentoFormulario(MapeamentoFormulario mapeamentoFormulario) {
		this.mapeamentoFormulario = mapeamentoFormulario;
		if (mapeamentoFormulario != null) {
			mapeamentoDialogo = null;
		}
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, new ScrollPane(tabela));
	}

	private void configurar() {
		tabela.getColumnModel().getColumn(0).setCellRenderer(new CellRenderer());
		tabela.getColumnModel().getColumn(1).setCellEditor(new MapeamentoEditor());
		tabela.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		toolbar.baixar();
	}

	@Override
	public void setJanela(Janela janela) {
		toolbar.setJanela(janela);
	}

	private class Toolbar extends BarraButton implements ActionListener {
		private static final long serialVersionUID = 1L;
		private transient SelecaoTabela selecao;

		public void ini(Janela janela) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, ABRIR_EM_FORMULARO, NOVO, BAIXAR, SALVAR,
					EXCLUIR, COPIAR);
			txtPesquisa.addActionListener(this);
			add(txtPesquisa);
			add(label);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!Util.isEmpty(txtPesquisa.getText())) {
				selecao = Util.getSelecaoTabela(tabela, selecao, 0, txtPesquisa.getText());
				selecao.selecionar(label);
			} else {
				label.limpar();
			}
		}

		@Override
		protected void destacarEmFormulario() {
			if (formulario.excluirPagina(MapeamentoContainer.this)) {
				MapeamentoFormulario.criar(formulario, MapeamentoContainer.this);
			} else if (mapeamentoDialogo != null) {
				mapeamentoDialogo.excluirContainer();
				MapeamentoFormulario.criar(formulario, MapeamentoContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (mapeamentoFormulario != null) {
				mapeamentoFormulario.excluirContainer();
				formulario.adicionarPagina(MapeamentoContainer.this);
			} else if (mapeamentoDialogo != null) {
				mapeamentoDialogo.excluirContainer();
				formulario.adicionarPagina(MapeamentoContainer.this);
			}
		}

		@Override
		protected void abrirEmFormulario() {
			if (mapeamentoDialogo != null) {
				mapeamentoDialogo.excluirContainer();
			}
			MapeamentoFormulario.criar(formulario);
		}

		@Override
		public void windowOpenedHandler(Window window) {
			buttonDestacar.estadoFormulario();
			toolbar.focusInputPesquisar();
		}

		@Override
		public void dialogOpenedHandler(Dialog dialog) {
			buttonDestacar.estadoDialogo();
			toolbar.focusInputPesquisar();
		}

		void adicionadoAoFichario() {
			buttonDestacar.estadoFichario();
			toolbar.focusInputPesquisar();
		}

		@Override
		protected void novo() {
			String nome = getValor(Constantes.VAZIO);
			if (nome != null) {
				try {
					adicionar(new Mapeamento(nome));
				} catch (ArgumentoException ex) {
					Util.mensagem(MapeamentoContainer.this, ex.getMessage());
				}
			}
		}

		private void adicionar(Mapeamento map) {
			if (MapeamentoProvedor.contem(map)) {
				Util.mensagem(MapeamentoContainer.this,
						Mensagens.getString("label.indentificador_ja_existente") + " " + map.getNome());
				return;
			}
			MapeamentoProvedor.adicionar(map);
			mapeamentoModelo.fireTableDataChanged();
			ajustarTabela();
		}

		private String getValor(String padrao) {
			Object resp = Util.getValorInputDialog(MapeamentoContainer.this, "label.id",
					MapeamentoMensagens.getString("label.nome_mapeamento"), padrao);
			if (resp == null || Util.isEmpty(resp.toString())) {
				return null;
			}
			return resp.toString();
		}

		@Override
		protected void baixar() {
			MapeamentoProvedor.inicializar();
			mapeamentoModelo.fireTableDataChanged();
			ajustarTabela();
		}

		@Override
		protected void salvar() {
			try {
				MapeamentoProvedor.salvar();
				salvoMensagem();
			} catch (Exception e) {
				LOG.log(Level.SEVERE, Constantes.ERRO, e);
			}
		}

		@Override
		protected void copiar() {
			int[] linhas = tabela.getSelectedRows();
			if (linhas != null) {
				for (int i : linhas) {
					Mapeamento m = MapeamentoProvedor.getMapeamento(i);
					String nome = getValor(m.getNome());
					if (nome != null) {
						try {
							adicionar(m.clonar(nome));
						} catch (ArgumentoException ex) {
							Util.mensagem(MapeamentoContainer.this, ex.getMessage());
						}
					}
				}
			}
		}

		@Override
		protected void excluir() {
			int[] linhas = tabela.getSelectedRows();
			if (linhas != null && linhas.length > 0 && Util.confirmaExclusao(MapeamentoContainer.this, false)) {
				MapeamentoProvedor.excluir(linhas);
				mapeamentoModelo.fireTableDataChanged();
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
		return MapeamentoFabrica.class;
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
				return MapeamentoMensagens.getString(MapeamentoConstantes.LABEL_MAPEAMENTOS_MIN);
			}

			@Override
			public String getTitulo() {
				return Mensagens.getString(Constantes.LABEL_MAPEAMENTOS);
			}

			@Override
			public String getHint() {
				return Mensagens.getString(Constantes.LABEL_MAPEAMENTOS);
			}

			@Override
			public Icon getIcone() {
				return Icones.REFERENCIA;
			}
		};
	}

	private void ajustarTabela() {
		Util.ajustar(tabela, getGraphics());
	}
}