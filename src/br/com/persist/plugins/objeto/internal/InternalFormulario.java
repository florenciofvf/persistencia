package br.com.persist.plugins.objeto.internal;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyVetoException;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.persist.abstrato.AbstratoInternalFrame;
import br.com.persist.abstrato.DesktopAlinhamento;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Util;
import br.com.persist.plugins.conexao.Conexao;
import br.com.persist.plugins.objeto.Desktop;
import br.com.persist.plugins.objeto.Objeto;
import br.com.persist.plugins.objeto.auto.GrupoBuscaAuto;
import br.com.persist.plugins.objeto.auto.GrupoBuscaAutoApos;
import br.com.persist.plugins.objeto.auto.GrupoLinkAuto;
import br.com.persist.plugins.objeto.auto.TabelaBuscaAuto;
import br.com.persist.plugins.objeto.auto.TabelaBuscaAutoApos;
import br.com.persist.plugins.objeto.auto.TabelaLinkAuto;
import br.com.persist.plugins.objeto.vinculo.Grupo;
import br.com.persist.plugins.objeto.vinculo.Referencia;
import br.com.persist.plugins.variaveis.Variavel;
import br.com.persist.plugins.variaveis.VariavelProvedor;

public class InternalFormulario extends AbstratoInternalFrame {
	private static final Logger LOG = Logger.getGlobal();
	private static final long serialVersionUID = 1L;
	private boolean processadoBuscaAutomatica;
	private final InternalContainer container;
	private boolean processadoPesquisa;
	private Desktop desktop;
	private String apelido;

