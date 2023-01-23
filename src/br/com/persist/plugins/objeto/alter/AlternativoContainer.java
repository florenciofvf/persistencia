package br.com.persist.plugins.objeto.alter;

import static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO;
import static br.com.persist.componente.BarraButtonEnum.APLICAR_BOTAO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.JTable;

import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.assistencia.CellRenderer;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.componente.ScrollPane;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;

public class AlternativoContainer extends AbstratoContainer {
	private static final long serialVersionUID = 1L;
	private final AlternativoModelo alternativoModelo = new AlternativoModelo();
	private final JTable tabela = new JTable(alternativoModelo);
	private static final Logger LOG = Logger.getGlobal();
	private AlternativoFormulario alternativoFormulario;
	private final Toolbar toolbar = new Toolbar();
	private AlternativoDialogo alternativoDialogo;

	public AlternativoContainer(Janela janela, Formulario formulario, AlternativoListener listener) {
		super(formulario);
		toolbar.ini(janela, listener);
		montarLayout();
		configurar();
	}

	public AlternativoDialogo getAlternativoDialogo() {
		return alternativoDialogo;
	}

	public void setAlternativoDialogo(AlternativoDialogo alternativoDialogo) {
		this.alternativoDialogo = alternativoDialogo;
		if (alternativoDialogo != null) {
			alternativoFormulario = null;
		}
	}

	public AlternativoFormulario getAlternativoFormulario() {
		return alternativoFormulario;
	}

