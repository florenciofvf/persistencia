package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class BibliotecaContexto extends Container {
	private final String nome;

	public BibliotecaContexto(String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}

	@Override
	public void reservado(Compilador compilador, Token token) throws InstrucaoException {
		if ("function".equals(token.getString())) {
			compilador.setContexto(new FuncaoContexto());
			adicionar((Container) compilador.getContexto());
		} else if ("function_native".equals(token.getString())) {
			compilador.setContexto(new FuncaoNativaContexto());
			adicionar((Container) compilador.getContexto());
		} else if ("const".equals(token.getString())) {
			compilador.setContexto(new ConstanteContexto());
			adicionar((Container) compilador.getContexto());
		} else {
			compilador.invalidar(token);
		}
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
			}
		}
	}

	@Override
	public void salvar(PrintWriter pw) {
		for (Container c : componentes) {
			if (c instanceof ConstanteContexto) {
				pw.println();
				c.salvar(pw);
			}
		}
		for (Container c : componentes) {
			if (c instanceof FuncaoContexto || c instanceof FuncaoNativaContexto) {
				pw.println();
				c.salvar(pw);
			}
		}
	}

	@Override
	public String toString() {
		return nome;
	}
}