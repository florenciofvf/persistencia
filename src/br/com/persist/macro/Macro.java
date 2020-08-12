package br.com.persist.macro;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.persist.desktop.Objeto;
import br.com.persist.desktop.Relacao;

public class Macro {
	public static final String AJUSTE_AUTO_ENTER = "ajusteAutoEnter";
	public static final String COPIAR_DESTACADO = "copiarDestacado";
	public static final String AJUSTE_AUTO_FORM = "ajusteAutoForm";
	public static final String DESENHAR_ID_DESC = "desenharIdDesc";
	public static final String DESCLOC_X_ID_DESC = "deslocXIdDesc";
	public static final String DESCLOC_Y_ID_DESC = "deslocYIdDesc";
	public static final String PONTO_DESTINO = "pontoDestino";
	public static final String TRANSPARENTE = "transparente";
	public static final String PONTO_ORIGEM = "pontoOrigem";
	public static final String COLUNA_INFO = "colunaInfo";
	public static final String ABRIR_AUTO = "abrirAuto";
	public static final String COR_FONTE = "corFonte";
	public static final String LINK_AUTO = "linkAuto";
	public static final String QUEBRADO = "quebrado";
	public static final String ICONE = "icone";
	private final Map<String, Instrucao> mapa;
	private final List<Instrucao> instrucoes;
	public static final String CCSC = "ccsc";
	public static final String BPNT = "bpnt";
	public static final String COR = "cor";
	public static final String X = "x";
	public static final String Y = "y";

	public Macro() {
		instrucoes = new ArrayList<>();
		mapa = new HashMap<>();

		mapa.put(DESCLOC_X_ID_DESC, new DeslocamentoXIdDescricao());
		mapa.put(DESCLOC_Y_ID_DESC, new DeslocamentoYIdDescricao());
		mapa.put(DESENHAR_ID_DESC, new DesenharIdDescricao());
		mapa.put(AJUSTE_AUTO_ENTER, new AjusteAutoEnter());
		mapa.put(COPIAR_DESTACADO, new CopiarDestacado());
		mapa.put(AJUSTE_AUTO_FORM, new AjusteAutoForm());
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
		mapa.put(BPNT, new Bpnt());
		mapa.put(COR, new Cor());
		mapa.put(X, new XPos());
		mapa.put(Y, new YPos());
	}

	public List<Instrucao> getInstrucoes() {
		return instrucoes;
	}

	public abstract class Instrucao {
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

