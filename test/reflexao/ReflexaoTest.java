package reflexao;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import br.com.persist.assistencia.ReflexaoException;
import br.com.persist.assistencia.ReflexaoUtil;

public class ReflexaoTest {
	@Test(expected = ReflexaoException.class)
	public void classeInexistente() throws ReflexaoException {
		ReflexaoUtil.processar("reflexao.PessoaX", "", "");
	}

	@Test(expected = ReflexaoException.class)
	public void construtorInexistente() throws ReflexaoException {
		ReflexaoUtil.processar("reflexao.Pessoa", "java.lang.Long@@@10", "");
	}

	@Test(expected = ReflexaoException.class)
	public void metodoInexistente() throws ReflexaoException {
		StringBuilder builder = new StringBuilder();
		builder.append("setMesParaFeriasX");
		builder.append("%%%");
		builder.append("reflexao.Mes@@@FEVEREIRO");
		ReflexaoUtil.processar("reflexao.Pessoa", "java.lang.String@@@Francisco", builder.toString());
	}

	@Test(expected = ReflexaoException.class)
	public void valorInvalido() throws ReflexaoException {
		StringBuilder builder = new StringBuilder();
		builder.append("setMesParaFerias");
		builder.append("%%%");
		builder.append("java.lang.Double@@@FEVEREIRO");
		ReflexaoUtil.processar("reflexao.Pessoa", "java.lang.String@@@Francisco", builder.toString());
	}

	@Test
	public void criandoEConfigurando() throws ReflexaoException {
		StringBuilder builder = new StringBuilder();
		builder.append("setMesParaFerias");
		builder.append("%%%");
		builder.append("reflexao.Mes@@@FEVEREIRO");
		Object obj = ReflexaoUtil.processar("reflexao.Pessoa", "java.lang.String@@@Francisco", builder.toString());
		System.out.println(obj);
		assertNotNull(obj);
	}

	@Test
	public void semMes() throws ReflexaoException {
		StringBuilder builder = new StringBuilder();
		builder.append("setMesParaFerias");
		builder.append("%%%");
		builder.append("reflexao.Mes@@@NULL");
		Object obj = ReflexaoUtil.processar("reflexao.Pessoa", "java.lang.String@@@Francisco", builder.toString());
		assertNotNull(obj);
	}

	@Test
	public void comValor() throws ReflexaoException {
		StringBuilder builder = new StringBuilder();
		builder.append("setValor");
		builder.append("%%%");
		builder.append("java.lang.Double@@@5.01");
		Object obj = ReflexaoUtil.processar("reflexao.Pessoa", "java.lang.String@@@Francisco", builder.toString());
		assertNotNull(obj);
	}

	@Test
	public void semValor() throws ReflexaoException {
		StringBuilder builder = new StringBuilder();
		builder.append("setValor");
		builder.append("%%%");
		builder.append("java.lang.Double@@@NULL");
		Object obj = ReflexaoUtil.processar("reflexao.Pessoa", "java.lang.String@@@Francisco", builder.toString());
		assertNotNull(obj);
	}

	@Test
	public void comData() throws ReflexaoException {
		StringBuilder builder = new StringBuilder();
		builder.append("setAgendamento");
		builder.append("%%%");
		builder.append("java.util.Date@@@25/02/1978");
		Object obj = ReflexaoUtil.processar("reflexao.Pessoa", "java.lang.String@@@Francisco", builder.toString());
		assertNotNull(obj);
	}

	@Test
	public void comDataEHora() throws ReflexaoException {
		StringBuilder builder = new StringBuilder();
		builder.append("setAgendamento");
		builder.append("%%%");
		builder.append("java.util.Date@@@25/02/1978 10:13:01");
		Object obj = ReflexaoUtil.processar("reflexao.Pessoa", "java.lang.String@@@Francisco", builder.toString());
		assertNotNull(obj);
	}

	@Test(expected = ReflexaoException.class)
	public void comExcecao() throws ReflexaoException {
		StringBuilder builder = new StringBuilder();
		builder.append("setAgendamento");
		builder.append("%%%");
		builder.append("java.util.Date@@@ABC");
		ReflexaoUtil.processar("reflexao.Pessoa", "java.lang.String@@@Francisco", builder.toString());
	}

	@Test
	public void semData() throws ReflexaoException {
		StringBuilder builder = new StringBuilder();
		builder.append("setAgendamento");
		builder.append("%%%");
		builder.append("java.util.Date@@@null");
		Object obj = ReflexaoUtil.processar("reflexao.Pessoa", "java.lang.String@@@Francisco", builder.toString());
		assertNotNull(obj);
	}
}