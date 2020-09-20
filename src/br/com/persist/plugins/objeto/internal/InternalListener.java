package br.com.persist.plugins.objeto.internal;

import java.awt.Component;
import java.awt.Dimension;

import br.com.persist.plugins.objeto.auto.GrupoBuscaAuto;
import br.com.persist.plugins.objeto.auto.GrupoBuscaAutoApos;
import br.com.persist.plugins.objeto.auto.GrupoLinkAuto;

public interface InternalListener {
	public interface BuscaAutomaticaApos {
		public void buscaAutomaticaApos(InternalContainer objetoContainer, GrupoBuscaAutoApos grupoApos);
	}

	public interface BuscaAutomatica {
		public void buscaAutomatica(GrupoBuscaAuto grupoBusca, String argumentos);
	}

	public interface LinkAutomatico {
		public void linkAutomatico(GrupoLinkAuto grupoLink, String argumento);
	}

	public interface ConfigAlturaAutomatica {
		public void configAlturaAutomatica(int total);
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

	public interface Apelido {
		public void setApelido(String string);

		public String selecionarApelido();

		public String getApelido();
	}
}