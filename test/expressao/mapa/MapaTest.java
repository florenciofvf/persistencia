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
	public void teste1() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(MAPA, "mapa"));

		processador = new Processador();
		biblio = "br.com.teste.mapa";

		result = processador.processar(biblio, "main");
		equals("[NOME=florencio vieira filho]", result.toString());
	}

	@Test
	public void teste2() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(MAPA, "mapa2"));
	}

	@Test
	public void teste3() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(MAPA, "mapa3"));

		processador = new Processador();
		biblio = "br.com.teste.mapa3";

		result = processador.processar(biblio, "teste1");
		equals("[{}]", result.toString());
	}

	@Test
	public void teste4() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(MAPA, "mapa4"));

		processador = new Processador();
		biblio = "br.com.teste.mapa4";

		result = processador.processar(biblio, "main");
		equals("[Francisco da Silva]", result.toString());
	}

	@Test
	public void teste5() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(MAPA, "mapa5"));

		processador = new Processador();
		biblio = "br.com.teste.mapa5";

		result = processador.processar(biblio, "main");
		equals("[Francisco da Silva]", result.toString());
	}

	@Test
	public void teste6() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(MAPA, "mapa6"));

		processador = new Processador();
		biblio = "br.com.teste.mapa6";

		result = processador.processar(biblio, "main");
		equals("[Maria da Silva]", result.toString());
	}

	@Test
	public void teste7() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile(MAPA, "mapa7"));

		processador = new Processador();
		biblio = "br.com.teste.mapa7";

		result = processador.processar(biblio, "main");
		equals("[Escola]", result.toString());
	}
}