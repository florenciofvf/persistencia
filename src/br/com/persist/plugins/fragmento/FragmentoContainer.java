package br.com.persist.plugins.fragmento;

import static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO;
import static br.com.persist.componente.BarraButtonEnum.APLICAR_BOTAO3;
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
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
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
import br.com.persist.assistencia.TabelaPesquisa;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.componente.ScrollPane;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;

public class FragmentoContainer extends AbstratoContainer implements PluginTabela {
	private final FragmentoModelo fragmentoModelo = new FragmentoModelo();
	private final JTable tabela = new JTable(fragmentoModelo);
	private static final Logger LOG = Logger.getGlobal();
	private static final long serialVersionUID = 1L;
	private FragmentoFormulario fragmentoFormulario;
	private final Toolbar toolbar = new Toolbar();
	private FragmentoDialogo fragmentoDialogo;

	public FragmentoContainer(Janela janela, Formulario formulario, FragmentoListener listener) {
		super(formulario);
		toolbar.ini(janela, listener);
		montarLayout();
		configurar();
	}

	public FragmentoDialogo getFragmentoDialogo() {
		return fragmentoDialogo;
	}

	public void setFragmentoDialogo(FragmentoDialogo fragmentoDialogo) {
		this.fragmentoDialogo = fragmentoDialogo;
		if (fragmentoDialogo != null) {
			fragmentoFormulario = null;
		}
	}

	public FragmentoFormulario getFragmentoFormulario() {
		return fragmentoFormulario;
	}

