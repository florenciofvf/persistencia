package instrucao;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class FuncionalTest extends AbstratoTeste {
	public FuncionalTest() {
		super("funcional");
	}

	@Test
	public void test() throws IOException, InstrucaoException {
		compilar();

		result = processador.processar(bibliotecaContexto.getNome(), "main");
		equals("[Minha Função]", result.toString());

		result = processador.processar(bibliotecaContexto.getNome(), "main2");
		equals("[minhaFuncao([])]", result.toString());
	}
}