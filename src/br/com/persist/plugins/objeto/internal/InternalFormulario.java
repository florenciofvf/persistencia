package br.com.persist.plugins.objeto.internal;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyVetoException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.persist.abstrato.AbstratoInternalFrame;
import br.com.persist.abstrato.DesktopAlinhamento;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.plugins.conexao.Conexao;
import br.com.persist.plugins.objeto.Desktop;
import br.com.persist.plugins.objeto.Objeto;
import br.com.persist.plugins.objeto.vinculo.Pesquisa;
import br.com.persist.plugins.objeto.vinculo.Referencia;
import br.com.persist.plugins.variaveis.Variavel;
import br.com.persist.plugins.variaveis.VariavelProvedor;

public class InternalFormulario extends AbstratoInternalFrame {
	private static final Logger LOG = Logger.getGlobal();
	private static final long serialVersionUID = 1L;
	private final InternalContainer container;
	private boolean processadoPesquisa;
	private Desktop desktop;

	public InternalFormulario(Conexao padrao, Objeto objeto, Graphics g, boolean buscaAuto) {
		super(objeto.getId());
		container = new InternalContainer(this, padrao, objeto, g, buscaAuto);
		container.setConfiguraAlturaListener(InternalFormulario.this::configurarAltura);
		container.setVisibilidadeListener(InternalFormulario.this::setVisible);
		container.setAlinhamentoListener(InternalFormulario.this::alinhar);
		container.setSelecaoListener(InternalFormulario.this::selecionar);
		container.setComponenteListener(InternalFormulario.this::getThis);
		container.setDimensaoListener(InternalFormulario.this::getSize);
		container.setTituloListener(InternalFormulario.this::setTitle);
		container.setLarguraListener(InternalFormulario.this::mesma);
		container.setVinculoListener(vinculoListener);
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
					configurarAjustes(false);
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

	public void configurarAjustes(boolean updateTree) {
		if (desktop != null) {
			desktop.getAjuste().empilharFormularios();
			desktop.getAjuste().aproximarObjetoFormulario(true, updateTree);
			desktop.getAjustar().usarFormularios(false);
		}
	}

	public void configurarAltura(int total) {
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
		configurarAjustes(true);
	}

	private transient InternalListener.Vinculo vinculoListener = new InternalListener.Vinculo() {
		@Override
		public void pesquisar(Pesquisa pesquisa, String argumentos) {
			checarDesktop();
			if (desktop != null) {
				desktop.pesquisar(pesquisa, argumentos);
			}
		}

		@Override
		public void pesquisarApos(Pesquisa pesquisa) {
			checarDesktop();
			if (desktop != null) {
				desktop.pesquisarApos(pesquisa);
			}
		}

		@Override
		public void pesquisarLink(List<Referencia> refs, String argumentos) {
			checarDesktop();
			if (desktop != null) {
				desktop.pesquisarLink(refs, argumentos);
			}
		}
	};

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

	public boolean ehReferencia(Referencia referencia) {
		return referencia.igual(container.getObjeto());
	}

	public boolean ehTabela(Objeto objeto) {
		return container.getObjeto().getTabela2().equalsIgnoreCase(objeto.getTabela2());
	}

	public boolean ehTabela(InternalConfig config) {
		return container.getObjeto().getTabela2().equalsIgnoreCase(config.getTabela());
	}

	public InternalContainer getInternalContainer() {
		return container;
	}

	public void pesquisar(Referencia referencia, String argumentos) {
		container.pesquisar(referencia, argumentos);
	}

	public void pesquisarLink(Referencia referencia, String argumentos) {
		container.pesquisarLink(referencia, argumentos);
	}

	public void pesquisarApos() {
		container.pesquisarApos();
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

	public void aplicarConfig(InternalConfig config) {
		if (config != null && ehTabela(config)) {
			container.aplicarConfig(config);
		}
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