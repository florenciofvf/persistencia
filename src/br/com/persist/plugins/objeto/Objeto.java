package br.com.persist.plugins.objeto;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.Icon;

import org.xml.sax.Attributes;

import br.com.persist.assistencia.AssistenciaException;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Imagens;
import br.com.persist.assistencia.Util;
import br.com.persist.marca.XMLUtil;
import br.com.persist.plugins.conexao.Conexao;
import br.com.persist.plugins.metadado.Metadado;
import br.com.persist.plugins.objeto.vinculo.Filtro;
import br.com.persist.plugins.objeto.vinculo.Instrucao;
import br.com.persist.plugins.objeto.vinculo.Pesquisa;
import br.com.persist.plugins.objeto.vinculo.PesquisaUtil;
import br.com.persist.plugins.objeto.vinculo.Referencia;
import br.com.persist.plugins.objeto.vinculo.ReferenciaUtil;
import br.com.persist.plugins.persistencia.OrdenacaoModelo;
import br.com.persist.plugins.persistencia.PersistenciaModelo;

public class Objeto implements Runnable {
	public static final Color COR_PADRAO = new Color(64, 105, 128);
	public static final Color COR_PADRAO_FONTE = Color.BLACK;
	private Pesquisa pesquisaAdicaoHierarquico;
	private final List<Referencia> referencias;
	private Map<String, String> mapaSequencias;
	private final List<Instrucao> instrucoes;
	private boolean clonarAoDestacar = true;
	private final List<Pesquisa> pesquisas;
	private final Set<String> complementos;
	private boolean tituloFormularioComId;
	public static final int DIAMETRO = 36;
	private Referencia referenciaPesquisa;
	private boolean ajusteAutoForm = true;
	private boolean ajustarLargura = true;
	private boolean chaveamentoAlterado;
	private final List<Filtro> filtros;
	private boolean mapeamentoAlterado;
	private boolean sequenciasAlterado;
	private boolean desenharId = true;
	private String esquemaAlternativo;
	Color corFonte = COR_PADRAO_FONTE;
	private boolean compararRegistro;
	private String tabelaAlternativo;
	private boolean abrirAuto = true;
	private int deslocamentoXId = -5;
	private int deslocamentoYId = -5;
	private String selectAlternativo;
	private String prefixoNomeTabela;
	private String apelidoParaJoins;
	private boolean linkAuto = true;
	private boolean transparenteBkp;
	private ObjetoListener listener;
	private boolean larguraRotulos;
	private Color cor = COR_PADRAO;
	private String biblioChecagem;
	private boolean checarLargura;
	private static long sequencia;
	private boolean buscaAutoTemp;
	private String finalConsulta;
	private boolean transparente;
	private boolean selecionado;
	private boolean ccsc = true;
	private boolean sane = true;
	private long totalRegistros;
	private String complemento;
	private String classBiblio;
	private String destacaveis;
	private String chaveamento;
	private boolean colunaInfo;
	protected Objeto associado;
	private String sequencias;
	private String mapeamento;
	private boolean processar;
	private Metadado metadado;
	private String descricao;
	private boolean ignorar;
	private Memento memento;
	protected int larguraId;
	private String orderBy;
	boolean visivel = true;
	private String arquivo;
	private String tabelas;
	private String tabela;
	private Thread thread;
	private int intervalo;
	private String chaves;
	private String joins;
	private String icone;
	private boolean bpnt;
	private String grupo;
	private Color corTmp;
	private Icon icon;
	private String id;
	protected int x;
	protected int y;
	Color corTemp;

	public Objeto() throws AssistenciaException {
		this(0, 0, null, null);
	}

	public Objeto(int x, int y) throws AssistenciaException {
		this(x, y, null, null);
	}

	public Objeto(int x, int y, Color cor) throws AssistenciaException {
		this(x, y, cor, null);
	}

	public Objeto(int x, int y, String icone) throws AssistenciaException {
		this(x, y, COR_PADRAO, icone);
	}

	public Objeto(int x, int y, Color cor, String icone) throws AssistenciaException {
		id = Constantes.VAZIO + (++sequencia);
		referencias = new ArrayList<>();
		complementos = new HashSet<>();
		instrucoes = new ArrayList<>();
		pesquisas = new ArrayList<>();
		filtros = new ArrayList<>();
		setIcone(icone);
		setCor(cor);
		this.x = x;
		this.y = y;
	}

