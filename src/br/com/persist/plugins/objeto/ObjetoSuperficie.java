package br.com.persist.plugins.objeto;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.StringWriter;
import java.sql.Connection;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.swing.Icon;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import br.com.persist.abstrato.AbstratoMensagens;
import br.com.persist.abstrato.DesktopLargura;
import br.com.persist.assistencia.AssistenciaException;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.HoraUtil;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.assistencia.TextPool;
import br.com.persist.assistencia.Util;
import br.com.persist.assistencia.Vetor;
import br.com.persist.componente.Acao;
import br.com.persist.componente.Action;
import br.com.persist.componente.Label;
import br.com.persist.componente.Menu;
import br.com.persist.componente.MenuItem;
import br.com.persist.componente.MenuPadrao1;
import br.com.persist.componente.Popup;
import br.com.persist.componente.SetLista;
import br.com.persist.componente.SetLista.Coletor;
import br.com.persist.fichario.Fichario;
import br.com.persist.formulario.Formulario;
import br.com.persist.icone.IconeContainer;
import br.com.persist.marca.XMLException;
import br.com.persist.marca.XMLUtil;
import br.com.persist.plugins.arquivo.ArquivoProvedor;
import br.com.persist.plugins.conexao.Conexao;
import br.com.persist.plugins.conexao.ConexaoProvedor;
import br.com.persist.plugins.consulta.ConsultaDialogo;
import br.com.persist.plugins.consulta.ConsultaFormulario;
import br.com.persist.plugins.instrucao.InstrucaoEvento;
import br.com.persist.plugins.instrucao.processador.Processador;
import br.com.persist.plugins.metadado.Metadado;
import br.com.persist.plugins.metadado.MetadadoConstantes;
import br.com.persist.plugins.metadado.MetadadoEvento;
import br.com.persist.plugins.metadado.MetadadoException;
import br.com.persist.plugins.objeto.Objeto.Estado;
import br.com.persist.plugins.objeto.circular.CircularContainer.Tipo;
import br.com.persist.plugins.objeto.circular.CircularDialogo;
import br.com.persist.plugins.objeto.config.ObjetoDialogo;
import br.com.persist.plugins.objeto.config.RelacaoContainer;
import br.com.persist.plugins.objeto.config.RelacaoDialogo;
import br.com.persist.plugins.objeto.internal.Argumento;
import br.com.persist.plugins.objeto.internal.InternalConfig;
import br.com.persist.plugins.objeto.internal.InternalContainer;
import br.com.persist.plugins.objeto.internal.InternalFormulario;
import br.com.persist.plugins.objeto.macro.MacroProvedor;
import br.com.persist.plugins.objeto.vinculo.Pesquisa;
import br.com.persist.plugins.objeto.vinculo.Referencia;
import br.com.persist.plugins.objeto.vinculo.RelacaoVinculo;
import br.com.persist.plugins.objeto.vinculo.Vinculacao;
import br.com.persist.plugins.ouvinte.OuvinteEvento;
import br.com.persist.plugins.persistencia.MemoriaModelo;
import br.com.persist.plugins.persistencia.Persistencia;
import br.com.persist.plugins.persistencia.PersistenciaModelo;

public abstract class ObjetoSuperficie extends Desktop implements ObjetoListener, RelacaoListener {
	public static final String LABEL_OBJETOS_COM_TABELA = "label.objetos_com_tabela";
	public static final String LABEL_PROCESSAR_OBJETOS = "label.processar_objetos";
	protected static final Logger LOG = Logger.getGlobal();
	protected final transient Linha linha = new Linha();
	protected final transient Area area = new Area();
	protected final transient Vinculacao vinculacao;
	private static final long serialVersionUID = 1L;
	protected transient Relacao selecionadoRelacao;
	protected transient Objeto selecionadoObjeto;
	protected final ObjetoContainer container;
	protected transient Relacao[] relacoes;
	protected transient Objeto[] objetos;
	protected boolean validoArrastar;
	protected String arquivoVinculo;
	protected int totalArrastado;
	protected byte estado;
	protected int ultX;
	protected int ultY;

	ObjetoSuperficie(Formulario formulario, ObjetoContainer container) {
		super(formulario, true);
		this.container = Objects.requireNonNull(container);
		vinculacao = new Vinculacao();
		limpar();
	}

	public void checarLargura(InternalContainer invocador) {
		if (isAjusteAutoLarguraForm()) {
			setSize(container.getSize());
			for (JInternalFrame frame : getAllFrames()) {
				if (frame instanceof InternalFormulario) {
					InternalFormulario interno = (InternalFormulario) frame;
					if (interno.getInternalContainer() == invocador) {
						larguras.configurar(DesktopLargura.TOTAL_A_DIREITA, frame);
					}
				}
			}
		}
	}

	public static KeyStroke getKeyStrokeCtrl(int keyCode) {
		return KeyStroke.getKeyStroke(keyCode, InputEvent.CTRL_MASK);
	}

	public static KeyStroke getKeyStrokeMeta(int keyCode) {
		return KeyStroke.getKeyStroke(keyCode, InputEvent.META_MASK);
	}

	public void setSelecionadoRelacao(Relacao selecionadoRelacao) {
		this.selecionadoRelacao = selecionadoRelacao;
	}

	public Relacao getSelecionadoRelacao() {
		return selecionadoRelacao;
	}

	public void setSelecionadoObjeto(Objeto selecionadoObjeto) {
		this.selecionadoObjeto = selecionadoObjeto;
	}

	public Objeto getSelecionadoObjeto() {
		return selecionadoObjeto;
	}

	public abstract ThreadManager getThreadManager();

	public Vinculacao getVinculacao() {
		return vinculacao;
	}

	public Relacao[] getRelacoes() {
		return relacoes;
	}

	public Objeto[] getObjetos() {
		return objetos;
	}

	public void setArquivoVinculo(String arquivoVinculo) {
		this.arquivoVinculo = arquivoVinculo;
	}

	public String getArquivoVinculo() {
		return arquivoVinculo;
	}

	public int getTotalArrastado() {
		return totalArrastado;
	}

	public void setTotalArrastado(int totalArrastado) {
		this.totalArrastado = totalArrastado;
		repaint();
	}

	@Override
	protected boolean contemReferencia(Objeto objeto) {
		return ObjetoSuperficieUtil.contemReferencia(this, objeto);
	}

	public void localizarInternalFormulario(Objeto objeto) {
		if (objeto == null) {
			return;
		}
		InternalFormulario interno = ObjetoSuperficieUtil.getInternalFormulario(ObjetoSuperficie.this, objeto);
		if (interno != null) {
			interno.setMargemInferior(objeto.getMargemInferior());
			aproximarEmpilharUsarForms();
			larguras.configurar(DesktopLargura.TOTAL_A_DIREITA, null);
		}
	}

	public void margemInferiorInternalFormulario(Objeto objeto) {
		if (objeto == null) {
			return;
		}
		InternalFormulario interno = ObjetoSuperficieUtil.getInternalFormulario(ObjetoSuperficie.this, objeto);
		if (interno != null) {
			interno.setMargemInferior(objeto.getMargemInferior());
		}
	}

	public void aproximarEmpilharUsarForms() {
		menuAjuste.aproximarEmpilharUsarForms();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;
		Stroke stroke = g2.getStroke();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if (estado == ObjetoConstantes.RELACAO) {
			linha.desenhar(g2);
		}
		if (totalArrastado == 1) {
			Color cor = g2.getColor();
			g2.setColor(Color.LIGHT_GRAY);
			int lar = getWidth();
			int alt = getHeight();
			for (int x = Constantes.GRADE; x < lar; x += Constantes.GRADE) {
				g2.drawLine(x, 0, x, alt);
			}
			for (int y = Constantes.GRADE; y < alt; y += Constantes.GRADE) {
				g2.drawLine(0, y, lar, y);
			}
			g2.setColor(cor);
		}
		for (Relacao relacao : relacoes) {
			relacao.desenhar(g2, stroke);
		}
		for (Objeto objeto : objetos) {
			objeto.desenhar(this, g2, stroke);
		}
		area.desenhar(g2);
		if (getThreadManager().validoDesenhar()) {
			g2.setFont(ObjetoConstantes.FONT_HORAS);

			int xValor = 40;
			int xTexto = 550;
			int yTodos = 300;

			g2.drawString(HoraUtil.formatar(getThreadManager().totalHoras), xValor, yTodos);
			g2.drawString("<<< TRABALHANDO", xTexto, yTodos);
			yTodos += 180;

			g2.drawString(HoraUtil.getHoraAtual(), xValor, yTodos);
			g2.drawString("<<< HORA ATUAL", xTexto, yTodos);
			yTodos += 180;

			int faltando = HoraUtil.OITO_HORAS - getThreadManager().totalHoras;
			if (faltando > 0) {
				g2.drawString(HoraUtil.formatar(faltando), xValor, yTodos);
				g2.drawString("<<< FALTANDO", xTexto, yTodos);
			} else if (faltando < 0) {
				g2.drawString(HoraUtil.formatar(faltando * -1), xValor, yTodos);
				g2.drawString("<<< SALDO POSITIVO", xTexto, yTodos);
			} else {
				g2.drawString(HoraUtil.formatar(faltando), xValor, yTodos);
				g2.drawString("<<< 8 HORAS", xTexto, yTodos);
			}
		}
	}

	@Override
	public boolean contemId(Objeto objeto, String id) {
		if (id != null) {
			return ObjetoSuperficieUtil.contemId(this, objeto, id);
		}
		return false;
	}

	public void addObjeto(Objeto obj) {
		if (obj == null || ObjetoSuperficieUtil.contem(this, obj)) {
			return;
		}
		obj.setCompararRegistro(container.isCompararRegistros());
		Objeto[] bkp = objetos;
		objetos = new Objeto[bkp.length + 1];
		System.arraycopy(bkp, 0, objetos, 0, bkp.length);
		objetos[bkp.length] = obj;
		obj.setListener(this);
	}

	public void excluir(Objeto obj) {
		int indice = ObjetoSuperficieUtil.getIndice(this, obj);
		if (indice >= 0) {
			objetos[indice].setListener(null);
			objetos[indice].associado = null;
			objetos[indice].desativar();
			objetos[indice] = null;
			Objeto[] bkp = objetos;
			objetos = new Objeto[0];
			for (int i = 0; i < bkp.length; i++) {
				if (bkp[i] != null) {
					addObjeto(bkp[i]);
				}
			}
			Relacao relacao = ObjetoSuperficieUtil.getRelacao(this, obj);
			while (relacao != null) {
				excluir(relacao);
				relacao = ObjetoSuperficieUtil.getRelacao(this, obj);
			}
		}
	}

	public void addRelacao(Relacao obj) {
		if (obj == null || ObjetoSuperficieUtil.contem(this, obj)) {
			return;
		}
		Relacao[] bkp = relacoes;
		relacoes = new Relacao[bkp.length + 1];
		System.arraycopy(bkp, 0, relacoes, 0, bkp.length);
		relacoes[bkp.length] = obj;
		obj.setListener(this);
	}

	public boolean excluir(Relacao obj) {
		int indice = ObjetoSuperficieUtil.getIndice(this, obj);
		if (indice >= 0) {
			relacoes[indice].setListener(null);
			relacoes[indice] = null;
			Relacao[] bkp = relacoes;
			relacoes = new Relacao[0];
			for (int i = 0; i < bkp.length; i++) {
				if (bkp[i] != null) {
					addRelacao(bkp[i]);
				}
			}
			return true;
		}
		return false;
	}

	public void limpar() {
		relacoes = new Relacao[0];
		objetos = new Objeto[0];
		repaint();
	}

	@Override
	public boolean processadoMetadado(Metadado metadado, Point point, boolean labelDireito, boolean checarNomear)
			throws AssistenciaException {
		if (metadado == null) {
			return false;
		}
		Objeto novo = new Objeto(point.x, point.y);
		novo.setChaves(metadado.getChaves());
		String id = metadado.getDescricao();
		novo.setTabela(id);
		if (labelDireito) {
			novo.setDeslocamentoXId(Objeto.DIAMETRO);
			novo.setDeslocamentoYId(Objeto.DIAMETRO / 2);
		}
		if (checarNomear
				&& Util.confirmar(ObjetoSuperficie.this, ObjetoMensagens.getString("msg.nomear_arrasto"), false)) {
			Object resp = Util.getValorInputDialog(ObjetoSuperficie.this, "label.id", id, id);
			if (resp != null && !Util.isEmpty(resp.toString())) {
				id = resp.toString();
			}
		}
		novo.setId(id);
		ObjetoSuperficieUtil.checagemId(this, novo, id, "-");
		addObjeto(novo);
		repaint();
		return true;
	}

	static Action acaoMenu(String chave, Icon icon) {
		return Action.acaoMenu(ObjetoMensagens.getString(chave), icon);
	}

	static Action acaoMenu(String chave) {
		return acaoMenu(chave, null);
	}

	public void criarNovoObjeto(int x, int y) throws AssistenciaException {
		Objeto novo = new Objeto(x, y);
		ObjetoSuperficieUtil.checagemId(this, novo, Constantes.VAZIO, Constantes.VAZIO);
		addObjeto(novo);
		ObjetoSuperficieUtil.limparSelecao(this);
		repaint();
	}

	public void abrir(ObjetoColetor coletor) throws XMLException, ObjetoException, AssistenciaException {
		limpar();
		for (Objeto objeto : coletor.getObjetos()) {
			addObjeto(objeto);
		}
		for (Relacao relacao : coletor.getRelacoes()) {
			addRelacao(relacao);
		}
		removeAll();
		repaint();
		setPreferredSize(coletor.getDimension());
		Util.updateComponentTreeUI(getParent());
		for (Objeto objeto : coletor.getObjetos()) {
			objeto.ativar();
		}
		for (Relacao relacao : coletor.getRelacoes()) {
			relacao.ativar();
		}
		getThreadManager().ativar();
		arquivoVinculo = coletor.getArquivoVinculo();
		vinculacao.abrir(ObjetoSuperficieUtil.criarArquivoVinculo(this), ObjetoSuperficie.this);
		for (Objeto objeto : objetos) {
			vinculacao.processar(objeto);
		}
		ObjetoSuperficieUtil.processarRelacaoVinculo(this, vinculacao, coletor);
	}

