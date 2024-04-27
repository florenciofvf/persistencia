package br.com.persist.plugins.objeto.internal;

import java.awt.Component;
import java.awt.Dimension;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import br.com.persist.abstrato.DesktopAlinhamento;
import br.com.persist.componente.SetLista.Coletor;
import br.com.persist.formulario.Formulario;
import br.com.persist.marca.XMLException;
import br.com.persist.plugins.conexao.Conexao;
import br.com.persist.plugins.objeto.Objeto;
import br.com.persist.plugins.objeto.Relacao;
import br.com.persist.plugins.objeto.vinculo.Pesquisa;
import br.com.persist.plugins.objeto.vinculo.Referencia;
import br.com.persist.plugins.objeto.vinculo.Vinculacao;

public interface InternalListener {
	public interface Vinculo {
		public void pesquisar(Conexao conexao, Pesquisa pesquisa, Argumento argumento, boolean soTotal,
				boolean emForms);

		public void selecionarCampo(Objeto objeto, Coletor coletor, Component c, String selecionarItem);

		public void adicionarHierarquico(Conexao conexao, Objeto objeto, Map<String, Object> mapaRef);

		public void adicionarHierarquicoInvisivelAbaixo(Conexao conexao, Objeto objeto);

		public void adicionarHierarquicoInvisivelAcima(Conexao conexao, Objeto objeto);

		public void adicionarHierarquicoAvulsoAbaixo(Conexao conexao, Objeto objeto);

		public void adicionarHierarquicoAvulsoAcima(Conexao conexao, Objeto objeto);

		public void preencherVinculacao(Vinculacao vinculacao) throws XMLException;

		public void getMetadado(AtomicReference<Object> ref, Objeto objeto);

		public void pesquisarLink(List<Referencia> refs, String argumentos);

		public void pesquisarApos(Objeto fonte, Pesquisa pesquisa);

		public void salvarVinculacao(Vinculacao vinculacao);

		public void atualizarComplemento(Objeto objeto);

		public Objeto getObjeto(Referencia ref);

		public List<Objeto> objetosComTabela();

		public boolean validoInvisibilidade();
	}

	public enum ConfiguraAlturaSemRegistros {
		SCROLL_NORTE, SCROLL_SUL
	}

	public interface ConfiguraAltura {
		public void configurarAltura(ConfiguraAlturaSemRegistros semRegistros, boolean update);

		public int getAlturaAtual();
	}

	public interface Alinhamento {
		public void alinhar(DesktopAlinhamento opcao);
	}

	public interface RelacaoObjeto {
		public List<Relacao> listar(Objeto objeto);
	}

	public interface Titulo {
		public void setTitulo(String titulo);
	}

	public interface Visibilidade {
		public void checarLargura(InternalContainer invocador);

		public void limparOutros(InternalContainer invocador);

		public void checarRedimensionamento();

		public void setVisible(boolean b);
	}

	public interface Selecao {
		public void visibilidade(boolean b);

		public void corFundo(boolean b);
	}

	public interface Componente {
		public Formulario getFormulario();

		public Component getComponente();
	}

	public interface Dimensao {
		public Dimension getDimensoes();
	}

	public interface Largura {
		public void mesma();
	}
}