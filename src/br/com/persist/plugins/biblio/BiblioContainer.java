package br.com.persist.plugins.biblio;

import static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO;
import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.DESTACAR_EM_FORMULARIO;
import static br.com.persist.componente.BarraButtonEnum.EXCLUIR;
import static br.com.persist.componente.BarraButtonEnum.NOVO;
import static br.com.persist.componente.BarraButtonEnum.RETORNAR_AO_FICHARIO;
import static br.com.persist.componente.BarraButtonEnum.SALVAR;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Window;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.abstrato.PluginBasico;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.TabbedPane;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;

public class BiblioContainer extends AbstratoContainer implements PluginBasico {
	private final BiblioJarModelo biblioJarModelo = new BiblioJarModelo();
	private final BiblioModelo biblioModelo = new BiblioModelo();
	private final JTable tabelaJar = new JTable(biblioJarModelo);
	private final FicharioInner fichario = new FicharioInner();
	private final JTable tabela = new JTable(biblioModelo);
	private static final Logger LOG = Logger.getGlobal();
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private BiblioFormulario biblioFormulario;
	private BiblioDialogo biblioDialogo;

	public BiblioContainer(Janela janela, Formulario formulario) {
		super(formulario);
		toolbar.ini(janela);
		montarLayout();
		toolbar.baixar();
	}

	public BiblioDialogo getBiblioDialogo() {
		return biblioDialogo;
	}

	public void setBiblioDialogo(BiblioDialogo biblioDialogo) {
		this.biblioDialogo = biblioDialogo;
		if (biblioDialogo != null) {
			biblioFormulario = null;
		}
	}

	public BiblioFormulario getBiblioFormulario() {
		return biblioFormulario;
	}

	public void setBiblioFormulario(BiblioFormulario biblioFormulario) {
		this.biblioFormulario = biblioFormulario;
		if (biblioFormulario != null) {
			biblioDialogo = null;
		}
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, fichario);
		fichario.addTab("label.jars", Icones.CONFIG, new ScrollPane(tabelaJar));
		fichario.addTab("label.outros", Icones.CONFIG2, new ScrollPane(tabela));
	}

	private class FicharioInner extends TabbedPane {
		private static final long serialVersionUID = 1L;
	}

	@Override
	public void setJanela(Janela janela) {
		toolbar.setJanela(janela);
	}

	private class Toolbar extends BarraButton {
		private Action descerAcao = actionIcon("label.descer", Icones.BAIXAR2);
		private Action subirAcao = actionIcon("label.subir", Icones.TOP);
		private static final long serialVersionUID = 1L;

		public void ini(Janela janela) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, ABRIR_EM_FORMULARO, NOVO, BAIXAR, SALVAR,
					EXCLUIR);
			addButton(true, descerAcao);
			addButton(subirAcao);
			descerAcao.setActionListener(e -> descer());
			subirAcao.setActionListener(e -> subir());
		}

		@Override
		protected void destacarEmFormulario() {
			if (formulario.excluirPagina(BiblioContainer.this)) {
				BiblioFormulario.criar(formulario, BiblioContainer.this);
			} else if (biblioDialogo != null) {
				biblioDialogo.excluirContainer();
				BiblioFormulario.criar(formulario, BiblioContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (biblioFormulario != null) {
				biblioFormulario.excluirContainer();
				formulario.adicionarPagina(BiblioContainer.this);
			} else if (biblioDialogo != null) {
				biblioDialogo.excluirContainer();
				formulario.adicionarPagina(BiblioContainer.this);
			}
		}

		@Override
		protected void abrirEmFormulario() {
			if (biblioDialogo != null) {
				biblioDialogo.excluirContainer();
			}
			BiblioFormulario.criar(formulario);
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
			int[] linhas = tabela.getSelectedRows();
			Biblio biblio = new Biblio("#-" + BiblioProvedor.nextInt());
			if (linhas != null && linhas.length == 1) {
				int indice = linhas[0];
				BiblioProvedor.adicionar(biblio, indice);
				SwingUtilities.invokeLater(() -> tabela.setRowSelectionInterval(indice, indice));
			} else {
				BiblioProvedor.adicionar(biblio);
			}
			biblioModelo.fireTableDataChanged();
		}

		@Override
		protected void baixar() {
			BiblioProvedor.inicializar();
			biblioModelo.fireTableDataChanged();
		}

		@Override
		protected void salvar() {
			try {
				BiblioProvedor.salvar();
				salvoMensagem();
			} catch (Exception e) {
				LOG.log(Level.SEVERE, Constantes.ERRO, e);
			}
		}

		@Override
		protected void excluir() {
			int[] linhas = tabela.getSelectedRows();
			if (linhas != null && linhas.length == 1 && Util.confirmaExclusao(BiblioContainer.this, false)) {
				BiblioProvedor.excluir(linhas[0]);
				biblioModelo.fireTableDataChanged();
			}
		}

		private void subir() {
			int[] linhas = tabela.getSelectedRows();
			int registros = biblioModelo.getRowCount();
			if (linhas != null && linhas.length == 1 && registros > 1 && linhas[0] > 0) {
				int i = BiblioProvedor.anterior(linhas[0]);
				biblioModelo.fireTableDataChanged();
				if (i != -1) {
					tabela.setRowSelectionInterval(i, i);
				}
			}
		}

		private void descer() {
			int[] linhas = tabela.getSelectedRows();
			int registros = biblioModelo.getRowCount();
			if (linhas != null && linhas.length == 1 && registros > 1 && linhas[0] + 1 < registros) {
				int i = BiblioProvedor.proximo(linhas[0]);
				biblioModelo.fireTableDataChanged();
				if (i != -1) {
					tabela.setRowSelectionInterval(i, i);
				}
			}
		}
	}

	@Override
	public void adicionadoAoFichario(Fichario fichario) {
		toolbar.adicionadoAoFichario();
	}

	@Override
	public void windowOpenedHandler(Window window) {
		toolbar.windowOpenedHandler(window);
	}

	@Override
	public void dialogOpenedHandler(Dialog dialog) {
		toolbar.dialogOpenedHandler(dialog);
	}

	@Override
	public String getStringPersistencia() {
		return Constantes.VAZIO;
	}

	@Override
	public Class<?> getClasseFabrica() {
		return BiblioFabrica.class;
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
				return BiblioMensagens.getString(BiblioConstantes.LABEL_BIBLIO_MIN);
			}

			@Override
			public String getTitulo() {
				return BiblioMensagens.getString(BiblioConstantes.LABEL_BIBLIO);
			}

			@Override
			public String getHint() {
				return BiblioMensagens.getString(BiblioConstantes.LABEL_BIBLIO);
			}

			@Override
			public Icon getIcone() {
				return Icones.COR;
			}
		};
	}
}