	public void setFragmentoFormulario(FragmentoFormulario fragmentoFormulario) {
		this.fragmentoFormulario = fragmentoFormulario;
		if (fragmentoFormulario != null) {
			fragmentoDialogo = null;
		}
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, new ScrollPane(tabela));
	}

	private void configurar() {
		tabela.getColumnModel().getColumn(0).setCellRenderer(new CellRenderer());
		tabela.getColumnModel().getColumn(1).setCellRenderer(new CellRenderer());
		tabela.getColumnModel().getColumn(2).setCellEditor(new FragmentoEditor());
		tabela.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		toolbar.baixar();
	}

	@Override
	public void setJanela(Janela janela) {
		toolbar.setJanela(janela);
	}

	private class Toolbar extends BarraButton implements ActionListener {
		private Action descerAcao = actionIcon("label.descer", Icones.BAIXAR2);
		private Action subirAcao = actionIcon("label.subir", Icones.TOP);
		private static final long serialVersionUID = 1L;
		private transient FragmentoListener listener;
		private transient TabelaPesquisa pesquisa;

		public void ini(Janela janela, FragmentoListener listener) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, ABRIR_EM_FORMULARO, NOVO, BAIXAR, SALVAR,
					EXCLUIR, COPIAR, APLICAR_BOTAO3);
			buttonAplicar.setTextAplicar2(FragmentoMensagens.getString("label.aplicar_concatenado_and"));
			buttonAplicar.setTextAplicar3(FragmentoMensagens.getString("label.aplicar_concatenado_or"));
			txtPesquisa.addActionListener(this);
			addButton(true, descerAcao);
			addButton(subirAcao);
			setListener(listener);
			add(txtPesquisa);
			add(chkPorParte);
			chkPsqConteudo.setTag(Constantes.TABELA);
			add(chkPsqConteudo);
			add(label);
			descerAcao.setActionListener(e -> descer());
			subirAcao.setActionListener(e -> subir());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!Util.isEmpty(txtPesquisa.getText())) {
				if (chkPsqConteudo.isSelected()) {
					Set<String> set = new LinkedHashSet<>();
					fragmentoModelo.contemConteudo(set, txtPesquisa.getText(), chkPorParte.isSelected());
					Util.mensagem(FragmentoContainer.this, Util.getString(set));
				} else {
					pesquisa = Util.getTabelaPesquisa(tabela, pesquisa, 0, txtPesquisa.getText(),
							chkPorParte.isSelected());
					pesquisa.selecionar(label);
				}
			} else {
				label.limpar();
			}
		}

		private void setListener(FragmentoListener listener) {
			buttonAplicar.setEnabled(listener != null);
			descerAcao.setEnabled(listener == null);
			subirAcao.setEnabled(listener == null);
			this.listener = listener;
		}

		@Override
		protected void destacarEmFormulario() {
			setListener(null);
			if (formulario.excluirPagina(FragmentoContainer.this)) {
				FragmentoFormulario.criar(formulario, FragmentoContainer.this);
			} else if (fragmentoDialogo != null) {
				fragmentoDialogo.excluirContainer();
				FragmentoFormulario.criar(formulario, FragmentoContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			setListener(null);
			if (fragmentoFormulario != null) {
				fragmentoFormulario.excluirContainer();
				formulario.adicionarPagina(FragmentoContainer.this);
			} else if (fragmentoDialogo != null) {
				fragmentoDialogo.excluirContainer();
				formulario.adicionarPagina(FragmentoContainer.this);
			}
		}

		@Override
		protected void abrirEmFormulario() {
			setListener(null);
			if (fragmentoDialogo != null) {
				fragmentoDialogo.excluirContainer();
			}
			FragmentoFormulario.criar(formulario);
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
			String resumo = getValor(FragmentoMensagens.getString("label.nome_fragmento"), Constantes.VAZIO);
			if (resumo == null) {
				return;
			}
			String grupo = getValor(Mensagens.getString("label.grupo"), Constantes.VAZIO);
			if (grupo == null) {
				return;
			}
			try {
				adicionar(new Fragmento(resumo, grupo));
			} catch (ArgumentoException ex) {
				Util.mensagem(FragmentoContainer.this, ex.getMessage());
			}
		}

		private void adicionar(Fragmento frag) {
			if (FragmentoProvedor.contem(frag)) {
				Util.mensagem(FragmentoContainer.this,
						Mensagens.getString("label.indentificador_ja_existente") + " " + frag.getResumo());
				return;
			}
			FragmentoProvedor.adicionar(frag);
			fragmentoModelo.fireTableDataChanged();
			ajustarTabela();
		}

		private String getValor(String mensagem, String padrao) {
			Object resp = Util.getValorInputDialog(FragmentoContainer.this, "label.id", mensagem, padrao);
			if (resp == null || Util.isEmpty(resp.toString())) {
				return null;
			}
			return resp.toString();
		}

		@Override
		protected void baixar() {
			FragmentoProvedor.inicializar();
			if (listener != null) {
				FragmentoProvedor.filtrar(listener.getGrupoFiltro());
			}
			fragmentoModelo.fireTableDataChanged();
			ajustarTabela();
		}

		@Override
		protected void salvar() {
			if (Util.confirmaSalvar(FragmentoContainer.this)) {
				try {
					FragmentoProvedor.salvar();
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
					Fragmento f = FragmentoProvedor.getFragmento(i);
					String resumo = getValor(FragmentoMensagens.getString("label.nome_fragmento"), f.getResumo());
					if (resumo != null) {
						try {
							adicionar(f.clonar(resumo));
						} catch (ArgumentoException ex) {
							Util.mensagem(FragmentoContainer.this, ex.getMessage());
						}
					}
				}
			}
		}

		private void mensagem() {
			Util.mensagem(FragmentoContainer.this, Mensagens.getString("msg.nenhum_registro_selecionado"));
		}

		@Override
		protected void aplicar() {
			int[] linhas = tabela.getSelectedRows();
			if (linhas != null && linhas.length > 0) {
				aplicarListaFragmento(linhas, false, false);
			} else {
				mensagem();
			}
		}

		@Override
		protected void aplicar2() {
			int[] linhas = tabela.getSelectedRows();
			if (linhas != null && linhas.length > 0) {
				aplicarListaFragmento(linhas, true, true);
			} else {
				mensagem();
			}
		}

		@Override
		protected void aplicar3() {
			int[] linhas = tabela.getSelectedRows();
			if (linhas != null && linhas.length > 0) {
				aplicarListaFragmento(linhas, true, false);
			} else {
				mensagem();
			}
		}

		private void aplicarListaFragmento(int[] linhas, boolean concatenar, boolean and) {
			List<Fragmento> frags = new ArrayList<>();
			for (int i : linhas) {
				Fragmento fragmento = FragmentoProvedor.getFragmento(i);
				if (concatenar && !and) {
					try {
						frags.add(fragmento.cloneOr());
					} catch (ArgumentoException ex) {
						Util.mensagem(FragmentoContainer.this, ex.getMessage());
					}
				} else {
					frags.add(fragmento);
				}
			}
			aplicarFragmento(frags, concatenar, and);
		}

		private void aplicarFragmento(List<Fragmento> frags, boolean concatenar, boolean and) {
			try {
				listener.aplicarFragmento(frags, concatenar, and);
			} finally {
				if (janela != null) {
					janela.fechar();
				}
			}
		}

		@Override
		protected void excluir() {
			int[] linhas = tabela.getSelectedRows();
			if (linhas != null && linhas.length > 0 && Util.confirmaExclusao(FragmentoContainer.this, false)) {
				FragmentoProvedor.excluir(linhas);
				fragmentoModelo.fireTableDataChanged();
			}
		}

		private void subir() {
			int[] linhas = tabela.getSelectedRows();
			int registros = fragmentoModelo.getRowCount();
			if (linhas != null && linhas.length == 1 && registros > 1 && linhas[0] > 0) {
				int i = FragmentoProvedor.anterior(linhas[0]);
				fragmentoModelo.fireTableDataChanged();
				if (i != -1) {
					tabela.setRowSelectionInterval(i, i);
				}
			}
		}

		private void descer() {
			int[] linhas = tabela.getSelectedRows();
			int registros = fragmentoModelo.getRowCount();
			if (linhas != null && linhas.length == 1 && registros > 1 && linhas[0] + 1 < registros) {
				int i = FragmentoProvedor.proximo(linhas[0]);
				fragmentoModelo.fireTableDataChanged();
				if (i != -1) {
					tabela.setRowSelectionInterval(i, i);
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
		return FragmentoFabrica.class;
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
				return FragmentoMensagens.getString(FragmentoConstantes.LABEL_FRAGMENTO_MIN);
			}

			@Override
			public String getTitulo() {
				return Mensagens.getString(Constantes.LABEL_FRAGMENTO);
			}

			@Override
			public String getHint() {
				return Mensagens.getString(Constantes.LABEL_FRAGMENTO);
			}

			@Override
			public Icon getIcone() {
				return Icones.FRAGMENTO;
			}
		};
	}

	private void ajustarTabela() {
		Util.ajustar(tabela, getGraphics());
	}
}