	public Objeto clonar() throws AssistenciaException {
		Objeto o = new Objeto(x, y, cor, icone);
		o.referenciaPesquisa = referenciaPesquisa;
		o.esquemaAlternativo = esquemaAlternativo;
		o.tabelaAlternativo = tabelaAlternativo;
		o.selectAlternativo = selectAlternativo;
		o.clonarAoDestacar = clonarAoDestacar;
		o.apelidoParaJoins = apelidoParaJoins;
		o.deslocamentoXId = deslocamentoXId;
		o.deslocamentoYId = deslocamentoYId;
		o.ajusteAutoForm = ajusteAutoForm;
		o.ajustarLargura = ajustarLargura;
		o.larguraRotulos = larguraRotulos;
		o.biblioChecagem = biblioChecagem;
		o.finalConsulta = finalConsulta;
		o.transparente = transparente;
		o.addInstrucoes(instrucoes);
		o.complemento = complemento;
		o.classBiblio = classBiblio;
		o.destacaveis = destacaveis;
		o.chaveamento = chaveamento;
		o.desenharId = desenharId;
		o.colunaInfo = colunaInfo;
		o.mapeamento = mapeamento;
		o.sequencias = sequencias;
		o.abrirAuto = abrirAuto;
		o.descricao = descricao;
		o.addFiltros(filtros);
		o.corFonte = corFonte;
		o.linkAuto = linkAuto;
		o.tabelas = tabelas;
		o.arquivo = arquivo;
		o.orderBy = orderBy;
		o.ignorar = ignorar;
		o.tabela = tabela;
		o.chaves = chaves;
		o.grupo = grupo;
		o.joins = joins;
		o.ccsc = ccsc;
		o.sane = sane;
		o.bpnt = bpnt;
		o.setId(id);
		return o;
	}

	public void aplicar(Attributes attr) throws AssistenciaException {
		ajusteAutoForm = Boolean.parseBoolean(attr.getValue("ajusteAutoForm"));
		ajustarLargura = Boolean.parseBoolean(attr.getValue("ajustarLargura"));
		clonarAoDestacar = Boolean.parseBoolean(attr.getValue("copiarDestac"));
		larguraRotulos = Boolean.parseBoolean(attr.getValue("larguraRotulos"));
		transparente = Boolean.parseBoolean(attr.getValue("transparente"));
		corFonte = new Color(Integer.parseInt(attr.getValue("corFonte")));
		deslocamentoXId = Integer.parseInt(attr.getValue("desloc_x_id"));
		deslocamentoYId = Integer.parseInt(attr.getValue("desloc_y_id"));
		desenharId = Boolean.parseBoolean(attr.getValue("desenharId"));
		colunaInfo = Boolean.parseBoolean(attr.getValue("colunaInfo"));
		abrirAuto = Boolean.parseBoolean(attr.getValue("abrirAuto"));
		processar = Boolean.parseBoolean(attr.getValue("processar"));
		linkAuto = Boolean.parseBoolean(attr.getValue("linkAuto"));
		esquemaAlternativo = attr.getValue("esquemaAlternativo");
		ignorar = Boolean.parseBoolean(attr.getValue("ignorar"));
		cor = new Color(Integer.parseInt(attr.getValue("cor")));
		selectAlternativo = attr.getValue("selectAlternativo");
		tabelaAlternativo = attr.getValue("tabelaAlternativo");
		apelidoParaJoins = attr.getValue("apelidoParaJoins");
		ccsc = Boolean.parseBoolean(attr.getValue("ccsc"));
		sane = Boolean.parseBoolean(attr.getValue("sane"));
		bpnt = Boolean.parseBoolean(attr.getValue("bpnt"));
		biblioChecagem = attr.getValue("biblioChecagem");
		finalConsulta = attr.getValue("finalConsulta");
		chaveamento = attr.getValue("chaveamento");
		complemento = attr.getValue("complemento");
		classBiblio = attr.getValue("classBiblio");
		destacaveis = attr.getValue("destacaveis");
		x = Integer.parseInt(attr.getValue("x"));
		y = Integer.parseInt(attr.getValue("y"));
		mapeamento = attr.getValue("mapeamento");
		sequencias = attr.getValue("sequencias");
		orderBy = attr.getValue("orderBy");
		arquivo = attr.getValue("arquivo");
		tabelas = attr.getValue("tabelas");
		setIcone(attr.getValue("icone"));
		tabela = attr.getValue("tabela");
		chaves = attr.getValue("chaves");
		grupo = attr.getValue("grupo");
		joins = attr.getValue("joins");
		id = attr.getValue("id");
		String strIntervalo = attr.getValue("intervalo");
		if (!Util.isEmpty(strIntervalo)) {
			intervalo = Integer.parseInt(strIntervalo);
		}
	}