	public void excluido() {
		for (Objeto objeto : objetos) {
			objeto.setListener(null);
			objeto.desativar();
		}
		for (Relacao relacao : relacoes) {
			relacao.setListener(null);
			relacao.desativar();
		}
		getThreadManager().desativar();
	}

	@Override
	protected void internoPesquisarAntes(Objeto pesquisador, Objeto pesquisado) throws ObjetoException {
		Relacao relacao = ObjetoSuperficieUtil.getRelacao(this, pesquisador, pesquisado);
		if (relacao != null) {
			relacao.setObjetoTemp(pesquisado);
			relacao.setSelecionado(true);
		}
	}

	@Override
	public void pesquisar(Conexao conexao, Pesquisa pesquisa, Argumento argumento, boolean soTotal, boolean emForms)
			throws ObjetoException, AssistenciaException {
		if (conexao == null) {
			conexao = container.getConexaoPadrao();
		}
		ObjetoSuperficieUtil.deselRelacoes(this);
		super.pesquisar(conexao, pesquisa, argumento, soTotal, emForms);
		if (ObjetoPreferencia.isAbrirAuto()) {
			ObjetoSuperficieUtil.limparSelecao(this);
			processarReferencias(conexao, pesquisa, argumento);
			if (ObjetoSuperficieUtil.getPrimeiroObjetoSelecionado(this) != null) {
				destacar(conexao, ObjetoPreferencia.getTipoContainerPesquisaAuto(), null);
			}
		}
	}

	private void processarReferencias(Conexao conexao, Pesquisa pesquisa, Argumento argumento)
			throws AssistenciaException {
		for (Referencia referencia : pesquisa.getReferencias()) {
			if (!referencia.isProcessado()) {
				pesquisarReferencia(conexao, pesquisa, referencia, argumento);
			}
		}
	}

	private void pesquisarReferencia(Conexao conexao, Pesquisa pesquisa, Referencia referencia, Argumento argumento)
			throws AssistenciaException {
		Objeto objeto = null;
		for (Objeto obj : objetos) {
			if (referencia.igual(obj)) {
				objeto = obj;
				break;
			}
		}
		if (objeto != null && objeto.isAbrirAuto()) {
			pesquisarReferencia(conexao, pesquisa, referencia, argumento, objeto);
		}
	}

	private void pesquisarReferencia(Conexao conexao, Pesquisa pesquisa, Referencia referencia, Argumento argumento,
			Objeto objeto) throws AssistenciaException {
		ObjetoSuperficieUtil.pesquisarReferencia(this, conexao, pesquisa, referencia, argumento, objeto);
	}

	public void atualizarTotal(Conexao conexao, MenuItem[] menuItens, Label label) {
		if (conexao == null) {
			Util.mensagem(ObjetoSuperficie.this, Constantes.CONEXAO_NULA);
			return;
		}
		if (Preferencias.isDesconectado()) {
			Util.mensagem(ObjetoSuperficie.this, Constantes.DESCONECTADO);
			return;
		}
		if (!ObjetoSuperficieUtil.objetosComTabela(this, Estado.INDIFERENTE).isEmpty()) {
			String[] opcoes = new String[] { ObjetoMensagens.getString("label.total_sem_exibicao"),
					ObjetoMensagens.getString("label.total_com_exibicao") };
			String opcao = Util.getValorInputDialog(ObjetoSuperficie.this, opcoes);
			if (opcao == null) {
				Util.mensagem(ObjetoSuperficie.this, "ABORTADO");
				return;
			}
			boolean exibirTotal = opcoes[opcoes.length - 1].equals(opcao);
			Font font = getFont();
			if (font != null) {
				FontMetrics fm = getFontMetrics(font);
				if (fm != null) {
					int total = preTotalRecente(label);
					if (total > 0) {
						new ThreadTotal(ObjetoSuperficie.this, menuItens, conexao, label, total, exibirTotal, fm)
								.start();
					}
				}
			}
		}
	}

	private int preTotalRecente(Label label) {
		return ObjetoSuperficieUtil.preTotalRecente(this, label);
	}

	public void compararRecent(Conexao conexao, MenuItem[] menuItens, Label label) {
		if (conexao == null) {
			Util.mensagem(ObjetoSuperficie.this, Constantes.CONEXAO_NULA);
			return;
		}
		if (Preferencias.isDesconectado()) {
			Util.mensagem(ObjetoSuperficie.this, Constantes.DESCONECTADO);
			return;
		}
		Font font = getFont();
		if (font != null) {
			FontMetrics fm = getFontMetrics(font);
			if (fm != null) {
				int total = preTotalRecente(label);
				if (total > 0) {
					new ThreadRecente(ObjetoSuperficie.this, menuItens, conexao, label, total, fm).start();
				}
			}
		}
	}

	public void exportarMetadadoRaiz(Metadado metadado) throws AssistenciaException {
		int y = 20;
		for (int i = 0; i < metadado.getTotal(); i++) {
			Metadado filho = metadado.getMetadado(i);
			if (filho.isSelecionado()) {
				processadoMetadado(filho, new Point(10, y), true, false);
				y += 30;
			}
		}
		setPreferredSize(new Dimension(0, y));
		Util.updateComponentTreeUI(getParent());
	}

	public void msgInexistenteMetadado(Objeto objeto) {
		Util.mensagem(ObjetoSuperficie.this, ObjetoMensagens.getString("msb.inexistente_get_metadado", objeto.getId()));
	}

	public void adicionarHierarquico(Conexao conexao, Objeto objeto, Map<String, Object> mapaRef)
			throws MetadadoException, ObjetoException, AssistenciaException {
		Map<String, Object> args = new HashMap<>();
		args.put(MetadadoEvento.GET_METADADO_OBJETO, objeto.getTabela());
		formulario.processar(args);
		Metadado metadado = (Metadado) args.get(MetadadoConstantes.METADADO);
		if (metadado == null) {
			mapaRef.put(ObjetoConstantes.ERROR, Boolean.TRUE);
			msgInexistenteMetadado(objeto);
		} else {
			if (conexao == null) {
				conexao = container.getConexaoPadrao();
			}
			criarObjetoHierarquico(conexao, objeto, mapaRef, metadado);
		}
	}

	private void criarObjetoHierarquico(Conexao conexao, Objeto principal, Map<String, Object> mapaRef, Metadado tabela)
			throws MetadadoException, ObjetoException, AssistenciaException {
		Exportacao exportacao = new Exportacao(ObjetoSuperficie.this, principal, mapaRef, tabela.getPai());
		exportacao.criarPesquisa();
		exportacao.processarDetalhes(tabela);
		if (exportacao.erro) {
			mapaRef.put(ObjetoConstantes.ERROR, Boolean.TRUE);
			return;
		}
		exportacao.localizarObjetoAbaixo();
		exportacao.setScriptAdicaoHierarquico();
		destacar(conexao, ObjetoConstantes.TIPO_CONTAINER_PROPRIO, null);
	}

	public Coletor getObjetosComTabela(Estado estado) {
		List<Objeto> listaObjeto = ObjetoSuperficieUtil.objetosComTabela(this, estado);
		Coletor coletor = new Coletor();
		if (listaObjeto.isEmpty()) {
			Util.mensagem(getFormulario(), ObjetoMensagens.getString("msg.nenhum_objeto_com_tabela"));
			return coletor;
		}
		List<String> lista = new ArrayList<>();
		for (Objeto objeto : listaObjeto) {
			lista.add(objeto.getTabela());
		}
		SetLista.view(ObjetoMensagens.getString(LABEL_OBJETOS_COM_TABELA), lista, coletor, this, null);
		return coletor;
	}

	public void getMetadado(AtomicReference<Object> ref, Objeto objeto) {
		Map<String, Object> args = new HashMap<>();
		args.put(MetadadoEvento.GET_METADADO_OBJETO, objeto.getTabela());
		formulario.processar(args);
		Metadado metadado = (Metadado) args.get(MetadadoConstantes.METADADO);
		if (metadado == null) {
			msgInexistenteMetadado(objeto);
		} else {
			ref.set(metadado);
		}
	}

	public void abrirExportacaoImportacaoMetadado(Conexao conexao, Metadado tabela, boolean exportacao,
			boolean circular, AtomicReference<String> ref)
			throws MetadadoException, ObjetoException, AssistenciaException {
		ExportacaoImportacao expImp = criarExportacaoImportacao(exportacao, circular);
		expImp.processarPrincipal(tabela);
		expImp.checarCriarPesquisa();
		expImp.processarDetalhes(tabela);
		expImp.localizarObjetos();
		if (!expImp.circular) {
			if (conexao == null) {
				conexao = container.getConexaoPadrao();
			}
			destacar(conexao, ObjetoConstantes.TIPO_CONTAINER_PROPRIO, null);
		}
		expImp.vincular(ref);
		Util.mensagemFormulario(formulario, expImp.getString());
	}

	private ExportacaoImportacao criarExportacaoImportacao(boolean exportacao, boolean circular)
			throws AssistenciaException {
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		ExportacaoImportacao expImp = new ExportacaoImportacao(ObjetoSuperficie.this, exportacao, circular);
		expImp.definirCentros(d);
		expImp.criarVetor(d);
		return expImp;
	}

	void processarIdTabelaGrupoExportacao(Objeto objeto, Metadado tabelaRef) {
		String nomeTabela = tabelaRef.getNomeTabela();
		if (ObjetoSuperficieUtil.contemObjetoComTabela(this, nomeTabela)) {
			String id = nomeTabela + Constantes.SEP2 + tabelaRef.getNomeCampo();
			objeto.setId(id);
			ObjetoSuperficieUtil.checagemId(this, objeto, id, Constantes.SEP2);
			objeto.setGrupo(tabelaRef.getNomeCampo());
		} else {
			objeto.setId(nomeTabela);
		}
	}

	void processarIdTabelaGrupoExportacaoAvulso(Objeto objeto, Metadado tabela) {
		String nomeTabela = tabela.getDescricao();
		if (ObjetoSuperficieUtil.contemObjetoComTabela(this, nomeTabela)) {
			String id = nomeTabela + Constantes.SEP2 + tabela.getDescricao();
			objeto.setId(id);
			ObjetoSuperficieUtil.checagemId(this, objeto, id, Constantes.SEP2);
			objeto.setGrupo(tabela.getDescricao());
		} else {
			objeto.setId(nomeTabela);
		}
	}

	void destacar(Conexao conexao, int tipoContainer, InternalConfig config) {
		try {
			List<Objeto> lista = ObjetoSuperficieUtil.getSelecionados(this);
			if (ObjetoSuperficieUtil.getContinua(lista)) {
				List<Objeto> selecionados = montarSelecionados(lista,
						tipoContainer == ObjetoConstantes.TIPO_CONTAINER_PROPRIO);
				destacar(conexao, tipoContainer, config, selecionados);
			}
		} catch (AssistenciaException ex) {
			Util.mensagem(ObjetoSuperficie.this, ex.getMessage());
		}
	}

	private List<Objeto> montarSelecionados(List<Objeto> lista, boolean proprioContainer) throws AssistenciaException {
		List<Objeto> selecionados = new ArrayList<>();
		for (Objeto objeto : lista) {
			montarSelecionado(proprioContainer, selecionados, objeto);
		}
		return selecionados;
	}

	private void montarSelecionado(boolean proprioContainer, List<Objeto> selecionados, Objeto objeto)
			throws AssistenciaException {
		if (objeto.isClonarAoDestacar()) {
			if (proprioContainer) {
				montarSelecionadoProprio(selecionados, objeto);
			} else {
				selecionados.add(objeto.clonar());
				objeto.setSelecionado(false);
			}
		} else {
			selecionados.add(objeto);
		}
	}

	private void montarSelecionadoProprio(List<Objeto> selecionados, Objeto objeto) throws AssistenciaException {
		InternalFormulario interno = ObjetoSuperficieUtil.getInternalFormulario(this, objeto);
		if (interno == null) {
			selecionados.add(objeto);
		} else {
			selecionados.add(objeto.clonar());
			objeto.setSelecionado(false);
		}
	}

	private void destacar(Conexao conexao, int tipoContainer, InternalConfig config, List<Objeto> selecionados) {
		if (tipoContainer == ObjetoConstantes.TIPO_CONTAINER_FORMULARIO) {
			ObjetoSuperficieDestacar.destacarDesktopFormulario(selecionados, conexao, config, formulario);
		} else if (tipoContainer == ObjetoConstantes.TIPO_CONTAINER_DESKTOP) {
			ObjetoSuperficieDestacar.destacarDeskopPagina(selecionados, conexao, config, formulario);
		} else if (tipoContainer == ObjetoConstantes.TIPO_CONTAINER_FICHARIO) {
			ObjetoSuperficieDestacar.destacarObjetoPagina(selecionados, conexao, formulario);
		} else if (tipoContainer == ObjetoConstantes.TIPO_CONTAINER_PROPRIO) {
			ObjetoSuperficieDestacar.destacarPropriaSuperficie(this, selecionados, conexao, config);
		}
	}

