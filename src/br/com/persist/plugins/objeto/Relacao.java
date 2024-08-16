package br.com.persist.plugins.objeto;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.Objects;

import org.xml.sax.Attributes;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.HoraUtil;
import br.com.persist.assistencia.Util;
import br.com.persist.marca.XMLUtil;

public class Relacao implements Runnable {
	private static final Color COR_PADRAO_FONTE = Color.BLACK;
	private static final Color COR_PADRAO = Color.BLACK;
	private Color corFonte = COR_PADRAO_FONTE;
	private int deslocamentoXDesc = -5;
	private int deslocamentoYDesc = -5;
	private boolean desenharDescricao;
	private RelacaoListener listener;
	private static int diametro = 6;
	private Color cor = COR_PADRAO;
	private final Objeto destino;
	private boolean pontoDestino;
	static int m = diametro / 2;
	private boolean pontoOrigem;
	private String chaveDestino;
	private final Objeto origem;
	private boolean selecionado;
	private String chaveOrigem;
	private Objeto objetoTemp;
	private boolean processar;
	private boolean quebrado;
	private String descricao;
	private Thread thread;

	public Relacao(Objeto origem, Objeto destino) throws ObjetoException {
		this(origem, false, destino, false);
	}

	public Relacao(Objeto origem, boolean pontoOrigem, Objeto destino) throws ObjetoException {
		this(origem, pontoOrigem, destino, false);
	}

	public Relacao(Objeto origem, Objeto destino, boolean pontoDestino) throws ObjetoException {
		this(origem, false, destino, pontoDestino);
	}

	public Relacao(Objeto origem, boolean pontoOrigem, Objeto destino, boolean pontoDestino) throws ObjetoException {
		this.pontoDestino = pontoDestino;
		this.pontoOrigem = pontoOrigem;
		this.destino = Objects.requireNonNull(destino);
		this.origem = Objects.requireNonNull(origem);
		if (origem.equals(destino)) {
			throw new ObjetoException("origem e destino iguais");
		}
	}

