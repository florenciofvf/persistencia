package br.com.persist.plugins.fragmento;

import static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO;
import static br.com.persist.componente.BarraButtonEnum.APLICAR;
import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.DESTACAR_EM_FORMULARIO;
import static br.com.persist.componente.BarraButtonEnum.NOVO;
import static br.com.persist.componente.BarraButtonEnum.RETORNAR_AO_FICHARIO;
import static br.com.persist.componente.BarraButtonEnum.SALVAR;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTable;

import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.componente.ScrollPane;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.Icones;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Util;

public class FragmentoContainer extends AbstratoContainer {
	private static final long serialVersionUID = 1L;
	private final FragmentoModelo fragmentoModelo = new FragmentoModelo();
	private final JTable tabela = new JTable(fragmentoModelo);
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
		tabela.getColumnModel().getColumn(2).setCellEditor(new FragmentoEditor());
		tabela.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		toolbar.baixar();
	}

	@Override
	public void setJanela(Janela janela) {
		toolbar.setJanela(janela);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		private transient FragmentoListener listener;

		public void ini(Janela janela, FragmentoListener listener) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, ABRIR_EM_FORMULARO, NOVO, BAIXAR, SALVAR,
					COPIAR, APLICAR);
			setListener(listener);
		}

		private void setListener(FragmentoListener listener) {
			aplicarAcao.setEnabled(listener != null);
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
			Object resp = Util.getValorInputDialog(FragmentoContainer.this, "label.id",
					Mensagens.getString("label.nome_fragmento"), Constantes.VAZIO);

			if (resp == null || Util.estaVazio(resp.toString())) {
				return;
			}

			String resumo = resp.toString();

			if (FragmentoProvedor.contem(resumo)) {
				Util.mensagem(FragmentoContainer.this, Mensagens.getString("label.indentificador_ja_existente"));
				return;
			}

			resp = Util.getValorInputDialog(FragmentoContainer.this, "label.id", Mensagens.getString("label.grupo"),
					Constantes.VAZIO);

			if (resp == null || Util.estaVazio(resp.toString())) {
				return;
			}

			String grupo = resp.toString();

			FragmentoProvedor.adicionar(new Fragmento(resumo, grupo));
			fragmentoModelo.fireTableDataChanged();
			ajustarTabela();
		}

		@Override
		protected void baixar() {
			FragmentoProvedor.inicializar();
			if (listener != null) {
				FragmentoProvedor.filtrarPeloGrupo(listener.getGrupoFiltro());
			}
			fragmentoModelo.fireTableDataChanged();
			ajustarTabela();
		}

		@Override
		protected void salvar() {
			FragmentoProvedor.salvar();
		}

		@Override
		protected void copiar() {
			int[] linhas = tabela.getSelectedRows();

			if (linhas != null) {
				for (int i : linhas) {
					Fragmento f = FragmentoProvedor.getFragmento(i);
					String resumo = f.getResumo() + "_" + Constantes.TEMP;
					FragmentoProvedor.adicionar(new Fragmento(resumo, f.getGrupo()));
				}

				fragmentoModelo.fireTableDataChanged();
			}
		}

		@Override
		protected void aplicar() {
			int[] linhas = tabela.getSelectedRows();

			if (linhas != null && linhas.length == 1) {
				Fragmento f = FragmentoProvedor.getFragmento(linhas[0]);
				try {
					listener.aplicarFragmento(f);
				} finally {
					FragmentoProvedor.removerFiltroPeloGrupo();
					if (janela != null) {
						janela.fechar();
					}
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
				return Mensagens.getString(Constantes.LABEL_FRAGMENTO_MIN);
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