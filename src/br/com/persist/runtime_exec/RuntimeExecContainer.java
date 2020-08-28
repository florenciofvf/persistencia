package br.com.persist.runtime_exec;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;

import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.container.AbstratoContainer;
import br.com.persist.fichario.Fichario;
import br.com.persist.fmt.Parser;
import br.com.persist.fmt.Tipo;
import br.com.persist.icone.Icones;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Action;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Util;

public class RuntimeExecContainer extends AbstratoContainer implements Fichario.IFicharioSalvar {
	private static final long serialVersionUID = 1L;
	private static final File file = new File("runtime_exec");
	private RuntimeExecFormulario runtimeExecFormulario;
	private final Fichario fichario = new Fichario();
	private final Toolbar toolbar = new Toolbar();

	public RuntimeExecContainer(IJanela janela, Formulario formulario, String conteudo, String idPagina) {
		super(formulario);
		toolbar.ini(janela);
		montarLayout();
		config();
		abrir(conteudo, idPagina);
	}

	public RuntimeExecFormulario getRuntimeExecFormulario() {
		return runtimeExecFormulario;
	}

	public void setRuntimeExecFormulario(RuntimeExecFormulario runtimeExecFormulario) {
		this.runtimeExecFormulario = runtimeExecFormulario;
	}

