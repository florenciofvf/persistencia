package br.com.persist.objeto;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.FontMetrics;
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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.xml.sax.Attributes;

import br.com.persist.busca_auto.TabelaBuscaAuto;
import br.com.persist.conexao.Conexao;
import br.com.persist.instrucao.Instrucao;
import br.com.persist.superficie.Superficie;
import br.com.persist.tabela.OrdenacaoModelo;
import br.com.persist.util.Constantes;
import br.com.persist.util.Imagens;
import br.com.persist.util.Util;
import br.com.persist.xml.XMLUtil;

public class Objeto implements Runnable {
	public static final Color COR_PADRAO = new Color(64, 105, 128);
	public static final Color COR_PADRAO_FONTE = Color.BLACK;
	private static final Logger LOG = Logger.getGlobal();
	private Map<String, String> mapaSequencias;
	private Color corFonte = COR_PADRAO_FONTE;
	private final List<Instrucao> instrucoes;
	private TabelaBuscaAuto tabelaBuscaAuto;
	private final Set<String> complementos;
	public static final int DIAMETRO = 36;
	private String buscaAutomaticaApos;
	private int deslocamentoXId = -5;
	private int deslocamentoYId = -5;
	private String prefixoNomeTabela;
	private boolean ajusteAutoEnter;
	private boolean copiarDestacado;
	private boolean transparenteBkp;
	private boolean ajusteAutoForm;
	private Color cor = COR_PADRAO;
	private String buscaAutomatica;
	private String linkAutomatico;
	private static long sequencia;
	private Superficie superficie;
	private String finalConsulta;
	private boolean transparente;
	private boolean selecionado;
	private boolean ccsc = true;
	private boolean controlado;
	private boolean desenharId;
	private String complemento;
	private String chaveamento;
	private boolean colunaInfo;
	private String selectAlter;
	private String sequencias;
	private boolean abrirAuto;
	private String mapeamento;
	private boolean processar;
	private boolean linkAuto;
	private String descricao;
	private String arquivo;
	private String tabelas;
	private String tabela;
	private Thread thread;
	private int intervalo;
	private String chaves;
	private String joins;
	private String icone;
	private boolean bpnt;
	private Icon icon;
	private String id;
	private long tag;
	public int x;
	public int y;

	public Objeto() {
		this(0, 0, null, null);
	}

	public Objeto(int x, int y) {
		this(x, y, null, null);
	}

	public Objeto(int x, int y, Color cor) {
		this(x, y, cor, null);
	}

	public Objeto(int x, int y, String icone) {
		this(x, y, COR_PADRAO, icone);
	}

	public Objeto(int x, int y, Color cor, String icone) {
		id = Constantes.VAZIO + (++sequencia);
		complementos = new HashSet<>();
		instrucoes = new ArrayList<>();
		desenharId = true;
		setIcone(icone);
		setCor(cor);
		this.x = x;
		this.y = y;
	}

	public Objeto clonar() {
		Objeto o = new Objeto(x, y, cor, icone);

		o.buscaAutomaticaApos = buscaAutomaticaApos;
		o.tabelaBuscaAuto = tabelaBuscaAuto;
		o.buscaAutomatica = buscaAutomatica;
		o.deslocamentoXId = deslocamentoXId;
		o.deslocamentoYId = deslocamentoYId;
		o.copiarDestacado = copiarDestacado;
		o.ajusteAutoEnter = ajusteAutoEnter;
		o.linkAutomatico = linkAutomatico;
		o.ajusteAutoForm = ajusteAutoForm;
		o.finalConsulta = finalConsulta;
		o.transparente = transparente;
		o.complemento = complemento;
		o.chaveamento = chaveamento;
		o.selectAlter = selectAlter;
		o.desenharId = desenharId;
		o.colunaInfo = colunaInfo;
		o.mapeamento = mapeamento;
		o.sequencias = sequencias;
		o.abrirAuto = abrirAuto;
		o.descricao = descricao;
		o.corFonte = corFonte;
		o.linkAuto = linkAuto;
		o.tabelas = tabelas;
		o.arquivo = arquivo;
		o.tabela = tabela;
		o.chaves = chaves;
		o.joins = joins;
		o.ccsc = ccsc;
		o.bpnt = bpnt;
		o.setId(id);

		for (Instrucao i : instrucoes) {
			o.addInstrucao(i.clonar());
		}

		return o;
	}

