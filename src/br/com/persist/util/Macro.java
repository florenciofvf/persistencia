package br.com.persist.util;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.persist.Objeto;
import br.com.persist.Relacao;

public class Macro {
	public static final String DESENHAR_ID_DESC = "desenharIdDesc";
	public static final String DESCLOC_X_ID_DESC = "deslocXIdDesc";
	public static final String DESCLOC_Y_ID_DESC = "deslocYIdDesc";
	public static final String PONTO_DESTINO = "pontoDestino";
	public static final String TRANSPARENTE = "transparente";
	public static final String PONTO_ORIGEM = "pontoOrigem";
	public static final String ABRIR_AUTO = "abrirAuto";
	public static final String COR_FONTE = "corFonte";
	public static final String ICONE = "icone";
	private final Map<String, Instrucao> mapa;
	private final List<Instrucao> instrucoes;
	public static final String COR = "cor";
	public static final String X = "x";
	public static final String Y = "y";

	public Macro() {
		instrucoes = new ArrayList<>();
		mapa = new HashMap<>();

		mapa.put(DESCLOC_X_ID_DESC, new DESLOCAMENTO_X_ID_DESCRICAO());
		mapa.put(DESCLOC_Y_ID_DESC, new DESLOCAMENTO_Y_ID_DESCRICAO());
		mapa.put(DESENHAR_ID_DESC, new DESENHAR_ID_DESCRICAO());
		mapa.put(PONTO_DESTINO, new PONTO_DESTINO());
		mapa.put(TRANSPARENTE, new TRANSPARENTE());
		mapa.put(PONTO_ORIGEM, new PONTO_ORIGEM());
		mapa.put(ABRIR_AUTO, new ABRIR_AUTO());
		mapa.put(COR_FONTE, new COR_FONTE());
		mapa.put(ICONE, new ICONE());
		mapa.put(COR, new COR());
		mapa.put(X, new X());
		mapa.put(Y, new Y());
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

	class DESLOCAMENTO_X_ID_DESCRICAO extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			relacao.deslocamentoXDesc = (Integer) valor;
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.deslocamentoXId = (Integer) valor;
		}
	}

	class DESLOCAMENTO_Y_ID_DESCRICAO extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			relacao.deslocamentoYDesc = (Integer) valor;
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.deslocamentoYId = (Integer) valor;
		}
	}

	class DESENHAR_ID_DESCRICAO extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			relacao.setDesenharDescricao((Boolean) valor);
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setDesenharId((Boolean) valor);
		}
	}

	class ABRIR_AUTO extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setAbrirAuto((Boolean) valor);
		}
	}

	class TRANSPARENTE extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setTransparente((Boolean) valor);
		}
	}

	class PONTO_DESTINO extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			relacao.setPontoDestino((Boolean) valor);
		}

		@Override
		public void executar(Objeto objeto) {
		}
	}

	class PONTO_ORIGEM extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			relacao.setPontoOrigem((Boolean) valor);
		}

		@Override
		public void executar(Objeto objeto) {
		}
	}

	class COR_FONTE extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			relacao.setCorFonte((Color) valor);
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setCorFonte((Color) valor);
		}
	}

	class ICONE extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setIcone(valor.toString());
		}
	}

	class COR extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			relacao.setCor((Color) valor);
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setCor((Color) valor);
		}
	}

	class X extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.x = (Integer) valor;
		}
	}

	class Y extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.y = (Integer) valor;
		}
	}

	public void desenharIdDescricao(Object valor) {
		Instrucao instrucao = mapa.get(DESENHAR_ID_DESC);
		instrucao.setValor(valor);
		adicionar(instrucao);
	}

	public void abrirAuto(Object valor) {
		Instrucao instrucao = mapa.get(ABRIR_AUTO);
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

	public void transparente(Object valor) {
		Instrucao instrucao = mapa.get(TRANSPARENTE);
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

	public void icone(Object valor) {
		Instrucao instrucao = mapa.get(ICONE);
		instrucao.setValor(valor);
		adicionar(instrucao);
	}

	public void cor(Object valor) {
		Instrucao instrucao = mapa.get(COR);
		instrucao.setValor(valor);
		adicionar(instrucao);
	}

	public void x(Object valor) {
		Instrucao instrucao = mapa.get(X);
		instrucao.setValor(valor);
		adicionar(instrucao);
	}

	public void y(Object valor) {
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