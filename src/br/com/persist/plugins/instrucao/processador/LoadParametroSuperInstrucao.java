package br.com.persist.plugins.instrucao.processador;

import br.com.persist.plugins.instrucao.InstrucaoException;
import br.com.persist.plugins.instrucao.compilador.IdentityContexto;
import br.com.persist.plugins.instrucao.compilador.ParametroContexto;

public class LoadParametroSuperInstrucao extends Instrucao {
	public static final String ERRO_FUNCAO_PARENT_INEXISTENTE = "erro.funcao_parent_inexistente";

	public LoadParametroSuperInstrucao() {
		super(ParametroContexto.LOAD_PARAM_SUPER);
	}

	@Override
	public Instrucao clonar() {
		return new LoadParametroSuperInstrucao();
	}

	@Override
	public void processar(CacheBiblioteca cacheBiblioteca, Biblioteca biblioteca, Funcao funcao,
			PilhaFuncao pilhaFuncao, PilhaOperando pilhaOperando) throws InstrucaoException {
		Funcao principal = getFuncaoPrincipal(funcao.getParent());
		if (principal == null) {
			throw new InstrucaoException(ERRO_FUNCAO_PARENT_INEXISTENTE, funcao.getNome(), biblioteca.getNome());
		}
		Object valor = principal.getValorParametro(parametros);
		if (valor instanceof Funcao) {
			valor = ((Funcao) valor).clonar();
		}
		pilhaOperando.push(valor);
	}

	private Funcao getFuncaoPrincipal(Funcao funcao) {
		while (funcao != null) {
			if (!IdentityContexto.isLamb(funcao.getNome())) {
				return funcao;
			}
			funcao = funcao.getParent();
		}
		return null;
	}
}