package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.Token.Tipo;

public class ConstanteContexto extends Container {
	public static final String LOAD_CONST = "load_const";
	private final ConstanteIdentityContexto identity;

	public ConstanteContexto() {
		identity = new ConstanteIdentityContexto();
		adicionar(new ExpressaoContexto(null));
		contexto = Contextos.ABRE_PARENTESES;
	}

	public ExpressaoContexto getExpressao() {
		return (ExpressaoContexto) get(0);
	}

	@Override
	public void inicializador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.inicializador(compilador, token);
		contexto = identity;
	}

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.finalizador(compilador, token);
		if (getExpressao().isEmpty()) {
			throw new InstrucaoException("Valor indefinido para: " + identity.token.string, false);
		}
		compilador.setContexto(getPai());
	}

	@Override
	public void separador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.separador(compilador, token);
		compilador.setContexto(getExpressao());
		contexto = Contextos.PONTO_VIRGULA;
	}

	@Override
	public void identity(Compilador compilador, Token token) throws InstrucaoException {
		contexto.identity(compilador, token);
		contexto = Contextos.VIRGULA;
	}

	public void indexar() {
		Indexador indexador = new Indexador();
		indexar(indexador);
	}

	@Override
	public void indexar(Indexador indexador) {
		pontoDeslocamento = indexador.value();
		getExpressao().indexar(indexador);
		sequencia = indexador.get();
	}

	@Override
	public void salvar(Compilador compilador, PrintWriter pw) throws InstrucaoException {
		if (getFuncao() == null) {
			pw.println(InstrucaoConstantes.PREFIXO_CONSTANTE + identity);
		}
		getExpressao().salvar(compilador, pw);
		print(pw, InstrucaoConstantes.DEF_CONST, identity.toString());
	}

	@Override
	public String toString() {
		return identity.toString();
	}
}

class ConstanteIdentityContexto extends AbstratoContexto {
	Token token;

	@Override
	public void identity(Compilador compilador, Token token) throws InstrucaoException {
		if (token.getString().indexOf(".") != -1) {
			compilador.invalidar(token);
		} else {
			this.token = token;
			compilador.tokens.add(token.novo(Tipo.CONSTANTE));
		}
	}

	@Override
	public String toString() {
		return token.getString();
	}
}