package br.com.persist.plugins.objeto.macro;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.persist.plugins.objeto.Objeto;
import br.com.persist.plugins.objeto.Relacao;
import br.com.persist.plugins.objeto.macro.Macro.AbrirAuto;
import br.com.persist.plugins.objeto.macro.Macro.AjusteAutoForm;
import br.com.persist.plugins.objeto.macro.Macro.Bpnt;
import br.com.persist.plugins.objeto.macro.Macro.Ccsc;
import br.com.persist.plugins.objeto.macro.Macro.ChecarRegistro;
import br.com.persist.plugins.objeto.macro.Macro.ColunaInfo;
import br.com.persist.plugins.objeto.macro.Macro.CopiarDestacado;
import br.com.persist.plugins.objeto.macro.Macro.Cor;
import br.com.persist.plugins.objeto.macro.Macro.CorFonte;
import br.com.persist.plugins.objeto.macro.Macro.DesenharIdDescricao;
import br.com.persist.plugins.objeto.macro.Macro.DeslocamentoXIdDescricao;
import br.com.persist.plugins.objeto.macro.Macro.DeslocamentoYIdDescricao;
import br.com.persist.plugins.objeto.macro.Macro.Icone;
import br.com.persist.plugins.objeto.macro.Macro.LarguraRotulos;
import br.com.persist.plugins.objeto.macro.Macro.LinkAuto;
import br.com.persist.plugins.objeto.macro.Macro.PontoDestino;
import br.com.persist.plugins.objeto.macro.Macro.PontoOrigem;
import br.com.persist.plugins.objeto.macro.Macro.Quebrado;
import br.com.persist.plugins.objeto.macro.Macro.Sane;
import br.com.persist.plugins.objeto.macro.Macro.Transparente;
import br.com.persist.plugins.objeto.macro.Macro.XPos;
import br.com.persist.plugins.objeto.macro.Macro.YPos;

public class MacroProvedor {
	private static final List<Instrucao> instrucoes = new ArrayList<>();
	private static final Map<String, Instrucao> mapa = new HashMap<>();
	private static final String COPIAR_DESTACADO = "copiarDestacado";
	private static final String AJUSTE_AUTO_FORM = "ajusteAutoForm";
	private static final String DESENHAR_ID_DESC = "desenharIdDesc";
	private static final String DESCLOC_X_ID_DESC = "deslocXIdDesc";
	private static final String DESCLOC_Y_ID_DESC = "deslocYIdDesc";
	private static final String LARGURA_ROTULOS = "larguraRotulos";
	private static final String CHECAR_REGISTRO = "checarRegistro";
	private static final String PONTO_DESTINO = "pontoDestino";
	private static final String TRANSPARENTE = "transparente";
	private static final String PONTO_ORIGEM = "pontoOrigem";
	private static final String COLUNA_INFO = "colunaInfo";
	private static final String ABRIR_AUTO = "abrirAuto";
	private static final String COR_FONTE = "corFonte";
	private static final String LINK_AUTO = "linkAuto";
	private static final String QUEBRADO = "quebrado";
	private static final String ICONE = "icone";
	private static final String CCSC = "ccsc";
	private static final String SANE = "sane";
	private static final String BPNT = "bpnt";
	private static final String COR = "cor";
	private static final String X = "x";
	private static final String Y = "y";

	private MacroProvedor() {
	}

	public static void adicionar(Instrucao instrucao) {
		if (instrucao != null) {
			instrucoes.remove(instrucao);
			instrucoes.add(instrucao);
		}
	}

	public static Instrucao get(String chave) {
		return mapa.get(chave);
	}

	public static List<Instrucao> getInstrucoes() {
		return instrucoes;
	}

	public static Instrucao getInstrucao(int indice) {
		return instrucoes.get(indice);
	}

	public static void excluir(int indice) {
		instrucoes.remove(indice);
	}

	public static boolean isEmpty() {
		return instrucoes.isEmpty();
	}

	public static void limpar() {
		instrucoes.clear();
	}

	public static void inicializar() {
		mapa.clear();
		mapa.put(DESCLOC_X_ID_DESC, new DeslocamentoXIdDescricao());
		mapa.put(DESCLOC_Y_ID_DESC, new DeslocamentoYIdDescricao());
		mapa.put(DESENHAR_ID_DESC, new DesenharIdDescricao());
		mapa.put(COPIAR_DESTACADO, new CopiarDestacado());
		mapa.put(AJUSTE_AUTO_FORM, new AjusteAutoForm());
		mapa.put(LARGURA_ROTULOS, new LarguraRotulos());
		mapa.put(CHECAR_REGISTRO, new ChecarRegistro());
		mapa.put(PONTO_DESTINO, new PontoDestino());
		mapa.put(TRANSPARENTE, new Transparente());
		mapa.put(PONTO_ORIGEM, new PontoOrigem());
		mapa.put(COLUNA_INFO, new ColunaInfo());
		mapa.put(ABRIR_AUTO, new AbrirAuto());
		mapa.put(LINK_AUTO, new LinkAuto());
		mapa.put(COR_FONTE, new CorFonte());
		mapa.put(QUEBRADO, new Quebrado());
		mapa.put(ICONE, new Icone());
		mapa.put(CCSC, new Ccsc());
		mapa.put(SANE, new Sane());
		mapa.put(BPNT, new Bpnt());
		mapa.put(COR, new Cor());
		mapa.put(X, new XPos());
		mapa.put(Y, new YPos());
	}

