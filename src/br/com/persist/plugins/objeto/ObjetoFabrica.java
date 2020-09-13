package br.com.persist.plugins.objeto;

import java.awt.Graphics;
import java.awt.event.InputEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import br.com.persist.abstrato.AbstratoFabricaContainer;
import br.com.persist.componente.MenuPadrao1;
import br.com.persist.fichario.Pagina;
import br.com.persist.fichario.PaginaServico;
import br.com.persist.plugins.arquivo.ArquivoProvedor;
import br.com.persist.plugins.objeto.internal.InternalConfig;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Constantes;
import br.com.persist.util.Icones;
import br.com.persist.util.Mensagens;
import br.com.persist.util.PosicaoDimensao;
import br.com.persist.util.Util;
import br.com.persist.xml.XML;

public class ObjetoFabrica extends AbstratoFabricaContainer {

	@Override
	public PaginaServico getPaginaServico() {
		return new ObjetoPaginaServico();
	}

	private class ObjetoPaginaServico implements PaginaServico {
		@Override
		public Pagina criarPagina(Formulario formulario, String stringPersistencia) {
			File file = ArquivoProvedor.restaurarStringPersistencia(stringPersistencia);
			ObjetoContainer container = new ObjetoContainer(null, formulario);
			container.setAbortarFecharComESCSuperficie(true);
			container.abrirArquivo(file);
			return container;
		}
	}

	@Override
	public List<JMenuItem> criarMenuItens(Formulario formulario, JMenu menu) {
		if (menu.getItemCount() > 0) {
			menu.addSeparator();
		}

		List<JMenuItem> lista = new ArrayList<>();

		JMenuItem itemNovo = new JMenuItem(Mensagens.getString(Constantes.LABEL_NOVO), Icones.CUBO);
		itemNovo.addActionListener(e -> novo(formulario));
		lista.add(itemNovo);

		lista.add(new MenuAbrir(formulario));
		return lista;
	}

	private class MenuAbrir extends MenuPadrao1 {
		private static final long serialVersionUID = 1L;

		private MenuAbrir(Formulario formulario) {
			super("label.abrir", Icones.ABRIR, false);
			formularioAcao.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('F', InputEvent.CTRL_MASK));
			ficharioAcao.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('A', InputEvent.CTRL_MASK));

			formularioAcao.setActionListener(e -> abrirEmFormulario(formulario));
			ficharioAcao.setActionListener(e -> abrirNoFichario(formulario));
		}

		private void abrirNoFichario(Formulario formulario) {
			File[] files = getSelectedFiles(formulario, true);

			if (files != null) {
				for (File file : files) {
					Pagina pagina = getPaginaServico().criarPagina(formulario, file.getAbsolutePath());
					ObjetoProvedor.setParentFile(file.getParentFile());
					formulario.adicionarPagina(pagina);
				}
			}
		}

		private void abrirEmFormulario(Formulario formulario) {
			File[] files = getSelectedFiles(formulario, true);

			if (files != null) {
				for (File file : files) {
					abrirNoFormulario(formulario, file);
				}
			}
		}

		private File[] getSelectedFiles(Formulario formulario, boolean multiSelection) {
			JFileChooser fileChooser = Util.criarFileChooser(ObjetoProvedor.getParentFile(), multiSelection);
			int opcao = fileChooser.showOpenDialog(formulario);
			if (opcao != JFileChooser.APPROVE_OPTION) {
				return new File[0];
			}
			return fileChooser.getSelectedFiles();
		}
	}

	private void novo(Formulario formulario) {
		Pagina pagina = getPaginaServico().criarPagina(formulario, Constantes.VAZIO);
		formulario.adicionarPagina(pagina);
	}

	public static void abrirNoFormulario(Formulario formulario, File file) {
		if (file == null || !file.isFile()) {
			return;
		}

		ObjetoFormulario form = ObjetoFormulario.criar(formulario, file);
		form.abrirArquivo(file);
		formulario.checarPreferenciasLarguraAltura();
		PosicaoDimensao pd = formulario.criarPosicaoDimensaoSeValido();

		if (pd != null) {
			form.setBounds(pd.getX(), pd.getY(), pd.getLargura(), pd.getAltura());
		} else {
			form.setLocationRelativeTo(formulario);
		}

		form.setVisible(true);
	}

	public static void abrirNoFormulario(Formulario formulario, String stringPersistencia, Graphics g,
			InternalConfig config) {
		File file = ArquivoProvedor.restaurarStringPersistencia(stringPersistencia);

		if (file == null || !file.isFile()) {
			return;
		}

		try {
			ObjetoColetor objetoColetor = new ObjetoColetor();
			XML.processar(file, new ObjetoHandler(objetoColetor));
			abrirNoFormulario(formulario, file, objetoColetor, g, config);
		} catch (Exception ex) {
			Util.stackTraceAndMessage("ABRIR: " + file.getAbsolutePath(), ex, formulario);
		}
	}

	private static void abrirNoFormulario(Formulario formulario, File file, ObjetoColetor coletor, Graphics g,
			InternalConfig config) {
		ObjetoFormulario form = ObjetoFormulario.criar(formulario, file);
		form.abrirArquivo(file, coletor, g, config);
		formulario.checarPreferenciasLarguraAltura();
		PosicaoDimensao pd = formulario.criarPosicaoDimensaoSeValido();

		if (pd != null) {
			form.setBounds(pd.getX(), pd.getY(), pd.getLargura(), pd.getAltura());
		} else {
			form.setLocationRelativeTo(formulario);
		}

		form.setVisible(true);
	}
}