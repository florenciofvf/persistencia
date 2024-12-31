package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;

public class FuncaoContexto extends Container implements IFuncaoContexto {
	public static final AbreChaveOuOperador CHAVE_OU_OPERADOR = new AbreChaveOuOperador();
	public static final String LOAD_FUNCTION = "load_function";
	private final FuncaoIdentityContexto identity;
	private boolean faseParametros;
	private boolean identityVoid;
	private boolean retornoVoid;

	public FuncaoContexto() {
		identity = new FuncaoIdentityContexto();
		adicionar(new ParametrosContexto());
		adicionar(new CorpoContexto());
		faseParametros = true;
		contexto = identity;
	}

	public String getNome() {
		return getIdentity().toString();
	}

	public ParametrosContexto getParametros() {
		return (ParametrosContexto) get(0);
	}

	public CorpoContexto getCorpo() {
		return (CorpoContexto) get(1);
	}

	public FuncaoIdentityContexto getIdentity() {
		return identity;
	}

	public boolean isRetornoVoid() {
		return retornoVoid;
	}

	public void setRetornoVoid(boolean retornoVoid) {
		this.retornoVoid = retornoVoid;
	}

	@Override
	public void inicializador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.inicializador(compilador, token);
		if (faseParametros) {
			compilador.setContexto(getParametros());
			contexto = CHAVE_OU_OPERADOR;
			faseParametros = false;
		} else {
			compilador.setContexto(getCorpo());
			getCorpo().setFinalizadorPai(true);
			contexto = Contextos.FECHA_CHAVES;
		}
	}

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.finalizador(compilador, token);
		compilador.setContexto(getPai());
	}

	@Override
	public void operador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.operador(compilador, token);
		contexto = Contextos.IDENTITY;
		identityVoid = true;
	}

	@Override
	public void identity(Compilador compilador, Token token) throws InstrucaoException {
		contexto.identity(compilador, token);
		if (identityVoid) {
			if (!InstrucaoConstantes.VOID.equals(token.getString())) {
				compilador.invalidar(token);
			} else {
				contexto = Contextos.ABRE_CHAVES;
				retornoVoid = true;
			}
		} else {
			contexto = Contextos.ABRE_PARENTESES;
		}
	}

	public void indexar() {
		Indexador indexador = new Indexador();
		indexar(indexador);
	}

	@Override
	public void salvar(Compilador compilador, PrintWriter pw) throws InstrucaoException {
		pw.println(InstrucaoConstantes.PREFIXO_FUNCAO + identity);
		if (retornoVoid) {
			pw.println(InstrucaoConstantes.PREFIXO_TIPO_VOID);
		}
		getParametros().salvar(compilador, pw);
		getCorpo().salvar(compilador, pw);
	}

	@Override
	public String toString() {
		return InstrucaoConstantes.DEFUN + " " + identity + " >>> " + getParametros().toString();
	}
}

class AbreChaveOuOperador extends AbstratoContexto {
	@Override
	public void inicializador(Compilador compilador, Token token) throws InstrucaoException {
		if (!"{".equals(token.getString())) {
			compilador.invalidar(token);
		}
	}

	@Override
	public void operador(Compilador compilador, Token token) throws InstrucaoException {
		if (!":".equals(token.getString())) {
			compilador.invalidar(token);
		}
	}
}