	public String montarJoin() {
		if (Util.isEmpty(getChaveOrigem()) || Util.isEmpty(getChaveDestino())) {
			return Constantes.VAZIO;
		}
		StringBuilder sb = new StringBuilder(" ON");
		sb.append(" " + origem.getApelidoParaJoinOuTabela() + "." + getChaveOrigem());
		sb.append(" =");
		sb.append(" " + destino.getApelidoParaJoinOuTabela() + "." + getChaveDestino());
		return sb.toString();
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public void setPontoDestino(boolean ponto) {
		this.pontoDestino = ponto;
	}

	public void setPontoOrigem(boolean ponto) {
		this.pontoOrigem = ponto;
	}

	public Color getCorFonte() {
		return corFonte;
	}

	public void setCorFonte(Color corFonte) {
		this.corFonte = corFonte;
		if (this.corFonte == null) {
			this.corFonte = COR_PADRAO_FONTE;
		}
	}

	public boolean isDesenharDescricao() {
		return desenharDescricao;
	}

	public void setDesenharDescricao(boolean desenharDescricao) {
		this.desenharDescricao = desenharDescricao;
	}

	public boolean isQuebrado() {
		return quebrado;
	}

	public void setQuebrado(boolean quebrado) {
		this.quebrado = quebrado;
	}

	public boolean isPontoDestino() {
		return pontoDestino;
	}

	public boolean isPontoOrigem() {
		return pontoOrigem;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setCor(Color cor) {
		this.cor = cor;
		if (this.cor == null) {
			this.cor = COR_PADRAO;
		}
	}

	public String getDescricao() {
		if (descricao == null) {
			descricao = Constantes.VAZIO;
		}
		return descricao;
	}

	public Objeto getDestino() {
		return destino;
	}

	public Objeto getOrigem() {
		return origem;
	}

	public Color getCor() {
		return cor;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof Relacao) {
			Relacao outro = (Relacao) obj;
			return (origem.equals(outro.origem) && destino.equals(outro.destino))
					|| (origem.equals(outro.destino) && destino.equals(outro.origem));
		}
		return false;
	}

	@Override
	public int hashCode() {
		return origem.hashCode() + destino.hashCode();
	}

	public boolean contem(Objeto objeto) {
		if (!origem.visivel || !destino.visivel) {
			return false;
		}
		return origem.equals(objeto) || destino.equals(objeto);
	}

	public boolean contem(int posX, int posY) {
		if (!origem.visivel || !destino.visivel) {
			return false;
		}
		int raio = Objeto.DIAMETRO / 2;
		int x1 = origem.x + raio;
		int y1 = origem.y + raio;
		int x2 = destino.x + raio;
		int y2 = destino.y + raio;
		return quebrado ? contemQuebrada(posX, posY, x1, y1, x2, y2) : contemVetor(posX, posY, x1, y1, x2, y2);
	}

	private boolean contemQuebrada(int posX, int posY, int x1, int y1, int x2, int y2) {
		if (x1 == x2 || y1 == y2) {
			return contemVetor(posX, posY, x1, y1, x2, y2);
		}
		int[] x1x2Aux = x1x2(x1, x2);
		int[] y1y2Aux = y1y2(y1, y2);
		if (contemV(posX, posY, y1y2Aux[0], y1y2Aux[1], x1x2Aux[0])) {
			return true;
		}
		int y = 0;
		if (x1 < x2) {
			if (y1 < y2) {
				y = y1y2Aux[1];
			} else {
				y = y1y2Aux[0];
			}
		} else {
			if (y1 < y2) {
				y = y1y2Aux[0];
			} else {
				y = y1y2Aux[1];
			}
		}
		return contemH(posX, posY, x1x2Aux[0], x1x2Aux[1], y);
	}

	private boolean contemVetor(int posX, int posY, int x1, int y1, int x2, int y2) {
		int x = x2 - x1;
		int y = y2 - y1;
		double h = Math.sqrt((x * x + y * y));
		int xPos = posX - x1;
		int yPos = posY - y1;
		double hPos = Math.sqrt((xPos * xPos + yPos * yPos));
		if (hPos > h) {
			return false;
		}
		double auxX = x / h;
		double auxY = y / h;
		int auxX1 = (int) (auxX * hPos);
		int auxY1 = (int) (auxY * hPos);
		return comprimento(auxX1, auxY1, xPos, yPos) < 9;
	}

	private int comprimento(int x1, int y1, int x2, int y2) {
		int x = x2 - x1;
		int y = y2 - y1;
		return (int) Math.sqrt((x * x + y * y));
	}

	private int[] x1x2(int x1, int x2) {
		return x1 < x2 ? new int[] { x1, x2 } : new int[] { x2, x1 };
	}

	private int[] y1y2(int y1, int y2) {
		return y1 < y2 ? new int[] { y1, y2 } : new int[] { y2, y1 };
	}

	private boolean contemH(int posX, int posY, int x1, int x2, int y) {
		int l = x2 - x1;
		y -= 8;
		return contem(posX, posY, x1, y, l, 16);
	}

	private boolean contemV(int posX, int posY, int y1, int y2, int x) {
		int a = y2 - y1;
		x -= 8;
		return contem(posX, posY, x, y1, 16, a);
	}

	private boolean contem(int posX, int posY, int x, int y, int l, int a) {
		return (posX >= x && posX <= x + l) && (posY >= y && posY <= y + a);
	}

	public Objeto criarObjetoMeio() {
		Objeto objeto = new Objeto();
		int difX = (destino.x - origem.x) / 2;
		int difY = (destino.y - origem.y) / 2;
		objeto.x = origem.x + difX;
		objeto.y = origem.y + difY;
		return objeto;
	}

	public void aplicar(Attributes attr) {
		desenharDescricao = Boolean.parseBoolean(attr.getValue("desenharDescricao"));
		deslocamentoXDesc = Integer.parseInt(attr.getValue("desloc_x_desc"));
		deslocamentoYDesc = Integer.parseInt(attr.getValue("desloc_y_desc"));
		corFonte = new Color(Integer.parseInt(attr.getValue("corFonte")));
		processar = Boolean.parseBoolean(attr.getValue("processar"));
		quebrado = Boolean.parseBoolean(attr.getValue("quebrado"));
		cor = new Color(Integer.parseInt(attr.getValue("cor")));
		chaveDestino = attr.getValue("chaveDestino");
		chaveOrigem = attr.getValue("chaveOrigem");
	}

	public void salvar(XMLUtil util) {
		util.abrirTag("relacao");
		util.atributoCheck("origem", origem.getId());
		util.atributoCheck("destino", destino.getId());
		util.atributoCheck("chaveOrigem", getChaveOrigem());
		util.atributoCheck("chaveDestino", getChaveDestino());
		util.atributoCheck("desenharDescricao", desenharDescricao);
		util.atributo("desloc_x_desc", deslocamentoXDesc);
		util.atributo("desloc_y_desc", deslocamentoYDesc);
		util.atributo("corFonte", corFonte.getRGB());
		util.atributoCheck("pontoDestino", pontoDestino);
		util.atributoCheck("pontoOrigem", pontoOrigem);
		util.atributoCheck("processar", processar);
		util.atributoCheck("quebrado", quebrado);
		util.atributo("cor", cor.getRGB());
		util.fecharTag();
		if (!Util.isEmpty(getDescricao())) {
			util.abrirTag2("desc");
			util.conteudo("<![CDATA[").ql();
			util.tab().conteudo(getDescricao()).ql();
			util.conteudo("]]>").ql();
			util.finalizarTag("desc");
		}
		util.finalizarTag("relacao");
	}

	public void desenhar(Graphics2D g2, Stroke stroke) {
		if (!origem.visivel || !destino.visivel) {
			return;
		}
		int raio = Objeto.DIAMETRO / 2;
		int meta = raio / 2;
		g2.setColor(cor);
		if (selecionado) {
			g2.setStroke(Constantes.STROKE_PADRAO);
			if (objetoTemp == null || objetoTemp.corTemp == null) {
				g2.setColor(Color.CYAN);
			} else {
				g2.setColor(objetoTemp.corTemp);
			}
		}
		int x1 = origem.x + raio;
		int y1 = origem.y + raio;
		int x2 = destino.x + raio;
		int y2 = destino.y + raio;
		if (quebrado) {
			desenharLinhaQuebrada(g2, x1, y1, x2, y2);
		} else {
			g2.drawLine(x1, y1, x2, y2);
		}
		desenharPontos(g2, x1, y1, x2, y2, raio, meta);
		if (desenharDescricao && descricao != null) {
			g2.setColor(corFonte);
			g2.drawString(descricao, x1 + deslocamentoXDesc, y1 + deslocamentoYDesc);
		}
		if (selecionado) {
			g2.setStroke(stroke);
		}
	}

	private void desenharLinhaQuebrada(Graphics2D g2, int x1, int y1, int x2, int y2) {
		if (x1 == x2 || y1 == y2) {
			g2.drawLine(x1, y1, x2, y2);
		} else {
			int[] x1x2Aux = x1x2(x1, x2);
			int[] y1y2Aux = y1y2(y1, y2);
			g2.drawLine(x1x2Aux[0], y1y2Aux[0], x1x2Aux[0], y1y2Aux[1]);
			if (x1 < x2) {
				if (y1 < y2) {
					g2.drawLine(x1x2Aux[0], y1y2Aux[1], x1x2Aux[1], y1y2Aux[1]);
				} else {
					g2.drawLine(x1x2Aux[0], y1y2Aux[0], x1x2Aux[1], y1y2Aux[0]);
				}
			} else {
				if (y1 < y2) {
					g2.drawLine(x1x2Aux[0], y1y2Aux[0], x1x2Aux[1], y1y2Aux[0]);
				} else {
					g2.drawLine(x1x2Aux[0], y1y2Aux[1], x1x2Aux[1], y1y2Aux[1]);
				}
			}
		}
	}

	private void desenharPontos(Graphics2D g2, int x1, int y1, int x2, int y2, int raio, int meta) {
		if (quebrado) {
			desenharPontosLinhaQuebrada(g2, x1, y1, x2, y2, raio, meta);
		} else {
			desenharPontos2(g2, x1, y1, x2, y2, raio, meta);
		}
	}

	private void desenharPontosLinhaQuebrada(Graphics2D g2, int x1, int y1, int x2, int y2, int raio, int meta) {
		if (pontoOrigem || pontoDestino) {
			pontoQuebradoOrigem(g2, x1, y1, x2, y2, raio, meta);
			pontoQuebradoDestino(g2, x1, y1, x2, y2, raio, meta);
		}
	}

	private void pontoQuebradoOrigem(Graphics2D g2, int x1, int y1, int x2, int y2, int raio, int meta) {
		if (pontoOrigem) {
			int valor = origem.isTransparente() ? meta : raio;
			if (y1 < y2) {
				if (x1 < x2) {
					g2.fillOval(x1 - m, y1 + valor - m, diametro, diametro);
				} else {
					g2.fillOval(x1 - valor - m, y1 - m, diametro, diametro);
				}
			} else {
				if (x1 < x2) {
					g2.fillOval(x1 - m, y1 - valor - m, diametro, diametro);
				} else {
					g2.fillOval(x1 - valor - m, y1 - m, diametro, diametro);
				}
			}
		}
	}

	private void pontoQuebradoDestino(Graphics2D g2, int x1, int y1, int x2, int y2, int raio, int meta) {
		if (pontoDestino) {
			int valor = destino.isTransparente() ? meta : raio;
			if (y2 < y1) {
				if (x2 < x1) {
					g2.fillOval(x2 - m, y2 + valor - m, diametro, diametro);
				} else {
					g2.fillOval(x2 - valor - m, y2 - m, diametro, diametro);
				}
			} else {
				if (x2 < x1) {
					g2.fillOval(x2 - m, y2 - valor - m, diametro, diametro);
				} else {
					g2.fillOval(x2 - valor - m, y2 - m, diametro, diametro);
				}
			}
		}
	}

	private void desenharPontos2(Graphics2D g2, int x1, int y1, int x2, int y2, int raio, int meta) {
		if (pontoOrigem || pontoDestino) {
			int x = x2 - x1;
			int y = y2 - y1;
			double h = Math.sqrt((x * x + y * y));
			double auxX = x / h;
			double auxY = y / h;
			if (pontoOrigem) {
				int valor = origem.isTransparente() ? meta : raio;
				int auxX1 = (int) (auxX * valor);
				int auxY1 = (int) (auxY * valor);
				g2.fillOval(x1 + auxX1 - m, y1 + auxY1 - m, diametro, diametro);
			}
			if (pontoDestino) {
				int valor = destino.isTransparente() ? meta : raio;
				int auxX2 = (int) (auxX * (h - valor));
				int auxY2 = (int) (auxY * (h - valor));
				g2.fillOval(x1 + auxX2 - m, y1 + auxY2 - m, diametro, diametro);
			}
		}
	}

	public void reiniciarHoras(boolean checar, ObjetoSuperficie superficie) {
		if (checar) {
			if (HoraUtil.formatoValido(getOrigem().getId()) || HoraUtil.formatoValido(getDestino().getId())) {
				reiniciarHora(getOrigem(), superficie);
				reiniciarHora(getDestino(), superficie);
				processarHoraDiff(false);
				desativar();
			}
		} else {
			reiniciarHora(getOrigem(), superficie);
			reiniciarHora(getDestino(), superficie);
			processarHoraDiff(false);
		}
	}

	private void reiniciarHora(Objeto objeto, ObjetoSuperficie superficie) {
		final String id = "00:00:";
		Objeto obj = new Objeto();
		int cont = 0;
		obj.setId(id + get(cont));
		while (ObjetoSuperficieUtil.contemId(superficie, obj)) {
			cont++;
			obj.setId(id + get(cont));
		}
		objeto.setId(obj.getId());
	}

	private String get(int i) {
		return i < 10 ? "0" + i : "" + i;
	}

	public void processarHoraDiff(boolean horaAtual) {
		int ori = 0;
		int des = 0;
		try {
			ori = HoraUtil.getSegundos(getOrigem().getId());
		} catch (Exception e) {
			return;
		}
		try {
			if (horaAtual) {
				getDestino().setId(HoraUtil.getHoraAtual());
			}
			des = HoraUtil.getSegundos(getDestino().getId());
		} catch (Exception e) {
			return;
		}
		int diff = HoraUtil.getDiff(ori, des);
		setDescricao(HoraUtil.formatar(diff));
		setDesenharDescricao(true);
	}

	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			processarHoraDiff(true);
			if (listener != null) {
				listener.repaint();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	public void ativar() {
		if (processar && thread == null) {
			thread = new Thread(this);
			thread.start();
		}
	}

	public void desativar() {
		if (thread != null) {
			thread.interrupt();
			processar = false;
			thread = null;
		}
	}

	public RelacaoListener getListener() {
		return listener;
	}

	public void setListener(RelacaoListener listener) {
		this.listener = listener;
	}

	public boolean isProcessar() {
		return processar;
	}

	public void setProcessar(boolean processar) {
		this.processar = processar;
	}

	public int getDeslocamentoXDesc() {
		return deslocamentoXDesc;
	}

	public void setDeslocamentoXDesc(int deslocamentoXDesc) {
		this.deslocamentoXDesc = deslocamentoXDesc;
	}

	public void deslocamentoXDescDelta(int delta) {
		this.deslocamentoXDesc += delta;
	}

	public int getDeslocamentoYDesc() {
		return deslocamentoYDesc;
	}

	public void setDeslocamentoYDesc(int deslocamentoYDesc) {
		this.deslocamentoYDesc = deslocamentoYDesc;
	}

	public void deslocamentoYDescDelta(int delta) {
		this.deslocamentoYDesc += delta;
	}

	public String getChaveDestino() {
		if (Util.isEmpty(chaveDestino)) {
			chaveDestino = Constantes.VAZIO;
		}
		return chaveDestino;
	}

	public void setChaveDestino(String chaveDestino) {
		this.chaveDestino = chaveDestino;
	}

	public String getChaveOrigem() {
		if (Util.isEmpty(chaveOrigem)) {
			chaveOrigem = Constantes.VAZIO;
		}
		return chaveOrigem;
	}

	public void setChaveOrigem(String chaveOrigem) {
		this.chaveOrigem = chaveOrigem;
	}

	public Objeto getObjetoTemp() {
		return objetoTemp;
	}

	public void setObjetoTemp(Objeto objetoTemp) {
		this.objetoTemp = objetoTemp;
	}
}