	public abstract static class Instrucao {
		Object valor;

		public abstract void executar(Relacao relacao);

		public abstract void executar(Objeto objeto);

		public void setValor(Object valor) {
			this.valor = valor;
		}

		@Override
		public String toString() {
			return getClass().getSimpleName();
		}
	}

	public static void desenharIdDescricao(Object valor) {
		Instrucao instrucao = get(DESENHAR_ID_DESC);
		instrucao.setValor(valor);
		adicionar(instrucao);
	}

	public static void checarRegistro(Object valor) {
		Instrucao instrucao = get(CHECAR_REGISTRO);
		instrucao.setValor(valor);
		adicionar(instrucao);
	}

	public static void linhaQuebrada(Object valor) {
		Instrucao instrucao = get(QUEBRADO);
		instrucao.setValor(valor);
		adicionar(instrucao);
	}

	public static void colunaInfo(Object valor) {
		Instrucao instrucao = get(COLUNA_INFO);
		instrucao.setValor(valor);
		adicionar(instrucao);
	}

	public static void abrirAuto(Object valor) {
		Instrucao instrucao = get(ABRIR_AUTO);
		instrucao.setValor(valor);
		adicionar(instrucao);
	}

	public static void linkAuto(Object valor) {
		Instrucao instrucao = get(LINK_AUTO);
		instrucao.setValor(valor);
		adicionar(instrucao);
	}

	public static void larguraRotulos(Object valor) {
		Instrucao instrucao = get(LARGURA_ROTULOS);
		instrucao.setValor(valor);
		adicionar(instrucao);
	}

	public static void confirmarCsc(Object valor) {
		Instrucao instrucao = get(CCSC);
		instrucao.setValor(valor);
		adicionar(instrucao);
	}

	public static void semArgNaoExec(Object valor) {
		Instrucao instrucao = get(SANE);
		instrucao.setValor(valor);
		adicionar(instrucao);
	}

	public static void bloquearPnt(Object valor) {
		Instrucao instrucao = get(BPNT);
		instrucao.setValor(valor);
		adicionar(instrucao);
	}

	public static void deslocarXIdDescricao(Object valor) {
		Instrucao instrucao = get(DESCLOC_X_ID_DESC);
		instrucao.setValor(valor);
		adicionar(instrucao);
	}

	public static void deslocarYIdDescricao(Object valor) {
		Instrucao instrucao = get(DESCLOC_Y_ID_DESC);
		instrucao.setValor(valor);
		adicionar(instrucao);
	}

	public static void pontoDestino(Object valor) {
		Instrucao instrucao = get(PONTO_DESTINO);
		instrucao.setValor(valor);
		adicionar(instrucao);
	}

	public static void transparencia(Object valor) {
		Instrucao instrucao = get(TRANSPARENTE);
		instrucao.setValor(valor);
		adicionar(instrucao);
	}

	public static void copiarDestacado(Object valor) {
		Instrucao instrucao = get(COPIAR_DESTACADO);
		instrucao.setValor(valor);
		adicionar(instrucao);
	}

	public static void ajusteAutoForm(Object valor) {
		Instrucao instrucao = get(AJUSTE_AUTO_FORM);
		instrucao.setValor(valor);
		adicionar(instrucao);
	}

	public static void pontoOrigem(Object valor) {
		Instrucao instrucao = get(PONTO_ORIGEM);
		instrucao.setValor(valor);
		adicionar(instrucao);
	}

	public static void corFonte(Object valor) {
		Instrucao instrucao = get(COR_FONTE);
		instrucao.setValor(valor);
		adicionar(instrucao);
	}

	public static void imagem(Object valor) {
		Instrucao instrucao = get(ICONE);
		instrucao.setValor(valor);
		adicionar(instrucao);
	}

	public static void corFundo(Object valor) {
		Instrucao instrucao = get(COR);
		instrucao.setValor(valor);
		adicionar(instrucao);
	}

	public static void xLocal(Object valor) {
		Instrucao instrucao = get(X);
		instrucao.setValor(valor);
		adicionar(instrucao);
	}

	public static void yLocal(Object valor) {
		Instrucao instrucao = get(Y);
		instrucao.setValor(valor);
		adicionar(instrucao);
	}
}