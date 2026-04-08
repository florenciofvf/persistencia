package instrucao;

import static org.junit.Assert.assertEquals;

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
		assertEquals("[Minha Função]", result.toString());

		result = processador.processar(bibliotecaContexto.getNome(), "main2");
		assertEquals("[minhaFuncao([])]", result.toString());
	}
}