package expressao;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Compilacao;
import br.com.persist.plugins.expressao.processador.Processador;

public class FuncionalTest extends AbstratoTest {

	@Test
	public void teste0() throws IOException, ExpressaoException {
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(getFile("funcional", "simples15"));

		Processador processador = new Processador();

		String biblio = "br.com.teste.simples15";

		List<Object> result;

		result = processador.processar(biblio, "main");
		assertEquals("[7]", result.toString());
	}

}