	public void setAlternativoFormulario(AlternativoFormulario alternativoFormulario) {
		this.alternativoFormulario = alternativoFormulario;
		if (alternativoFormulario != null) {
			alternativoDialogo = null;
		}
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, new ScrollPane(tabela));
	}

	private void configurar() {
		tabela.getColumnModel().getColumn(0).setCellRenderer(new CellRenderer());
		tabela.getColumnModel().getColumn(1).setCellRenderer(new CellRenderer());
		tabela.getColumnModel().getColumn(2).setCellEditor(new AlternativoEditor());
		tabela.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		toolbar.baixar();
	}

	@Override
	public void setJanela(Janela janela) {
		toolbar.setJanela(janela);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		private transient AlternativoListener listener;

		public void ini(Janela janela, AlternativoListener listener) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, ABRIR_EM_FORMULARO, NOVO, BAIXAR, SALVAR,
					EXCLUIR, COPIAR, APLICAR_BOTAO);
			buttonAplicar.setTextAplicar2(AlternativoMensagens.getString("label.aplicar_concatenado"));
			setListener(listener);
		}

		private void setListener(AlternativoListener listener) {
			buttonAplicar.setEnabled(listener != null);
			this.listener = listener;
		}

		@Override
		protected void destacarEmFormulario() {
			setListener(null);
			if (formulario.excluirPagina(AlternativoContainer.this)) {
				AlternativoFormulario.criar(formulario, AlternativoContainer.this);
			} else if (alternativoDialogo != null) {
				alternativoDialogo.excluirContainer();
				AlternativoFormulario.criar(formulario, AlternativoContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			setListener(null);
			if (alternativoFormulario != null) {
				alternativoFormulario.excluirContainer();
				formulario.adicionarPagina(AlternativoContainer.this);
			} else if (alternativoDialogo != null) {
				alternativoDialogo.excluirContainer();
				formulario.adicionarPagina(AlternativoContainer.this);
			}
		}

		@Override
		protected void abrirEmFormulario() {
			setListener(null);
			if (alternativoDialogo != null) {
				alternativoDialogo.excluirContainer();
			}
			AlternativoFormulario.criar(formulario);
		}

		@Override
		public void windowOpenedHandler(Window window) {
			buttonDestacar.estadoFormulario();
		}

		void adicionadoAoFichario() {
			buttonDestacar.estadoFichario();
		}

		@Override
		public void dialogOpenedHandler(Dialog dialog) {
			buttonDestacar.estadoDialogo();
		}

		@Override
		protected void novo() {
			String resumo = getValor(AlternativoMensagens.getString("label.nome_alternativo"), Constantes.VAZIO);
			if (resumo == null) {
				return;
			}
			String grupo = getValor(Mensagens.getString("label.grupo"), Constantes.VAZIO);
			if (grupo == null) {
				return;
			}
			adicionar(new Alternativo(resumo, grupo));
		}

		private void adicionar(Alternativo frag) {
			if (AlternativoProvedor.contem(frag)) {
				Util.mensagem(AlternativoContainer.this,
						Mensagens.getString("label.indentificador_ja_existente") + " " + frag.getResumo());
				return;
			}
			AlternativoProvedor.adicionar(frag);
			alternativoModelo.fireTableDataChanged();
			ajustarTabela();
		}

		private String getValor(String mensagem, String padrao) {
			Object resp = Util.getValorInputDialog(AlternativoContainer.this, "label.id", mensagem, padrao);
			if (resp == null || Util.estaVazio(resp.toString())) {
				return null;
			}
			return resp.toString();
		}

		@Override
		protected void baixar() {
			AlternativoProvedor.inicializar();
			if (listener != null) {
				AlternativoProvedor.filtrar(listener.getGrupoFiltro());
			}
			alternativoModelo.fireTableDataChanged();
			ajustarTabela();
		}

		@Override
		protected void salvar() {
			try {
				AlternativoProvedor.salvar();
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
					Alternativo f = AlternativoProvedor.getAlternativo(i);
					String resumo = getValor(AlternativoMensagens.getString("label.nome_alternativo"), f.getResumo());
					if (resumo != null) {
						adicionar(f.clonar(resumo));
					}
				}
			}
		}

		private void mensagem() {
			Util.mensagem(AlternativoContainer.this, Mensagens.getString("msg.nenhum_registro_selecionado"));
		}

		@Override
		protected void aplicar() {
			int[] linhas = tabela.getSelectedRows();
			if (linhas != null && linhas.length > 0) {
				aplicarListaAlternativo(linhas, false);
			} else {
				mensagem();
			}
		}

		@Override
		protected void aplicar2() {
			int[] linhas = tabela.getSelectedRows();
			if (linhas != null && linhas.length > 0) {
				aplicarListaAlternativo(linhas, true);
			} else {
				mensagem();
			}
		}

		private void aplicarListaAlternativo(int[] linhas, boolean concatenar) {
			List<Alternativo> frags = new ArrayList<>();
			for (int i : linhas) {
				frags.add(AlternativoProvedor.getAlternativo(i));
			}
			aplicarAlternativo(frags, concatenar);
		}

		private void aplicarAlternativo(List<Alternativo> frags, boolean concatenar) {
			try {
				listener.aplicarAlternativo(frags, concatenar);
			} finally {
				if (janela != null) {
					janela.fechar();
				}
			}
		}

		@Override
		protected void excluir() {
			int[] linhas = tabela.getSelectedRows();
			if (linhas != null && linhas.length > 0 && Util.confirmaExclusao(AlternativoContainer.this, false)) {
				AlternativoProvedor.excluir(linhas);
				alternativoModelo.fireTableDataChanged();
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
		return AlternativoFabrica.class;
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
				return AlternativoMensagens.getString(AlternativoConstantes.LABEL_ALTERNATIVO_MIN);
			}

			@Override
			public String getTitulo() {
				return Mensagens.getString(Constantes.LABEL_ALTERNATIVO);
			}

			@Override
			public String getHint() {
				return Mensagens.getString(Constantes.LABEL_ALTERNATIVO);
			}

			@Override
			public Icon getIcone() {
				return Icones.VAR;
			}
		};
	}

	private void ajustarTabela() {
		Util.ajustar(tabela, getGraphics());
	}
}