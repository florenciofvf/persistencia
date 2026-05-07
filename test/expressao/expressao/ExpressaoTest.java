package expressao.expressao;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Compilacao;
import br.com.persist.plugins.expressao.processador.Processador;
import expressao.AbstratoTest;

public class ExpressaoTest extends AbstratoTest {
	@Test
	public void teste3() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile("expressao", "__simples3"));

		Processador processador = new Processador();

		String biblio = "br.com.teste.__simples3";

		result = processador.processar(biblio, "mesmo", bi(30));
		equals("[30]", result.toString());

		result = processador.processar(biblio, "mesmoNegado", bi(30));
		equals("[-30]", result.toString());

		result = processador.processar(biblio, "mesmoNegado2", bi(30));
		equals("[-30]", result.toString());
	}

	@Test
	public void teste4() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile("expressao", "__simples4"));

		processador = new Processador();

		String biblio = "br.com.teste.__simples4";

		result = processador.processar(biblio, "dobrar", bi(30));
		equals("[60]", result.toString());

		result = processador.processar(biblio, "quadrado", bi(30));
		equals("[900]", result.toString());

		result = processador.processar(biblio, "somar", bi(30), bi(3));
		equals("[-33]", result.toString());

		result = processador.processar(biblio, "area", bi(30));
		equals("[2827.4057100]", result.toString());
	}

	@Test
	public void teste5() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile("expressao", "__simples5"));

		processador = new Processador();

		String biblio = "br.com.teste.__simples5";

		result = processador.processar(biblio, "teste");
		equals("[2]", result.toString());

		result = processador.processar(biblio, "dividir", bi(30), bi(3));
		equals("[0]", result.toString());
	}

	@Test
	public void teste6() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile("expressao", "__simples6"));

		processador = new Processador();

		String biblio = "br.com.teste.__simples6";

		result = processador.processar(biblio, "expressao_01", bi(3), bi(4), bi(5));
		equals("[23]", result.toString());

		result = processador.processar(biblio, "expressao_02", bi(3), bi(4), bi(5));
		equals("[35]", result.toString());

		result = processador.processar(biblio, "expressao_03", bi(3), bi(4), bi(5));
		equals("[-35]", result.toString());
	}

	@Test
	public void teste7() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile("expressao", "__simples7"));

		processador = new Processador();

		String biblio = "br.com.teste.__simples7";

		result = processador.processar(biblio, "expressao");
		equals("[14]", result.toString());
	}

	@Test
	public void teste8() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile("expressao", "__simples8"));

		processador = new Processador();

		String biblio = "br.com.teste.__simples8";

		result = processador.processar(biblio, "igual", bi(3), bi(3));
		equals("[1]", result.toString());

		result = processador.processar(biblio, "diff", bi(3), bi(3));
		equals("[0]", result.toString());
	}

	@Test
	public void teste9() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile("expressao", "__simples9"));

		processador = new Processador();

		String biblio = "br.com.teste.__simples9";

		result = processador.processar(biblio, "main");
		equals("[1000]", result.toString());
	}
}