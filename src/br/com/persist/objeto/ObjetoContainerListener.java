package br.com.persist.objeto;

import java.awt.Component;
import java.awt.Dimension;

import br.com.persist.busca_auto.GrupoBuscaAuto;
import br.com.persist.busca_auto.GrupoBuscaAutoApos;
import br.com.persist.link_auto.GrupoLinkAuto;

public interface ObjetoContainerListener {
	public interface BuscaAutomaticaApos {
		public void buscaAutomaticaApos(ObjetoContainer objetoContainer, GrupoBuscaAutoApos grupoApos,
				boolean limparFormulariosRestantes);
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