	public void salvar(XMLUtil util) {
		util.abrirTag("objeto");
		util.atributo("id", id);
		util.atributoCheck("transparente", thread == null ? transparente : transparenteBkp);
		util.atributoCheck("finalConsulta", getFinalConsulta());
		util.atributoCheck("chaveamento", getChaveamento());
		util.atributoCheck("complemento", getComplemento());
		util.atributoCheck("classBiblio", getClassBiblio());
		util.atributoCheck("destacaveis", getDestacaveis());
		util.atributoCheck("esquemaAlternativo", getEsquemaAlternativo());
		util.atributoCheck("selectAlternativo", getSelectAlternativo());
		util.atributoCheck("tabelaAlternativo", getTabelaAlternativo());
		util.atributoCheck("apelidoParaJoins", getApelidoParaJoins());
		util.atributoCheck("biblioChecagem", getBiblioChecagem());
		util.atributoCheck("orderBy", getOrderBy());
		util.atributoCheck("ajusteAutoForm", ajusteAutoForm);
		util.atributoCheck("ajustarLargura", ajustarLargura);
		util.atributoCheck("copiarDestac", clonarAoDestacar);
		util.atributoCheck("larguraRotulos", larguraRotulos);
		util.atributo("desloc_x_id", deslocamentoXId);
		util.atributo("desloc_y_id", deslocamentoYId);
		util.atributo("corFonte", corFonte.getRGB());
		util.atributoCheck("mapeamento", getMapeamento());
		util.atributoCheck("sequencias", getSequencias());
		util.atributo("intervalo", getIntervalo());
		util.atributoCheck("desenharId", desenharId);
		util.atributoCheck("colunaInfo", colunaInfo);
		util.atributoCheck("arquivo", getArquivo());
		util.atributoCheck("tabelas", getTabelas());
		util.atributoCheck("abrirAuto", abrirAuto);
		util.atributoCheck("processar", processar);
		util.atributoCheck("tabela", getTabela());
		util.atributoCheck("chaves", getChaves());
		util.atributoCheck("linkAuto", linkAuto);
		util.atributoCheck("grupo", getGrupo());
		util.atributoCheck("ignorar", ignorar);
		util.atributo("cor", cor.getRGB());
		util.atributoCheck("joins", getJoins());
		util.atributoCheck("icone", icone);
		util.atributoCheck("ccsc", ccsc);
		util.atributoCheck("sane", sane);
		util.atributoCheck("bpnt", bpnt);
		util.atributo("x", x);
		util.atributo("y", y);
		util.fecharTag();
		if (!Util.isEmpty(getDescricao())) {
			util.abrirTag2("desc");
			util.conteudo("<![CDATA[").ql();
			util.tab().conteudo(getDescricao()).ql();
			util.conteudo("]]>").ql();
			util.finalizarTag("desc");
		}
		util.finalizarTag("objeto");
	}

	public void desenhar(Component c, Graphics2D g2, Stroke stroke) {
		if (!visivel) {
			return;
		}
		Composite composite = g2.getComposite();
		Shape shape = g2.getClip();
		final int raio = DIAMETRO / 2;
		final int margem2 = 2;
		final int margem3 = 3;
		final int margem4 = 4;
		final int largura = DIAMETRO - margem2;
		final int altura = DIAMETRO - margem2;
		final int largura2 = largura - margem4;
		final int altura2 = altura - margem4;
		final int altura22 = altura2 / 2;
		final int altura3 = altura2 / 3;
		if (!transparente) {
			g2.setColor(Color.DARK_GRAY);
			g2.fillRoundRect(x, y, largura + 1, altura + 1, DIAMETRO, DIAMETRO);
			Color inicio = cor.darker();
			Color finall = cor.brighter();
			Paint paint = new GradientPaint(x, y, inicio, x, (y + altura), finall, false);
			g2.setPaint(paint);
			g2.fillRoundRect(x, y, largura, altura, DIAMETRO, DIAMETRO);
			inicio = Color.WHITE;
			paint = new GradientPaint(x, (y + margem3), inicio, x, (y + margem3 + altura22), finall, false);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
			g2.setPaint(paint);
			g2.setClip(new RoundRectangle2D.Float((x + margem3), (y + margem3), (largura2 - 2), altura22, altura3,
					altura3));
			g2.fillRoundRect(x + margem3, y + margem3, largura2 - 2, altura2, altura2, altura2);
			g2.setComposite(composite);
			g2.setClip(shape);
		}
		if (icon != null) {
			icon.paintIcon(c, g2, x + raio - 8, y + raio - 8);
		}
		if (desenharId) {
			g2.setColor(corFonte);
			g2.drawString(id, x + deslocamentoXId, y + deslocamentoYId);
		}
		if (selecionado) {
			g2.setStroke(Constantes.STROKE_PADRAO);
			g2.setColor(Color.CYAN);
			g2.drawOval(x - margem3, y - margem3, DIAMETRO + margem4, DIAMETRO + margem4);
			g2.setStroke(stroke);
		}
	}

	public static long novaSequencia() {
		return ++sequencia;
	}

	public static long getSequencia() {
		return sequencia;
	}

	public boolean isAjusteAutoForm() {
		return ajusteAutoForm;
	}

	public boolean isAjustarLargura() {
		return ajustarLargura;
	}

	public boolean isTituloFormularioComId() {
		return tituloFormularioComId;
	}

