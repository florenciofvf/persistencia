package br.com.persist.plugins.mapeamento;

import static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO;
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

public class MapeamentoContainer extends AbstratoContainer {
	private static final long serialVersionUID = 1L;
	private final MapeamentoModelo mapeamentoModelo = new MapeamentoModelo();
	private final JTable tabela = new JTable(mapeamentoModelo);
	private MapeamentoFormulario mapeamentoFormulario;
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
		tabela.getColumnModel().getColumn(1).setCellEditor(new MapeamentoEditor());
		tabela.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		toolbar.baixar();
	}

	@Override
	public void setJanela(Janela janela) {
		toolbar.setJanela(janela);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;

		public void ini(Janela janela) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, ABRIR_EM_FORMULARO, NOVO, BAIXAR, SALVAR,
					COPIAR);
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
			Object resp = Util.getValorInputDialog(MapeamentoContainer.this, "label.id",
					Mensagens.getString("label.nome_mapeamento"), Constantes.VAZIO);

			if (resp == null || Util.estaVazio(resp.toString())) {
				return;
			}

			String nome = resp.toString();

			if (MapeamentoProvedor.contem(nome)) {
				Util.mensagem(MapeamentoContainer.this, Mensagens.getString("label.indentificador_ja_existente"));
				return;
			}

			MapeamentoProvedor.adicionar(new Mapeamento(nome));
			mapeamentoModelo.fireTableDataChanged();
			ajustarTabela();
		}

		@Override
		protected void baixar() {
			MapeamentoProvedor.inicializar();
			mapeamentoModelo.fireTableDataChanged();
			ajustarTabela();
		}

		@Override
		protected void salvar() {
			MapeamentoProvedor.salvar();
		}

		@Override
		protected void copiar() {
			int[] linhas = tabela.getSelectedRows();

			if (linhas != null) {
				for (int i : linhas) {
					Mapeamento v = MapeamentoProvedor.getMapeamento(i);
					String nome = v.getNome() + "_" + Constantes.TEMP;
					MapeamentoProvedor.adicionar(new Mapeamento(nome, v.getValor()));
				}

				mapeamentoModelo.fireTableDataChanged();
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
				return Mensagens.getString(Constantes.LABEL_MAPEAMENTOS_MIN);
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