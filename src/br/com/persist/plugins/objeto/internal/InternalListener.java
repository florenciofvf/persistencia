package br.com.persist.plugins.objeto.internal;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import br.com.persist.abstrato.DesktopAlinhamento;
import br.com.persist.assistencia.AssistenciaException;
import br.com.persist.componente.SetLista.Coletor;
import br.com.persist.formulario.Formulario;
import br.com.persist.marca.XMLException;
import br.com.persist.plugins.conexao.Conexao;
import br.com.persist.plugins.metadado.MetadadoException;
import br.com.persist.plugins.objeto.Objeto;
import br.com.persist.plugins.objeto.Objeto.Estado;
import br.com.persist.plugins.objeto.ObjetoException;
import br.com.persist.plugins.objeto.Relacao;
import br.com.persist.plugins.objeto.vinculo.Pesquisa;
import br.com.persist.plugins.objeto.vinculo.Referencia;
import br.com.persist.plugins.objeto.vinculo.Vinculacao;

public interface InternalListener {
	public interface Vinculo {
		public void pesquisar(Conexao conexao, Pesquisa pesquisa, Argumento argumento, boolean soTotal, boolean emForms)
				throws ObjetoException, AssistenciaException;

		public void selecionarCampo(Objeto objeto, Coletor coletor, Component c, String selecionarItem);

		public void adicionarHierarquico(Conexao conexao, Objeto objeto, Map<String, Object> mapaRef)
				throws MetadadoException, ObjetoException, AssistenciaException;

		public void adicionarHierarquicoInvisivelAbaixo(Conexao conexao, Objeto objeto);

		public void adicionarHierarquicoInvisivelAcima(Conexao conexao, Objeto objeto);

		public void adicionarHierarquicoAvulsoAbaixo(Conexao conexao, Objeto objeto) throws AssistenciaException;

		public void adicionarHierarquicoAvulsoAcima(Conexao conexao, Objeto objeto) throws AssistenciaException;

		public void preencherVinculacao(Vinculacao vinculacao) throws XMLException;

		public void getMetadado(AtomicReference<Object> ref, Objeto objeto);

		public void pesquisarLink(List<Referencia> refs, String argumentos);

		public void listarNomeBiblio(List<String> lista, Component c);

		public void pesquisarDestacar(Pesquisa pesquisa, boolean b);

		public void pesquisarApos(Objeto fonte, Pesquisa pesquisa);

		public void salvarVinculacao(Vinculacao vinculacao);

		public List<Objeto> objetosComTabela(Estado estado);

		public void atualizarComplemento(Objeto objeto);

		public String getStringArquivoVinculado();

		public Objeto getObjeto(Referencia ref);

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

		public void setXY(int x, int y);

		public Point getPosicao();
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
		public void setLargAltura(int l, int a);

		public Dimension getDimensoes();
	}

	public interface Largura {
		public void mesma();
	}
}