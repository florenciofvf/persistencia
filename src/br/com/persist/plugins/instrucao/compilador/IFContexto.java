package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class IFContexto extends Container {
	public static final ReservadoOuFinalizar RESERVADO_OU_FINALIZAR = new ReservadoOuFinalizar();
	private IFEqContexto ifEqContexto = new IFEqContexto();
	private GotoContexto gotoContexto = new GotoContexto();
	private boolean faseExpressao;

	public IFContexto() {
		contexto = Contextos.ABRE_PARENTESES;
		adicionar(new ExpressaoContexto());
		adicionar(new CorpoContexto());
		faseExpressao = true;
	}

	public ExpressaoContexto getExpressao() {
		return (ExpressaoContexto) get(0);
	}

	public CorpoContexto getCorpo() {
		return (CorpoContexto) get(1);
	}

	@Override
	public void inicializador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.inicializador(compilador, token);
		if (faseExpressao) {
			compilador.setContexto(getExpressao());
			contexto = Contextos.ABRE_CHAVES;
			faseExpressao = false;
		} else {
			compilador.setContexto(getCorpo());
			contexto = RESERVADO_OU_FINALIZAR;
		}
	}

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.finalizador(compilador, token);
		compilador.setContexto(getPai());
		normalizarArvore(compilador, token);
	}

	@Override
	public void reservado(Compilador compilador, Token token) throws InstrucaoException {
		contexto.reservado(compilador, token);
		if ("elseif".equals(token.getString())) {
			compilador.setContexto(new ElseIFContexto());
			adicionarImpl(compilador, token, (Container) compilador.getContexto());
		} else if ("else".equals(token.getString())) {
			compilador.setContexto(new ElseContexto());
			adicionarImpl(compilador, token, (Container) compilador.getContexto());
		} else {
			compilador.invalidar(token);
		}
	}

	public void adicionarImpl(Compilador compilador, Token token, Container c) throws InstrucaoException {
		Container ultimo = getUltimo();
		if (ultimo instanceof ElseContexto) {
			compilador.invalidar(token);
		}
		adicionar(c);
	}

	private void normalizarArvore(Compilador compilador, Token token) throws InstrucaoException {
		if (getSize() == 2 || (getSize() == 3 && getUltimo() instanceof ElseContexto)) {
			return;
		}
		List<Container> lista = new ArrayList<>();
		for (int i = 2; i < getSize(); i++) {
			lista.add(get(i));
		}
		for (Container c : lista) {
			excluir(c);
		}
		IFContexto sel = this;
		Iterator<Container> it = lista.iterator();
		while (it.hasNext()) {
			Container c = it.next();
			sel = sel.fragmentar(c, compilador, token);
		}
	}

	private IFContexto fragmentar(Container c, Compilador compilador, Token token) throws InstrucaoException {
		IFContexto resposta = new IFContexto();
		if (c instanceof ElseIFContexto) {
			ElseIFContexto elseIFContexto = (ElseIFContexto) c;
			resposta.clear();

			ExpressaoContexto expressao = elseIFContexto.getExpressao();
			CorpoContexto corpo = elseIFContexto.getCorpo();
			resposta.adicionar(expressao);
			resposta.adicionar(corpo);

			ElseContexto elseContexto = new ElseContexto();
			elseContexto.getCorpo().adicionar(resposta);
			adicionar(elseContexto);
		} else if (c instanceof ElseContexto) {
			resposta = this;
			adicionar(c);
		} else {
			compilador.invalidar(token);
		}
		return resposta;
	}

	@Override
	public void indexar(Indexador indexador) {
		getExpressao().indexar(indexador);
		ifEqContexto.indexar(indexador);

		if (getSize() == 2) {
			getCorpo().indexar(indexador);
			ifEqContexto.posicao = indexador.value();
			return;
		}

		if (getCorpo().getUltimo() instanceof RetornoContexto) {
			getCorpo().indexar(indexador);
			ifEqContexto.posicao = indexador.value();
			getUltimo().indexar(indexador);
			return;
		}

		getCorpo().indexar(indexador);
		gotoContexto.indexar(indexador);
		ifEqContexto.posicao = indexador.value();
		getUltimo().indexar(indexador);
		gotoContexto.posicao = indexador.value();
	}

	@Override
	public void salvar(PrintWriter pw) {
		getExpressao().salvar(pw);
		ifEqContexto.salvar(pw);

		if (getSize() == 2) {
			getCorpo().salvar(pw);
			return;
		}

		if (getCorpo().getUltimo() instanceof RetornoContexto) {
			getCorpo().salvar(pw);
			getUltimo().salvar(pw);
			return;
		}

		getCorpo().salvar(pw);
		gotoContexto.salvar(pw);
		getUltimo().salvar(pw);
	}

	@Override
	public String toString() {
		return "if >>> " + getExpressao().toString();
	}
}

class ReservadoOuFinalizar extends AbstratoContexto {
	private final String[] strings = { "elseif", "else" };

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		if (!";".equals(token.getString())) {
			compilador.invalidar(token);
		}
	}

	@Override
	public void reservado(Compilador compilador, Token token) throws InstrucaoException {
		if (!igual(token.getString())) {
			compilador.invalidar(token);
		}
	}

	private boolean igual(String s) {
		for (String string : strings) {
			if (string.equals(s)) {
				return true;
			}
		}
		return false;
	}
}