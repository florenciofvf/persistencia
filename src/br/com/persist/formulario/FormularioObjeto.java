package br.com.persist.formulario;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JToolBar;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import br.com.persist.Objeto;
import br.com.persist.comp.Button;
import br.com.persist.comp.ScrollPane;
import br.com.persist.tabela.CabecalhoColuna;
import br.com.persist.tabela.CellRenderer;
import br.com.persist.tabela.Coluna;
import br.com.persist.tabela.ModeloOrdenacao;
import br.com.persist.tabela.ModeloRegistro;
import br.com.persist.tabela.Persistencia;
import br.com.persist.tabela.Tabela;
import br.com.persist.util.Acao;
import br.com.persist.util.Icones;

public class FormularioObjeto extends JFrame {
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private final Objeto objeto;
	private Tabela tabela;

	public FormularioObjeto(Formulario formulario, Objeto objeto) {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle(objeto.getId());
		this.objeto = objeto;
		setSize(800, 600);
		setLocationRelativeTo(formulario);
		processarObjeto("");
		montarLayout();
		setVisible(true);
	}

	private void processarObjeto(String complemento) {
		String[] chaves = objeto.getChaves().trim().split(",");
		String consulta = "SELECT * FROM " + objeto.getTabela() + " WHERE 1=1" + complemento;

		try {
			ModeloRegistro modeloRegistro = Persistencia.criarModeloRegistro(consulta, chaves);
			ModeloOrdenacao modeloOrdenacao = new ModeloOrdenacao(modeloRegistro);
			tabela = new Tabela(modeloOrdenacao);

			TableColumnModel columnModel = tabela.getColumnModel();
			List<Coluna> colunas = modeloRegistro.getColunas();

			for (int i = 0; i < colunas.size(); i++) {
				TableColumn tableColumn = columnModel.getColumn(i);
				Coluna coluna = colunas.get(i);

				if (coluna.isChave()) {
					tableColumn.setCellRenderer(new CellRenderer());
				}

				CabecalhoColuna cabecalhoColuna = new CabecalhoColuna(modeloOrdenacao, coluna);
				tableColumn.setHeaderRenderer(cabecalhoColuna);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void montarLayout() {
		setLayout(new BorderLayout());
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, new ScrollPane(tabela));
	}

	private class Toolbar extends JToolBar {
		private static final long serialVersionUID = 1L;

		public Toolbar() {
			add(new Button(new FecharAcao(false)));
			addSeparator();
			add(new Button(new ExcluirRegistrosAcao()));
		}
	}

	private class FecharAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public FecharAcao(boolean menu) {
			super(menu, "label.fechar", Icones.SAIR);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			dispose();
		}
	}

	private class ExcluirRegistrosAcao extends Acao {
		private static final long serialVersionUID = 1L;

		public ExcluirRegistrosAcao() {
			super(false, "label.excluir_registro", Icones.EXCLUIR);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
		}
	}
}