package expressao.retorno;

import java.io.IOException;

import org.junit.Test;

import br.com.persist.plugins.expressao.ExpressaoException;
import br.com.persist.plugins.expressao.compilador.Compilacao;
import br.com.persist.plugins.expressao.processador.Processador;
import expressao.AbstratoTest;

public class RetornoInstrucaoTest extends AbstratoTest {
	@Test
	public void teste1() throws IOException, ExpressaoException {
		compilacao = new Compilacao();
		compilacao.compilar(getFile("retorno", "retorno2"));

		processador = new Processador();
		biblio = "br.com.teste.retorno2";

		result = processador.processar(biblio, "getString");
		equals("[Olá Mundo!]", result.toString());

		result = processador.processar(biblio, "pi");
		equals("[3.1415169]", result.toString());

		result = processador.processar(biblio, "mil");
		equals("[1000]", result.toString());

		result = processador.processar(biblio, "somar", bi(2), bi(2));
		equals("[4]", result.toString());
	}
}