	public void aplicar(Attributes attr) {
		ajusteAutoEnter = Boolean.parseBoolean(attr.getValue("ajusteAutoEnter"));
		ajusteAutoForm = Boolean.parseBoolean(attr.getValue("ajusteAutoForm"));
		copiarDestacado = Boolean.parseBoolean(attr.getValue("copiarDestac"));
		transparente = Boolean.parseBoolean(attr.getValue("transparente"));
		corFonte = new Color(Integer.parseInt(attr.getValue("corFonte")));
		deslocamentoXId = Integer.parseInt(attr.getValue("desloc_x_id"));
		deslocamentoYId = Integer.parseInt(attr.getValue("desloc_y_id"));
		desenharId = Boolean.parseBoolean(attr.getValue("desenharId"));
		colunaInfo = Boolean.parseBoolean(attr.getValue("colunaInfo"));
		abrirAuto = Boolean.parseBoolean(attr.getValue("abrirAuto"));
		processar = Boolean.parseBoolean(attr.getValue("processar"));
		buscaAutomaticaApos = attr.getValue("buscaAutomaticaApos");
		linkAuto = Boolean.parseBoolean(attr.getValue("linkAuto"));
		cor = new Color(Integer.parseInt(attr.getValue("cor")));
		ccsc = Boolean.parseBoolean(attr.getValue("ccsc"));
		bpnt = Boolean.parseBoolean(attr.getValue("bpnt"));
		buscaAutomatica = attr.getValue("buscaAutomatica");
		linkAutomatico = attr.getValue("linkAutomatico");
		finalConsulta = attr.getValue("finalConsulta");
		chaveamento = attr.getValue("chaveamento");
		complemento = attr.getValue("complemento");
		selectAlter = attr.getValue("selectAlter");
		x = Integer.parseInt(attr.getValue("x"));
		y = Integer.parseInt(attr.getValue("y"));
		mapeamento = attr.getValue("mapeamento");
		sequencias = attr.getValue("sequencias");
		arquivo = attr.getValue("arquivo");
		tabelas = attr.getValue("tabelas");
		setIcone(attr.getValue("icone"));
		tabela = attr.getValue("tabela");
		chaves = attr.getValue("chaves");
		joins = attr.getValue("joins");
		id = attr.getValue("id");

		String strIntervalo = attr.getValue("intervalo");

		if (!Util.estaVazio(strIntervalo)) {
			intervalo = Integer.parseInt(strIntervalo);
		}
	}

	public void salvar(XMLUtil util) {
		util.abrirTag("objeto");
		util.atributo("id", Util.escapar(id));
		util.atributo("transparente", thread == null ? transparente : transparenteBkp);
		util.atributo("buscaAutomaticaApos", Util.escapar(getBuscaAutomaticaApos()));
		util.atributo("buscaAutomatica", Util.escapar(getBuscaAutomatica()));
		util.atributo("linkAutomatico", Util.escapar(getLinkAutomatico()));
		util.atributo("finalConsulta", Util.escapar(getFinalConsulta()));
		util.atributo("chaveamento", Util.escapar(getChaveamento()));
		util.atributo("complemento", Util.escapar(getComplemento()));
		util.atributo("ajusteAutoEnter", ajusteAutoEnter);
		util.atributo("ajusteAutoForm", ajusteAutoForm);
		util.atributo("copiarDestac", copiarDestacado);
		util.atributo("selectAlter", getSelectAlter());
		util.atributo("desloc_x_id", deslocamentoXId);
		util.atributo("desloc_y_id", deslocamentoYId);
		util.atributo("corFonte", corFonte.getRGB());
		util.atributo("mapeamento", getMapeamento());
		util.atributo("sequencias", getSequencias());
		util.atributo("intervalo", getIntervalo());
		util.atributo("desenharId", desenharId);
		util.atributo("colunaInfo", colunaInfo);
		util.atributo("arquivo", getArquivo());
		util.atributo("tabelas", getTabelas());
		util.atributo("abrirAuto", abrirAuto);
		util.atributo("processar", processar);
		util.atributo("tabela", getTabela2());
		util.atributo("chaves", getChaves());
		util.atributo("linkAuto", linkAuto);
		util.atributo("cor", cor.getRGB());
		util.atributo("joins", getJoins());
		util.atributo("icone", icone);
		util.atributo("ccsc", ccsc);
		util.atributo("bpnt", bpnt);
		util.atributo("x", x);
		util.atributo("y", y);
		util.fecharTag();

		if (!Util.estaVazio(getDescricao())) {
			util.abrirTag2("desc");
			util.conteudo(Util.escapar(getDescricao())).ql();
			util.finalizarTag("desc");
		}

		for (Instrucao i : instrucoes) {
			i.salvar(util);
		}

		util.finalizarTag("objeto");
	}

