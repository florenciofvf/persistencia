package br.com.persist.plugins.requisicao;

import static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO;
import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.CLONAR_EM_FORMULARIO;
import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COLAR2;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR2;
import static br.com.persist.componente.BarraButtonEnum.DESTACAR_EM_FORMULARIO;
import static br.com.persist.componente.BarraButtonEnum.LIMPAR;
import static br.com.persist.componente.BarraButtonEnum.NOVO;
import static br.com.persist.componente.BarraButtonEnum.RETORNAR_AO_FICHARIO;
import static br.com.persist.componente.BarraButtonEnum.SALVAR;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.Icon;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;

import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.componente.Action;
//import br.com.persist.chave_valor.ChaveValor;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.Janela;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.principal.Formulario;
import br.com.persist.util.Base64Util;
import br.com.persist.util.Constantes;
//import br.com.persist.fmt.Parser;
//import br.com.persist.fmt.Tipo;
import br.com.persist.util.Icones;
import br.com.persist.util.Mensagens;
import br.com.persist.util.Preferencias;
import br.com.persist.util.Util;
//import br.com.persist.variaveis.VariaveisModelo;

public class RequisicaoContainer extends AbstratoContainer {
	private static final long serialVersionUID = 1L;
	private final RequisicaoFichario fichario = new RequisicaoFichario();
	private static final File file = new File("requisicoes");
	private RequisicaoFormulario requisicaoFormulario;
	private final Toolbar toolbar = new Toolbar();
	private RequisicaoDialogo requisicaoDialogo;

	public RequisicaoContainer(Janela janela, Formulario formulario, String conteudo, String idPagina) {
		super(formulario);
		toolbar.ini(janela);
		montarLayout();
		configurar();
		abrir(conteudo, idPagina);
	}

	public RequisicaoDialogo getRequisicaoDialogo() {
		return requisicaoDialogo;
	}

	public void setRequisicaoDialogo(RequisicaoDialogo requisicaoDialogo) {
		this.requisicaoDialogo = requisicaoDialogo;
		if (requisicaoDialogo != null) {
			requisicaoFormulario = null;
		}
	}

	public RequisicaoFormulario getRequisicaoFormulario() {
		return requisicaoFormulario;
	}

	public void setRequisicaoFormulario(RequisicaoFormulario requisicaoFormulario) {
		this.requisicaoFormulario = requisicaoFormulario;
		if (requisicaoFormulario != null) {
			requisicaoDialogo = null;
		}
	}

