package br.com.persist.container;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.text.AbstractDocument;
import javax.swing.text.StyledDocument;

import br.com.persist.comp.BarraButton;
import br.com.persist.comp.CheckBox;
import br.com.persist.comp.Panel;
import br.com.persist.comp.ScrollPane;
import br.com.persist.fichario.Fichario;
import br.com.persist.fmt.Parser;
import br.com.persist.fmt.Tipo;
import br.com.persist.formulario.RequisicaoFormulario;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Action;
import br.com.persist.util.Base64Util;
import br.com.persist.util.Constantes;
import br.com.persist.util.IJanela;
import br.com.persist.util.Icones;
import br.com.persist.util.Preferencias;
import br.com.persist.util.Util;

public class RequisicaoContainer extends AbstratoContainer implements Fichario.IFicharioSalvar {
	private static final long serialVersionUID = 1L;
	private static final String PAINEL_REQUISICAO = "PAINEL REQUISICAO";
	private static final File file = new File("requisicoes");
	private RequisicaoFormulario requisicaoFormulario;
	private final Toolbar toolbar = new Toolbar();
	private Fichario fichario = new Fichario();

	public RequisicaoContainer(IJanela janela, Formulario formulario, String conteudo) {
		super(formulario);
		toolbar.ini(janela);
		montarLayout();
		config();
		abrir(conteudo);
	}

	public RequisicaoFormulario getRequisicaoFormulario() {
		return requisicaoFormulario;
	}

	public void setRequisicaoFormulario(RequisicaoFormulario requisicaoFormulario) {
		this.requisicaoFormulario = requisicaoFormulario;
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
			ativa.getConteudo();
		}

