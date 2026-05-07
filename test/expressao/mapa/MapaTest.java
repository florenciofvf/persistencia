package expressao.mapa;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Compilacao;
import br.com.persist.plugins.expressao.processador.Processador;
import expressao.AbstratoTest;

public class MapaTest extends AbstratoTest {
	private static final String MAPA = "mapa";

	@Test
	public void teste0() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(MAPA, "__mapa0"));
	}

	@Test
	public void teste1() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(MAPA, "__teste1"));

		processador = new Processador();

		String biblio = "br.com.teste.__teste1";

		result = processador.processar(biblio, "main");
		equals("[NOME=florencio vieira filho]", result.toString());
	}

	@Test
	public void mapa1() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(MAPA, "__mapa1"));

		processador = new Processador();

		String biblio = "br.com.teste.__mapa1";

		result = processador.processar(biblio, "teste1");
		equals("[{}]", result.toString());
	}
}