	public void desenhar(Component c, Graphics2D g2, Stroke stroke) {
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
			Paint paint = new GradientPaint(x, y, inicio, x, (float) (y + altura), finall, false);
			g2.setPaint(paint);
			g2.fillRoundRect(x, y, largura, altura, DIAMETRO, DIAMETRO);

			inicio = Color.WHITE;
			paint = new GradientPaint(x, (float) (y + margem3), inicio, x, (float) (y + margem3 + altura22), finall,
					false);

			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
			g2.setPaint(paint);

			g2.setClip(new RoundRectangle2D.Float((float) (x + margem3), (float) (y + margem3), (float) (largura2 - 2),
					altura22, altura3, altura3));
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

	public void setAjusteAutoForm(boolean ajusteAutoForm) {
		this.ajusteAutoForm = ajusteAutoForm;
	}

	public boolean isAjusteAutoEnter() {
		return ajusteAutoEnter;
	}

	public void setAjusteAutoEnter(boolean ajusteAutoEnter) {
		this.ajusteAutoEnter = ajusteAutoEnter;
	}

	public void setTransparente(boolean transparente) {
		this.transparente = transparente;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;

		if (!this.selecionado) {
			controlado = false;
		}
	}

	public void setFinalConsulta(String finalConsulta) {
		this.finalConsulta = finalConsulta;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;

		if (!Util.estaVazio(complemento)) {
			complementos.add(complemento);
		}
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

	public List<Instrucao> getInstrucoes() {
		return instrucoes;
	}

	public void addInstrucao(Instrucao i) {
		if (i != null) {
			instrucoes.add(i);
		}
	}

	public Instrucao getUltInstrucao() {
		if (instrucoes.isEmpty()) {
			return null;
		}

		return instrucoes.get(instrucoes.size() - 1);
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public void setTabela(String tabela) {
		this.tabela = tabela;
	}

	public void setSelectAlter(String selectAlter) {
		this.selectAlter = selectAlter;
	}

	public void setTabelas(String tabelas) {
		this.tabelas = tabelas;
	}

	public void setJoins(String joins) {
		this.joins = joins;
	}

	public void setChaves(String chaves) {
		this.chaves = chaves;
	}

	public void setIcone(String icone) {
		this.icone = icone;

		if (Util.estaVazio(this.icone)) {
			this.icone = Constantes.VAZIO;
		} else {
			icon = Imagens.getIcon(this.icone);
		}
	}

	public void limparIcone() {
		this.icone = null;
		this.icon = null;
	}

	public String getFinalConsulta() {
		if (Util.estaVazio(finalConsulta)) {
			finalConsulta = Constantes.VAZIO;
		}

		return finalConsulta;
	}

	public boolean isTransparente() {
		return transparente;
	}

	public String getComplemento() {
		if (Util.estaVazio(complemento)) {
			complemento = Constantes.VAZIO;
		}

		return complemento;
	}

	public String getChaves() {
		if (Util.estaVazio(chaves)) {
			chaves = Constantes.VAZIO;
		}

		return chaves;
	}

	public String getMapeamento() {
		if (Util.estaVazio(mapeamento)) {
			mapeamento = Constantes.VAZIO;
		}

		return mapeamento;
	}

	public void setSequencias(String sequencias) {
		this.sequencias = sequencias;
	}

	public String getSequencias() {
		if (Util.estaVazio(sequencias)) {
			sequencias = Constantes.VAZIO;
		}

		return sequencias;
	}

	public String[] getChavesArray() {
		String chavesTmp = getChaves();

		if (Util.estaVazio(chavesTmp)) {
			return Constantes.ARRAY_LENGTH_ZERO;
		}

		return chavesTmp.trim().split(",");
	}

	public String getTabelaEsquema(String esquema) {
		if (Util.estaVazio(tabela)) {
			tabela = Constantes.VAZIO;
		}

		return (Util.estaVazio(esquema) ? Constantes.VAZIO : esquema + ".") + getPrefixoNomeTabela() + tabela;
	}

	public void select(StringBuilder sb, Conexao conexao) {
		String sel = getSelectAlter();

		if (Util.estaVazio(sel)) {
			sb.append("SELECT * FROM " + getTabelaEsquema(conexao.getEsquema()));
		} else {
			sb.append(sel + " FROM " + getTabelaEsquema(conexao.getEsquema()));
		}
	}

	public void joins(StringBuilder sb, Conexao conexao, String prefixoNomeTabela) {
		String tabs = getTabelas();
		String jois = getJoins();

		if (Util.estaVazio(tabs) || Util.estaVazio(jois)) {
			return;
		}

		String[] tabsArray = tabs.split(",");
		String[] joisArray = jois.split(",");

		if (tabsArray.length != joisArray.length) {
			return;
		}

		for (int i = 0; i < tabsArray.length; i++) {
			String tab = tabsArray[i];
			String on = joisArray[i];

			sb.append(" INNER JOIN");
			sb.append(" " + prefixarEsquema(conexao, prefixoNomeTabela, tab));
			sb.append(" " + on);
			sb.append(Constantes.QL);
		}
	}

	public void where(StringBuilder sb) {
		sb.append(" WHERE 1=1");
	}

	public static String prefixarEsquema(Conexao conexao, String prefixoNomeTabela, String string) {
		String esquema = conexao == null ? Constantes.VAZIO : conexao.getEsquema();

		return (Util.estaVazio(esquema) ? Constantes.VAZIO : esquema + ".") + prefixoNomeTabela + string;
	}

	public String getNomeSequencia(String nomeColuna) {
		String resp = null;

		if (nomeColuna != null) {
			resp = getMapaSequencias().get(nomeColuna.trim().toLowerCase());
		}

		return resp;
	}

	public String getTabela2() {
		if (Util.estaVazio(tabela)) {
			tabela = Constantes.VAZIO;
		}

		return tabela;
	}

	public String getSelectAlter() {
		if (Util.estaVazio(selectAlter)) {
			selectAlter = Constantes.VAZIO;
		}

		return selectAlter;
	}

	public String getTabelas() {
		if (Util.estaVazio(tabelas)) {
			tabelas = Constantes.VAZIO;
		}

		return tabelas;
	}

	public String getJoins() {
		if (Util.estaVazio(joins)) {
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

	public boolean isCopiarDestacado() {
		return copiarDestacado;
	}

	public void setCopiarDestacado(boolean copiarDestacado) {
		this.copiarDestacado = copiarDestacado;
	}

	public boolean isAbrirAuto() {
		return abrirAuto;
	}

	public void setId(String id) {
		if (!Util.estaVazio(id)) {
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

	public String getBuscaAutomatica() {
		if (Util.estaVazio(buscaAutomatica)) {
			buscaAutomatica = Constantes.VAZIO;
		}

		return buscaAutomatica;
	}

	public String getBuscaAutomaticaApos() {
		if (Util.estaVazio(buscaAutomaticaApos)) {
			buscaAutomaticaApos = Constantes.VAZIO;
		}

		return buscaAutomaticaApos;
	}

	public String getLinkAutomatico() {
		if (Util.estaVazio(linkAutomatico)) {
			linkAutomatico = Constantes.VAZIO;
		}

		return linkAutomatico;
	}

	public String getChaveamento() {
		if (Util.estaVazio(chaveamento)) {
			chaveamento = Constantes.VAZIO;
		}

		return chaveamento;
	}

	public void setChaveamento(String chaveamento) {
		this.chaveamento = chaveamento;
	}

	public void setBuscaAutomatica(String buscaAutomatica) {
		this.buscaAutomatica = buscaAutomatica;
	}

	public void setBuscaAutomaticaApos(String buscaAutomaticaApos) {
		this.buscaAutomaticaApos = buscaAutomaticaApos;
	}

	public void setLinkAutomatico(String linkAutomatico) {
		this.linkAutomatico = linkAutomatico;
	}

	public boolean contem(int x, int y) {
		return (x >= this.x && x <= this.x + DIAMETRO) && (y >= this.y && y <= this.y + DIAMETRO);
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

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public String toString() {
		return id;
	}

	public void alinhar(FontMetrics fm) {
		LOG.log(Level.FINEST, "alinhar");
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

	public Superficie getSuperficie() {
		return superficie;
	}

	public void setSuperficie(Superficie superficie) {
		this.superficie = superficie;
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			transparente = !transparente;

			if (superficie != null) {
				superficie.repaint(x, y, DIAMETRO, DIAMETRO);
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

	public String getTitle(OrdenacaoModelo modelo) {
		return getTabela2() + " - " + getId() + " [" + modelo.getRowCount() + "]";
	}

	public String getTitle(OrdenacaoModelo modelo, String complemento) {
		return getTabela2() + " - " + getId() + " [" + modelo.getRowCount() + "] - " + complemento;
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

	public boolean isControlado() {
		return controlado;
	}

	public void setControlado(boolean controlado) {
		this.controlado = controlado;
	}

	public TabelaBuscaAuto getTabelaBuscaAuto() {
		return tabelaBuscaAuto;
	}

	public void setTabelaBuscaAuto(TabelaBuscaAuto tabelaBuscaAuto) {
		this.tabelaBuscaAuto = tabelaBuscaAuto;
	}

	public boolean isCcsc() {
		return ccsc;
	}

	public void setCcsc(boolean ccsc) {
		this.ccsc = ccsc;
	}

	public long getTag() {
		return tag;
	}

	public void setTag(long tag) {
		this.tag = tag;
	}

	public void setMapeamento(String mapeamento) {
		this.mapeamento = mapeamento;
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
		if (Util.estaVazio(prefixoNomeTabela)) {
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
		if (Util.estaVazio(arquivo)) {
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
}