	private void configurar() {
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), Constantes.EXEC);
		getActionMap().put(Constantes.EXEC, toolbar.atualizarAcao);
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
		return fichario.getIndiceAtivo();
	}

	private void abrir(String conteudo, String idPagina) {
		fichario.excluirPaginas();

		if (file.isDirectory()) {
			File[] files = file.listFiles();

			if (files == null) {
				return;
			}

			for (File f : files) {
				Pagina pagina = new Pagina(f);
				fichario.adicionarPagina(pagina);
			}
		}

		fichario.setConteudo(conteudo, idPagina);
	}

	@Override
	public void setJanela(Janela janela) {
		toolbar.setJanela(janela);
	}

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;
		private Action formatarAcao = Action.actionIcon("label.formatar_frag_json", Icones.BOLA_VERDE);
		private Action base64Acao = Action.actionIcon("label.criar_base64", Icones.BOLA_AMARELA);
		private Action baixarAtivoAcao = Action.actionIcon("label.baixar_ativo", Icones.BAIXAR);
		private Action excluirAtivoAcao = Action.actionIcon("label.excluir2", Icones.EXCLUIR);
		private Action atualizarAcao = Action.actionIcon("label.requisicao", Icones.URL);
		private CheckBox chkRespostaJson = new CheckBox("label.resposta_json");
		private CheckBox chkCopiarAccessT = new CheckBox();

		public void ini(Janela janela) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, ABRIR_EM_FORMULARO, CLONAR_EM_FORMULARIO,
					BAIXAR, NOVO, LIMPAR, SALVAR, COPIAR, COLAR, COPIAR2, COLAR2);

			addButton(baixarAtivoAcao);
			addButton(excluirAtivoAcao);
			add(chkRespostaJson);
			add(chkCopiarAccessT);
			addButton(true, atualizarAcao);
			addButton(true, formatarAcao);
			addButton(true, base64Acao);

			String hint = Mensagens.getString("label.copiar_access_token", Mensagens.getString("label.resposta_json"));
			chkCopiarAccessT.setToolTipText(hint);

			eventos();
		}

		private void eventos() {
			chkRespostaJson.addActionListener(
					e -> Preferencias.setBoolean("requisicao_response_json", chkRespostaJson.isSelected()));
			chkCopiarAccessT.addActionListener(
					e -> Preferencias.setBoolean("copiar_access_token", chkCopiarAccessT.isSelected()));
			chkRespostaJson.setSelected(Preferencias.getBoolean("requisicao_response_json"));
			chkCopiarAccessT.setSelected(Preferencias.getBoolean("copiar_access_token"));
			excluirAtivoAcao.setActionListener(e -> excluirAtivo());
			baixarAtivoAcao.setActionListener(e -> abrirAtivo());
			atualizarAcao.setActionListener(e -> atualizar());
			formatarAcao.setActionListener(e -> formatar());
			base64Acao.setActionListener(e -> base64());
		}

		@Override
		protected void destacarEmFormulario() {
			if (formulario.excluirPagina(RequisicaoContainer.this)) {
				RequisicaoFormulario.criar(formulario, RequisicaoContainer.this);

			} else if (requisicaoDialogo != null) {
				requisicaoDialogo.excluirContainer();
				RequisicaoFormulario.criar(formulario, RequisicaoContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (requisicaoFormulario != null) {
				requisicaoFormulario.excluirContainer();
				formulario.adicionarPagina(RequisicaoContainer.this);

			} else if (requisicaoDialogo != null) {
				requisicaoDialogo.excluirContainer();
				formulario.adicionarPagina(RequisicaoContainer.this);
			}
		}

		@Override
		protected void clonarEmFormulario() {
			if (requisicaoDialogo != null) {
				requisicaoDialogo.excluirContainer();
			}
			RequisicaoFormulario.criar(formulario, getConteudo(), getIdPagina());
		}

		@Override
		protected void abrirEmFormulario() {
			if (requisicaoDialogo != null) {
				requisicaoDialogo.excluirContainer();
			}
			RequisicaoFormulario.criar(formulario, null, null);
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
		protected void baixar() {
			abrir(null, null);
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

		@Override
		protected void copiar() {
			Pagina ativa = fichario.getPaginaAtiva();

			if (ativa != null) {
				StringBuilder sb = new StringBuilder();
				ativa.copiar1(sb);
				copiarMensagem(sb.toString());
			}
		}

		@Override
		protected void copiar2() {
			Pagina ativa = fichario.getPaginaAtiva();

			if (ativa != null) {
				StringBuilder sb = new StringBuilder();
				ativa.copiar2(sb);
				copiar2Mensagem(sb.toString());
			}
		}

		@Override
		protected void colar() {
			Pagina ativa = fichario.getPaginaAtiva();

			if (ativa != null) {
				ativa.colar1();
			}
		}

		@Override
		protected void colar2() {
			Pagina ativa = fichario.getPaginaAtiva();

			if (ativa != null) {
				ativa.colar2();
			}
		}

		@Override
		protected void novo() {
			Object resp = Util.getValorInputDialog(RequisicaoContainer.this, "label.id",
					Mensagens.getString("label.nome_arquivo"), Constantes.VAZIO);

			if (resp == null || Util.estaVazio(resp.toString())) {
				return;
			}

			String nome = resp.toString();

			File f = new File(file, nome);

			if (f.exists()) {
				Util.mensagem(RequisicaoContainer.this, Mensagens.getString("label.indentificador_ja_existente"));
				return;
			}

			try {
				if (f.createNewFile()) {
					Pagina pagina = new Pagina(f);
					fichario.adicionarPagina(pagina);
				}
			} catch (IOException ex) {
				Util.stackTraceAndMessage(Constantes.PAINEL_REQUISICAO, ex, RequisicaoContainer.this);
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
	}

	@Override
	public void adicionadoAoFichario(Fichario fichario) {
		toolbar.paginaVisivel();
	}

	public void formularioVisivel() {
		toolbar.formularioVisivel();
	}

	public void dialogoVisivel() {
		toolbar.dialogoVisivel();
	}

	@Override
	public String getStringPersistencia() {
		Pagina ativa = fichario.getPaginaAtiva();

		if (ativa != null) {
			return ativa.getNome();
		}

		return Constantes.VAZIO;
	}

	@Override
	public Class<?> getClasseFabrica() {
		return RequisicaoFabrica.class;
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
				return Mensagens.getString(Constantes.LABEL_REQUISICAO_MIN);
			}

			@Override
			public String getTitulo() {
				return Mensagens.getString(Constantes.LABEL_REQUISICAO);
			}

			@Override
			public String getHint() {
				return Mensagens.getString(Constantes.LABEL_REQUISICAO);
			}

			@Override
			public Icon getIcone() {
				return Icones.URL;
			}
		};
	}

	private class RequisicaoFichario extends JTabbedPane {
		private static final long serialVersionUID = 1L;

		private void adicionarPagina(Pagina pagina) {
			addTab(pagina.getNome(), pagina);
			int ultimoIndice = getTabCount() - 1;
			setSelectedIndex(ultimoIndice);
		}

		private void excluirPaginas() {
			while (getTabCount() > 0) {
				removeTabAt(0);
			}
		}

		private int getIndiceAtivo() {
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
			for (int i = 0; i < getTabCount(); i++) {
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

		private int getIndicePagina(Pagina pagina) {
			for (int i = 0; i < getTabCount(); i++) {
				Component cmp = getComponentAt(i);

				if (cmp instanceof Pagina) {
					Pagina p = (Pagina) cmp;

					if (p == pagina) {
						return i;
					}
				}
			}

			return -1;
		}

		private void setConteudo(String conteudo, String idPagina) {
			Pagina pagina = getPagina(idPagina);

			if (pagina != null) {
				if (!Util.estaVazio(conteudo)) {
					pagina.areaParametros.setText(conteudo);
				}
				setSelectedIndex(getIndicePagina(pagina));
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
					Util.stackTraceAndMessage(Constantes.PAINEL_REQUISICAO, ex, RequisicaoContainer.this);
				}
			}
		}

		private void excluir() {
			if (file.exists()) {
				Path path = FileSystems.getDefault().getPath(file.getAbsolutePath());

				try {
					Files.delete(path);
				} catch (IOException e) {
					Util.stackTraceAndMessage(Constantes.PAINEL_REQUISICAO, e, RequisicaoContainer.this);
				}
			}
		}

		private void limpar() {
			areaParametros.setText(Constantes.VAZIO);
		}

		private void salvar() {
			if (!Util.confirmaSalvar(RequisicaoContainer.this, Constantes.TRES)) {
				return;
			}

			try (PrintWriter pw = new PrintWriter(file, StandardCharsets.UTF_8.name())) {
				pw.print(areaParametros.getText());
			} catch (Exception ex) {
				Util.stackTraceAndMessage(Constantes.PAINEL_REQUISICAO, ex, RequisicaoContainer.this);
			}
		}

		private void formatar() {
			if (Util.estaVazio(areaParametros.getText())) {
				return;
			}

			String string = Util.getString(areaParametros);
			areaResultados.setText(Constantes.VAZIO);

			// try {
			// Parser parser = new Parser();
			// Tipo json = parser.parse(string);
			//
			// StyledDocument styledDoc = areaResultados.getStyledDocument();
			//
			// if (styledDoc instanceof AbstractDocument) {
			// AbstractDocument doc = (AbstractDocument) styledDoc;
			// json.toString(doc, false, 0);
			// }
			// areaParametros.requestFocus();
			// } catch (Exception ex) {
			// Util.stackTraceAndMessage(Constantes.PAINEL_REQUISICAO, ex,
			// this);
			// }
		}

		private void copiar1(StringBuilder sb) {
			String string = Util.getString(areaParametros);
			Util.setContentTransfered(string);
			sb.append(string);
			areaParametros.requestFocus();
		}

		private void colar1() {
			Util.getContentTransfered(areaParametros);
		}

		private void copiar2(StringBuilder sb) {
			String string = Util.getString(areaResultados);
			Util.setContentTransfered(string);
			sb.append(string);
			areaResultados.requestFocus();
		}

		private void colar2() {
			Util.getContentTransfered(areaResultados);
		}

		private void base64() {
			if (Util.estaVazio(areaParametros.getText())) {
				return;
			}

			String string = Util.getString(areaParametros);
			areaResultados.setText(Constantes.VAZIO);

			try {
				areaResultados.setText(Base64Util.criarBase64(string));
				areaParametros.requestFocus();
			} catch (Exception ex) {
				Util.stackTraceAndMessage(Constantes.PAINEL_REQUISICAO, ex, this);
			}
		}

		private void atualizar() {
			if (Util.estaVazio(areaParametros.getText())) {
				return;
			}

			String string = Util.getString(areaParametros);
			areaResultados.setText(Constantes.VAZIO);

			// try {
			// Parser parser = new Parser();
			//
			// ChaveValor cvAccessToken =
			// VariaveisModelo.get(Constantes.VAR_ACCESS_TOKEN);
			//
			// if (cvAccessToken != null) {
			// string = Util.substituir(string, cvAccessToken);
			// }
			//
			// Tipo parametros = parser.parse(string);
			// String resposta = Util.requisicao(parametros);
			//
			// if (!Util.estaVazio(resposta) &&
			// toolbar.chkRespostaJson.isSelected()) {
			// StyledDocument styledDoc = areaResultados.getStyledDocument();
			// Tipo json = parser.parse(resposta);
			//
			// if (styledDoc instanceof AbstractDocument) {
			// AbstractDocument doc = (AbstractDocument) styledDoc;
			// json.toString(doc, false, 0);
			// }
			//
			// String accessToken = Util.getAccessToken(json);
			// setAccesToken(accessToken);
			// } else {
			// areaResultados.setText(resposta);
			// }
			// areaParametros.requestFocus();
			// } catch (Exception ex) {
			// Util.stackTraceAndMessage(Constantes.PAINEL_REQUISICAO, ex,
			// this);
			// }
		}

		private void setAccesToken(String accessToken) {
			// if (!Util.estaVazio(accessToken)) {
			// ChaveValor cvAccessToken =
			// VariaveisModelo.get(Constantes.VAR_ACCESS_TOKEN);
			//
			// if (cvAccessToken == null) {
			// cvAccessToken = new ChaveValor(Constantes.VAR_ACCESS_TOKEN,
			// accessToken);
			// VariaveisModelo.adicionar(cvAccessToken);
			// } else {
			// cvAccessToken.setValor(accessToken);
			// }
			//
			// if (toolbar.chkCopiarAccessT.isSelected()) {
			// Util.setContentTransfered(accessToken);
			// }
			// }
		}
	}
}