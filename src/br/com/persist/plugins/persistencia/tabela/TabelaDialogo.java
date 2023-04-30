package br.com.persist.plugins.persistencia.tabela;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.util.List;

import br.com.persist.abstrato.AbstratoDialogo;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.TransferidorTabular;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.ButtonPopup;
import br.com.persist.componente.Janela;
import br.com.persist.componente.OrdemModel;
import br.com.persist.componente.OrdemTable;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.plugins.persistencia.MemoriaModelo;

public class TabelaDialogo extends AbstratoDialogo {
	private static final long serialVersionUID = 1L;
	private final TabelaContainer container;

	private TabelaDialogo(Dialog dialog, String titulo, MemoriaModelo modelo) {
		super(dialog, titulo + " [" + modelo.getRowCount() + "]");
		container = new TabelaContainer(this, modelo);
		montarLayout();
	}

	private TabelaDialogo(Frame frame, String titulo, MemoriaModelo modelo) {
		super(frame, titulo + " [" + modelo.getRowCount() + "]");
		container = new TabelaContainer(this, modelo);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public static void criar(Component c, String titulo, MemoriaModelo modelo) {
		Component comp = Util.getViewParent(c);
		TabelaDialogo form = null;
		if (comp instanceof Frame) {
			form = new TabelaDialogo((Frame) comp, titulo, modelo);
			Util.configSizeLocation((Frame) comp, form, c);
		} else if (comp instanceof Dialog) {
			form = new TabelaDialogo((Dialog) comp, titulo, modelo);
			Util.configSizeLocation((Dialog) comp, form, c);
		} else {
			form = new TabelaDialogo((Frame) null, titulo, modelo);
			form.setLocationRelativeTo(comp != null ? comp : c);
		}
		form.setVisible(true);
	}

	@Override
	public void dialogOpenedHandler(Dialog dialog) {
		container.dialogOpenedHandler(dialog);
	}
}

class TabelaMemoria extends OrdemTable {
	private static final long serialVersionUID = 1L;

	public TabelaMemoria(MemoriaModelo modelo) {
		super(new OrdemModel(modelo));
	}
}

class TabelaContainer extends Panel {
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private final TabelaMemoria tabela;

	public TabelaContainer(Janela janela, MemoriaModelo modelo) {
		tabela = new TabelaMemoria(modelo);
		toolbar.ini(janela);
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, new ScrollPane(tabela));
	}

	private class Toolbar extends BarraButton {
		private ButtonCopiar buttonCopiar = new ButtonCopiar();
		private static final long serialVersionUID = 1L;

		public void ini(Janela janela) {
			super.ini(janela);
			add(buttonCopiar);
		}

		private class ButtonCopiar extends ButtonPopup {
			private Action umaColunaSemAcao = actionMenu("label.uma_coluna_sem_aspas");
			private Action umaColunaComAcao = actionMenu("label.uma_coluna_com_aspas");
			private Action transferidorAcao = actionMenu("label.transferidor");
			private Action tabularAcao = actionMenu("label.tabular");
			private Action htmlAcao = actionMenu("label.html");
			private static final long serialVersionUID = 1L;

			private ButtonCopiar() {
				super("label.copiar_tabela", Icones.TABLE2);
				addMenuItem(htmlAcao);
				addMenuItem(true, tabularAcao);
				addMenuItem(true, transferidorAcao);
				addMenuItem(true, umaColunaSemAcao);
				addMenuItem(umaColunaComAcao);
				umaColunaSemAcao.setActionListener(e -> umaColuna(false));
				umaColunaComAcao.setActionListener(e -> umaColuna(true));
				transferidorAcao.setActionListener(e -> processar(0));
				tabularAcao.setActionListener(e -> processar(1));
				htmlAcao.setActionListener(e -> processar(2));
			}

			private void umaColuna(boolean comAspas) {
				String titulo = comAspas ? Mensagens.getString("label.uma_coluna_com_aspas")
						: Mensagens.getString("label.uma_coluna_sem_aspas");
				Util.copiarColunaUnicaString(titulo, tabela, comAspas, null);
			}

			private void processar(int tipo) {
				List<Integer> indices = Util.getIndicesLinha(tabela);
				TransferidorTabular transferidor = Util.criarTransferidorTabular(tabela, null, indices);
				if (transferidor != null) {
					if (tipo == 0) {
						Util.setTransfered(transferidor);
					} else if (tipo == 1) {
						Util.setContentTransfered(transferidor.getTabular());
					} else if (tipo == 2) {
						Util.setContentTransfered(transferidor.getHtml());
					}
				}
			}
		}
	}

	void dialogOpenedHandler(Dialog dialog) {
		Util.ajustar(tabela, getGraphics());
	}
}