package br.com.persist.plugins.objeto.internal;

import java.awt.Component;
import java.awt.Dimension;

import br.com.persist.abstrato.DesktopAlinhamento;
import br.com.persist.plugins.objeto.vinculo.Grupo;
import br.com.persist.plugins.objeto.vinculo.Referencia;

public interface InternalListener {
	public interface Vinculo {
		public void pesquisarLink(Referencia ref, String argumentos);

		public void pesquisarLink(Grupo grupo, String argumentos);

		public void pesquisar(Grupo grupo, String argumentos);

		public void pesquisarApos(Grupo grupo);
	}

	public interface ConfigAlturaAutomatica {
		public void configAlturaAutomatica(int total);
	}

	public interface Alinhamento {
		public void alinhar(DesktopAlinhamento opcao);
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