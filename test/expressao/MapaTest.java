package expressao;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Compilacao;
import br.com.persist.plugins.expressao.processador.Processador;

public class MapaTest extends ExpressaoTest {
	private static final String MAPA = "mapa";

	@Test
	public void teste0() throws IOException, ExpressaoException {
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(getFile(MAPA, "__mapa0"));
	}

	@Test
	public void teste1() throws IOException, ExpressaoException {
		Compilacao compilacao = new Compilacao();
		compilacao.compilar(getFile(MAPA, "__teste1"));

		Processador processador = new Processador();

		String biblio = "br.com.teste.__teste1";

		List<Object> result;

		result = processador.processar(biblio, "main");
		assertEquals("[NOME=florencio vieira filho]", result.toString());
	}
}