	public void selecionarCampo(Objeto objeto, Coletor coletor, Component c, String selecionarItem) {
		Conexao conexao = container.getConexaoPadrao();
		if (conexao == null) {
			Util.mensagem(c, ObjetoMensagens.getString("msg.sem_conexao_sel"));
			return;
		}
		if (objeto != null && Util.isEmpty(objeto.getTabela())) {
			Util.mensagem(c, ObjetoMensagens.getString("msg.obj_sem_config_tabela", objeto.getId()));
			return;
		}
		if (objeto != null) {
			try {
				Connection conn = ConexaoProvedor.getConnection(conexao);
				MemoriaModelo modelo = Persistencia.criarModeloMetaDados(conn, conexao, objeto.getTabela());
				SetLista.view(ObjetoMensagens.getString("label.colunas_de", objeto.getId()), modelo.getLista(2),
						coletor, c, new SetLista.Config(false, true, selecionarItem));
			} catch (Exception ex) {
				Util.stackTraceAndMessage("META-DADOS", ex, ObjetoSuperficie.this);
			}
		}
	}

	@Override
	public void labelTotalRegistros(Objeto objeto) {
		InternalFormulario interno = ObjetoSuperficieUtil.getInternalFormulario(this, objeto);
		if (interno != null) {
			interno.labelTotalRegistros(objeto.getTotalRegistros());
		}
	}

	@Override
	public void adicionadoAoFichario(Fichario fichario) {
		LOG.log(Level.FINEST, "adicionadoAoFichario");
	}

	@Override
	public void windowActivatedHandler(Window window) {
		for (JInternalFrame frame : getAllFrames()) {
			try {
				frame.setSelected(true);
			} catch (PropertyVetoException e) {
				LOG.log(Level.FINEST, "{0}", e.getMessage());
			}
		}
	}

	@Override
	public void nivelTransparenciaFormsIgnorados() {
		Object resp = JOptionPane.showInputDialog(this,
				AbstratoMensagens.getString("label.nivel_transp_forms_ignorados"), Mensagens.getString("label.atencao"),
				JOptionPane.INFORMATION_MESSAGE, null, ObjetoConstantes.NIVEIS_TRANSPARENCIA_FORM,
				"" + ObjetoPreferencia.getNivelTransparencia());
		if (resp != null && !Util.isEmpty(resp.toString())) {
			ObjetoSuperficieUtil.setTransparenciaInternalFormulario(this, Float.parseFloat(resp.toString().trim()));
		}
	}
}

abstract class ThreadComparacao extends Thread {
	final ObjetoSuperficie superficie;
	final MenuItem[] menuItens;
	final Conexao conexao;
	final FontMetrics fm;
	final Label label;
	final int total;

	ThreadComparacao(ObjetoSuperficie superficie, MenuItem[] menuItens, Conexao conexao, Label label, int total,
			FontMetrics fm) {
		this.superficie = superficie;
		this.menuItens = menuItens;
		this.conexao = conexao;
		this.label = label;
		this.total = total;
		this.fm = fm;
	}

	void setItensEnabled(boolean b) {
		for (MenuItem Item : menuItens) {
			Item.setEnabled(b);
		}
	}

	void sleepIntervaloComparacao() {
		try {
			Thread.sleep(ObjetoPreferencia.getIntervaloComparacao());
		} catch (Exception ex) {
			Thread.currentThread().interrupt();
		}
	}

	void setText(String string) {
		SwingUtilities.invokeLater(() -> label.setText(string));
	}

	String getString(String chave, Object... args) {
		return ObjetoMensagens.getString(chave, args);
	}

	void restaurarMemento() {
		for (Objeto objeto : superficie.getObjetos()) {
			objeto.restaurarMemento();
		}
	}

	void repaint() {
		superficie.repaint();
	}

	@Override
	public void run() {
	}
}

class ThreadTotal extends ThreadComparacao {
	private boolean exibirTotal;

	ThreadTotal(ObjetoSuperficie superficie, MenuItem[] menuItens, Conexao conexao, Label label, int total,
			boolean exibirTotal, FontMetrics fm) {
		super(superficie, menuItens, conexao, label, total, fm);
		this.exibirTotal = exibirTotal;
	}

	@Override
	public void run() {
		if (Preferencias.isDesconectado()) {
			Util.mensagem(superficie, Constantes.DESCONECTADO);
			return;
		}
		sleepIntervaloComparacao();
		label.setForeground(ObjetoPreferencia.getCorTotalAtual());
		label.setText("0 / " + total);
		boolean processado = false;
		setItensEnabled(false);
		int atual = 0;
		List<Objeto> novos = new ArrayList<>();
		for (Objeto objeto : superficie.getObjetos()) {
			if (!Util.isEmpty(objeto.getTabela())) {
				try {
					String[] array = { "0", "0" };
					String suf = " (" + (++atual) + " / " + total + ")";
					setText(getString("label.totalizando_id", objeto.getId()) + suf);
					if (!Preferencias.isDesconectado()) {
						Connection conn = ConexaoProvedor.getConnection(conexao);
						String aposFROM = PersistenciaModelo.prefixarEsquema(conexao, objeto.getPrefixoNomeTabela(),
								objeto.getTabela(), null);
						array = Persistencia.getTotalRegistros(conn, aposFROM);
					}
					objeto.setCorFonte(ObjetoPreferencia.getCorTotalAtual());
					objeto.setTotalRegistros(Long.parseLong(array[1]));
					if (exibirTotal) {
						processarTotal(objeto, fm, novos);
					}
					setText(getString("label.totalizado_id", objeto.getId()) + suf);
					processado = true;
					repaint();
					sleepIntervaloComparacao();
				} catch (Exception ex) {
					Util.stackTraceAndMessage("TOTAL", ex, superficie);
					Thread.currentThread().interrupt();
				}
			}
		}
		processarFinal(processado, novos);
		setItensEnabled(true);
	}

	private void processarTotal(Objeto objeto, FontMetrics fm, List<Objeto> novos) throws AssistenciaException {
		objeto.setCorFonte(ObjetoPreferencia.getCorComparaRec());
		objeto.larguraId = fm.stringWidth(objeto.getId());
		Objeto info = new Objeto(0, 0, "create2");
		info.setDeslocamentoXId(objeto.getDeslocamentoXId());
		info.setDeslocamentoYId(objeto.getDeslocamentoYId());
		info.setId("" + objeto.getTotalRegistros());
		info.setCorFonte(objeto.getCorFonte());
		info.setIdSub(info.getId());
		info.setTransparente(true);
		info.associado = objeto;
		novos.add(info);
	}

	private void processarFinal(boolean processado, List<Objeto> novos) {
		if (processado) {
			label.setText(getString("label.threadTotalAtual"));
			for (Objeto objeto : novos) {
				incluir(objeto);
			}
			restaurarMemento();
			repaint();
		}
	}

	private void incluir(Objeto info) {
		String id = info.getId();
		ObjetoSuperficieUtil.checagemId(superficie, info, id, Constantes.SEP2);
		Objeto origem = info.associado;
		superficie.excluir(origem.associado);
		superficie.addObjeto(info);
		origem.associado = info;
		origem.configLocalAssociado();
	}
}

class ThreadRecente extends ThreadComparacao {
	ThreadRecente(ObjetoSuperficie superficie, MenuItem[] menuItens, Conexao conexao, Label label, int total,
			FontMetrics fm) {
		super(superficie, menuItens, conexao, label, total, fm);
	}

	@Override
	public void run() {
		if (Preferencias.isDesconectado()) {
			Util.mensagem(superficie, Constantes.DESCONECTADO);
			return;
		}
		sleepIntervaloComparacao();
		label.setForeground(ObjetoPreferencia.getCorComparaRec());
		label.setText("0 / " + total);
		boolean processado = false;
		setItensEnabled(false);
		int atual = 0;
		List<Objeto> novos = new ArrayList<>();
		for (Objeto objeto : superficie.getObjetos()) {
			if (!Util.isEmpty(objeto.getTabela())) {
				try {
					String[] array = { "0", "0" };
					String suf = " (" + (++atual) + " / " + total + ")";
					setText(getString("label.comparando_id", objeto.getId()) + suf);
					if (!Preferencias.isDesconectado()) {
						Connection conn = ConexaoProvedor.getConnection(conexao);
						String aposFROM = PersistenciaModelo.prefixarEsquema(conexao, objeto.getPrefixoNomeTabela(),
								objeto.getTabela(), null);
						array = Persistencia.getTotalRegistros(conn, aposFROM);
					}
					processarRecente(objeto, Integer.parseInt(array[1]), fm, novos);
					setText(getString("label.comparado_id", objeto.getId()) + suf);
					processado = true;
					repaint();
					sleepIntervaloComparacao();
				} catch (Exception ex) {
					Util.stackTraceAndMessage("RECENTE", ex, superficie);
					Thread.currentThread().interrupt();
				}
			}
		}
		if (processado) {
			label.setText(getString("label.threadCompRecente"));
			for (Objeto objeto : novos) {
				incluir(objeto);
			}
			restaurarMemento();
			repaint();
		}
		setItensEnabled(true);
	}

	private void processarRecente(Objeto objeto, int totalRegistros, FontMetrics fm, List<Objeto> novos)
			throws AssistenciaException {
		objeto.setCorFonte(ObjetoPreferencia.getCorComparaRec());
		long diff = totalRegistros - objeto.getTotalRegistros();
		if (diff == 0) {
			return;
		}
		objeto.larguraId = fm.stringWidth(objeto.getId());
		Objeto info = new Objeto(0, 0, diff > 0 ? "create2" : "delete");
		String id = null;
		if (diff > 0) {
			id = objeto.getTotalRegistros() + "+" + diff + "=" + totalRegistros;
		} else {
			id = objeto.getTotalRegistros() + "" + diff + "=" + totalRegistros;
		}
		info.setId(id);
		info.setDeslocamentoXId(objeto.getDeslocamentoXId());
		info.setDeslocamentoYId(objeto.getDeslocamentoYId());
		info.setCorFonte(objeto.getCorFonte());
		info.setTransparente(true);
		info.associado = objeto;
		novos.add(info);
	}

	private void incluir(Objeto info) {
		String id = info.getId();
		ObjetoSuperficieUtil.checagemId(superficie, info, id, Constantes.SEP2);
		Objeto origem = info.associado;
		superficie.excluir(origem.associado);
		superficie.addObjeto(info);
		origem.associado = info;
		origem.configLocalAssociado();
	}
}

class CopiarColar {
	private static final List<Objeto> copiados = new ArrayList<>();

	private CopiarColar() {
	}

	public static boolean copiar(ObjetoSuperficie superficie) throws AssistenciaException {
		copiados.clear();
		for (Objeto objeto : ObjetoSuperficieUtil.getSelecionados(superficie)) {
			copiados.add(objeto.clonar());
		}
		return !copiados.isEmpty();
	}

	public static void colar(ObjetoSuperficie superficie, boolean b, int x, int y) throws AssistenciaException {
		ObjetoSuperficieUtil.limparSelecao(superficie);
		Objeto colado = null;
		for (Objeto objeto : copiados) {
			Objeto clone = get(objeto, superficie);
			superficie.addObjeto(clone);
			clone.setSelecionado(true);
			colado = clone;
			if (b) {
				clone.setX(x);
				clone.setY(y);
			}
		}
		if (copiados.size() == 1 && colado != null) {
			InternalFormulario interno = ObjetoSuperficieUtil.getInternalFormularioIntersecao(superficie, colado);
			if (interno != null) {
				Objeto objeto = interno.getInternalContainer().getObjeto();
				objeto.setMargemInferior(Constantes.QUARENTA);
				interno.setMargemInferior(objeto.getMargemInferior());
				superficie.aproximarEmpilharUsarForms();
				colado.setTemp(objeto);
			}
		}
		superficie.repaint();
	}

	public static boolean copiadosIsEmpty() {
		return copiados.isEmpty();
	}

	private static Objeto get(Objeto objeto, ObjetoSuperficie superficie) throws AssistenciaException {
		Objeto o = objeto.clonar();
		o.deltaX(Objeto.DIAMETRO);
		o.deltaY(Objeto.DIAMETRO);
		o.setId(objeto.getId() + "-" + Objeto.getSequencia());
		boolean contem = ObjetoSuperficieUtil.contem(superficie, o);
		while (contem) {
			o.setId(objeto.getId() + "-" + Objeto.novaSequencia());
			contem = ObjetoSuperficieUtil.contem(superficie, o);
		}
		return o;
	}
}

class SuperficiePopup2 extends Popup {
	private Action atualizarFormulariosAndOuvir = ObjetoSuperficie.acaoMenu("label.forms_atualizar_e_ouv",
			Icones.ATUALIZAR);
	private Action limparFormulariosFiltroAcao = ObjetoSuperficie.acaoMenu("label.limpar_formularios_filtro",
			Icones.NOVO);
	private Action atualizarFormulariosAcao = ObjetoSuperficie.acaoMenu("label.atualizar_forms", Icones.ATUALIZAR);
	private Action objetosComTabelaAcao = ObjetoSuperficie.acaoMenu(ObjetoSuperficie.LABEL_OBJETOS_COM_TABELA);
	private Action processarObjetosAcao = ObjetoSuperficie.acaoMenu(ObjetoSuperficie.LABEL_PROCESSAR_OBJETOS);
	private Action limparFormulariosAcao = ObjetoSuperficie.acaoMenu("label.limpar_formularios", Icones.NOVO);
	private Action formulariosComExcecaoAcaoMsg = ObjetoSuperficie.acaoMenu("label.forms_com_excecao_msg");
	private Action formulariosComExcecaoAcaoOuv = ObjetoSuperficie.acaoMenu("label.forms_com_excecao_ouv");
	private Action formulariosInvisiveisAcao = ObjetoSuperficie.acaoMenu("label.forms_invisiveis");
	private Action criarObjetoAcao = ObjetoSuperficie.acaoMenu("label.criar_objeto", Icones.CRIAR);
	private Action propriedadesAcao = actionMenu("label.propriedades");
	private Action colarAcao = actionMenu("label.colar", Icones.COLAR);
	private MenuIgnorados menuIgnorados = new MenuIgnorados();
	private static final long serialVersionUID = 1L;
	final ObjetoSuperficieExt superficie;
	int xLocal;
	int yLocal;