	public InternalFormulario(Conexao padrao, Objeto objeto, Graphics g, boolean buscaAuto) {
		super(objeto.getId());
		container = new InternalContainer(this, padrao, objeto, g, buscaAuto);
		container.setConfigAlturaAutomaticaListener(InternalFormulario.this::configAlturaAutomatica);
		container.setBuscaAutomaticaAposListener(InternalFormulario.this::buscaAutomaticaApos);
		container.setBuscaAutomaticaListener(InternalFormulario.this::buscaAutomatica);
		container.setLinkAutomaticoListener(InternalFormulario.this::linkAutomatico);
		container.setVisibilidadeListener(InternalFormulario.this::setVisible);
		container.setAlinhamentoListener(InternalFormulario.this::alinhar);
		container.setSelecaoListener(InternalFormulario.this::selecionar);
		container.setComponenteListener(InternalFormulario.this::getThis);
		container.setDimensaoListener(InternalFormulario.this::getSize);
		container.setTituloListener(InternalFormulario.this::setTitle);
		container.setLarguraListener(InternalFormulario.this::mesma);
		container.setPesquisaListener(pesquisaListener);
		container.setApelidoListener(apelidoListener);
		setFrameIcon(Icones.VAZIO);
		montarLayout();
		configurar2();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	private void configurar2() {
		addPropertyChangeListener(IS_MAXIMUM_PROPERTY, evt -> {
			checarDesktop();
			if (desktop != null) {
				Object valor = evt.getNewValue();
				desktop.setAjusteAutomatico(Boolean.FALSE.equals(valor));
			}
		});

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				checarDesktop();
				if (desktop != null && desktop.isAjusteAutomatico() && desktop.isAjusteAutomaticoForm()) {
					configAjustes(false);
				}
			}
		});
	}

	public Component getThis() {
		return this;
	}

	public static InternalFormulario criar(Conexao padrao, Objeto objeto, Graphics g, boolean buscaAuto) {
		return new InternalFormulario(padrao, objeto, g, buscaAuto);
	}

	private void checarDesktop() {
		if (desktop == null) {
			Container parent = getParent();
			while (parent != null) {
				if (parent instanceof Desktop) {
					desktop = (Desktop) parent;
					break;
				}
				parent = parent.getParent();
			}
		}
	}

	public void configAjustes(boolean updateTree) {
		if (desktop != null) {
			desktop.getAjuste().empilharFormularios();
			desktop.getAjuste().aproximarObjetoFormulario(true, updateTree);
			desktop.getAjustar().usarFormularios(false);
		}
	}

	public void configAlturaAutomatica(int total) {
		Dimension d = getSize();
		boolean salvar = false;
		Variavel variavelDadosToolbarTableHeader = VariavelProvedor
				.getVariavel(Constantes.ALTURMA_MINIMA_FORMULARIO_DADOS_TOOLBAR_TABLEHEADER);
		Variavel variavelMaximoRegistros = VariavelProvedor
				.getVariavel(Constantes.ALTURMA_MINIMA_FORMULARIO_MAXIMO_DE_REGISTROS);
		Variavel variavelMinimoForm = VariavelProvedor.getVariavel(Constantes.ALTURMA_MINIMA_FORMULARIO_SEM_REGISTROS);
		if (variavelDadosToolbarTableHeader == null) {
			variavelDadosToolbarTableHeader = new Variavel(
					Constantes.ALTURMA_MINIMA_FORMULARIO_DADOS_TOOLBAR_TABLEHEADER,
					Constantes.VAZIO + Constantes.SETENTA);
			VariavelProvedor.adicionar(variavelDadosToolbarTableHeader);
			salvar = true;
		}
		if (variavelMaximoRegistros == null) {
			variavelMaximoRegistros = new Variavel(Constantes.ALTURMA_MINIMA_FORMULARIO_MAXIMO_DE_REGISTROS,
					Constantes.VAZIO + Constantes.DEZ);
			VariavelProvedor.adicionar(variavelMaximoRegistros);
			salvar = true;
		}
		if (variavelMinimoForm == null) {
			variavelMinimoForm = new Variavel(Constantes.ALTURMA_MINIMA_FORMULARIO_SEM_REGISTROS,
					Constantes.VAZIO + Constantes.TRINTA);
			VariavelProvedor.adicionar(variavelMinimoForm);
			salvar = true;
		}
		if (salvar) {
			VariavelProvedor.salvar();
			VariavelProvedor.inicializar();
		}
		int dadosToolbarTableHeader = variavelDadosToolbarTableHeader.getInteiro(Constantes.SETENTA);
		int maximoRegistros = variavelMaximoRegistros.getInteiro(Constantes.DEZ);
		int minimoForm = variavelMinimoForm.getInteiro(Constantes.TRINTA);
		if (total < 1) {
			setSize(d.width, minimoForm);
		} else if (total <= maximoRegistros) {
			setSize(d.width, dadosToolbarTableHeader + total * 20 + (total == 1 ? 5 : -total));
		} else {
			setSize(d.width, dadosToolbarTableHeader + maximoRegistros * 20 + (-maximoRegistros));
		}
		checarDesktop();
		configAjustes(true);
	}

	public void buscaAutomatica(GrupoBuscaAuto grupo, String argumentos) {
		checarDesktop();
		if (desktop != null) {
			desktop.buscaAutomatica(grupo, argumentos, container);
		}
	}

	private transient InternalListener.Pesquisa pesquisaListener = new InternalListener.Pesquisa() {
		@Override
		public void pesquisar(Grupo grupo, String argumentos) {
			checarDesktop();
			if (desktop != null) {
				desktop.pesquisar(grupo, argumentos);
			}
		}

		@Override
		public void pesquisarApos(Grupo grupo) {
			checarDesktop();
			if (desktop != null) {
				desktop.pesquisarApos(grupo);
			}
		}

		@Override
		public void pesquisarLink(Referencia ref, String argumentos) {
			checarDesktop();
			if (desktop != null) {
				desktop.pesquisarLink(ref, argumentos);
			}
		}

		@Override
		public void pesquisarLink(Grupo grupo, String argumentos) {
			checarDesktop();
			if (desktop != null) {
				desktop.pesquisarLink(grupo, argumentos);
			}
		}
	};

	public void linkAutomatico(GrupoLinkAuto link, String argumento) {
		checarDesktop();
		if (desktop != null) {
			desktop.linkAutomatico(link, argumento, container);
		}
	}

	public void buscaAutomaticaApos(InternalContainer objetoContainer, GrupoBuscaAutoApos grupoApos) {
		checarDesktop();
		if (desktop != null) {
			desktop.buscaAutomaticaApos(objetoContainer, grupoApos);
		}
	}

	public void alinhar(DesktopAlinhamento opcao) {
		checarDesktop();
		if (desktop != null) {
			desktop.getAlinhamento().alinhar(this, opcao);
		}
	}

	public void mesma() {
		checarDesktop();
		if (desktop != null) {
			desktop.getLarguras().mesma(this);
		}
	}

	public void selecionar(boolean b) {
		try {
			setSelected(b);
		} catch (PropertyVetoException e) {
			LOG.log(Level.FINEST, "{0}", b);
		}
	}

	public boolean ehTabela(TabelaBuscaAuto tabela) {
		return apelidoListener.getApelido().equalsIgnoreCase(tabela.getApelido())
				&& container.getObjeto().getTabela2().equalsIgnoreCase(tabela.getNome());
	}

	public boolean ehReferencia(Referencia referencia) {
		return referencia.refIgual(container.getObjeto());
	}

	public boolean ehTabela(TabelaBuscaAutoApos tabela) {
		return apelidoListener.getApelido().equalsIgnoreCase(tabela.getApelido())
				&& container.getObjeto().getTabela2().equalsIgnoreCase(tabela.getNome());
	}

	public boolean ehTabela(TabelaLinkAuto tabela) {
		return apelidoListener.getApelido().equalsIgnoreCase(tabela.getApelido())
				&& container.getObjeto().getTabela2().equalsIgnoreCase(tabela.getNome());
	}

	public boolean ehTabela(Objeto objeto) {
		return container.getObjeto().getTabela2().equalsIgnoreCase(objeto.getTabela2());
	}

	public boolean ehTabela(InternalConfig config) {
		return config.isChecarApelido()
				? apelidoListener.getApelido().equalsIgnoreCase(config.getApelido())
						&& container.getObjeto().getTabela2().equalsIgnoreCase(config.getTabela())
				: container.getObjeto().getTabela2().equalsIgnoreCase(config.getTabela());
	}

	public InternalContainer getInternalContainer() {
		return container;
	}

	public void buscaAutomatica(String campo, String argumentos) {
		container.buscaAutomatica(campo, argumentos);
	}

	public void pesquisar(String campo, String argumentos) {
		container.pesquisar(campo, argumentos);
	}

	public void pesquisarLink(String campo, String argumentos) {
		container.pesquisarLink(campo, argumentos);
	}

	public void buscaAutomaticaApos() {
		container.buscaAutomaticaApos();
	}

	public void pesquisarApos() {
		container.pesquisarApos();
	}

	public void linkAutomatico(String campo, String argumento) {
		container.linkAutomatico(campo, argumento);
	}

	public void atualizarFormulario() {
		container.atualizarFormulario();
	}

	public void limpar2() {
		container.limpar2();
	}

	public boolean ehObjeto(Objeto objeto) {
		if (objeto == null) {
			return false;
		}
		return container.getObjeto() == objeto || container.getObjeto().getId().equals(objeto.getId());
	}

	public String getComplementoChaves() {
		return container.getComplementoChaves();
	}

	private transient InternalListener.Apelido apelidoListener = new InternalListener.Apelido() {
		@Override
		public void setApelido(String string) {
			apelido = string;
		}

		@Override
		public String selecionarApelido() {
			Object resp = Util.getValorInputDialog(InternalFormulario.this, "label.apelido", getApelido(),
					getApelido());
			if (resp != null) {
				return resp.toString().trim();
			}
			return null;
		}

		@Override
		public String getApelido() {
			if (apelido == null) {
				apelido = Constantes.VAZIO;
			}
			return apelido.trim();
		}
	};

	public InternalListener.Apelido getApelidoListener() {
		return apelidoListener;
	}

	public void selecionarConexao(Conexao conexao) {
		container.selecionarConexao(conexao);
	}

	public void aproximarObjetoAoFormulario(int deltaX, int deltaY) {
		Objeto objeto = container.getObjeto();
		if (objeto != null) {
			objeto.setX(getX() - deltaX);
			objeto.setY(getY() - deltaY);
		}
	}

	public void aproximarFormularioAoObjeto(int deltaX, int deltaY) {
		Objeto objeto = container.getObjeto();
		if (objeto != null) {
			setLocation(objeto.getX() + deltaX, objeto.getY() + deltaY);
		}
	}

	public void aplicarConfigArquivo(InternalConfig config) {
		if (config != null && ehTabela(config)) {
			container.aplicarConfigArquivo(config);
		}
	}

	public boolean isProcessadoBuscaAutomatica() {
		return processadoBuscaAutomatica;
	}

	public void setProcessadoBuscaAutomatica(boolean processadoBuscaAutomatica) {
		this.processadoBuscaAutomatica = processadoBuscaAutomatica;
	}

	public boolean isProcessadoPesquisa() {
		return processadoPesquisa;
	}

	public void setProcessadoPesquisa(boolean processadoPesquisa) {
		this.processadoPesquisa = processadoPesquisa;
	}

	@Override
	public void executarAoAbrirFormulario() {
		container.formularioVisivel();
	}
}