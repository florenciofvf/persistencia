package br.com.persist.plugins.objeto.internal;

import java.awt.Component;
import java.awt.Dimension;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import br.com.persist.abstrato.DesktopAlinhamento;
import br.com.persist.componente.SetLista.Coletor;
import br.com.persist.formulario.Formulario;
import br.com.persist.plugins.conexao.Conexao;
import br.com.persist.plugins.objeto.Objeto;
import br.com.persist.plugins.objeto.Relacao;
import br.com.persist.plugins.objeto.vinculo.Pesquisa;
import br.com.persist.plugins.objeto.vinculo.Referencia;
import br.com.persist.plugins.objeto.vinculo.Vinculacao;

public interface InternalListener {
	public interface Vinculo {
		public void adicionarHierarquico(Conexao conexao, Objeto objeto, Map<String, Object> mapaRef);

		public void pesquisar(Conexao conexao, Pesquisa pesquisa, String argumentos);

		public void selecionarCampo(Objeto objeto, Coletor coletor, Component c);

		public void pesquisarLink(List<Referencia> refs, String argumentos);

		public void preencherVinculacao(Vinculacao vinculacao);

		public void salvarVinculacao(Vinculacao vinculacao);

		public void pesquisarApos(Pesquisa pesquisa);

		public List<Objeto> objetosComTabela();

		public boolean validoInvisibilidade();
	}

	public interface ConfiguraAltura {
		public void configurarAltura(int total);
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

		public void setVisible(boolean b);
	}

	public interface Selecao {
		public void selecionar(boolean b);
	}

	public interface Componente {
		public void getFormulario(AtomicReference<Formulario> ref);

		public Component getComponente();
	}

	public interface Dimensao {
		public Dimension getDimensoes();
	}

	public interface Largura {
		public void mesma();
	}
}