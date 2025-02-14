package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;

import br.com.persist.plugins.instrucao.InstrucaoConstantes;
import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.processador.CacheBiblioteca;

public class BibliotecaContexto extends Container {
	protected CacheBiblioteca cacheBiblioteca = new CacheBiblioteca();
	private final String nome;
	private int idDinamico;

	public BibliotecaContexto(String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}

	public int getIdDinamico() {
		return idDinamico++;
	}

	@Override
	public void reservado(Compilador compilador, Token token) throws InstrucaoException {
		if (InstrucaoConstantes.DEFUN.equals(token.getString())) {
			compilador.setContexto(new FuncaoContexto());
			adicionar((Container) compilador.getContexto());
		} else if (InstrucaoConstantes.DEFUN_NATIVE.equals(token.getString())) {
			compilador.setContexto(new FuncaoNativaContexto());
			adicionar((Container) compilador.getContexto());
		} else if (InstrucaoConstantes.CONST.equals(token.getString())) {
			compilador.setContexto(new ConstanteContexto());
			adicionar((Container) compilador.getContexto());
		} else {
			compilador.invalidar(token);
		}
	}

	public boolean contemFuncao(String nome) throws InstrucaoException {
		return getFuncao(nome) != null;
	}

	public Container getFuncao(String nome) throws InstrucaoException {
		for (Container c : componentes) {
			if (c instanceof FuncaoContexto) {
				FuncaoContexto funcao = (FuncaoContexto) c;
				if (funcao.getNome().equals(nome)) {
					return funcao;
				}
			} else if (c instanceof FuncaoNativaContexto) {
				FuncaoNativaContexto funcao = (FuncaoNativaContexto) c;
				if (funcao.getNome().equals(nome)) {
					return funcao;
				}
			} else if (c instanceof LambContexto) {
				LambContexto funcao = (LambContexto) c;
				if (funcao.getNome().equals(nome)) {
					return funcao;
				}
			}
		}
		return null;
	}

	public void indexar() {
		for (Container c : componentes) {
			if (c instanceof ConstanteContexto) {
				ConstanteContexto constante = (ConstanteContexto) c;
				constante.indexar();
			}
		}
		for (Container c : componentes) {
			if (c instanceof FuncaoContexto) {
				FuncaoContexto funcao = (FuncaoContexto) c;
				funcao.indexar();
			} else if (c instanceof FuncaoNativaContexto) {
				FuncaoNativaContexto funcao = (FuncaoNativaContexto) c;
				funcao.indexar();
			} else if (c instanceof LambContexto) {
				LambContexto funcao = (LambContexto) c;
				funcao.indexar();
			}
		}
	}

	@Override
	public void salvar(Compilador compilador, PrintWriter pw) throws InstrucaoException {
		for (Container c : componentes) {
			if (c instanceof ConstanteContexto) {
				c.salvar(compilador, pw);
				pw.println();
			}
		}
		for (Container c : componentes) {
			if (c instanceof FuncaoContexto || c instanceof FuncaoNativaContexto || c instanceof LambContexto) {
				c.salvar(compilador, pw);
				pw.println();
			}
		}
	}

	@Override
	public String toString() {
		return nome;
	}
}