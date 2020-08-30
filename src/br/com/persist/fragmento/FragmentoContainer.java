package br.com.persist.fragmento;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JTable;

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

public class FragmentoContainer extends AbstratoContainer implements IIni, IFicharioSalvar {
	private static final long serialVersionUID = 1L;
	private final FragmentoModelo fragmentoModelo = new FragmentoModelo();
	private final JTable tabela = new JTable(fragmentoModelo);
	private final transient FragmentoListener listener;
	private FragmentoFormulario fragmentoFormulario;
	private final Toolbar toolbar = new Toolbar();

	public FragmentoContainer(IJanela janela, Formulario formulario, FragmentoListener listener) {
		super(formulario);
		this.listener = listener;
		toolbar.ini(janela);
		montarLayout();
		configurar();
	}

	public FragmentoFormulario getFragmentoFormulario() {
		return fragmentoFormulario;
	}

	public void setFragmentoFormulario(FragmentoFormulario fragmentoFormulario) {
		this.fragmentoFormulario = fragmentoFormulario;
	}

	private void montarLayout() {
		toolbar.configListener();
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, new ScrollPane(tabela));
	}

	private void configurar() {
		tabela.getColumnModel().getColumn(2).setCellEditor(new ChaveValorEditor());
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
			FragmentoModelo.novo();
			fragmentoModelo.fireTableDataChanged();
		}

		@Override
		protected void salvar() {
			try {
				FragmentoModelo.salvar();
			} catch (Exception ex) {
				Util.stackTraceAndMessage("SALVAR: ", ex, FragmentoContainer.this);
			}
		}

		private void eventos() {
			getLimparAcao().rotulo(Constantes.LABEL_NOVO);

			getBaixarAcao().setActionListener(e -> {
				if (listener != null) {
					FragmentoModelo.reiniciar();
					FragmentoModelo.filtar(listener.getGruposFiltro());
				} else {
					FragmentoModelo.inicializar();
				}

				FragmentoModelo.ordenar();
				fragmentoModelo.fireTableDataChanged();
				TabelaUtil.ajustar(tabela, getGraphics());
			});

			copiarAcao.setActionListener(e -> {
				int[] linhas = tabela.getSelectedRows();

				if (linhas != null && linhas.length > 0) {
					for (int i : linhas) {
						Fragmento f = FragmentoModelo.getFragmento(i);
						FragmentoModelo.adicionar(f.clonar());
					}

					fragmentoModelo.fireTableDataChanged();
				}
			});
		}

		private void configListener() {
			if (listener != null) {
				Action configAcao = Action.actionIcon("label.fragmento", Icones.SUCESSO, e -> {
					int[] linhas = tabela.getSelectedRows();

					if (linhas != null && linhas.length == 1) {
						Fragmento f = FragmentoModelo.getFragmento(linhas[0]);
						listener.configFragmento(f);

						if (janela != null) {
							janela.fechar();
						}
					}
				});

				addButton(configAcao);
			}
		}
	}

	@Override
	protected void destacarEmFormulario() {
		if (formulario.excluirFicharioAba(this)) {
			FragmentoFormulario.criar(formulario, this);
		}
	}

	@Override
	protected void clonarEmFormulario() {
		if (formulario.excluirFicharioAba(this)) {
			FragmentoFormulario.criar(formulario);
		}
	}

	@Override
	protected void abrirEmFormulario() {
		FragmentoFormulario.criar(formulario);
	}

	@Override
	protected void retornoAoFichario() {
		if (fragmentoFormulario != null) {
			fragmentoFormulario.retornoAoFichario();
			formulario.adicionarFicharioAba(this);
		}
	}

	@Override
	public File getFileSalvarAberto() {
		return new File(getClasseFabricaEContainerDetalhe());
	}

	@Override
	public String getClasseFabricaEContainerDetalhe() {
		return classeFabricaEContainer(FragmentoFabrica.class, FragmentoContainer.class);
	}

	@Override
	public String getChaveTituloMin() {
		return Constantes.LABEL_FRAGMENTO_MIN;
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public String getChaveTitulo() {
		return Constantes.LABEL_FRAGMENTO;
	}

	@Override
	public String getHintTitulo() {
		return Mensagens.getString(Constantes.LABEL_FRAGMENTO);
	}

	@Override
	public Icon getIcone() {
		return Icones.FRAGMENTO;
	}
}