	private void config() {
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), Constantes.EXEC);
		getActionMap().put(Constantes.EXEC, toolbar.atualizarAcao);
	}

	@Override
	public File getFileSalvarAberto() {
		return new File(Constantes.III + getClass().getName());
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, fichario);
	}

	public String getConteudo() {
		Pagina ativa = fichario.getPaginaAtiva();

		if (ativa != null) {
			return ativa.getConteudo();
		}

		return null;
	}

	public String getIdPagina() {
		Pagina ativa = fichario.getPaginaAtiva();

		if (ativa != null) {
			return ativa.getNome();
		}

		return null;
	}

	public int getIndice() {
		return fichario.getIndice();
	}

	private void abrir(String conteudo, String idPagina) {
		fichario.limpar();

		if (file.isDirectory()) {
			File[] files = file.listFiles();

			if (files == null) {
				return;
			}

			for (File f : files) {
				Pagina pagina = new Pagina(f);
				fichario.pagina(pagina);
			}
		}

		fichario.conteudo(conteudo, idPagina);
	}

	@Override
	protected void destacarEmFormulario() {
		formulario.getFichario().getRuntimeExec().destacarEmFormulario(formulario, this);
	}

	@Override
	protected void clonarEmFormulario() {
		formulario.getFichario().getRuntimeExec().clonarEmFormulario(formulario, this);
	}

	@Override
	protected void abrirEmFormulario() {
		RuntimeExecFormulario.criar(formulario, Constantes.VAZIO, null);
	}

	@Override
	protected void retornoAoFichario() {
		if (runtimeExecFormulario != null) {
			runtimeExecFormulario.retornoAoFichario();
		}
	}

	@Override
	public void setJanela(IJanela janela) {
		toolbar.setJanela(janela);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		private Action baixarAtivoAcao = Action.actionIcon("label.baixar_ativo", Icones.BAIXAR);
		private Action atualizarAcao = Action.actionIcon("label.runtime_exec", Icones.EXECUTAR);
		private Action excluirAtivoAcao = Action.actionIcon("label.excluir2", Icones.EXCLUIR);

		public void ini(IJanela janela) {
			super.ini(janela, true, true, true);
			configButtonDestacar(e -> destacarEmFormulario(), e -> abrirEmFormulario(), e -> retornoAoFichario(),
					e -> clonarEmFormulario());
			configAbrirAutoFichario(Constantes.ABRIR_AUTO_FICHARIO_RUNTIME_EXEC);
			configBaixarAcao(e -> abrir(null, null));
			addButton(baixarAtivoAcao);
			addButton(excluirAtivoAcao);
			addButton(true, atualizarAcao);
			configCopiar1Acao(true);
			configCopiar2Acao(true);

			eventos();
		}

		private void eventos() {
			excluirAtivoAcao.setActionListener(e -> excluirAtivo());

			baixarAtivoAcao.setActionListener(e -> abrirAtivo());

			atualizarAcao.setActionListener(e -> atualizar());
		}

		@Override
		protected void novo() {
			Object resp = Util.getValorInputDialog(RuntimeExecContainer.this, "label.id",
					Mensagens.getString("label.nome_arquivo"), Constantes.VAZIO);

			if (resp == null || Util.estaVazio(resp.toString())) {
				return;
			}

			String nome = resp.toString();

			File f = new File(file, nome);

			if (f.exists()) {
				Util.mensagem(RuntimeExecContainer.this, Mensagens.getString("label.indentf_existente"));
				return;
			}

			try {
				if (f.createNewFile()) {
					Pagina pagina = new Pagina(f);
					fichario.pagina(pagina);
				}
			} catch (IOException ex) {
				Util.stackTraceAndMessage(Constantes.PAINEL_RUNTIME_EXEC, ex, RuntimeExecContainer.this);
			}
		}

		@Override
		protected void copiar1() {
			Pagina ativa = fichario.getPaginaAtiva();

			if (ativa != null) {
				ativa.copiar1();
			}
		}

		@Override
		protected void colar1() {
			Pagina ativa = fichario.getPaginaAtiva();

			if (ativa != null) {
				ativa.colar1();
			}
		}

		@Override
		protected void copiar2() {
			Pagina ativa = fichario.getPaginaAtiva();

			if (ativa != null) {
				ativa.copiar2();
			}
		}

		@Override
		protected void colar2() {
			Pagina ativa = fichario.getPaginaAtiva();

			if (ativa != null) {
				ativa.colar2();
			}
		}

		private void excluirAtivo() {
			Pagina ativa = fichario.getPaginaAtiva();

			if (ativa != null) {
				int indice = fichario.getSelectedIndex();
				ativa.excluir();
				fichario.remove(indice);
			}
		}

		private void abrirAtivo() {
			Pagina ativa = fichario.getPaginaAtiva();

			if (ativa != null) {
				ativa.abrir();
			}
		}

		private void atualizar() {
			Pagina ativa = fichario.getPaginaAtiva();

			if (ativa != null) {
				ativa.atualizar();
			}
		}

		@Override
		protected void limpar() {
			Pagina ativa = fichario.getPaginaAtiva();

			if (ativa != null) {
				ativa.limpar();
			}
		}

		@Override
		protected void salvar() {
			Pagina ativa = fichario.getPaginaAtiva();

			if (ativa != null) {
				ativa.salvar();
			}
		}
	}

	private class Fichario extends JTabbedPane {
		private static final long serialVersionUID = 1L;

		private void pagina(Pagina pag) {
			addTab(pag.getNome(), pag);
			int ultimoIndice = getTabCount() - 1;
			setSelectedIndex(ultimoIndice);
		}

		private void limpar() {
			int count = getTabCount();

			while (count > 0) {
				removeTabAt(0);
				count = getTabCount();
			}
		}

		private int getIndice() {
			return getSelectedIndex();
		}

		private Pagina getPaginaAtiva() {
			int indice = getSelectedIndex();

			if (indice != -1) {
				return (Pagina) getComponentAt(indice);
			}

			return null;
		}

		private Pagina getPagina(String idPagina) {
			int total = getTabCount();

			for (int i = 0; i < total; i++) {
				Component cmp = getComponentAt(i);

				if (cmp instanceof Pagina) {
					Pagina p = (Pagina) cmp;

					if (p.getNome().equals(idPagina)) {
						return p;
					}
				}
			}

			return null;
		}

		private void conteudo(String conteudo, String idPagina) {
			if (!Util.estaVazio(conteudo) && !Util.estaVazio(idPagina)) {
				Pagina pagina = getPagina(idPagina);

				if (pagina != null) {
					pagina.areaParametros.setText(conteudo);
				}
			}
		}
	}

	private class Pagina extends Panel {
		private static final long serialVersionUID = 1L;
		private final JTextPane areaParametros = new JTextPane();
		private final JTextPane areaResultados = new JTextPane();
		private final File file;

		private Pagina(File file) {
			this.file = file;
			montarLayout();
			abrir();
		}

		private void montarLayout() {
			Panel panelParametros = new Panel();
			panelParametros.add(areaParametros);

			Panel panelResultados = new Panel();
			panelResultados.add(areaResultados);

			JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new ScrollPane(panelParametros),
					new ScrollPane(panelResultados));
			split.setDividerLocation(Constantes.SIZE.height / 2);

			add(BorderLayout.CENTER, split);
		}

		private String getConteudo() {
			return areaParametros.getText();
		}

		private String getNome() {
			return file.getName();
		}

		private void copiar1() {
			String string = Util.getString(areaParametros);
			Util.setContentTransfered(string);
			areaParametros.requestFocus();
		}

		private void colar1() {
			Util.getContentTransfered(areaParametros);
		}

		private void copiar2() {
			String string = Util.getString(areaResultados);
			Util.setContentTransfered(string);
			areaResultados.requestFocus();
		}

		private void colar2() {
			Util.getContentTransfered(areaResultados);
		}

		private void abrir() {
			areaParametros.setText(Constantes.VAZIO);

			if (file.exists()) {
				try (BufferedReader br = new BufferedReader(
						new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
					StringBuilder sb = new StringBuilder();
					String linha = br.readLine();

					while (linha != null) {
						sb.append(linha + Constantes.QL);
						linha = br.readLine();
					}

					areaParametros.setText(sb.toString());
				} catch (Exception ex) {
					Util.stackTraceAndMessage(Constantes.PAINEL_RUNTIME_EXEC, ex, RuntimeExecContainer.this);
				}
			}
		}

		private void excluir() {
			if (file.exists()) {
				Path path = FileSystems.getDefault().getPath(file.getAbsolutePath());

				try {
					Files.delete(path);
				} catch (IOException e) {
					Util.stackTraceAndMessage(Constantes.PAINEL_RUNTIME_EXEC, e, RuntimeExecContainer.this);
				}
			}
		}

		private void limpar() {
			areaParametros.setText(Constantes.VAZIO);
		}

		private void salvar() {
			if (!Util.confirmaSalvar(RuntimeExecContainer.this, Constantes.TRES)) {
				return;
			}

			try (PrintWriter pw = new PrintWriter(file, StandardCharsets.UTF_8.name())) {
				pw.print(areaParametros.getText());
			} catch (Exception ex) {
				Util.stackTraceAndMessage(Constantes.PAINEL_RUNTIME_EXEC, ex, RuntimeExecContainer.this);
			}
		}

		private void atualizar() {
			if (Util.estaVazio(areaParametros.getText())) {
				return;
			}

			String string = Util.getString(areaParametros);
			areaResultados.setText(Constantes.VAZIO);

			try {
				Parser parser = new Parser();
				Tipo param = parser.parse(string);
				Process process = montarProcess(param);

				if (process == null) {
					return;
				}

				new ThreadInputStream(process.getErrorStream(), areaResultados, "ERROS").start();
				new ThreadInputStream(process.getInputStream(), areaResultados, "SAIDA").start();

				int exit = process.waitFor();
				String conteudo = areaResultados.getText();
				areaResultados.setText(conteudo + Constantes.QL + "EXIT=" + exit);
				areaParametros.requestFocus();
			} catch (Exception ex) {
				Util.stackTraceAndMessage(Constantes.PAINEL_RUNTIME_EXEC, ex, this);
			}
		}

		private Process montarProcess(Tipo objeto) throws IOException {
			ProcessBuilder builder = Util.criarProcessBuilder(objeto);

			if (builder != null) {
				return builder.start();
			}

			return null;
		}
	}

	private class ThreadInputStream extends Thread {
		private final InputStream is;
		private final JTextPane area;
		private final String tipo;

		private ThreadInputStream(InputStream is, JTextPane area, String tipo) {
			this.area = area;
			this.tipo = tipo;
			this.is = is;
		}

		@Override
		public void run() {
			try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
				StringBuilder sb = new StringBuilder();
				String linha = br.readLine();

				while (linha != null) {
					sb.append(linha + Constantes.QL);
					linha = br.readLine();
				}

				if (sb.length() > 0) {
					sb.append(tipo);
					area.setText(sb.toString());
				}
			} catch (Exception ex) {
				Util.stackTraceAndMessage(Constantes.PAINEL_RUNTIME_EXEC, ex, RuntimeExecContainer.this);
			}
		}
	}
}