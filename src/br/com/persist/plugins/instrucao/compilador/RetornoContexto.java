package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;

public class RetornoContexto extends Container {
	public RetornoContexto() {
		adicionar(new ExpressaoContexto(this));
		contexto = Contextos.PONTO_VIRGULA;
	}

	public ExpressaoContexto getExpressao() {
		return (ExpressaoContexto) get(0);
	}

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.finalizador(compilador, token);
		IFuncaoContexto funcao = getIFuncaoContexto();
		if (funcao == null) {
			compilador.invalidar(token);
		} else if (funcao.isRetornoVoid() && getExpressao().getSize() > 0) {
			compilador.invalidar(token, funcao.getNome() + " --> deve retornar void");
		} else if (!funcao.isRetornoVoid() && getExpressao().isEmpty()) {
			compilador.invalidar(token, funcao.getNome() + " --> deve retornar um valor");
		}
		compilador.setContexto(getPai());
	}

	@Override
	public void indexar(Indexador indexador) {
		pontoDeslocamento = indexador.value();
		super.indexar(indexador);
		sequencia = indexador.get();
	}

	@Override
	public void salvar(Compilador compilador, PrintWriter pw) throws InstrucaoException {
		super.salvar(compilador, pw);
		print(pw, InstrucaoConstantes.RETURN);
	}

	@Override
	public String toString() {
		return InstrucaoConstantes.RETURN + " >>> " + getExpressao().toString();
	}
}