	SuperficiePopup2(ObjetoSuperficieExt superficie) {
		this.superficie = superficie;
		addMenuItem(processarObjetosAcao);
		addMenuItem(objetosComTabelaAcao);
		addMenuItem(true, criarObjetoAcao);
		addMenuItem(true, colarAcao);
		add(true, superficie.getMenuAjustar());
		addMenuItem(true, formulariosComExcecaoAcaoMsg);
		addMenuItem(formulariosComExcecaoAcaoOuv);
		addMenuItem(true, formulariosInvisiveisAcao);
		addMenuItem(true, atualizarFormulariosAndOuvir);
		addMenuItem(atualizarFormulariosAcao);
		addMenuItem(true, limparFormulariosFiltroAcao, ObjetoMensagens.getString("hint.limpar_formularios_filtro"));
		addMenuItem(limparFormulariosAcao);
		add(true, superficie.getMenuLargura());
		add(true, superficie.getMenuAjuste());
		add(true, menuIgnorados);
		addMenuItem(true, propriedadesAcao);
		eventos();
	}

	private void eventos() {
		atualizarFormulariosAcao.setActionListener(e -> superficie.atualizarFormularios());
		formulariosComExcecaoAcaoMsg.setActionListener(e -> formulariosComExcecaoMsg());
		formulariosComExcecaoAcaoOuv.setActionListener(e -> formulariosComExcecaoOuv());
		atualizarFormulariosAndOuvir.setActionListener(e -> atualizarFormsAndOuvir());
		formulariosInvisiveisAcao.setActionListener(e -> formulariosInvisiveis());
		limparFormulariosFiltroAcao.setActionListener(e -> superficie.limpar3());
		limparFormulariosAcao.setActionListener(e -> superficie.limpar2());
		objetosComTabelaAcao.setActionListener(e -> objetosComTabela());
		processarObjetosAcao.setActionListener(e -> processarObjetos());
		criarObjetoAcao.setActionListener(e -> criarNovoObjeto());
		propriedadesAcao.setActionListener(e -> propriedades());
		colarAcao.setActionListener(e -> colar());
	}

	private void colar() {
		try {
			int raio = Objeto.DIAMETRO / 2;
			CopiarColar.colar(superficie, true, superficie.popup2.xLocal - raio, superficie.popup2.yLocal - raio);
		} catch (AssistenciaException ex) {
			Util.mensagem(superficie, ex.getMessage());
		}
	}

	private void criarNovoObjeto() {
		try {
			int raio = Objeto.DIAMETRO / 2;
			superficie.criarNovoObjeto(superficie.popup2.xLocal - raio, superficie.popup2.yLocal - raio);
		} catch (AssistenciaException ex) {
			Util.mensagem(superficie, ex.getMessage());
		}
	}

	void preShow(boolean contemFrames) {
		formulariosComExcecaoAcaoMsg.setEnabled(contemFrames);
		formulariosComExcecaoAcaoOuv.setEnabled(contemFrames);
		colarAcao.setEnabled(!CopiarColar.copiadosIsEmpty());
		limparFormulariosFiltroAcao.setEnabled(contemFrames);
		superficie.getMenuLargura().habilitar(contemFrames);
		superficie.getMenuAjustar().habilitar(contemFrames);
		superficie.getMenuAjuste().habilitar(contemFrames);
		formulariosInvisiveisAcao.setEnabled(contemFrames);
		atualizarFormulariosAcao.setEnabled(contemFrames);
		limparFormulariosAcao.setEnabled(contemFrames);
		menuIgnorados.preShow();
		if (contemFrames && contemExcecao()) {
			formulariosComExcecaoAcaoMsg.icon(Icones.GLOBO_GIF);
			formulariosComExcecaoAcaoOuv.icon(Icones.GLOBO_GIF);
		} else {
			formulariosComExcecaoAcaoMsg.icon(null);
			formulariosComExcecaoAcaoOuv.icon(null);
		}
	}

	private class MenuIgnorados extends Menu {
		private Action invisivelAcao = actionMenu("label.invisivel");
		private Action visivelAcao = actionMenu("label.visivel");
		private Action falseAcao = actionMenu("label.false");
		private static final long serialVersionUID = 1L;

		private MenuIgnorados() {
			super("label.ignorados");
			addMenuItem(invisivelAcao);
			addMenuItem(true, visivelAcao);
			addMenuItem(true, falseAcao);
			invisivelAcao.setActionListener(e -> processar(false));
			visivelAcao.setActionListener(e -> processar(true));
			falseAcao.setActionListener(e -> ignorar(false));
		}

		private void preShow() {
			setEnabled(!ObjetoSuperficieUtil.getIgnorados(superficie).isEmpty());
		}

		private void processar(boolean b) {
			List<Objeto> ignorados = ObjetoSuperficieUtil.getIgnorados(superficie);
			for (Objeto objeto : ignorados) {
				objeto.setVisivel(b);
				InternalFormulario interno = ObjetoSuperficieUtil.getInternalFormulario(superficie, objeto);
				if (interno != null) {
					interno.setVisible(b);
				}
			}
			superficie.aproximarEmpilharUsarForms();
		}

		private void ignorar(boolean b) {
			List<Objeto> ignorados = ObjetoSuperficieUtil.getIgnorados(superficie);
			for (Objeto objeto : ignorados) {
				objeto.setIgnorar(b);
			}
			superficie.repaint();
		}
	}

	private boolean contemExcecao() {
		for (JInternalFrame frame : superficie.getAllFrames()) {
			if (frame instanceof InternalFormulario) {
				InternalFormulario interno = (InternalFormulario) frame;
				if (interno.contemExcecao()) {
					return true;
				}
			}
		}
		return false;
	}

	private void formulariosComExcecaoMsg() {
		StringBuilder builder = new StringBuilder();
		getStringExcecao(builder);
		if (builder.length() == 0) {
			Util.mensagem(superficie.getFormulario(), ObjetoMensagens.getString("msg.nenhum_form_com_excecao"));
		} else {
			Util.mensagem(superficie.getFormulario(), builder.toString());
		}
	}

	private void formulariosComExcecaoOuv() {
		StringBuilder builder = new StringBuilder();
		getStringExcecao(builder);
		Map<String, Object> args = new HashMap<>();
		if (builder.length() == 0) {
			args.put(OuvinteEvento.GET_STRING, ObjetoMensagens.getString("msg.nenhum_form_com_excecao"));
		} else {
			args.put(OuvinteEvento.GET_STRING, builder.toString());
		}
		superficie.getFormulario().processar(args);
		String string = (String) args.get(OuvinteEvento.GET_RESULT);
		if (string == null) {
			Util.mensagem(superficie.getFormulario(), ObjetoMensagens.getString("msg.nenhum_form_com_excecao_result"));
		}
	}

	private void atualizarFormsAndOuvir() {
		superficie.atualizarFormularios();
		SwingUtilities.invokeLater(this::formulariosComExcecaoOuv);
	}

	private void getStringExcecao(StringBuilder builder) {
		for (JInternalFrame frame : superficie.getAllFrames()) {
			if (frame instanceof InternalFormulario) {
				InternalFormulario interno = (InternalFormulario) frame;
				if (interno.contemExcecao()) {
					Objeto objeto = interno.getInternalContainer().getObjeto();
					if (builder.length() > 0) {
						builder.append(Constantes.QL);
					}
					String string = objeto.getId() + " - " + interno.getTitle();
					builder.append(string + Constantes.QL);
					builder.append(Util.completar("", string.length(), '-') + Constantes.QL);
					builder.append(interno.detalhesExcecao() + Constantes.QL2);
				}
			}
		}
	}

	private void objetosComTabela() {
		Coletor coletor = superficie.getObjetosComTabela(Estado.INDIFERENTE);
		if (coletor.size() == 0) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		for (String tabela : coletor.getLista()) {
			if (sb.length() > 0) {
				sb.append(Constantes.QL);
			}
			sb.append(tabela);
		}
		Util.mensagem(superficie, sb.toString());
	}

	private void processarObjetos() {
		Map<String, Object> args = new HashMap<>();
		args.put(InstrucaoEvento.BIBLIO_PARA_OBJETO_REQUEST, "");
		superficie.getFormulario().processar(args);
		String result = (String) args.get(InstrucaoEvento.BIBLIO_PARA_OBJETO_RESPONSE);
		if (result == null) {
			Util.mensagem(superficie.getFormulario(),
					ObjetoMensagens.getString("msg.nenhum_instrucao_biblio_paraObjeto_result"));
			return;
		}
		String nomeBiblio = ObjetoUtil.getSelBiblio(superficie, result);
		if (nomeBiblio == null) {
			return;
		}
		try {
			nomeBiblio = nomeBiblio.trim();
			Processador processador = new Processador();
			List<Object> resp = processador.processar(nomeBiblio, "processar",
					ObjetoUtil.criarLista(superficie.getObjetos()));
			if (ObjetoUtil.isTextPool(resp)) {
				TextPool textPool = (TextPool) resp.get(0);
				Util.mensagem(superficie, textPool.getListaText());
			} else {
				String string = ObjetoUtil.getStringResposta(resp);
				Util.mensagem(superficie, string);
			}
		} catch (Exception ex) {
			Util.stackTraceAndMessage("PROCESSAR_OBJETOS", ex, superficie);
		}
	}

	private void formulariosInvisiveis() {
		Coletor coletor = superficie.getHierarquicoVisivelManager().getColetorFormsInvisiveis();
		if (coletor.size() == 1) {
			tornarVisivel(coletor.get(0));
		}
	}

	private void tornarVisivel(String grupoTabela) {
		ObjetoSuperficieUtil.tornarVisivel(superficie, grupoTabela, null);
	}

