package br.com.persist.plugins.objeto.internal;

import java.awt.Component;
import java.awt.Dimension;
import java.util.List;

import br.com.persist.abstrato.DesktopAlinhamento;
import br.com.persist.plugins.objeto.Objeto;
import br.com.persist.plugins.objeto.Relacao;
import br.com.persist.plugins.objeto.vinculo.Pesquisa;
import br.com.persist.plugins.objeto.vinculo.Referencia;

public interface InternalListener {
	public interface Vinculo {
		public void pesquisarLink(List<Referencia> refs, String argumentos);

		public void pesquisar(Pesquisa pesquisa, String argumentos);

		public void pesquisarApos(Pesquisa pesquisa);

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
		public void setVisible(boolean b);
	}

	public interface Selecao {
		public void selecionar(boolean b);
	}

	public interface Componente {
		public Component getComponente();
	}

	public interface Dimensao {
		public Dimension getDimensoes();
	}

	public interface Largura {
		public void mesma();
	}
}