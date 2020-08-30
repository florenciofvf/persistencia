package br.com.persist.variaveis;

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

public class VariaveisContainer extends AbstratoContainer implements IIni, IFicharioSalvar {
	private static final long serialVersionUID = 1L;
	private final VariaveisModelo variaveisModelo = new VariaveisModelo();
	private final JTable tabela = new JTable(variaveisModelo);
	private VariaveisFormulario variaveisFormulario;
	private final Toolbar toolbar = new Toolbar();

	public VariaveisContainer(IJanela janela, Formulario formulario) {
		super(formulario);
		toolbar.ini(janela);
		montarLayout();
		configurar();
	}

	public VariaveisFormulario getVariaveisFormulario() {
		return variaveisFormulario;
	}

	public void setVariaveisFormulario(VariaveisFormulario variaveisFormulario) {
		this.variaveisFormulario = variaveisFormulario;
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
			configAbrirAutoFichario(Constantes.ABRIR_AUTO_FICHARIO_VARIAVEL);
			configBaixarAcao(null);
			addButton(copiarAcao);

			eventos();
		}

		@Override
		protected void limpar() {
			VariaveisModelo.novo();
			variaveisModelo.fireTableDataChanged();
		}

		@Override
		protected void salvar() {
			try {
				VariaveisModelo.salvar();
			} catch (Exception ex) {
				Util.stackTraceAndMessage("SALVAR: ", ex, VariaveisContainer.this);
			}
		}

		private void eventos() {
			getLimparAcao().rotulo(Constantes.LABEL_NOVO);

			getBaixarAcao().setActionListener(e -> {
				VariaveisModelo.inicializar();
				variaveisModelo.fireTableDataChanged();
				TabelaUtil.ajustar(tabela, getGraphics());
			});

			copiarAcao.setActionListener(e -> {
				int[] linhas = tabela.getSelectedRows();

				if (linhas != null && linhas.length > 0) {
					for (int i : linhas) {
						ChaveValor cv = VariaveisModelo.getChaveValor(i);
						ChaveValor clone = cv.clonar();
						clone.setChave(cv.getChave() + "_" + Constantes.TEMP);
						VariaveisModelo.adicionar(clone);
					}

					variaveisModelo.fireTableDataChanged();
				}
			});
		}
	}

	@Override
	protected void destacarEmFormulario() {
		if (formulario.excluirFicharioAba(this)) {
			VariaveisFormulario.criar(formulario, this);
		}
	}

	@Override
	protected void clonarEmFormulario() {
		if (formulario.excluirFicharioAba(this)) {
			VariaveisFormulario.criar(formulario);
		}
	}

	@Override
	protected void abrirEmFormulario() {
		VariaveisFormulario.criar(formulario);
	}

	@Override
	protected void retornoAoFichario() {
		if (variaveisFormulario != null) {
			variaveisFormulario.retornoAoFichario();
			formulario.adicionarFicharioAba(this);
		}
	}

	@Override
	public File getFileSalvarAberto() {
		return new File(getClasseFabricaEContainerDetalhe());
	}

	@Override
	public String getClasseFabricaEContainerDetalhe() {
		return classeFabricaEContainer(VariaveisFabrica.class, VariaveisContainer.class);
	}

	@Override
	public String getChaveTituloMin() {
		return Constantes.LABEL_VARIAVEIS_MIN;
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public String getChaveTitulo() {
		return Constantes.LABEL_VARIAVEIS;
	}

	@Override
	public String getHintTitulo() {
		return Mensagens.getString(Constantes.LABEL_VARIAVEIS);
	}

	@Override
	public Icon getIcone() {
		return Icones.VAR;
	}
}