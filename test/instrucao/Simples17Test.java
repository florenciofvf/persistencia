package instrucao;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.instrucao.InstrucaoException;

public class Simples17Test extends AbstratoTeste {
	public Simples17Test() {
		super("simples17");
	}

	@Test
	public void test() throws IOException, InstrucaoException {
		compilar();

		String nomeFuncao = "diaDaSemana";

		result = processador.processar(bibliotecaContexto.getNome(), nomeFuncao, bi(1));
		assertEquals("[FINAL DE SEMANA]", result.toString());

		result = processador.processar(bibliotecaContexto.getNome(), nomeFuncao, bi(-1));
		assertEquals("[DIA INVÁLIDO]", result.toString());

		result = processador.processar(bibliotecaContexto.getNome(), nomeFuncao, bi(6));
		assertEquals("[Meio da semana]", result.toString());

		result = processador.processar(bibliotecaContexto.getNome(), nomeFuncao, bi(7));
		assertEquals("[FINAL DE SEMANA]", result.toString());

		result = processador.processar(bibliotecaContexto.getNome(), nomeFuncao, bi(8));
		assertEquals("[DIA INVÁLIDO]", result.toString());
	}
}