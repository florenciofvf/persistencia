package instrucao;

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
		equals("[FINAL DE SEMANA]", result.toString());

		result = processador.processar(bibliotecaContexto.getNome(), nomeFuncao, bi(-1));
		equals("[DIA INVÁLIDO]", result.toString());

		result = processador.processar(bibliotecaContexto.getNome(), nomeFuncao, bi(6));
		equals("[Meio da semana]", result.toString());

		result = processador.processar(bibliotecaContexto.getNome(), nomeFuncao, bi(7));
		equals("[FINAL DE SEMANA]", result.toString());

		result = processador.processar(bibliotecaContexto.getNome(), nomeFuncao, bi(8));
		equals("[DIA INVÁLIDO]", result.toString());
	}
}