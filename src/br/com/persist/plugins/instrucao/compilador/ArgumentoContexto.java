package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;
import java.util.Iterator;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.processador.Biblioteca;
import br.com.persist.plugins.instrucao.processador.Funcao;

public class ArgumentoContexto extends Container {
	private final IdentityContexto identity;

	public ArgumentoContexto(IdentityContexto identity) {
		contexto = Contextos.PARENTESES;
		this.identity = identity;
	}

	public IdentityContexto getIdentity() {
		return identity;
	}

	@Override
	public void inicializador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.inicializador(compilador, token);
		if (getUltimo() instanceof IdentityContexto) {
			IdentityContexto ultimo = (IdentityContexto) excluirUltimo();
			compilador.setContexto(new ArgumentoContexto(ultimo));
		} else {
			compilador.setContexto(new ExpressaoContexto());
		}
		adicionarImpl(compilador, token, (Container) compilador.getContexto());
	}

	@Override
	public void finalizador(Compilador compilador, Token token) throws InstrucaoException {
		contexto.finalizador(compilador, token);
		compilador.setContexto(getPai());
		normalizarArvore(compilador);
	}

	@Override
	public void separador(Compilador compilador, Token token) throws InstrucaoException {
		if (isEmpty() || getUltimo() instanceof SeparadorContexto) {
			compilador.invalidar(token);
		}
		adicionarImpl(compilador, token, new SeparadorContexto(token));
	}

	@Override
	public void string(Compilador compilador, Token token) throws InstrucaoException {
		adicionarImpl(compilador, token, new StringContexto(token));
	}

	@Override
	public void lista(Compilador compilador, Token token) throws InstrucaoException {
		adicionarImpl(compilador, token, new ListaContexto(token));
	}

	@Override
	public void numero(Compilador compilador, Token token) throws InstrucaoException {
		adicionarImpl(compilador, token, new NumeroContexto(token));
	}

	@Override
	public void identity(Compilador compilador, Token token) throws InstrucaoException {
		adicionarImpl(compilador, token, new IdentityContexto(token));
	}

	public void adicionarImpl(Compilador compilador, Token token, Container c) throws InstrucaoException {
		Container ult = getUltimo();
		if (ult != null && !(ult instanceof SeparadorContexto) && !(c instanceof SeparadorContexto)) {
			compilador.invalidar(token);
		}
		adicionar(c);
	}

	private void normalizarArvore(Compilador compilador) throws InstrucaoException {
		if (getSize() == 0) {
			return;
		}
		if (getPrimeiro() instanceof SeparadorContexto) {
			SeparadorContexto separador = (SeparadorContexto) getPrimeiro();
			compilador.invalidar(separador.getToken());
		} else if (getUltimo() instanceof SeparadorContexto) {
			SeparadorContexto separador = (SeparadorContexto) getUltimo();
			compilador.invalidar(separador.getToken());
		}
		validarSequencia(compilador);
		normalizarArvore();
	}

	private void validarSequencia(Compilador compilador) throws InstrucaoException {
		for (int i = 0; i < getSize(); i++) {
			Container c = get(i);
			if (i % 2 == 0) {
				if (c instanceof SeparadorContexto) {
					SeparadorContexto separador = (SeparadorContexto) c;
					compilador.invalidar(separador.getToken());
				}
			} else {
				if (!(c instanceof SeparadorContexto)) {
					SeparadorContexto separador = (SeparadorContexto) c;
					compilador.invalidar(separador.getToken());
				}
			}
		}
	}

	private void normalizarArvore() {
		Iterator<Container> it = getComponentes().iterator();
		while (it.hasNext()) {
			Container c = it.next();
			if (c instanceof SeparadorContexto) {
				it.remove();
			}
		}
	}

	@Override
	public void indexar(Indexador indexador) {
		super.indexar(indexador);
		if (identity != null) {
			sequencia = indexador.get3();
			identity.indexarNegativo(indexador);
		}
	}

	@Override
	protected void validarImpl() throws InstrucaoException {
		if (identity == null) {
			return;
		}
		if (ehInvokeParam()) {
			return;
		}
		BibliotecaContexto biblio = getBiblioteca();
		String id = identity.getId();
		if (biblio == null) {
			throw new InstrucaoException("erro.funcao_parent", id);
		}
		String[] strings = id.split("\\.");
		if (strings.length == 1) {
			Container funcao = biblio.getFuncao(id);
			InvocacaoContexto.validarImpl(funcao, id, this, true);
		} else {
			try {
				Biblioteca biblioteca = biblio.cacheBiblioteca.getBiblioteca(strings[0]);
				Funcao funcao = biblioteca.getFuncao(strings[1]);
				InvocacaoContexto.validarImpl(funcao, this, true);
			} catch (InstrucaoException ex) {
				throw new InstrucaoException(ex.getMessage(), false);
			}
		}
	}

	@Override
	public void salvar(PrintWriter pw) throws InstrucaoException {
		super.salvar(pw);
		if (identity != null) {
			if (ehInvokeParam()) {
				print(pw, InvocacaoContexto.INVOKE_PARAM_EXP, identity.getId(), "" + getSize());
			} else {
				print(pw, InvocacaoContexto.INVOKE_EXP, identity.getId());
			}
			identity.salvarNegativo(pw);
		}
	}

	private boolean ehInvokeParam() throws InstrucaoException {
		FuncaoContexto funcao = getFuncao();
		if (funcao == null) {
			throw new InstrucaoException("erro.funcao_parent", identity.getId());
		}
		ParametrosContexto parametros = funcao.getParametros();
		return parametros.contem(identity.getId());
	}

	@Override
	public String toString() {
		return "Argumento(s): " + getComponentes().toString();
	}
}