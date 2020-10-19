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
import java.awt.Dialog;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.text.AbstractDocument;
import javax.swing.text.StyledDocument;

import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.assistencia.Base64Util;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.Janela;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;
import br.com.persist.parser.Objeto;
import br.com.persist.parser.Parser;
import br.com.persist.parser.ParserDialogo;
import br.com.persist.parser.ParserListener;
import br.com.persist.parser.Texto;
import br.com.persist.parser.Tipo;
import br.com.persist.plugins.variaveis.Variavel;
import br.com.persist.plugins.variaveis.VariavelProvedor;

public class RequisicaoContainer extends AbstratoContainer {
	private static final long serialVersionUID = 1L;
	private final RequisicaoFichario fichario = new RequisicaoFichario();
	private static final File file = new File("requisicoes");
	private static final Logger LOG = Logger.getGlobal();
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

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, fichario);
	}

	private void configurar() {
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), Constantes.EXEC);
		getActionMap().put(Constantes.EXEC, toolbar.atualizarAcao);
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
			if (files != null) {
				for (File f : files) {
					Pagina pagina = new Pagina(f);
					fichario.adicionarPagina(pagina);
				}
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
		private Action modeloAcao = Action.actionIcon("label.modelo", Icones.BOLA_VERDE);
		private Action atualizarAcao = Action.actionIcon("label.requisicao", Icones.URL);
		private CheckBox chkRespostaJson = new CheckBox("label.resposta_json");
		private CheckBox chkCopiarAccessT = new CheckBox();

		public void ini(Janela janela) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, CLONAR_EM_FORMULARIO, ABRIR_EM_FORMULARO,
					NOVO, BAIXAR, LIMPAR, SALVAR, COPIAR, COLAR, COPIAR2, COLAR2);
			addButton(baixarAtivoAcao);
			addButton(excluirAtivoAcao);
			add(chkRespostaJson);
			add(chkCopiarAccessT);
			addButton(true, atualizarAcao);
			addButton(true, formatarAcao);
			addButton(modeloAcao);
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
			modeloAcao.setActionListener(e -> modelo());
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
				ativa.copiar(sb);
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
				ativa.colar();
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

		@Override
		protected void atualizar() {
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

		private void modelo() {
			ParserDialogo form = ParserDialogo.criar((Dialog) null, parserListener);
			form.setLocationRelativeTo(formulario);
			form.setVisible(true);
		}

		private void base64() {
			Pagina ativa = fichario.getPaginaAtiva();
			if (ativa != null) {
				ativa.base64();
			}
		}
	}

	private transient ParserListener parserListener = new ParserListener() {
		@Override
		public void setParserTipo(Tipo tipo) {
			LOG.log(Level.FINEST, "setParserTipo");
		}

		@Override
		public boolean somenteModelo() {
			return true;
		}

		@Override
		public String getModelo() {
			return Mensagens.getString("requisicao.modelo");
		}

		@Override
		public String getTitle() {
			return Mensagens.getString(Constantes.LABEL_REQUISICAO);
		}
	};

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
		return new AbstratoTitulo() {
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
			try {
				Parser parser = new Parser();
				Tipo json = parser.parse(string);
				StyledDocument styledDoc = areaResultados.getStyledDocument();
				if (styledDoc instanceof AbstractDocument) {
					AbstractDocument doc = (AbstractDocument) styledDoc;
					json.toString(doc, false, 0);
				}
				areaParametros.requestFocus();
			} catch (Exception ex) {
				Util.stackTraceAndMessage(Constantes.PAINEL_REQUISICAO, ex, this);
			}
		}

		private void copiar(StringBuilder sb) {
			String string = Util.getString(areaParametros);
			Util.setContentTransfered(string);
			sb.append(string);
			areaParametros.requestFocus();
		}

		private void colar() {
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
			try {
				Parser parser = new Parser();
				Variavel vAccessToken = VariavelProvedor.getVariavel(Constantes.VAR_ACCESS_TOKEN);
				if (vAccessToken != null) {
					string = substituir(string, vAccessToken);
				}
				Tipo parametros = parser.parse(string);
				String resposta = requisicao(parametros);
				if (!Util.estaVazio(resposta) && toolbar.chkRespostaJson.isSelected()) {
					StyledDocument styledDoc = areaResultados.getStyledDocument();
					Tipo json = parser.parse(resposta);
					if (styledDoc instanceof AbstractDocument) {
						AbstractDocument doc = (AbstractDocument) styledDoc;
						json.toString(doc, false, 0);
					}
					String accessToken = getAccessToken(json);
					setAccesToken(accessToken);
				} else {
					areaResultados.setText(resposta);
				}
				areaParametros.requestFocus();
			} catch (Exception ex) {
				Util.stackTraceAndMessage(Constantes.PAINEL_REQUISICAO, ex, this);
			}
		}

		private String requisicao(Tipo parametros) throws IOException {
			if (parametros instanceof Objeto) {
				Objeto objeto = (Objeto) parametros;
				Tipo tipoUrl = objeto.getValor("url");
				String url = tipoUrl instanceof Texto ? tipoUrl.toString() : null;
				Map<String, String> mapHeader = null;
				Tipo tipoHeader = objeto.getValor("header");
				if (tipoHeader instanceof Objeto) {
					Objeto objHeader = (Objeto) tipoHeader;
					mapHeader = objHeader.getAtributosString();
				}
				Tipo tipoBody = objeto.getValor("body");
				String bodyParams = null;
				if (tipoBody instanceof Objeto) {
					Objeto objBody = (Objeto) tipoBody;
					Tipo params = objBody.getValor("parameters");
					bodyParams = params instanceof Texto ? params.toString() : null;
				}
				return requisicao(url, mapHeader, bodyParams);
			}
			return null;
		}

		private String requisicao(String url, Map<String, String> header, String parametros) throws IOException {
			if (Util.estaVazio(url)) {
				return null;
			}
			URL url2 = new URL(url);
			URLConnection conn = url2.openConnection();
			String verbo = null;
			if (header != null) {
				verbo = header.get("Request-Method");
				for (Map.Entry<String, String> entry : header.entrySet()) {
					conn.setRequestProperty(entry.getKey(), entry.getValue());
				}
			}
			if ("POST".equalsIgnoreCase(verbo) && !Util.estaVazio(parametros)) {
				conn.setDoOutput(true);
			}
			conn.connect();
			if ("POST".equalsIgnoreCase(verbo) && !Util.estaVazio(parametros)) {
				OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());
				osw.write(parametros);
				osw.flush();
			}
			return Util.getString(conn.getInputStream());
		}

		private String getAccessToken(Tipo tipo) {
			if (tipo instanceof Objeto) {
				Objeto objeto = (Objeto) tipo;
				Tipo tipoAccessToken = objeto.getValor("access_token");
				return tipoAccessToken instanceof Texto ? tipoAccessToken.toString() : null;
			}
			return null;
		}

		private void setAccesToken(String accessToken) {
			if (!Util.estaVazio(accessToken)) {
				Variavel vAccessToken = VariavelProvedor.getVariavel(Constantes.VAR_ACCESS_TOKEN);
				if (vAccessToken == null) {
					vAccessToken = new Variavel(Constantes.VAR_ACCESS_TOKEN, accessToken);
					VariavelProvedor.adicionar(vAccessToken);
				} else {
					vAccessToken.setValor(accessToken);
				}
				if (toolbar.chkCopiarAccessT.isSelected()) {
					Util.setContentTransfered(accessToken);
				}
			}
		}

		private String substituir(String instrucao, Variavel v) {
			if (instrucao == null) {
				instrucao = Constantes.VAZIO;
			}
			return instrucao.replaceAll("#" + v.getNome() + "#", v.getValor());
		}
	}
}