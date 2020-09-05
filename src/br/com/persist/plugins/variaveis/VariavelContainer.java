package br.com.persist.plugins.variaveis;

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

public class VariavelContainer extends AbstratoContainer {
	private static final long serialVersionUID = 1L;
	private final VariavelModelo variavelModelo = new VariavelModelo();
	private final JTable tabela = new JTable(variavelModelo);
	private VariavelFormulario variavelFormulario;
	private final Toolbar toolbar = new Toolbar();
	private VariavelDialogo variavelDialogo;

	public VariavelContainer(Janela janela, Formulario formulario) {
		super(formulario);
		toolbar.ini(janela);
		montarLayout();
		configurar();
	}

	public VariavelDialogo getVariavelDialogo() {
		return variavelDialogo;
	}

	public void setVariavelDialogo(VariavelDialogo variavelDialogo) {
		this.variavelDialogo = variavelDialogo;
		if (variavelDialogo != null) {
			variavelFormulario = null;
		}
	}

	public VariavelFormulario getVariavelFormulario() {
		return variavelFormulario;
	}

	public void setVariavelFormulario(VariavelFormulario variavelFormulario) {
		this.variavelFormulario = variavelFormulario;
		if (variavelFormulario != null) {
			variavelDialogo = null;
		}
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, new ScrollPane(tabela));
	}

	private void configurar() {
		tabela.getColumnModel().getColumn(1).setCellEditor(new VariavelEditor());
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
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, ABRIR_EM_FORMULARO, BAIXAR, NOVO, SALVAR,
					COPIAR);
		}

		@Override
		protected void destacarEmFormulario() {
			if (formulario.excluirPagina(VariavelContainer.this)) {
				VariavelFormulario.criar(formulario, VariavelContainer.this);

			} else if (variavelDialogo != null) {
				variavelDialogo.excluirContainer();
				VariavelFormulario.criar(formulario, VariavelContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (variavelFormulario != null) {
				variavelFormulario.excluirContainer();
				formulario.adicionarPagina(VariavelContainer.this);

			} else if (variavelDialogo != null) {
				variavelDialogo.excluirContainer();
				formulario.adicionarPagina(VariavelContainer.this);
			}
		}

		@Override
		protected void abrirEmFormulario() {
			if (variavelDialogo != null) {
				variavelDialogo.excluirContainer();
			}
			VariavelFormulario.criar(formulario);
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
			Object resp = Util.getValorInputDialog(VariavelContainer.this, "label.id",
					Mensagens.getString("label.nome_variavel"), Constantes.VAZIO);

			if (resp == null || Util.estaVazio(resp.toString())) {
				return;
			}

			String nome = resp.toString();

			if (VariavelProvedor.contem(nome)) {
				Util.mensagem(VariavelContainer.this, Mensagens.getString("label.indentificador_ja_existente"));
				return;
			}

			VariavelProvedor.adicionar(new Variavel(nome));
			variavelModelo.fireTableDataChanged();
			ajustarTabela();
		}

		@Override
		protected void baixar() {
			VariavelProvedor.inicializar();
			variavelModelo.fireTableDataChanged();
			ajustarTabela();
		}

		@Override
		protected void salvar() {
			VariavelProvedor.salvar();
		}

		@Override
		protected void copiar() {
			int[] linhas = tabela.getSelectedRows();

			if (linhas != null) {
				for (int i : linhas) {
					Variavel v = VariavelProvedor.getVariavel(i);
					String nome = v.getNome() + "_" + Constantes.TEMP;
					VariavelProvedor.adicionar(new Variavel(nome, v.getValor()));
				}

				variavelModelo.fireTableDataChanged();
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
		return VariavelFabrica.class;
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
				return Mensagens.getString(Constantes.LABEL_VARIAVEIS_MIN);
			}

			@Override
			public String getTitulo() {
				return Mensagens.getString(Constantes.LABEL_VARIAVEIS);
			}

			@Override
			public String getHint() {
				return Mensagens.getString(Constantes.LABEL_VARIAVEIS);
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