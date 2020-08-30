package br.com.persist.mapeamento;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JTable;

import br.com.persist.chave_valor.ChaveValor;
import br.com.persist.chave_valor.ChaveValorEditor;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.ScrollPane;
import br.com.persist.container.AbstratoContainer;
import br.com.persist.fichario.IFicharioSalvar;
import br.com.persist.icone.Icones;
import br.com.persist.principal.Formulario;
import br.com.persist.tabela.TabelaUtil;
import br.com.persist.util.Action;
import br.com.persist.util.Constantes;
import br.com.persist.util.IIni;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Util;

public class MapeamentoContainer extends AbstratoContainer implements IIni, IFicharioSalvar {
	private static final long serialVersionUID = 1L;
	private final MapeamentoModelo mapeamentoModelo = new MapeamentoModelo();
	private final JTable tabela = new JTable(mapeamentoModelo);
	private MapeamentoFormulario mapeamentoFormulario;
	private final Toolbar toolbar = new Toolbar();

	public MapeamentoContainer(IJanela janela, Formulario formulario) {
		super(formulario);
		toolbar.ini(janela);
		montarLayout();
		configurar();
	}

	public MapeamentoFormulario getMapeamentoFormulario() {
		return mapeamentoFormulario;
	}

	public void setMapeamentoFormulario(MapeamentoFormulario mapeamentoFormulario) {
		this.mapeamentoFormulario = mapeamentoFormulario;
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, new ScrollPane(tabela));
	}

	private void configurar() {
		tabela.getColumnModel().getColumn(1).setCellEditor(new ChaveValorEditor());
		tabela.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		toolbar.getBaixarAcao().actionPerformed(null);
	}

	@Override
	public void ini(Graphics graphics) {
		TabelaUtil.ajustar(tabela, graphics);
	}

	@Override
	public void setJanela(IJanela janela) {
		toolbar.setJanela(janela);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		private Action copiarAcao = Action.actionIcon("label.copiar", Icones.COPIA);

		public void ini(IJanela janela) {
			super.ini(janela, true, true);
			configButtonDestacar(e -> destacarEmFormulario(), e -> abrirEmFormulario(), e -> retornoAoFichario(),
					e -> clonarEmFormulario());
			configBaixarAcao(null);
			addButton(copiarAcao);

			eventos();
		}

		@Override
		protected void limpar() {
			MapeamentoModelo.novo();
			mapeamentoModelo.fireTableDataChanged();
		}

		@Override
		protected void salvar() {
			try {
				MapeamentoModelo.salvar();
			} catch (Exception ex) {
				Util.stackTraceAndMessage("SALVAR: ", ex, MapeamentoContainer.this);
			}
		}

		private void eventos() {
			getLimparAcao().rotulo(Constantes.LABEL_NOVO);

			getBaixarAcao().setActionListener(e -> {
				MapeamentoModelo.inicializar();
				mapeamentoModelo.fireTableDataChanged();
				TabelaUtil.ajustar(tabela, getGraphics());
			});

			copiarAcao.setActionListener(e -> {
				int[] linhas = tabela.getSelectedRows();

				if (linhas != null && linhas.length > 0) {
					for (int i : linhas) {
						ChaveValor cv = MapeamentoModelo.getChaveValor(i);
						ChaveValor clone = cv.clonar();
						clone.setChave(cv.getChave() + "_" + Constantes.TEMP);
						MapeamentoModelo.adicionar(clone);
					}

					mapeamentoModelo.fireTableDataChanged();
				}
			});
		}
	}

	@Override
	protected void destacarEmFormulario() {
		if (formulario.excluirFicharioAba(this)) {
			MapeamentoFormulario.criar(formulario, this);
		}
	}

	@Override
	protected void clonarEmFormulario() {
		if (formulario.excluirFicharioAba(this)) {
			MapeamentoFormulario.criar(formulario);
		}
	}

	@Override
	protected void abrirEmFormulario() {
		MapeamentoFormulario.criar(formulario);
	}

	@Override
	protected void retornoAoFichario() {
		if (mapeamentoFormulario != null) {
			mapeamentoFormulario.retornoAoFichario();
			formulario.adicionarFicharioAba(this);
		}
	}

	@Override
	public File getFileSalvarAberto() {
		return new File(getClasseFabricaEContainerDetalhe());
	}

	@Override
	public String getClasseFabricaEContainerDetalhe() {
		return classeFabricaEContainer(MapeamentoFabrica.class, MapeamentoContainer.class);
	}

	@Override
	public String getChaveTituloMin() {
		return Constantes.LABEL_MAPEAMENTOS_MIN;
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public String getChaveTitulo() {
		return Constantes.LABEL_MAPEAMENTOS;
	}

	@Override
	public String getHintTitulo() {
		return Mensagens.getString(Constantes.LABEL_MAPEAMENTOS);
	}

	@Override
	public Icon getIcone() {
		return Icones.REFERENCIA;
	}
}