	class DeslocamentoXIdDescricao extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			relacao.setDeslocamentoXDesc((Integer) valor);
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setDeslocamentoXId((Integer) valor);
		}
	}

	class DeslocamentoYIdDescricao extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			relacao.setDeslocamentoYDesc((Integer) valor);
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setDeslocamentoYId((Integer) valor);
		}
	}

	class DesenharIdDescricao extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			relacao.setDesenharDescricao((Boolean) valor);
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setDesenharId((Boolean) valor);
		}
	}

	class Quebrado extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			relacao.setQuebrado((Boolean) valor);
		}

		@Override
		public void executar(Objeto objeto) {
			throw new UnsupportedOperationException();
		}
	}

	class ColunaInfo extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setColunaInfo((Boolean) valor);
		}
	}

	class AbrirAuto extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setAbrirAuto((Boolean) valor);
		}
	}

	class LinkAuto extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setLinkAuto((Boolean) valor);
		}
	}

	class Ccsc extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setCcsc((Boolean) valor);
		}
	}

	class Bpnt extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setBpnt((Boolean) valor);
		}
	}

	class Transparente extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setTransparente((Boolean) valor);
		}
	}

	class CopiarDestacado extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setCopiarDestacado((Boolean) valor);
		}
	}

	class AjusteAutoForm extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setAjusteAutoForm((Boolean) valor);
		}
	}

	class AjusteAutoEnter extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setAjusteAutoEnter((Boolean) valor);
		}
	}

	class PontoDestino extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			relacao.setPontoDestino((Boolean) valor);
		}

		@Override
		public void executar(Objeto objeto) {
			throw new UnsupportedOperationException();
		}
	}

	class PontoOrigem extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			relacao.setPontoOrigem((Boolean) valor);
		}

		@Override
		public void executar(Objeto objeto) {
			throw new UnsupportedOperationException();
		}
	}

	class CorFonte extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			relacao.setCorFonte((Color) valor);
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setCorFonte((Color) valor);
		}
	}

	class Icone extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void executar(Objeto objeto) {
			if (valor != null) {
				objeto.setIcone(valor.toString());
			} else {
				objeto.limparIcone();
			}
		}
	}

	class Cor extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			relacao.setCor((Color) valor);
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setCor((Color) valor);
		}
	}

	class XPos extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setX((Integer) valor);
		}
	}

	class YPos extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setY((Integer) valor);
		}
	}

	public void desenharIdDescricao(Object valor) {
		Instrucao instrucao = mapa.get(DESENHAR_ID_DESC);
		instrucao.setValor(valor);
		adicionar(instrucao);
	}

	public void linhaQuebrada(Object valor) {
		Instrucao instrucao = mapa.get(QUEBRADO);
		instrucao.setValor(valor);
		adicionar(instrucao);
	}

	public void colunaInfo(Object valor) {
		Instrucao instrucao = mapa.get(COLUNA_INFO);
		instrucao.setValor(valor);
		adicionar(instrucao);
	}

	public void abrirAuto(Object valor) {
		Instrucao instrucao = mapa.get(ABRIR_AUTO);
		instrucao.setValor(valor);
		adicionar(instrucao);
	}

	public void linkAuto(Object valor) {
		Instrucao instrucao = mapa.get(LINK_AUTO);
		instrucao.setValor(valor);
		adicionar(instrucao);
	}

	public void confirmarCsc(Object valor) {
		Instrucao instrucao = mapa.get(CCSC);
		instrucao.setValor(valor);
		adicionar(instrucao);
	}

	public void bloquearPnt(Object valor) {
		Instrucao instrucao = mapa.get(BPNT);
		instrucao.setValor(valor);
		adicionar(instrucao);
	}

	public void deslocarXIdDescricao(Object valor) {
		Instrucao instrucao = mapa.get(DESCLOC_X_ID_DESC);
		instrucao.setValor(valor);
		adicionar(instrucao);
	}

	public void deslocarYIdDescricao(Object valor) {
		Instrucao instrucao = mapa.get(DESCLOC_Y_ID_DESC);
		instrucao.setValor(valor);
		adicionar(instrucao);
	}

	public void pontoDestino(Object valor) {
		Instrucao instrucao = mapa.get(PONTO_DESTINO);
		instrucao.setValor(valor);
		adicionar(instrucao);
	}

	public void transparencia(Object valor) {
		Instrucao instrucao = mapa.get(TRANSPARENTE);
		instrucao.setValor(valor);
		adicionar(instrucao);
	}

	public void copiarDestacado(Object valor) {
		Instrucao instrucao = mapa.get(COPIAR_DESTACADO);
		instrucao.setValor(valor);
		adicionar(instrucao);
	}

	public void ajusteAutoForm(Object valor) {
		Instrucao instrucao = mapa.get(AJUSTE_AUTO_FORM);
		instrucao.setValor(valor);
		adicionar(instrucao);
	}

	public void ajusteAutoEnter(Object valor) {
		Instrucao instrucao = mapa.get(AJUSTE_AUTO_ENTER);
		instrucao.setValor(valor);
		adicionar(instrucao);
	}

	public void pontoOrigem(Object valor) {
		Instrucao instrucao = mapa.get(PONTO_ORIGEM);
		instrucao.setValor(valor);
		adicionar(instrucao);
	}

	public void corFonte(Object valor) {
		Instrucao instrucao = mapa.get(COR_FONTE);
		instrucao.setValor(valor);
		adicionar(instrucao);
	}

	public void imagem(Object valor) {
		Instrucao instrucao = mapa.get(ICONE);
		instrucao.setValor(valor);
		adicionar(instrucao);
	}

	public void corFundo(Object valor) {
		Instrucao instrucao = mapa.get(COR);
		instrucao.setValor(valor);
		adicionar(instrucao);
	}

	public void xLocal(Object valor) {
		Instrucao instrucao = mapa.get(X);
		instrucao.setValor(valor);
		adicionar(instrucao);
	}

	public void yLocal(Object valor) {
		Instrucao instrucao = mapa.get(Y);
		instrucao.setValor(valor);
		adicionar(instrucao);
	}

	private void adicionar(Instrucao instrucao) {
		instrucoes.remove(instrucao);
		instrucoes.add(instrucao);
	}

	public void limpar() {
		instrucoes.clear();
	}

	public void excluir(int indice) {
		instrucoes.remove(indice);
	}

	public boolean isEmpty() {
		return instrucoes.isEmpty();
	}
}