	public void setTituloFormularioComId(boolean tituloFormularioComId) {
		this.tituloFormularioComId = tituloFormularioComId;
	}

	public void setAjusteAutoForm(boolean ajusteAutoForm) {
		this.ajusteAutoForm = ajusteAutoForm;
	}

	public void setAjustarLargura(boolean ajustarLargura) {
		this.ajustarLargura = ajustarLargura;
	}

	public void setTransparente(boolean transparente) {
		this.transparente = transparente;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	public void setFinalConsulta(String finalConsulta) {
		this.finalConsulta = finalConsulta;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
		if (!Util.isEmpty(complemento)) {
			complementos.add(complemento);
		}
	}

	public void setClassBiblio(String classBiblio) {
		this.classBiblio = classBiblio;
	}

	public void setDestacaveis(String destacaveis) {
		this.destacaveis = destacaveis;
	}

	public void setDesenharId(boolean desenharId) {
		this.desenharId = desenharId;
	}

	public void setAbrirAuto(boolean abrirAuto) {
		this.abrirAuto = abrirAuto;
	}

	public boolean isLinkAuto() {
		return linkAuto;
	}

	public void setLinkAuto(boolean linkAuto) {
		this.linkAuto = linkAuto;
	}

	public boolean isLarguraRotulos() {
		return larguraRotulos;
	}

	public void setLarguraRotulos(boolean larguraRotulos) {
		this.larguraRotulos = larguraRotulos;
	}

	public List<Instrucao> getInstrucoes() {
		return instrucoes;
	}

	public List<Filtro> getFiltros() {
		return filtros;
	}

	public boolean isVisivel() {
		return visivel;
	}

	public void setVisivel(boolean visivel) {
		this.visivel = visivel;
	}

	public void addInstrucao(Instrucao i) {
		if (i != null) {
			instrucoes.add(i);
		}
	}

	public void addFiltro(Filtro f) {
		if (f != null) {
			filtros.add(f);
		}
	}

	public void addInstrucoes(List<Instrucao> lista) {
		if (lista != null) {
			for (Instrucao i : lista) {
				addInstrucao(i);
			}
		}
	}

	public void addFiltros(List<Filtro> lista) {
		if (lista != null) {
			for (Filtro f : lista) {
				addFiltro(f);
			}
		}
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public void setTabela(String tabela) {
		this.tabela = tabela;
	}

	public void setSelectAlternativo(String selectAlternativo) {
		this.selectAlternativo = selectAlternativo;
	}

	public void setTabelas(String tabelas) {
		this.tabelas = tabelas;
	}

	public void setEsquemaAlternativo(String esquemaAlternativo) {
		this.esquemaAlternativo = esquemaAlternativo;
	}

	public void setTabelaAlternativo(String tabelaAlternativo) {
		this.tabelaAlternativo = tabelaAlternativo;
	}

	public void setJoins(String joins) {
		this.joins = joins;
	}

	public void setChaves(String chaves) {
		this.chaves = chaves;
	}

	public void setIcone(String icone) throws AssistenciaException {
		this.icone = icone;
		if (Util.isEmpty(this.icone)) {
			this.icone = Constantes.VAZIO;
		} else {
			icon = Imagens.getIcon(this.icone);
		}
	}

	public void limparIcone() {
		this.icone = null;
		this.icon = null;
	}

	public String getApelidoParaJoins() {
		if (Util.isEmpty(apelidoParaJoins)) {
			apelidoParaJoins = Constantes.VAZIO;
		}
		return apelidoParaJoins;
	}

	public void setApelidoParaJoins(String apelidoParaJoins) {
		this.apelidoParaJoins = apelidoParaJoins;
	}

	public String getFinalConsulta() {
		if (Util.isEmpty(finalConsulta)) {
			finalConsulta = Constantes.VAZIO;
		}
		return finalConsulta;
	}

	public String getOrderBy() {
		if (Util.isEmpty(orderBy)) {
			orderBy = Constantes.VAZIO;
		}
		return orderBy;
	}

	public boolean isTransparente() {
		return transparente;
	}

	public String getComplemento() {
		if (Util.isEmpty(complemento)) {
			complemento = Constantes.VAZIO;
		}
		return complemento;
	}

	public String getClassBiblio() {
		if (Util.isEmpty(classBiblio)) {
			classBiblio = Constantes.VAZIO;
		}
		return classBiblio;
	}

	public String getDestacaveis() {
		if (Util.isEmpty(destacaveis)) {
			destacaveis = Constantes.VAZIO;
		}
		return destacaveis;
	}

	public String getChaves() {
		if (Util.isEmpty(chaves)) {
			chaves = Constantes.VAZIO;
		}
		return chaves;
	}

	public String getMapeamento() {
		if (Util.isEmpty(mapeamento)) {
			mapeamento = Constantes.VAZIO;
		}
		return mapeamento;
	}

	public void setSequencias(String sequencias) {
		this.sequencias = sequencias;
		sequenciasAlterado = true;
	}

	public String getSequencias() {
		if (Util.isEmpty(sequencias)) {
			sequencias = Constantes.VAZIO;
		}
		return sequencias;
	}

	public String[] getChavesArray() {
		String chavesTmp = getChaves();
		if (Util.isEmpty(chavesTmp)) {
			return Constantes.ARRAY_LENGTH_ZERO;
		}
		return chavesTmp.trim().split(",");
	}

	public String getTabelaEsquema(Conexao conexao) {
		if (PersistenciaModelo.usarTabelaAlternativa(conexao, getEsquemaAlternativo(), getTabelaAlternativo())) {
			return PersistenciaModelo.prefixarEsquema(conexao, getPrefixoNomeTabela(), getTabelaAlternativo(),
					getApelidoParaJoins());
		}
		return PersistenciaModelo.prefixarEsquema(conexao, getPrefixoNomeTabela(), getTabela(), getApelidoParaJoins());
	}

	public String getTabelaEsquema2(Conexao conexao) {
		return PersistenciaModelo.prefixarEsquema(conexao, null, getTabela(), null);
	}

	public void select(StringBuilder sb, Conexao conexao) {
		String sel = getSelectAlternativo();
		if (Util.isEmpty(sel)) {
			if (!Util.isEmpty(apelidoParaJoins)) {
				sb.append("SELECT " + apelidoParaJoins + ".* FROM " + getTabelaEsquema(conexao));
			} else {
				sb.append("SELECT * FROM " + getTabelaEsquema(conexao));
			}
		} else {
			sb.append(sel + " FROM " + getTabelaEsquema(conexao));
		}
	}

	public String comApelido(String prefixo, String campo) {
		if (Util.isEmpty(apelidoParaJoins) || Util.isEmpty(campo)) {
			return prefixo + " " + campo;
		}
		return prefixo + " " + apelidoParaJoins + "." + campo;
	}

	public String comApelido(String campo) {
		if (Util.isEmpty(apelidoParaJoins) || Util.isEmpty(campo)) {
			return campo;
		}
		return apelidoParaJoins + "." + campo;
	}

	public String semApelido(String string) {
		if (Util.isEmpty(apelidoParaJoins)) {
			return string;
		}
		return Util.replaceAll(string, apelidoParaJoins + ".", "");
	}

	public void joins(StringBuilder sb, Conexao conexao, String prefixoNomeTabela) {
		String tabs = getTabelas();
		String jois = getJoins();
		if (Util.isEmpty(tabs) || Util.isEmpty(jois)) {
			return;
		}
		String[] tabsArray = tabs.split(",");
		String[] joisArray = jois.split(",");
		if (tabsArray.length == joisArray.length) {
			for (int i = 0; i < tabsArray.length; i++) {
				String tab = tabsArray[i];
				String on = joisArray[i];
				sb.append(" INNER JOIN");
				sb.append(" " + PersistenciaModelo.prefixarEsquema(conexao, prefixoNomeTabela, tab, null));
				sb.append(" " + on);
				sb.append(Constantes.QL);
			}
		}
	}

	public void orderBy(StringBuilder sb) {
		if (!Util.isEmpty(orderBy)) {
			sb.append(" ORDER BY ");
			if (Util.isEmpty(apelidoParaJoins)) {
				sb.append(orderBy);
			} else {
				String apelido = apelidoParaJoins + ".";
				String order = orderBy.trim();
				if (order.startsWith(apelido)) {
					sb.append(orderBy);
				} else {
					sb.append(apelido + order);
				}
			}
		}
	}

	public void where(StringBuilder sb, String... strings) {
		if (arrayValido(strings)) {
			sb.append(" WHERE");
			int i = append(sb, strings);
			for (; i < strings.length; i++) {
				concatenar(sb, strings[i]);
			}
		}
	}

	private boolean arrayValido(String... strings) {
		if (strings != null) {
			for (String string : strings) {
				if (!Util.isEmpty(string)) {
					return true;
				}
			}
		}
		return false;
	}

	private int append(StringBuilder sb, String... strings) {
		int i = 0;
		for (; i < strings.length; i++) {
			String s = strings[i];
			if (!Util.isEmpty(s)) {
				s = s.trim();
				insert(sb, s);
				return ++i;
			}
		}
		return i;
	}

	private void insert(StringBuilder sb, String s) {
		String t = s.toUpperCase();
		if (t.startsWith("AND")) {
			s = s.substring(3);
		} else if (t.startsWith("OR")) {
			s = s.substring(2);
		}
		sb.append(" " + s.trim());
	}

	public static void concatenar(StringBuilder builder, String string) {
		if (!Util.isEmpty(string)) {
			builder.append(" " + string.trim());
		}
	}

	public String getNomeSequencia(String nomeColuna) {
		String resp = null;
		if (nomeColuna != null) {
			resp = getMapaSequencias().get(nomeColuna.trim().toLowerCase());
		}
		return resp;
	}

	public String getTabela() {
		if (Util.isEmpty(tabela)) {
			tabela = Constantes.VAZIO;
		}
		return tabela;
	}

	public String getApelidoParaJoinOuTabela() {
		if (!Util.isEmpty(apelidoParaJoins)) {
			return apelidoParaJoins;
		}
		if (!Util.isEmpty(tabela)) {
			return tabela;
		}
		return Constantes.VAZIO;
	}

	public String getGrupo() {
		if (Util.isEmpty(grupo)) {
			grupo = Constantes.VAZIO;
		}
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public String getSelectAlternativo() {
		if (Util.isEmpty(selectAlternativo)) {
			selectAlternativo = Constantes.VAZIO;
		}
		return selectAlternativo;
	}

	public String getTabelas() {
		if (Util.isEmpty(tabelas)) {
			tabelas = Constantes.VAZIO;
		}
		return tabelas;
	}

	public String getTabelaAlternativo() {
		if (Util.isEmpty(tabelaAlternativo)) {
			tabelaAlternativo = Constantes.VAZIO;
		}
		return tabelaAlternativo;
	}

	public String getEsquemaAlternativo() {
		if (Util.isEmpty(esquemaAlternativo)) {
			esquemaAlternativo = Constantes.VAZIO;
		}
		return esquemaAlternativo;
	}

	public String getJoins() {
		if (Util.isEmpty(joins)) {
			joins = Constantes.VAZIO;
		}
		return joins;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public boolean isDesenharId() {
		return desenharId;
	}

	public boolean isColunaInfo() {
		return colunaInfo;
	}

	public void setColunaInfo(boolean colunaInfo) {
		this.colunaInfo = colunaInfo;
	}

	public boolean isClonarAoDestacar() {
		return clonarAoDestacar;
	}

	public void setClonarAoDestacar(boolean clonarAoDestacar) {
		this.clonarAoDestacar = clonarAoDestacar;
	}

	public boolean isAbrirAuto() {
		return abrirAuto;
	}

	public void setId(String id) {
		if (!Util.isEmpty(id)) {
			this.id = id;
		}
	}

	public String getDescricao() {
		if (descricao == null) {
			descricao = Constantes.VAZIO;
		}
		return descricao;
	}

	public String getIcone() {
		return icone;
	}

	public Icon getIcon() {
		return icon;
	}

	public Color getCorFonte() {
		return corFonte;
	}

	public Color getCor() {
		return cor;
	}

	public String getId() {
		return id;
	}

	public void setCorFonte(Color corFonte) {
		this.corFonte = corFonte;
		if (this.corFonte == null) {
			this.corFonte = COR_PADRAO_FONTE;
		}
	}

	public void setCor(Color cor) {
		this.cor = cor;
		if (this.cor == null) {
			this.cor = COR_PADRAO;
		}
	}

	public String getBiblioChecagem() {
		if (Util.isEmpty(biblioChecagem)) {
			biblioChecagem = Constantes.VAZIO;
		}
		return biblioChecagem;
	}

	public void setBiblioChecagem(String biblioChecagem) {
		this.biblioChecagem = biblioChecagem;
	}

	public String getChaveamento() {
		if (Util.isEmpty(chaveamento)) {
			chaveamento = Constantes.VAZIO;
		}
		return chaveamento;
	}

	public void setChaveamento(String chaveamento) {
		this.chaveamento = chaveamento;
		chaveamentoAlterado = true;
	}

	public boolean contem(int x, int y) {
		return visivel && (x >= this.x && x <= this.x + DIAMETRO) && (y >= this.y && y <= this.y + DIAMETRO);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof Objeto) {
			Objeto outro = (Objeto) obj;
			return id.equals(outro.id);
		}
		return false;
	}

	public boolean equalsId(Objeto outro) {
		return id.equals(outro.id);
	}

	public boolean igual(Objeto objeto) {
		return objeto != null && getGrupo().equalsIgnoreCase(objeto.getGrupo())
				&& getTabela().equalsIgnoreCase(objeto.getTabela());
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public String toString() {
		return id;
	}

	public void zoomMenos() {
		x -= x * 0.10;
		y -= y * 0.10;
	}

	public void zoomMais() {
		x += x * 0.10;
		y += y * 0.10;
	}

	public int getIntervalo() {
		if (intervalo < 500) {
			intervalo = 500;
		}
		return intervalo;
	}

	public void setIntervalo(int intervalo) {
		this.intervalo = intervalo;
	}

	public ObjetoListener getListener() {
		return listener;
	}

	public void setListener(ObjetoListener listener) {
		this.listener = listener;
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			transparente = !transparente;
			if (listener != null) {
				listener.repaint(x, y, DIAMETRO, DIAMETRO);
			}
			try {
				Thread.sleep(getIntervalo());
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	public void ativar() {
		if (processar && thread == null) {
			transparenteBkp = transparente;
			thread = new Thread(this);
			thread.start();
		}
	}

	public void desativar() {
		if (thread != null) {
			transparente = transparenteBkp;
			thread.interrupt();
			processar = false;
			thread = null;
		}
	}

	public boolean isProcessar() {
		return processar;
	}

	public void setProcessar(boolean processar) {
		this.processar = processar;
	}

	public String getTitle(String complemento) {
		return getId() + " - " + getTabela() + " - " + complemento + " ";
	}

	public String getTitle(OrdenacaoModelo modelo) {
		String totalColunas = "";
		if (ObjetoPreferencia.isExibirTotalColunasTabela()) {
			totalColunas = " - {" + modelo.getTotalColunas() + "}";
		}
		if (tituloFormularioComId) {
			return getId() + " - " + getTabela() + " [" + modelo.getRowCount() + "]" + totalColunas;
		}
		return getTabela() + " [" + modelo.getRowCount() + "]" + totalColunas;
	}

	public int getDeslocamentoXId() {
		return deslocamentoXId;
	}

	public void setDeslocamentoXId(int deslocamentoXId) {
		this.deslocamentoXId = deslocamentoXId;
	}

	public void deslocamentoXIdDelta(int delta) {
		this.deslocamentoXId += delta;
	}

	public int getDeslocamentoYId() {
		return deslocamentoYId;
	}

	public void setDeslocamentoYId(int deslocamentoYId) {
		this.deslocamentoYId = deslocamentoYId;
	}

	public void deslocamentoYIdDelta(int delta) {
		this.deslocamentoYId += delta;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void deltaX(int i) {
		x += i;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void deltaY(int i) {
		y += i;
	}

	public boolean isCcsc() {
		return ccsc;
	}

	public void setCcsc(boolean ccsc) {
		this.ccsc = ccsc;
	}

	public boolean isSane() {
		return sane;
	}

	public void setSane(boolean sane) {
		this.sane = sane;
	}

	public boolean isIgnorar() {
		return ignorar;
	}

	public void setIgnorar(boolean ignorar) {
		this.ignorar = ignorar;
	}

	public void setMapeamento(String mapeamento) {
		this.mapeamento = mapeamento;
		mapeamentoAlterado = true;
	}

	public Map<String, String> getMapaSequencias() {
		if (mapaSequencias == null) {
			mapaSequencias = new HashMap<>();
		}
		return mapaSequencias;
	}

	public void setMapaSequencias(Map<String, String> mapaSequencias) {
		this.mapaSequencias = mapaSequencias;
	}

	public String getPrefixoNomeTabela() {
		if (Util.isEmpty(prefixoNomeTabela)) {
			prefixoNomeTabela = Constantes.VAZIO;
		}
		return prefixoNomeTabela;
	}

	public void setPrefixoNomeTabela(String prefixoNomeTabela) {
		if (bpnt) {
			return;
		}
		this.prefixoNomeTabela = prefixoNomeTabela;
	}

	public boolean isBpnt() {
		return bpnt;
	}

	public void setBpnt(boolean bpnt) {
		this.bpnt = bpnt;
	}

	public Set<String> getComplementos() {
		return complementos;
	}

	public String getArquivo() {
		if (Util.isEmpty(arquivo)) {
			arquivo = Constantes.VAZIO;
		}
		return arquivo;
	}

	public void setArquivo(String arquivo) {
		this.arquivo = arquivo;
	}

	public void ordenarInstrucoes() {
		Collections.sort(instrucoes);
	}

	public void ordenarFiltros() {
		Collections.sort(filtros);
	}

	public List<Referencia> getReferencias() {
		return referencias;
	}

	public void addReferencia(Referencia ref) throws ObjetoException {
		if (ref != null && !ReferenciaUtil.contem(ref, referencias)) {
			referencias.add(ref);
		}
	}

	public String getInconsistencias() {
		Map<String, List<Referencia>> map = new HashMap<>();
		for (int i = 0; i < referencias.size(); i++) {
			Referencia ref = referencias.get(i);
			String chave = criarChave(ref);
			List<Referencia> refs = map.computeIfAbsent(chave, t -> new ArrayList<>());
			refs.add(ref);
		}

		StringBuilder sb = new StringBuilder();

		for (Map.Entry<String, List<Referencia>> entry : map.entrySet()) {
			List<Referencia> refs = entry.getValue();
			if (refs.size() > 1) {
				if (sb.length() > 0) {
					sb.append(Constantes.QL2);
				}
				sb.append(toString(refs));
			}
		}

		return sb.toString();
	}

	private String toString(List<Referencia> refs) {
		StringBuilder sb = new StringBuilder();
		for (Referencia ref : refs) {
			if (sb.length() > 0) {
				sb.append(Constantes.QL);
			}
			sb.append(toString(ref));
		}
		return sb.toString();
	}

	private String toString(Referencia ref) {
		if (ref.getPesquisa() != null) {
			Pesquisa p = ref.getPesquisa();
			return "pesquisa=" + p.getNome() + ", " + ref.toString();
		}
		return ref.toString();
	}

	private String criarChave(Referencia ref) {
		return "[" + ref.getGrupo() + "][" + ref.getTabela() + "]";
	}

	public void addReferencias(List<Referencia> referencias) throws ObjetoException {
		for (Referencia ref : referencias) {
			addReferencia(ref);
		}
	}

	public boolean addPesquisa(Pesquisa pesq) throws ObjetoException {
		if (pesq != null && !PesquisaUtil.contem(pesq, pesquisas)) {
			pesquisas.add(pesq);
			return true;
		}
		return false;
	}

	public List<Pesquisa> getPesquisas(boolean ordenar) {
		if (ordenar) {
			Collections.sort(pesquisas, (Pesquisa o1, Pesquisa o2) -> o1.getOrdem() - o2.getOrdem());
		}
		return pesquisas;
	}

	public boolean excluir(Pesquisa pesquisa) {
		return pesquisas.remove(pesquisa);
	}

	public Referencia getReferenciaPesquisa() {
		return referenciaPesquisa;
	}

	public void setReferenciaPesquisa(Referencia referenciaPesquisa) {
		this.referenciaPesquisa = referenciaPesquisa;
	}

	public boolean isChecarLargura() {
		return checarLargura;
	}

	public void setChecarLargura(boolean checarLargura) {
		this.checarLargura = checarLargura;
	}

	public Color getCorTmp() {
		return corTmp;
	}

	public void setCorTmp(Color corTmp) {
		this.corTmp = corTmp;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public void inverterPosicao(Objeto outro) {
		int xBkp = x;
		int yBkp = y;
		x = outro.x;
		y = outro.y;
		outro.x = xBkp;
		outro.y = yBkp;
	}

	public long getTotalRegistros() {
		return totalRegistros;
	}

	public void setTotalRegistros(long totalRegistros) {
		this.totalRegistros = totalRegistros;
		if (listener != null) {
			listener.labelTotalRegistros(this);
		}
	}

	public Pesquisa getPesquisaAdicaoHierarquico() {
		return pesquisaAdicaoHierarquico;
	}

	public void setPesquisaAdicaoHierarquico(Pesquisa pesquisa) {
		this.pesquisaAdicaoHierarquico = pesquisa;
	}

	public Pesquisa getPesquisa(Pesquisa pesquisa) {
		for (Pesquisa p : pesquisas) {
			if (p.igual(pesquisa)) {
				return p;
			}
		}
		return null;
	}

	public boolean isBuscaAutoTemp() {
		return buscaAutoTemp;
	}

	public void setBuscaAutoTemp(boolean buscaAutoTemp) {
		this.buscaAutoTemp = buscaAutoTemp;
	}

	public boolean isSequenciasAlterado() {
		return sequenciasAlterado;
	}

	public void setSequenciasAlterado(boolean sequenciasAlterado) {
		this.sequenciasAlterado = sequenciasAlterado;
	}

	public boolean isChaveamentoAlterado() {
		return chaveamentoAlterado;
	}

	public void setChaveamentoAlterado(boolean chaveamentoAlterado) {
		this.chaveamentoAlterado = chaveamentoAlterado;
	}

	public boolean isMapeamentoAlterado() {
		return mapeamentoAlterado;
	}

	public void setMapeamentoAlterado(boolean mapeamentoAlterado) {
		this.mapeamentoAlterado = mapeamentoAlterado;
	}

	public Metadado getMetadado() {
		return metadado;
	}

	public void setMetadado(Metadado metadado) {
		this.metadado = metadado;
	}

	public void setLarguraId(int larguraId) {
		this.larguraId = larguraId;
	}

	public void configLocalAssociado() {
		if (associado != null) {
			associado.x = x + larguraId + Objeto.DIAMETRO;
			associado.y = y;
		}
	}

	public void restaurarMemento() {
		if (memento != null) {
			memento.restaurar(this);
		}
	}

	public void criarMemento() {
		memento = new Memento(this);
	}

	public Color getCorTemp() {
		return corTemp;
	}

	public void setCorTemp(Color corTemp) {
		this.corTemp = corTemp;
	}

	public boolean isCompararRegistro() {
		return compararRegistro;
	}

	public void setCompararRegistro(boolean compararRegistro) {
		this.compararRegistro = compararRegistro;
	}
}

class Memento {
	private final Color corFonte;

	Memento(Objeto objeto) {
		corFonte = objeto.corFonte;
	}

	void restaurar(Objeto objeto) {
		objeto.corFonte = corFonte;
	}
}