	private void propriedades() {
		StringBuilder sbi = new StringBuilder();
		int invisiveis = 0;
		int visiveis = 0;
		for (Objeto objeto : superficie.getObjetos()) {
			if (objeto.visivel) {
				visiveis++;
			} else {
				sbi.append(objeto.getId() + Constantes.QL);
				invisiveis++;
			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append(ObjetoMensagens.getString("label.total_objetos") + " " + (visiveis + invisiveis) + Constantes.QL);
		sb.append(ObjetoMensagens.getString("label.objetos_visiveis", visiveis) + Constantes.QL);
		sb.append(ObjetoMensagens.getString("label.objetos_invisiveis", invisiveis) + Constantes.QL);
		if (sbi.length() > 0) {
			sb.append(sbi);
		}
		File file = superficie.container.getArquivo();
		if (file != null) {
			sb.append("------------------" + Constantes.QL);
			sb.append(ObjetoMensagens.getString("label.local_absoluto_arquivo") + " " + file.getAbsolutePath()
					+ Constantes.QL);
			sb.append(ObjetoMensagens.getString("label.local_relativo_arquivo") + " "
					+ ArquivoProvedor.criarStringPersistencia(file) + Constantes.QL);
		}
		Util.mensagem(superficie.getFormulario(), sb.toString());
	}
}

class SuperficiePopup extends Popup {
	private Action objetosComTabelaAcao = ObjetoSuperficie.acaoMenu(ObjetoSuperficie.LABEL_OBJETOS_COM_TABELA);
	private Action processarObjetosAcao = ObjetoSuperficie.acaoMenu(ObjetoSuperficie.LABEL_PROCESSAR_OBJETOS);
	private Action excluirAcao = ObjetoSuperficie.acaoMenu("label.excluir_selecionado", Icones.EXCLUIR);
	private Action criarRelacaoAcao = ObjetoSuperficie.acaoMenu("label.criar_relacao", Icones.SETA);
	private Action copiarIconeAcao = ObjetoSuperficie.acaoMenu("label.copiar_icone", Icones.COPIA);
	private Action colarIconeAcao = ObjetoSuperficie.acaoMenu("label.colar_icone", Icones.COLAR);
	Action configuracaoAcao = actionMenu("label.configuracoes", Icones.CONFIG);
	private Action relacoesAcao = actionMenu("label.relacoes", Icones.SETA);
	private MenuMestreDetalhe menuMestreDetalhe = new MenuMestreDetalhe();
	private Action copiarAcao = actionMenu("label.copiar", Icones.COPIA);
	private Action dadosAcao = actionMenu("label.dados", Icones.TABELA);
	private MenuDistribuicao menuDistribuicao = new MenuDistribuicao();
	private MenuAlinhamento menuAlinhamento = new MenuAlinhamento();
	private MenuArqVinculo menuArqVinculo = new MenuArqVinculo();
	private MenuItem itemPartir = new MenuItem(new PartirAcao());
	private MenuDestacar menuDestacar = new MenuDestacar();
	private MenuCircular menuCircular = new MenuCircular();
	private MenuItem itemDados = new MenuItem(dadosAcao);
	private MenuIgnorar menuIgnorar = new MenuIgnorar();
	private static final long serialVersionUID = 1L;
	final ObjetoSuperficieExt superficie;

	SuperficiePopup(ObjetoSuperficieExt superficie) {
		this.superficie = superficie;
		addMenuItem(processarObjetosAcao);
		addMenuItem(objetosComTabelaAcao);
		add(true, menuAlinhamento);
		add(true, menuDistribuicao);
		addMenuItem(true, copiarIconeAcao);
		addMenuItem(colarIconeAcao);
		addMenuItem(true, copiarAcao);
		add(true, menuDestacar);
		add(true, menuCircular);
		add(true, menuIgnorar);
		add(true, menuMestreDetalhe);
		addMenuItem(true, excluirAcao);
		add(true, menuArqVinculo);
		add(true, itemPartir);
		add(true, itemDados);
		addMenuItem(true, relacoesAcao);
		addMenuItem(criarRelacaoAcao);
		addMenuItem(true, configuracaoAcao);
		eventos();
	}

	private class MenuAlinhamento extends Menu {
		private static final long serialVersionUID = 1L;

		private MenuAlinhamento() {
			super("label.alinhamento");
			add(new MenuItem(new AlinhamentoAcao(true, "label.horizontal")));
			add(new MenuItem(new AlinhamentoAcao(false, "label.vertical")));
		}

		private void preShow(List<Objeto> selecionados) {
			setEnabled(selecionados.size() > Constantes.UM);
		}
	}

	private class AlinhamentoAcao extends Acao {
		private static final long serialVersionUID = 1L;
		private final boolean horizontal;

		private AlinhamentoAcao(boolean horizontal, String chave) {
			super(true, chave, true, horizontal ? Icones.HORIZONTAL : Icones.VERTICAL);
			this.horizontal = horizontal;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (superficie.getSelecionadoObjeto() != null) {
				MacroProvedor.limpar();
				if (horizontal) {
					MacroProvedor.yLocal(superficie.getSelecionadoObjeto().y);
				} else {
					MacroProvedor.xLocal(superficie.getSelecionadoObjeto().x);
				}
				superficie.getMacroManager().macro.actionPerformed(null);
			}
		}
	}

	private class MenuDistribuicao extends Menu {
		Action inverterAcao2 = ObjetoSuperficie.acaoMenu("label.inverter_posicao2");
		Action inverterAcao = ObjetoSuperficie.acaoMenu("label.inverter_posicao");
		Acao distribuicaoHorAcao = new DistribuicaoAcao(true, "label.horizontal");
		Acao distribuicaoVerAcao = new DistribuicaoAcao(false, "label.vertical");
		private static final long serialVersionUID = 1L;

		private MenuDistribuicao() {
			super("label.distribuicao");
			add(new MenuItem(distribuicaoHorAcao));
			add(new MenuItem(distribuicaoVerAcao));
			addMenuItem(true, inverterAcao);
			addMenuItem(inverterAcao2);
			inverterAcao2.setActionListener(e -> inverterPosicao2());
			inverterAcao.setActionListener(e -> inverterPosicao());
		}

		private void preShow(List<Objeto> selecionados) {
			inverterAcao.setEnabled(selecionados.size() == Constantes.UM);
			inverterAcao2.setEnabled(selecionados.size() == Constantes.DOIS);
			distribuicaoHorAcao.setEnabled(selecionados.size() > Constantes.DOIS);
			distribuicaoVerAcao.setEnabled(selecionados.size() > Constantes.DOIS);
		}

		private void inverterPosicao() {
			if (superficie.getSelecionadoObjeto() != null) {
				List<String> list = ObjetoSuperficieUtil.getListaStringIds(superficie);
				list.remove(superficie.getSelecionadoObjeto().getId());
				if (list.isEmpty()) {
					return;
				}
				list.sort(Collator.getInstance());
				Coletor coletor = new Coletor();
				SetLista.Config config = new SetLista.Config(true, true);
				config.setMensagem(ObjetoMensagens.getString("label.sel_outro_obj_para_trocar_pos_com",
						superficie.getSelecionadoObjeto().getId()));
				SetLista.view(superficie.getSelecionadoObjeto().getId(), list, coletor, superficie, config);
				if (coletor.size() == 1) {
					inverter(superficie.getSelecionadoObjeto(),
							ObjetoSuperficieUtil.getObjeto(superficie, coletor.get(0)));
				}
			}
		}

		private void inverterPosicao2() {
			List<Objeto> selecionados = ObjetoSuperficieUtil.getSelecionados(superficie);
			if (selecionados.size() == Constantes.DOIS) {
				Objeto objeto1 = selecionados.get(0);
				Objeto objeto2 = selecionados.get(1);
				inverter(objeto1, objeto2);
			}
		}

		private void inverter(Objeto objeto1, Objeto objeto2) {
			if (objeto1 == null || objeto2 == null) {
				return;
			}
			objeto1.inverterPosicao(objeto2);
			superficie.getAjuste().aproximarObjetoFormulario(false, false, null);
			superficie.getAjuste().empilharFormularios();
			superficie.getAjuste().aproximarObjetoFormulario(true, true, null);
			superficie.getAjustar().usarFormularios(false);
			superficie.repaint();
		}
	}

	private class DistribuicaoAcao extends Acao {
		private static final long serialVersionUID = 1L;
		private final boolean horizontal;

		private DistribuicaoAcao(boolean horizontal, String chave) {
			super(true, chave, true, horizontal ? Icones.HORIZONTAL : Icones.VERTICAL);
			this.horizontal = horizontal;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (superficie.getSelecionadoObjeto() != null) {
				List<Objeto> lista = ObjetoSuperficieUtil.getSelecionados(superficie);
				if (lista.size() < 3) {
					return;
				}
				Collections.sort(lista, new Compara());
				if (horizontal) {
					int totalDifX = lista.get(lista.size() - 1).x - lista.get(0).x;
					int fragmentoX = totalDifX / (lista.size() - 1);
					int x = lista.get(0).x;
					for (int i = 1; i < lista.size(); i++) {
						Objeto objeto = lista.get(i);
						x += fragmentoX;
						objeto.x = x;
					}
				} else {
					int totalDifY = lista.get(lista.size() - 1).y - lista.get(0).y;
					int fragmentoY = totalDifY / (lista.size() - 1);
					int y = lista.get(0).y;
					for (int i = 1; i < lista.size(); i++) {
						Objeto objeto = lista.get(i);
						y += fragmentoY;
						objeto.y = y;
					}
				}
				superficie.repaint();
			}
		}

		private class Compara implements Comparator<Objeto> {
			@Override
			public int compare(Objeto o1, Objeto o2) {
				return horizontal ? o1.x - o2.x : o1.y - o2.y;
			}
		}
	}

	private class MenuCircular extends Menu {
		private Action exportacaoAcao = actionMenu("label.exportacao");
		private Action importacaoAcao = actionMenu("label.importacao");
		private Action normalAcao = actionMenu("label.normal");
		private static final long serialVersionUID = 1L;

		private MenuCircular() {
			super(Constantes.LABEL_CIRCULAR);
			addMenuItem(exportacaoAcao);
			addMenuItem(importacaoAcao);
			addMenuItem(normalAcao);
			exportacaoAcao.setActionListener(e -> abrirModal(Tipo.EXPORTACAO));
			importacaoAcao.setActionListener(e -> abrirModal(Tipo.IMPORTACAO));
			normalAcao.setActionListener(e -> abrirModal(Tipo.NORMAL));
		}

		private void preShow(List<Objeto> selecionados) {
			setEnabled(selecionados.size() > Constantes.UM);
		}

		private void abrirModal(Tipo tipo) {
			if (ObjetoSuperficieUtil.getSelecionados(superficie).size() > Constantes.UM) {
				CircularDialogo.criar(superficie.container.getFrame(), superficie, tipo);
			}
		}
	}

	private class MenuIgnorar extends Menu {
		private Action ignorarFalseAcao = actionMenu("label.false");
		private Action ignorarTrueAcao = actionMenu("label.true");
		private static final long serialVersionUID = 1L;

		private MenuIgnorar() {
			super(Constantes.LABEL_IGNORAR);
			addMenuItem(ignorarFalseAcao);
			addMenuItem(true, ignorarTrueAcao);
			ignorarFalseAcao.setActionListener(e -> ignorar(false));
			ignorarTrueAcao.setActionListener(e -> ignorar(true));
		}

		private void preShow(List<Objeto> selecionados) {
			setEnabled(selecionados.size() > Constantes.ZERO);
		}

		private void ignorar(boolean b) {
			List<Objeto> selecionados = ObjetoSuperficieUtil.getSelecionados(superficie);
			for (Objeto objeto : selecionados) {
				objeto.setIgnorar(b);
			}
			superficie.repaint();
		}
	}

	private class MenuMestreDetalhe extends Menu {
		Action qtdObjetosQuePossuemXFilhosAcao = ObjetoSuperficie.acaoMenu("label.qtd_objetos_que_possuem_x_filhos");
		Action objetoComTotalDeSeusXFilhosAcao = ObjetoSuperficie.acaoMenu("label.objeto_com_total_seus_x_filhos");
		Action qtdObjetosQuePossuemFilhosAcao = ObjetoSuperficie.acaoMenu("label.qtd_objetos_que_possuem_filhos");
		Action maiorTotalFilhosDosObjetosAcao = ObjetoSuperficie.acaoMenu("label.maior_total_filhos_dos_obejtos");
		Action objetoComTotalDeSeusFilhosAcao = ObjetoSuperficie.acaoMenu("label.objeto_com_total_seus_filhos");
		private static final long serialVersionUID = 1L;

		private MenuMestreDetalhe() {
			super("label.mestre_detalhe");
			addMenuItem(qtdObjetosQuePossuemXFilhosAcao);
			addMenuItem(qtdObjetosQuePossuemFilhosAcao);
			addMenuItem(true, maiorTotalFilhosDosObjetosAcao);
			addMenuItem(true, objetoComTotalDeSeusXFilhosAcao);
			addMenuItem(objetoComTotalDeSeusFilhosAcao);
			qtdObjetosQuePossuemXFilhosAcao
					.setActionListener(e -> processar(2, qtdObjetosQuePossuemXFilhosAcao.getText()));
			qtdObjetosQuePossuemFilhosAcao
					.setActionListener(e -> processar(1, qtdObjetosQuePossuemFilhosAcao.getText()));
			objetoComTotalDeSeusXFilhosAcao
					.setActionListener(e -> processar(4, objetoComTotalDeSeusXFilhosAcao.getText()));
			objetoComTotalDeSeusFilhosAcao
					.setActionListener(e -> processar(3, objetoComTotalDeSeusFilhosAcao.getText()));
			maiorTotalFilhosDosObjetosAcao
					.setActionListener(e -> processar(5, maiorTotalFilhosDosObjetosAcao.getText()));
		}

		private void preShow(List<Objeto> selecionados) {
			boolean selecionado = false;
			if (selecionados.size() == Constantes.DOIS) {
				Objeto objeto1 = selecionados.get(0);
				Objeto objeto2 = selecionados.get(1);
				selecionado = !Util.isEmpty(objeto1.getTabela()) && !Util.isEmpty(objeto2.getTabela());
			}
			setEnabled(selecionado);
		}

		private void processar(int tipo, String titulo) {
			List<Objeto> selecionados = ObjetoSuperficieUtil.getSelecionados(superficie);
			if (selecionados.size() == Constantes.DOIS) {
				Objeto objeto1 = selecionados.get(0);
				Objeto objeto2 = selecionados.get(1);
				Coletor coletor = new Coletor();
				List<String> ids = new ArrayList<>();
				ids.add(objeto1.getId());
				ids.add(objeto2.getId());
				SetLista.view(ObjetoMensagens.getString("label.selecione_objeto_mestre"), ids, coletor, superficie,
						new SetLista.Config(true, true));
				if (coletor.size() == 1) {
					Objeto mestre = ObjetoSuperficieUtil.getObjeto(superficie, coletor.get(0));
					MestreDetalhe mestreDetalhe = new MestreDetalhe(superficie, mestre == objeto1 ? objeto1 : objeto2,
							mestre == objeto1 ? objeto2 : objeto1);
					mestreDetalhe.processar(tipo, false, superficie.container.getConexaoPadrao(), titulo);
				}
			}
		}
	}

	private class MenuDestacar extends MenuPadrao1 {
		Action proprioAcao = ObjetoSuperficie.acaoMenu("label.proprio_container");
		Action desktopAcao = Action.actionMenuDesktop();
		private static final long serialVersionUID = 1L;

		private MenuDestacar() {
			super(Constantes.LABEL_DESTACAR, Icones.ARRASTAR, false);
			addMenuItem(desktopAcao);
			addMenuItem(true, proprioAcao);
			formularioAcao.setActionListener(e -> superficie.destacar(superficie.container.getConexaoPadrao(),
					ObjetoConstantes.TIPO_CONTAINER_FORMULARIO, null));
			ficharioAcao.setActionListener(e -> superficie.destacar(superficie.container.getConexaoPadrao(),
					ObjetoConstantes.TIPO_CONTAINER_FICHARIO, null));
			desktopAcao.setActionListener(e -> superficie.destacar(superficie.container.getConexaoPadrao(),
					ObjetoConstantes.TIPO_CONTAINER_DESKTOP, null));
			proprioAcao.setActionListener(e -> destacarProprioContainer());
			formularioAcao.text(ObjetoMensagens.getString("label.abrir_sel_em_formulario"));
			ficharioAcao.text(ObjetoMensagens.getString("label.abrir_sel_em_fichario"));
			desktopAcao.text(ObjetoMensagens.getString("label.abrir_sel_em_desktop"));
		}

		private void destacarProprioContainer() {
			List<Objeto> lista = ObjetoSuperficieUtil.getSelecionados(superficie);
			if (ObjetoSuperficieUtil.getContinua(lista)) {
				String ajustes = nomeObjetosAjusteAuto(lista);
				if (!Util.isEmpty(ajustes) && !Util.confirmar(superficie,
						ObjetoMensagens.getString("msb.objeto_com_ajuste_auto", "[" + ajustes + "]"), false)) {
					return;
				}
				superficie.destacar(superficie.container.getConexaoPadrao(), ObjetoConstantes.TIPO_CONTAINER_PROPRIO,
						null);
			}
		}

		private String nomeObjetosAjusteAuto(List<Objeto> lista) {
			StringBuilder sb = new StringBuilder();
			for (Objeto objeto : lista) {
				if (objeto.isAjusteAutoForm()) {
					if (sb.length() > 0) {
						sb.append(Constantes.QL + ",");
					}
					sb.append(objeto.getId());
				}
			}
			return sb.toString();
		}
	}

	private void eventos() {
		dadosAcao.setActionListener(e -> {
			Object object = itemDados.getObject();
			if (object instanceof Objeto) {
				abrirObjeto((Objeto) object);
			}
		});
		excluirAcao.setActionListener(e -> ObjetoSuperficieUtil.excluirSelecionados(superficie));
		objetosComTabelaAcao.setActionListener(e -> objetosComTabela());
		processarObjetosAcao.setActionListener(e -> processarObjetos());
		criarRelacaoAcao.setActionListener(e -> criarRelacao());
		relacoesAcao.setActionListener(e -> {
			if (superficie.getSelecionadoObjeto() != null) {
				try {
					selecionarRelacao(superficie.getSelecionadoObjeto());
				} catch (ObjetoException ex) {
					Util.mensagem(SuperficiePopup.this, ex.getMessage());
				}
			}
		});
		configuracaoAcao.setActionListener(e -> {
			Frame frame = superficie.container.getFrame();
			if (superficie.getSelecionadoObjeto() != null) {
				ObjetoDialogo.criar(frame, superficie, superficie.getSelecionadoObjeto());
			} else if (superficie.getSelecionadoRelacao() != null) {
				try {
					RelacaoDialogo.criar(frame, superficie, superficie.getSelecionadoRelacao());
				} catch (AssistenciaException ex) {
					Util.mensagem(SuperficiePopup.this, ex.getMessage());
				}
			}
		});
		copiarIconeAcao.setActionListener(e -> copiarIcone(copiarIconeAcao));
		colarIconeAcao.setActionListener(e -> colarIcone(colarIconeAcao));
		copiarAcao.setActionListener(e -> copiar());
	}

	private void objetosComTabela() {
		Coletor coletor = superficie.getObjetosComTabela(Estado.SELECIONADO);
		if (coletor.size() == 0) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		for (String tabela : coletor.getLista()) {
			if (sb.length() > 0) {
				sb.append(Constantes.QL);
			}
			sb.append(tabela);
		}
		Util.mensagem(superficie, sb.toString());
	}

	private void processarObjetos() {
		Map<String, Object> args = new HashMap<>();
		args.put(InstrucaoEvento.BIBLIO_PARA_OBJETO_REQUEST, "");
		superficie.getFormulario().processar(args);
		String result = (String) args.get(InstrucaoEvento.BIBLIO_PARA_OBJETO_RESPONSE);
		if (result == null) {
			Util.mensagem(superficie.getFormulario(),
					ObjetoMensagens.getString("msg.nenhum_instrucao_biblio_paraObjeto_result"));
			return;
		}
		String nomeBiblio = ObjetoUtil.getSelBiblio(superficie, result);
		if (nomeBiblio == null) {
			return;
		}
		try {
			nomeBiblio = nomeBiblio.trim();
			Processador processador = new Processador();
			List<Object> resp = processador.processar(nomeBiblio, "processar",
					ObjetoUtil.criarLista(ObjetoSuperficieUtil.getSelecionados(superficie)));
			if (ObjetoUtil.isTextPool(resp)) {
				TextPool textPool = (TextPool) resp.get(0);
				Util.mensagem(superficie, textPool.getListaText());
			} else {
				String string = ObjetoUtil.getStringResposta(resp);
				Util.mensagem(superficie, string);
			}
		} catch (Exception ex) {
			Util.stackTraceAndMessage("PROCESSAR_OBJETOS", ex, superficie);
		}
	}

	private void criarRelacao() {
		List<Objeto> selecionados = ObjetoSuperficieUtil.getSelecionados(superficie);
		if (selecionados.size() == Constantes.DOIS) {
			Objeto objeto1 = selecionados.get(0);
			Objeto objeto2 = selecionados.get(1);
			try {
				Relacao relacao = ObjetoSuperficieUtil.getRelacao(superficie, objeto1, objeto2);
				if (relacao == null) {
					relacao = new Relacao(objeto1, true, objeto2, true);
					superficie.addRelacao(relacao);
				}
				superficie.repaint();
				RelacaoDialogo.criar(superficie.container.getFrame(), superficie, relacao);
			} catch (ObjetoException | AssistenciaException ex) {
				Util.mensagem(superficie, ex.getMessage());
			}
		}
	}

	private void copiar() {
		try {
			CopiarColar.copiar(superficie);
		} catch (AssistenciaException ex) {
			Util.mensagem(SuperficiePopup.this, ex.getMessage());
		}
	}

	private void copiarIcone(Action action) {
		if (action.getObject() instanceof Objeto) {
			Objeto objeto = (Objeto) action.getObject();
			IconeContainer.setNomeIconeCopiado(objeto.getIcone());
		}
	}

	private void colarIcone(Action action) {
		if (action.getObject() instanceof Objeto && !Util.isEmpty(IconeContainer.getNomeIconeCopiado())) {
			try {
				String nome = IconeContainer.getNomeIconeCopiado();
				Objeto objeto = (Objeto) action.getObject();
				objeto.setIcone(nome);
				MacroProvedor.imagem(objeto.getIcone());
				superficie.repaint();
			} catch (AssistenciaException ex) {
				Util.mensagem(SuperficiePopup.this, ex.getMessage());
			}
		}
	}

	private void abrirObjeto(Objeto objeto) {
		Conexao conexao = superficie.container.getConexaoPadrao();
		ObjetoSuperficieUtil.criarExternalFormulario(superficie, conexao, objeto);
	}

	private void selecionarRelacao(Objeto objeto) throws ObjetoException {
		List<Relacao> lista = ObjetoSuperficieUtil.getRelacoes(superficie, objeto);
		List<String> ids = montarIds(lista, objeto);
		if (!ids.isEmpty()) {
			Coletor coletor = new Coletor();
			SetLista.view(objeto.getId(), ids, coletor, superficie, new SetLista.Config(true, true));
			if (coletor.size() == 1) {
				superficie.setSelecionadoObjeto(null);
				String id = coletor.get(0);
				Objeto outro = ObjetoSuperficieUtil.getObjeto(superficie, id);
				superficie.setSelecionadoRelacao(ObjetoSuperficieUtil.getRelacao(superficie, objeto, outro));
				superficie.popup.configuracaoAcao.actionPerformed(null);
			}
		}
	}

	private List<String> montarIds(List<Relacao> lista, Objeto objeto) {
		List<String> resp = new ArrayList<>();
		for (Relacao rel : lista) {
			if (rel.getOrigem().equals(objeto)) {
				resp.add(rel.getDestino().getId());
			} else if (rel.getDestino().equals(objeto)) {
				resp.add(rel.getOrigem().getId());
			}
		}
		return resp;
	}

	void preShow(boolean objetoSelecionado) {
		List<Objeto> selecionados = ObjetoSuperficieUtil.getSelecionados(superficie);
		Objeto objeto = superficie.getSelecionadoObjeto();
		String nomeTabela = objeto != null ? objeto.getTabela() : null;
		boolean comTabela = objetoSelecionado && objeto != null && !Util.isEmpty(nomeTabela);
		relacoesAcao.setEnabled(objetoSelecionado && selecionados.size() == Constantes.UM);
		itemDados.setEnabled(comTabela && selecionados.size() == Constantes.UM);
		copiarIconeAcao.setEnabled(checarCopiarColarIcone(true, selecionados));
		copiarIconeAcao.setObject(copiarIconeAcao.isEnabled() ? objeto : null);
		colarIconeAcao.setEnabled(checarCopiarColarIcone(false, selecionados));
		colarIconeAcao.setObject(colarIconeAcao.isEnabled() ? objeto : null);
		criarRelacaoAcao.setEnabled(selecionados.size() == Constantes.DOIS);
		itemDados.setObject(itemDados.isEnabled() ? objeto : null);
		menuDistribuicao.setEnabled(objetoSelecionado);
		menuArqVinculo.setEnabled(!objetoSelecionado);
		itemPartir.setEnabled(!objetoSelecionado);
		copiarAcao.setEnabled(objetoSelecionado);
		menuMestreDetalhe.preShow(selecionados);
		menuDistribuicao.preShow(selecionados);
		menuAlinhamento.preShow(selecionados);
		menuCircular.preShow(selecionados);
		menuIgnorar.preShow(selecionados);
		menuDestacar.setEnabled(comTabela);
		if (objetoSelecionado) {
			configuracaoAcao.setEnabled(selecionados.size() == Constantes.UM);
		} else {
			configuracaoAcao.setEnabled(true);
		}
	}

	private boolean checarCopiarColarIcone(boolean copiar, List<Objeto> selecionados) {
		boolean resposta = false;
		if (selecionados.size() == Constantes.UM) {
			Objeto objeto = selecionados.get(0);
			resposta = copiar ? !Util.isEmpty(objeto.getIcone()) : !Util.isEmpty(IconeContainer.getNomeIconeCopiado());
		}
		return resposta;
	}

	private class PartirAcao extends Acao {
		private static final long serialVersionUID = 1L;

		private PartirAcao() {
			super(true, ObjetoMensagens.getString("label.partir"), false, Icones.PARTIR);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (superficie.getSelecionadoRelacao() != null) {
				try {
					Objeto novo = superficie.getSelecionadoRelacao().criarObjetoMeio();
					Objeto destino = superficie.getSelecionadoRelacao().getDestino();
					Objeto origem = superficie.getSelecionadoRelacao().getOrigem();
					ObjetoSuperficieUtil.checagemId(superficie, novo, Constantes.VAZIO, Constantes.VAZIO);
					superficie.addObjeto(novo);
					superficie.getSelecionadoRelacao().setSelecionado(false);
					superficie.excluir(superficie.getSelecionadoRelacao());
					superficie.setSelecionadoRelacao(null);
					superficie.addRelacao(new Relacao(origem, false, novo, false));
					superficie.addRelacao(new Relacao(novo, false, destino, false));
					superficie.repaint();
				} catch (ObjetoException | AssistenciaException ex) {
					Util.mensagem(SuperficiePopup.this, ex.getMessage());
				}
			}
		}
	}

	private class MenuArqVinculo extends Menu {
		private Action removeVinculadoAcao = acaoMenu(RelacaoContainer.LABEL_VINCULO_REMOVE, Icones.SUCESSO);
		private Action addVinculadoAcao = acaoMenu(ObjetoConstantes.LABEL_VINCULO, Icones.SUCESSO);
		private static final long serialVersionUID = 1L;

		private MenuArqVinculo() {
			super("label.arquivo_vinculo");
			addMenuItem(addVinculadoAcao);
			addMenuItem(removeVinculadoAcao);
			removeVinculadoAcao.setActionListener(e -> excluirVinculado());
			addVinculadoAcao.setActionListener(e -> adicionarVinculado());
		}

		Action acaoMenu(String chave, Icon icon) {
			return Action.acaoMenu(ObjetoMensagens.getString(chave), icon);
		}

		private void adicionarVinculado() {
			Relacao relacao = superficie.getSelecionadoRelacao();
			if (relacao == null) {
				return;
			}
			Vinculacao vinculacao = null;
			try {
				vinculacao = ObjetoSuperficieUtil.getVinculacao(superficie);
			} catch (Exception ex) {
				Util.stackTraceAndMessage("ADICIONAR OBJETO VINCULADO", ex, superficie);
				return;
			}
			if (vinculacao == null) {
				Util.mensagem(superficie, ObjetoMensagens.getString(ObjetoConstantes.CHAVE_MENSAGEM_VI));
				return;
			}
			try {
				RelacaoVinculo relacaoVinculo = vinculacao.getRelacaoVinculo(relacao);
				if (relacaoVinculo != null) {
					relacaoVinculo.copiarProps(relacao);
					salvarVinculacao(vinculacao);
				} else if (vinculacao.adicionarRelacaoVinculo(relacao)) {
					salvarVinculacao(vinculacao);
				}
			} catch (Exception ex) {
				Util.stackTraceAndMessage("ADICIONAR OBJETO VINCULADO", ex, superficie);
			}
		}

		private void excluirVinculado() {
			Relacao relacao = superficie.getSelecionadoRelacao();
			if (relacao == null) {
				return;
			}
			Vinculacao vinculacao = null;
			try {
				vinculacao = ObjetoSuperficieUtil.getVinculacao(superficie);
			} catch (Exception ex) {
				Util.stackTraceAndMessage("EXCLUIR OBJETO VINCULADO", ex, superficie);
				return;
			}
			if (vinculacao == null) {
				Util.mensagem(superficie, ObjetoMensagens.getString(ObjetoConstantes.CHAVE_MENSAGEM_VI));
				return;
			}
			RelacaoVinculo relacaoVinculo = vinculacao.getRelacaoVinculo(relacao);
			if (vinculacao.excluirRelacaoVinculo(relacaoVinculo)) {
				salvarVinculacao(vinculacao);
			}
		}

		private void salvarVinculacao(Vinculacao vinculacao) {
			ObjetoSuperficieUtil.salvarVinculacao(superficie, vinculacao);
		}
	}
}

class MestreDetalhe {
	final ObjetoSuperficie superficie;
	String colunaDetalhe;
	final Objeto detalhe;
	String colunaMestre;
	final Objeto mestre;
	Conexao conexao;

	MestreDetalhe(ObjetoSuperficie superficie, Objeto mestre, Objeto detalhe) {
		this.superficie = superficie;
		this.mestre = mestre;
		this.detalhe = detalhe;
	}

	void processar(int tipo, boolean abrirEmForm, Conexao conexao, String titulo) {
		this.conexao = conexao;
		if (conexao == null) {
			return;
		}
		InternalFormulario internalMestre = ObjetoSuperficieUtil.getInternalFormulario(superficie, mestre);
		if (internalMestre == null) {
			Util.mensagem(superficie, ObjetoMensagens.getString("msg.sem_form_internal_associado", mestre.getId()));
			return;
		}
		InternalFormulario internalDetalhe = ObjetoSuperficieUtil.getInternalFormulario(superficie, detalhe);
		if (internalDetalhe == null) {
			Util.mensagem(superficie, ObjetoMensagens.getString("msg.sem_form_internal_associado", detalhe.getId()));
			return;
		}
		List<String> colunasMestes = internalMestre.getNomeColunas();
		colunaMestre = selecionarColuna(colunasMestes, "msg.selecione_coluna_mestre", mestre.getId(), null);
		if (Util.isEmpty(colunaMestre)) {
			return;
		}
		List<String> colunasDetalhe = internalDetalhe.getNomeColunas();
		colunaDetalhe = selecionarColuna(colunasDetalhe, "msg.selecione_coluna_detalhe", detalhe.getId(), colunaMestre);
		if (Util.isEmpty(colunaDetalhe)) {
			return;
		}
		montarInstrucao(tipo, abrirEmForm, conexao, titulo);
	}

	private void montarInstrucao(int tipo, boolean abrirEmForm, Conexao conexao, String titulo) {
		String instrucao = null;
		if (tipo == 1) {
			instrucao = qtdObjetosQuePossuemFilhos();
		} else if (tipo == 2) {
			instrucao = qtdObjetosQuePossuemXFilhos();
		} else if (tipo == 3) {
			instrucao = objetoComTotalDeSeusFilhos();
		} else if (tipo == 4) {
			instrucao = objetoComTotalDeSeusXFilhos();
		} else if (tipo == 5) {
			instrucao = maiorTotalFilhosDosObjetos();
		}
		selectFormDialog(abrirEmForm, conexao, instrucao, titulo + " [Objeto mestre: " + mestre.getId() + "]");
	}

	private void selectFormDialog(boolean abrirEmForm, Conexao conexao, String instrucao, String titulo) {
		if (abrirEmForm) {
			Formulario frame = superficie.getFormulario();
			ConsultaFormulario form = ConsultaFormulario.criar2(frame, conexao, instrucao);
			Formulario.posicionarJanela(frame, form);
			form.setTitle(titulo);
			form.setVisible(true);
		} else {
			Formulario frame = superficie.getFormulario();
			Component comp = Util.getViewParent(superficie);
			ConsultaDialogo form = ConsultaDialogo.criar2(frame, conexao, instrucao);
			config2(comp, frame, form);
			form.setTitle(titulo);
			form.setVisible(true);
		}
	}

	private void config2(Component c, Window parent, Window child) {
		if (c instanceof Window) {
			Util.configSizeLocation((Window) c, child, superficie);
		} else {
			Util.configSizeLocation(parent, child, superficie);
		}
	}

	private String selecionarColuna(List<String> colunas, String chaveTitulo, String param, String selecionarItem) {
		Coletor coletor = new Coletor();
		SetLista.view(ObjetoMensagens.getString(chaveTitulo, param), colunas, coletor, superficie,
				new SetLista.Config(true, true, selecionarItem));
		if (coletor.size() == 1) {
			return coletor.get(0);
		}
		return null;
	}

	private String maiorTotalFilhosDosObjetos() {
		StringBuilder sb = new StringBuilder("SELECT MAX(tabela.TOTAL)");
		sb.append("\nFROM (" + selectColunaMestre() + ", " + countColunaDetalhe() + " AS TOTAL");
		sb.append(fromMestre());
		sb.append(innerJoinDetalhe());
		sb.append("\nGROUP BY " + colunaMestre());
		sb.append("\n) tabela");
		return sb.toString();
	}

	private String qtdObjetosQuePossuemFilhos() {
		StringBuilder sb = new StringBuilder(selectCountColunaMestre());
		sb.append(fromMestre());
		sb.append(whereExists());
		sb.append(selectColunaDetalhe());
		sb.append(fromDetalhe());
		sb.append(whereColunaMestreIgualColunaDetalhe());
		sb.append("\n)");
		return sb.toString();
	}

	private String qtdObjetosQuePossuemXFilhos() {
		StringBuilder sb = new StringBuilder(selectCountColunaMestre());
		sb.append(fromMestre());
		sb.append(existsCount());
		return sb.toString();
	}

	private String objetoComTotalDeSeusFilhos() {
		StringBuilder sb = new StringBuilder(selectColunaMestre() + ", " + countColunaDetalhe());
		sb.append(fromMestre());
		sb.append(innerJoinDetalhe());
		sb.append(groupByColunaMestre());
		return sb.toString();
	}

	private String objetoComTotalDeSeusXFilhos() {
		StringBuilder sb = new StringBuilder(selectColunaMestre() + ", " + countColunaDetalhe());
		sb.append(fromMestre());
		sb.append(innerJoinDetalhe());
		sb.append(existsCount());
		sb.append(groupByColunaMestre());
		return sb.toString();
	}

	private String existsCount() {
		StringBuilder sb = new StringBuilder();
		sb.append(whereExists());
		sb.append(selectColunaDetalhe() + ", COUNT(*)");
		sb.append(fromDetalhe());
		sb.append(whereColunaMestreIgualColunaDetalhe());
		sb.append("\n  GROUP BY " + colunaDetalhe());
		sb.append("\n  HAVING COUNT(*) > 1");
		sb.append("\n)");
		return sb.toString();
	}

	private String groupByColunaMestre() {
		StringBuilder sb = new StringBuilder();
		sb.append("\nGROUP BY " + colunaMestre());
		sb.append("\nORDER BY COUNT(" + colunaDetalhe() + ") ASC");
		if (!Util.isEmpty(conexao.getFiltro())) {
			sb.append("\n\n--" + conexao.getFiltro());
			sb.append("\n--ORDER BY " + colunaMestre() + " ASC");
		} else {
			sb.append("\n\n--ORDER BY " + colunaMestre() + " ASC");
		}
		return sb.toString();
	}

	private String innerJoinDetalhe() {
		return "\n  INNER JOIN " + fromDetalhe2() + " ON " + colunaMestreIgualColunaDetalhe();
	}

	private String fromDetalhe2() {
		return detalhe.getTabelaEsquema2(conexao) + " detalhe";
	}

	private String fromDetalhe() {
		return "\n  FROM " + detalhe.getTabelaEsquema2(conexao) + " detalhe";
	}

	private String fromMestre() {
		return "\nFROM " + mestre.getTabelaEsquema2(conexao) + " mestre";
	}

	private String whereColunaMestreIgualColunaDetalhe() {
		return "\n  WHERE " + colunaMestreIgualColunaDetalhe();
	}

	private String colunaMestreIgualColunaDetalhe() {
		return colunaMestre() + " = " + colunaDetalhe();
	}

	private String selectCountColunaMestre() {
		return "SELECT COUNT(" + colunaMestre() + ")";
	}

	private String countColunaDetalhe() {
		return "COUNT(" + colunaDetalhe() + ")";
	}

	private String selectColunaMestre() {
		return "SELECT " + colunaMestre();
	}

	private String selectColunaDetalhe() {
		return "SELECT " + colunaDetalhe();
	}

	private String colunaDetalhe() {
		return "detalhe." + colunaDetalhe;
	}

	private String colunaMestre() {
		return "mestre." + colunaMestre;
	}

	private String whereExists() {
		return whereExist;
	}

	String whereExist = "\nWHERE EXISTS (";
}

class Exportacao {
	private final Map<String, Object> mapaRef;
	final ObjetoSuperficie superficie;
	final Objeto principal;
	final Metadado raiz;
	Metadado campoFK;
	Relacao relacao;
	Objeto objeto;
	boolean erro;

	Exportacao(ObjetoSuperficie superficie, Objeto principal, Map<String, Object> mapaRef, Metadado raiz) {
		this.superficie = superficie;
		this.principal = principal;
		this.mapaRef = mapaRef;
		this.raiz = raiz;
	}

	void criarPesquisa() throws ObjetoException {
		criarPesquisa(Mensagens.getString("label.andamento"), principal.getGrupo(), principal.getTabela(),
				principal.getChaves(), "executar");
	}

	private void criarPesquisa(String nome, String grupo, String tabela, String campo, String iconeGrupo)
			throws ObjetoException {
		Referencia ref = new Referencia(grupo, tabela, campo);
		mapaRef.put(ObjetoConstantes.PESQUISA, new Pesquisa(nome, ref, iconeGrupo));
	}

	Metadado getTabelaAvulsa() {
		List<Metadado> tabelas = new ArrayList<>();
		raiz.preencher(tabelas);
		Coletor coletor = new Coletor();
		SetLista.view(principal.getId() + ObjetoMensagens.getString("label.selecione_tabela_avulsa"),
				nomeTabelas(tabelas), coletor, superficie, new SetLista.Config(true, true));
		if (coletor.size() == 1) {
			String nomeTabela = coletor.get(0);
			return raiz.getMetadado(nomeTabela);
		}
		return null;
	}

	void processarDetalhes(Metadado tabela) throws MetadadoException, ObjetoException, AssistenciaException {
		List<Metadado> campos = tabela.getListaCampoExportacaoImportacao(true);
		Coletor coletor = new Coletor();
		SetLista.view(principal.getId() + ObjetoMensagens.getString("label.adicionar_hierarquico"), nomeCampos(campos),
				coletor, superficie, new SetLista.Config(true, true));
		if (coletor.size() == 1) {
			processarCampoFK(getCampo(campos, coletor.get(0)));
		} else {
			erro = true;
		}
	}

	private List<String> nomeCampos(List<Metadado> campos) throws MetadadoException {
		List<String> resp = new ArrayList<>();
		for (Metadado metadado : campos) {
			resp.add(metadado.getChaveTabelaReferencia());
		}
		return resp;
	}

	private List<String> nomeTabelas(List<Metadado> tabelas) {
		return tabelas.stream().map(Metadado::getDescricao).collect(Collectors.toList());
	}

	private Metadado getCampo(List<Metadado> campos, String nomeCampo) throws MetadadoException {
		for (Metadado metadado : campos) {
			if (metadado.getChaveTabelaReferencia().equals(nomeCampo)) {
				return metadado;
			}
		}
		return null;
	}

	void adicionarObjetoAvulso(Metadado tabela) throws AssistenciaException {
		criarEAdicionarObjeto();
		processarIdTabelaGrupoAvulso(tabela);
		processarChavesAvulso(tabela);
	}

	private void processarCampoFK(Metadado campo) throws MetadadoException, ObjetoException, AssistenciaException {
		campoFK = campo;
		criarEAdicionarObjeto();
		criarEAdicionarRelacao();
		processarIdTabelaGrupo();
		processarChaves();
	}

	private void criarEAdicionarObjeto() throws AssistenciaException {
		Objeto obj = new Objeto(0, 0);
		ObjetoSuperficieUtil.checagemId(superficie, obj, obj.getId(), Constantes.SEP2);
		this.objeto = obj;
		superficie.addObjeto(obj);
	}

	private void criarEAdicionarRelacao() throws ObjetoException {
		Relacao rel = new Relacao(principal, false, objeto, true);
		rel.setPontoDestino(false);
		rel.setPontoOrigem(false);
		rel.setQuebrado(true);
		this.relacao = rel;
		superficie.addRelacao(rel);
	}

	private void processarIdTabelaGrupo() throws MetadadoException {
		Metadado tabelaRef = campoFK.getTabelaReferencia();
		superficie.processarIdTabelaGrupoExportacao(objeto, tabelaRef);
		objeto.setTabela(tabelaRef.getNomeTabela());
	}

	private void processarIdTabelaGrupoAvulso(Metadado tabela) {
		superficie.processarIdTabelaGrupoExportacaoAvulso(objeto, tabela);
		objeto.setTabela(tabela.getDescricao());
	}

	private void processarChaves() throws MetadadoException, ObjetoException {
		Metadado tabelaRef = campoFK.getTabelaReferencia();
		Metadado tabela = raiz.getMetadado(tabelaRef.getNomeTabela());
		if (tabela != null) {
			processarChaves(tabela);
		}
	}

	private void processarChaves(Metadado tabela) throws MetadadoException, ObjetoException {
		Metadado tabelaRef = campoFK.getTabelaReferencia();
		relacao.setChaveDestino(tabelaRef.getNomeCampo());
		relacao.setChaveOrigem(principal.getChaves());
		objeto.setChaves(tabela.getChaves());
		referenciaNaPesquisa();
	}

	private void processarChavesAvulso(Metadado tabela) {
		objeto.setChaves(tabela.getChaves());
	}

	private void referenciaNaPesquisa() throws MetadadoException, ObjetoException {
		Metadado tabelaRef = campoFK.getTabelaReferencia();
		ref(tabelaRef.getNomeTabela(), tabelaRef.getNomeCampo(), objeto.getGrupo());
	}

	private void ref(String tabela, String campo, String grupo) throws ObjetoException {
		Referencia ref = new Referencia(grupo, tabela, campo);
		ref.setVazioInvisivel(true);
		mapaRef.put("ref", ref);
		Pesquisa pesquisa = (Pesquisa) mapaRef.get(ObjetoConstantes.PESQUISA);
		objeto.addReferencia(pesquisa.getReferencia());
		pesquisa.add(ref);
	}

	void localizarObjetoAcima() {
		InternalFormulario interno = ObjetoSuperficieUtil.getInternalFormulario(superficie, principal);
		if (interno != null) {
			objeto.x = principal.x - Constantes.VINTE_CINCO;
			objeto.y = interno.getY() - Constantes.TRINTA;
			objeto.setDeslocamentoXId(28);
			objeto.setDeslocamentoYId(24);
			objeto.setChecarLargura(true);
			objeto.setCorTmp(Color.GREEN);
			ObjetoSuperficieUtil.limparSelecao(superficie);
			objeto.setSelecionado(true);
		}
	}

	void localizarObjetoAbaixo() {
		InternalFormulario interno = ObjetoSuperficieUtil.getInternalFormulario(superficie, principal);
		if (interno != null) {
			objeto.x = principal.x + Constantes.VINTE_CINCO;
			objeto.y = interno.getY() + Constantes.TRINTA;
			objeto.setDeslocamentoXId(28);
			objeto.setDeslocamentoYId(24);
			objeto.setChecarLargura(true);
			objeto.setCorTmp(Color.GREEN);
			ObjetoSuperficieUtil.limparSelecao(superficie);
			objeto.setSelecionado(true);
		}
	}

	void setScriptAdicaoHierarquico() throws ObjetoException {
		Pesquisa pesquisa = (Pesquisa) mapaRef.get(ObjetoConstantes.PESQUISA);
		objeto.setPesquisaAdicaoHierarquico(pesquisa);
		Referencia ref = pesquisa.get();
		if (ref == null) {
			return;
		}
		Pesquisa invertido = ref.rotuloDe(principal);
		if (invertido != null) {
			mapaRef.put(ObjetoConstantes.PESQUISA_INVERTIDO, invertido);
			objeto.addPesquisa(invertido);
			objeto.setBuscaAutoTemp(true);
			objeto.addReferencias(invertido.getReferencias());
			principal.addReferencia(invertido.getReferencia());
		}
	}
}

class ExportacaoImportacao {
	final List<Objeto> objetos = new ArrayList<>();
	private final Map<String, Object> mapaRef;
	private final List<Pesquisa> listaRef;
	final ObjetoSuperficie superficie;
	final boolean exportacao;
	Metadado campoProcessado;
	final boolean circular;
	final Objeto principal;
	Metadado raiz;
	Vetor vetor;
	int centroX;
	int centroY;
	int graus;
	int y;

	ExportacaoImportacao(ObjetoSuperficie superficie, boolean exportacao, boolean circular)
			throws AssistenciaException {
		mapaRef = new LinkedHashMap<>();
		this.superficie = superficie;
		this.exportacao = exportacao;
		listaRef = new ArrayList<>();
		principal = new Objeto(0, 0);
		this.circular = circular;
	}

	void definirCentros(Dimension d) {
		centroY = d.height / 2 - 25;
		centroX = d.width / 2;
	}

	void criarVetor(Dimension d) {
		int comprimento = Math.min(d.width, d.height) / 2 - 50;
		vetor = new Vetor(comprimento, 0);
	}

	void processarPrincipal(Metadado tabela) {
		iniciarPrincipal(tabela);
		superficie.addObjeto(principal);
	}

	private void iniciarPrincipal(Metadado metadado) {
		principal.setTabela(metadado.getDescricao());
		principal.setChaves(metadado.getChaves());
		principal.setId(metadado.getDescricao());
		raiz = metadado.getPai();
	}

	void checarCriarPesquisa() throws ObjetoException {
		if (exportacao) {
			criarPesquisa(Mensagens.getString("label.andamento"), principal.getGrupo(), principal.getTabela(),
					principal.getChaves(), "executar");
		}
	}

	private void criarPesquisa(String nome, String grupo, String tabela, String campo, String iconeGrupo)
			throws ObjetoException {
		Referencia ref = new Referencia(grupo, tabela, campo);
		mapaRef.put(ObjetoConstantes.PESQUISA, new Pesquisa(nome, ref, iconeGrupo));
	}

	void processarDetalhes(Metadado tabela) throws MetadadoException, ObjetoException, AssistenciaException {
		List<Metadado> campos = tabela.getListaCampoExportacaoImportacao(exportacao);
		processarLista(campos);
	}

	private void processarLista(List<Metadado> campos) throws MetadadoException, ObjetoException, AssistenciaException {
		for (Metadado campo : campos) {
			campoProcessado = campo;
			Objeto objeto = criarEAdicionarObjeto();
			Relacao relacao = criarEAdicionarRelacao(objeto);
			processarIdTabelaGrupo(objeto);
			processarChaves(objeto, relacao);
		}
	}

	private Objeto criarEAdicionarObjeto() throws AssistenciaException {
		Objeto objeto = new Objeto(0, 0);
		objetos.add(objeto);
		superficie.addObjeto(objeto);
		return objeto;
	}

	private Relacao criarEAdicionarRelacao(Objeto objeto) throws ObjetoException {
		Relacao relacao = new Relacao(principal, !exportacao, objeto, exportacao);
		relacao.setQuebrado(!circular);
		if (!circular) {
			relacao.setPontoDestino(false);
			relacao.setPontoOrigem(false);
		}
		superficie.addRelacao(relacao);
		return relacao;
	}

	private void processarIdTabelaGrupo(Objeto objeto) throws MetadadoException {
		Metadado tabelaRef = campoProcessado.getTabelaReferencia();
		if (exportacao) {
			superficie.processarIdTabelaGrupoExportacao(objeto, tabelaRef);
		} else {
			processarIdTabelaGrupoImportacao(objeto, tabelaRef);
		}
		objeto.setTabela(tabelaRef.getNomeTabela());
	}

	private void processarIdTabelaGrupoImportacao(Objeto objeto, Metadado tabelaRef) {
		String nomeTabela = tabelaRef.getNomeTabela();
		if (ObjetoSuperficieUtil.contemObjetoComTabela(superficie, nomeTabela)) {
			String id = nomeTabela + Constantes.SEP2 + campoProcessado.getDescricao();
			objeto.setId(id);
			ObjetoSuperficieUtil.checagemId(superficie, objeto, id, Constantes.SEP2);
			objeto.setGrupo(campoProcessado.getDescricao());
		} else {
			objeto.setId(nomeTabela);
		}
	}

	private void processarChaves(Objeto objeto, Relacao relacao) throws MetadadoException, ObjetoException {
		Metadado tabelaRef = campoProcessado.getTabelaReferencia();
		Metadado tabela = raiz.getMetadado(tabelaRef.getNomeTabela());
		if (tabela != null) {
			processarChaves(objeto, tabela, relacao);
		}
	}

	private void processarChaves(Objeto objeto, Metadado tabela, Relacao relacao)
			throws MetadadoException, ObjetoException {
		Metadado tabelaRef = campoProcessado.getTabelaReferencia();
		relacao.setChaveDestino(tabelaRef.getNomeCampo());
		objeto.setChaves(tabela.getChaves());
		if (exportacao) {
			referenciaNaPesquisa(objeto, relacao);
		} else {
			pesquisaIndividualDetalhe(objeto, relacao);
		}
	}

	private void referenciaNaPesquisa(Objeto objeto, Relacao relacao) throws MetadadoException, ObjetoException {
		Metadado tabelaRef = campoProcessado.getTabelaReferencia();
		ref(tabelaRef.getNomeTabela(), tabelaRef.getNomeCampo(), objeto.getGrupo());
		relacao.setChaveOrigem(principal.getChaves());
	}

	private void ref(String tabela, String campo, String grupo) throws ObjetoException {
		Referencia ref = new Referencia(grupo, tabela, campo);
		ref.setVazioInvisivel(true);
		mapaRef.put("ref", ref);
		Pesquisa pesquisa = (Pesquisa) mapaRef.get(ObjetoConstantes.PESQUISA);
		pesquisa.add(ref);
	}

	private void pesquisaIndividualDetalhe(Objeto objeto, Relacao relacao) throws MetadadoException, ObjetoException {
		Metadado campoDetalhe = campoProcessado;
		Metadado tabelaRef = campoDetalhe.getTabelaReferencia();
		pesquisaDetalhe(tabelaRef.getNomeTabela(), tabelaRef.getNomeCampo(), objeto.getGrupo(), principal.getGrupo(),
				principal.getTabela(), campoDetalhe.getDescricao());
		relacao.setChaveOrigem(campoDetalhe.getDescricao());
	}

	private void pesquisaDetalhe(String tabelaPrincipal, String campoPrincipal, String grupoPrincipal,
			String grupoDetalhe, String tabelaDetalhe, String campoDetalhe) throws ObjetoException {
		Pesquisa pesquisa = new Pesquisa(tabelaPrincipal, new Referencia(grupoDetalhe, tabelaDetalhe, campoDetalhe),
				null);
		Referencia ref = new Referencia(grupoPrincipal, tabelaPrincipal, campoPrincipal);
		ref.setVazioInvisivel(false);
		listaRef.add(pesquisa);
		pesquisa.add(ref);
	}

	void localizarObjetos() {
		if (circular) {
			principal.x = centroX;
			principal.y = centroY;
			if (!objetos.isEmpty()) {
				localizacaoCircular();
			}
		} else {
			localizacaoHierarquica();
		}
	}

	private void localizacaoCircular() {
		graus = 360 / objetos.size();
		for (Objeto objeto : objetos) {
			objeto.x = centroX + (int) vetor.getX();
			objeto.y = centroY + (int) vetor.getY();
			vetor.rotacionar(graus);
		}
	}

	private void localizacaoHierarquica() {
		configuracaoPrincipal();
		localizarHierarquico();
		configuracaoPrincipalFinal();
		atualizarSuperficie();
		principal.setSelecionado(true);
		for (Objeto objeto : objetos) {
			objeto.setSelecionado(true);
		}
	}

	private void configuracaoPrincipal() {
		principal.x = Constantes.VINTE;
		principal.y = Constantes.VINTE;
		deslocamentos(principal);
		y = principal.y;
		if (exportacao) {
			incY();
		}
	}

	private void deslocamentos(Objeto objeto) {
		objeto.setDeslocamentoXId(28);
		objeto.setDeslocamentoYId(24);
	}

	private void incY() {
		y += Constantes.CEM;
	}

	private void localizarHierarquico() {
		for (Objeto obj : objetos) {
			localizacaoDetalhe(obj);
			if (exportacao) {
				obj.x += Constantes.VINTE_CINCO;
			}
		}
	}

	private void localizacaoDetalhe(Objeto objeto) {
		objeto.x = Constantes.VINTE;
		deslocamentos(objeto);
		objeto.y = y;
		incY();
	}

	private void configuracaoPrincipalFinal() {
		if (!exportacao) {
			if (!objetos.isEmpty()) {
				principal.x += Constantes.VINTE_CINCO;
			}
			principal.y = y;
			incY();
		}
	}

	private void atualizarSuperficie() {
		if (!circular) {
			superficie.setPreferredSize(new Dimension(0, y));
			Util.updateComponentTreeUI(superficie.getParent());
		}
	}

	String getString() {
		try {
			Pesquisa pesquisa = (Pesquisa) mapaRef.get(ObjetoConstantes.PESQUISA);
			if (pesquisa != null) {
				return ObjetoUtil.getDescricao(pesquisa);
			}
			StringWriter sw = new StringWriter();
			XMLUtil util = new XMLUtil(sw);
			boolean ql = false;
			for (Pesquisa pesq : listaRef) {
				pesq.salvar(util, ql);
				ql = true;
			}
			util.close();
			return sw.toString();
		} catch (Exception ex) {
			Util.stackTraceAndMessage("DESCRICAO", ex, superficie);
		}
		return Constantes.VAZIO;
	}

	public void vincular(AtomicReference<String> ref) {
		try {
			String nomeTabela = principal.getTabela().toLowerCase();
			superficie.setArquivoVinculo(nomeTabela + "_vinculo.xml");
			Vinculacao vinculo = ObjetoSuperficieUtil.getVinculacao(superficie,
					ObjetoSuperficieUtil.criarArquivoVinculo(superficie), true);
			Pesquisa pesquisa = (Pesquisa) mapaRef.get(ObjetoConstantes.PESQUISA);
			if (vinculo != null && pesquisa != null) {
				salvar(pesquisa);
			} else if (vinculo != null && !listaRef.isEmpty()) {
				salvar(listaRef);
			}
			ref.set(nomeTabela + ".xml");
		} catch (Exception ex) {
			Util.stackTraceAndMessage("VINCULAR", ex, superficie);
		}
	}

	private void salvar(Pesquisa pesquisa) throws ObjetoException {
		try {
			superficie.getVinculacao().abrir(ObjetoSuperficieUtil.criarArquivoVinculo(superficie), superficie);
		} catch (Exception ex) {
			Util.stackTraceAndMessage("SALVAR", ex, superficie);
			return;
		}
		superficie.getVinculacao().adicionarPesquisa(pesquisa);
		for (Referencia ref : pesquisa.getReferencias()) {
			Pesquisa pesq = ref.rotuloDe(principal);
			superficie.getVinculacao().adicionarPesquisa(pesq);
		}
		ObjetoSuperficieUtil.salvarVinculacao(superficie, superficie.getVinculacao());
	}

	private void salvar(List<Pesquisa> listaRef) throws ObjetoException {
		try {
			superficie.getVinculacao().abrir(ObjetoSuperficieUtil.criarArquivoVinculo(superficie), superficie);
		} catch (Exception ex) {
			Util.stackTraceAndMessage("VINCULAR", ex, superficie);
			return;
		}
		for (Pesquisa pesq : listaRef) {
			superficie.getVinculacao().adicionarPesquisa(pesq);
			for (Referencia ref : pesq.getReferencias()) {
				superficie.getVinculacao().adicionarPesquisa(ref.rotuloDe(principal));
			}
		}
		ObjetoSuperficieUtil.salvarVinculacao(superficie, superficie.getVinculacao());
	}
}

class Area {
	int largura;
	int altura;
	int x1;
	int y1;
	int x2;
	int y2;
	int x;
	int y;

	void ini() {
		x = y = largura = altura = 0;
		x1 = y1 = x2 = y2 = 0;
	}

	void desenhar(Graphics2D g2) {
		if (largura > 2 && altura > 2) {
			g2.drawRect(x, y, largura, altura);
		}
	}

	void calc() {
		largura = Math.abs(x1 - x2);
		altura = Math.abs(y1 - y2);
		x = Math.min(x1, x2);
		y = Math.min(y1, y2);
	}

	boolean contem(Objeto objeto) {
		return objeto.visivel && (objeto.x >= this.x && objeto.x + Objeto.DIAMETRO <= this.x + largura)
				&& (objeto.y >= this.y && objeto.y + Objeto.DIAMETRO <= this.y + altura);
	}
}

class Linha {
	int x1;
	int y1;
	int x2;
	int y2;

	void ini() {
		x1 = y1 = x2 = y2 = 0;
	}

	void desenhar(Graphics2D g2) {
		g2.drawLine(x1, y1, x2, y2);
	}
}