		return null;
	}

	private void abrir(String conteudo) {
		/*
		 * if (!Util.estaVazio(conteudo)) { areaParametros.setText(conteudo);
		 * return; }
		 * 
		 * areaParametros.setText(Constantes.VAZIO);
		 */

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
	}

	@Override
	protected void destacarEmFormulario() {
		formulario.getFichario().getRequisicao().destacarEmFormulario(formulario, this);
	}

	@Override
	protected void clonarEmFormulario() {
		formulario.getFichario().getRequisicao().clonarEmFormulario(formulario, this);
	}

	@Override
	protected void abrirEmFormulario() {
		RequisicaoFormulario.criar(formulario, Constantes.VAZIO);
	}

	@Override
	protected void retornoAoFichario() {
		if (requisicaoFormulario != null) {
			requisicaoFormulario.retornoAoFichario();
		}
	}

	public void setJanela(IJanela janela) {
		toolbar.setJanela(janela);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		private Action formatarAcao = Action.actionIcon("label.formatar_frag_json", Icones.BOLA_VERDE);
		private Action base64Acao = Action.actionIcon("label.criar_base64", Icones.BOLA_AMARELA);
		private Action atualizarAcao = Action.actionIcon("label.requisicao", Icones.URL);
		private CheckBox chkRespostaJson = new CheckBox("label.resposta_json");

		public void ini(IJanela janela) {
			super.ini(janela, true, true, true);
			configButtonDestacar(e -> destacarEmFormulario(), e -> abrirEmFormulario(), e -> retornoAoFichario(),
					e -> clonarEmFormulario());
			configAbrirAutoFichario(Constantes.ABRIR_AUTO_FICHARIO_REQUISICAO);
			configBaixarAcao(e -> abrir(null));

			add(chkRespostaJson);
			addButton(true, atualizarAcao);
			addButton(true, formatarAcao);
			addButton(true, base64Acao);

			eventos();
		}

		private void eventos() {
			chkRespostaJson.setSelected(Preferencias.getBoolean("requisicao_response_json"));
			chkRespostaJson.addActionListener(
					e -> Preferencias.setBoolean("requisicao_response_json", chkRespostaJson.isSelected()));

			atualizarAcao.setActionListener(e -> atualizar());

			formatarAcao.setActionListener(e -> formatar());

			base64Acao.setActionListener(e -> base64());
		}

		@Override
		protected void novo() {
		}

		private void atualizar() {
			Pagina ativa = fichario.getPaginaAtiva();

			if (ativa != null) {
				ativa.atualizar();
			}
		}

		private void formatar() {
			Pagina ativa = fichario.getPaginaAtiva();

			if (ativa != null) {
				ativa.formatar();
			}
		}

		private void base64() {
			Pagina ativa = fichario.getPaginaAtiva();

			if (ativa != null) {
				ativa.base64();
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

	public class Fichario extends JTabbedPane {
		private static final long serialVersionUID = 1L;

		public void pagina(Pagina pag) {
			addTab(pag.getNome(), pag);
		}

		public Pagina getPaginaAtiva() {
			int indice = getSelectedIndex();

			if (indice != -1) {
				return (Pagina) getComponentAt(indice);
			}

			return null;
		}
	}

	public class Pagina extends Panel {
		private static final long serialVersionUID = 1L;
		private final JTextPane areaParametros = new JTextPane();
		private final JTextPane areaResultados = new JTextPane();
		private final File file;

		public Pagina(File file) {
			this.file = file;
			montarLayout();
			abrir(file);
		}

		private void montarLayout() {
			Panel panelParametros = new Panel();
			panelParametros.add(areaParametros);

			Panel panelResultados = new Panel();
			panelResultados.add(areaResultados);

			JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new ScrollPane(panelParametros),
					new ScrollPane(panelResultados));
			split.setDividerLocation(200);

			add(BorderLayout.CENTER, split);
		}

		public String getConteudo() {
			return areaParametros.getText();
		}

		public String getNome() {
			return file.getName();
		}

		private void abrir(/* String conteudo */File file) {
			/*
			 * if (!Util.estaVazio(conteudo)) {
			 * areaParametros.setText(conteudo); return; }
			 */

			areaParametros.setText(Constantes.VAZIO);

			if (file.exists()) {
				try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
					StringBuilder sb = new StringBuilder();
					String linha = br.readLine();

					while (linha != null) {
						sb.append(linha + Constantes.QL2);
						linha = br.readLine();
					}

					areaParametros.setText(sb.toString());
				} catch (Exception ex) {
					Util.stackTraceAndMessage(PAINEL_REQUISICAO, ex, RequisicaoContainer.this);
				}
			}
		}

		public void limpar() {
			areaParametros.setText(Constantes.VAZIO);
		}

		public void salvar() {
			if (!Util.confirmaSalvar(RequisicaoContainer.this, Constantes.TRES)) {
				return;
			}

			try (PrintWriter pw = new PrintWriter(file)) {
				pw.print(areaParametros.getText());
			} catch (Exception ex) {
				Util.stackTraceAndMessage(PAINEL_REQUISICAO, ex, RequisicaoContainer.this);
			}
		}

		public void formatar() {
			if (Util.estaVazio(areaParametros.getText())) {
				return;
			}

			String string = Util.getString(areaParametros);
			areaResultados.setText(Constantes.VAZIO);

			try {
				Parser parser = new Parser();
				Tipo json = parser.parse(string);

				StyledDocument styledDoc = areaResultados.getStyledDocument();

				if (styledDoc instanceof AbstractDocument) {
					AbstractDocument doc = (AbstractDocument) styledDoc;
					json.toString(doc, false, 0);
				}
			} catch (Exception ex) {
				Util.stackTraceAndMessage(PAINEL_REQUISICAO, ex, this);
			}
		}

		public void base64() {
			if (Util.estaVazio(areaParametros.getText())) {
				return;
			}

			String string = Util.getString(areaParametros);
			areaResultados.setText(Constantes.VAZIO);

			try {
				areaResultados.setText(Base64Util.criarBase64(string));
			} catch (Exception ex) {
				Util.stackTraceAndMessage(PAINEL_REQUISICAO, ex, this);
			}
		}

		public void atualizar() {
			if (Util.estaVazio(areaParametros.getText())) {
				return;
			}

			String string = Util.getString(areaParametros);
			areaResultados.setText(Constantes.VAZIO);

			try {
				Parser parser = new Parser();
				Tipo parametros = parser.parse(string);
				String resposta = Util.requisicao(parametros);

				if (!Util.estaVazio(resposta) && toolbar.chkRespostaJson.isSelected()) {
					StyledDocument styledDoc = areaResultados.getStyledDocument();
					Tipo json = parser.parse(resposta);

					if (styledDoc instanceof AbstractDocument) {
						AbstractDocument doc = (AbstractDocument) styledDoc;
						json.toString(doc, false, 0);
					}
				} else {
					areaResultados.setText(resposta);
				}
			} catch (Exception ex) {
				Util.stackTraceAndMessage(PAINEL_REQUISICAO, ex, this);
			}
		}
	}
}