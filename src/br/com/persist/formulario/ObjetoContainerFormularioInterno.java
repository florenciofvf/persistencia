package br.com.persist.formulario;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyVetoException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import br.com.persist.banco.Conexao;
import br.com.persist.banco.ConexaoProvedor;
import br.com.persist.busca_apos.GrupoBuscaAutoApos;
import br.com.persist.busca_apos.TabelaBuscaAutoApos;
import br.com.persist.busca_auto.GrupoBuscaAuto;
import br.com.persist.busca_auto.TabelaBuscaAuto;
import br.com.persist.container.ObjetoContainer;
import br.com.persist.desktop.Desktop;
import br.com.persist.desktop.Objeto;
import br.com.persist.link_auto.GrupoLinkAuto;
import br.com.persist.link_auto.TabelaLinkAuto;
import br.com.persist.listener.ObjetoContainerListener;
import br.com.persist.util.ChaveValor;
import br.com.persist.util.ConfigArquivo;
import br.com.persist.util.Constantes;
import br.com.persist.util.IIni;
import br.com.persist.util.IJanela;
import br.com.persist.variaveis.VariaveisModelo;

public class ObjetoContainerFormularioInterno extends AbstratoInternalFrame
		implements IJanela, ObjetoContainerListener, IIni {
	private static final Logger LOG = Logger.getGlobal();
	private static final long serialVersionUID = 1L;
	private final ObjetoContainer container;
	private Desktop desktop;
	private String apelido;

	public ObjetoContainerFormularioInterno(ConexaoProvedor provedor, Conexao padrao, Objeto objeto, Graphics g,
			boolean buscaAuto) {
		super(objeto.getId());
		container = new ObjetoContainer(this, provedor, padrao, objeto, this, g, buscaAuto);
		montarLayout();
		configurar();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	@Override
	public void fechar() {
		dispose();
	}

	@Override
	public void ini(Graphics graphics) {
		container.ini(graphics);
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

	private void configurar() {
		addInternalFrameListener(new InternalFrameAdapter() {
			@Override
			public void internalFrameOpened(InternalFrameEvent e) {
				container.ini(getGraphics());
			}
		});

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

	public void configAjustes(boolean updateTree) {
		if (desktop != null) {
			desktop.getAjuste().ajusteFormulario();
			desktop.getAjuste().ajusteObjetoFormulario(false, updateTree);
			desktop.getAjuste().ajusteDesktopUsandoForms();
		}
	}

	@Override
	public void configAlturaAutomatica(int total) {
		Dimension d = getDimensoes();

		boolean salvar = false;

		ChaveValor cvDadosToolbarTableHeader = VariaveisModelo
				.get(Constantes.ALTURMA_MINIMA_FORMULARIO_DADOS_TOOLBAR_TABLEHEADER);
		ChaveValor cvMaximoRegistros = VariaveisModelo.get(Constantes.ALTURMA_MINIMA_FORMULARIO_MAXIMO_DE_REGISTROS);
		ChaveValor cvMinimoForm = VariaveisModelo.get(Constantes.ALTURMA_MINIMA_FORMULARIO_SEM_REGISTROS);

		if (cvDadosToolbarTableHeader == null) {
			cvDadosToolbarTableHeader = new ChaveValor(Constantes.ALTURMA_MINIMA_FORMULARIO_DADOS_TOOLBAR_TABLEHEADER,
					Constantes.VAZIO + Constantes.SETENTA);
			VariaveisModelo.adicionar(cvDadosToolbarTableHeader);
			salvar = true;
		}

		if (cvMaximoRegistros == null) {
			cvMaximoRegistros = new ChaveValor(Constantes.ALTURMA_MINIMA_FORMULARIO_MAXIMO_DE_REGISTROS,
					Constantes.VAZIO + Constantes.DEZ);
			VariaveisModelo.adicionar(cvMaximoRegistros);
			salvar = true;
		}

		if (cvMinimoForm == null) {
			cvMinimoForm = new ChaveValor(Constantes.ALTURMA_MINIMA_FORMULARIO_SEM_REGISTROS,
					Constantes.VAZIO + Constantes.TRINTA);
			VariaveisModelo.adicionar(cvMinimoForm);
			salvar = true;
		}

		if (salvar) {
			VariaveisModelo.salvar();
			VariaveisModelo.inicializar();
		}

		int dadosToolbarTableHeader = cvDadosToolbarTableHeader.getInteiro(Constantes.SETENTA);
		int maximoRegistros = cvMaximoRegistros.getInteiro(Constantes.DEZ);
		int minimoForm = cvMinimoForm.getInteiro(Constantes.TRINTA);

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

	@Override
	public void buscaAutomatica(GrupoBuscaAuto grupo, String argumentos) {
		checarDesktop();

		if (desktop != null) {
			desktop.buscaAutomatica(grupo, argumentos, container);
		}
	}

	@Override
	public void linkAutomatico(GrupoLinkAuto link, String argumento) {
		checarDesktop();

		if (desktop != null) {
			desktop.linkAutomatico(link, argumento, container);
		}
	}

	@Override
	public void buscaAutomaticaApos(GrupoBuscaAutoApos grupoApos) {
		checarDesktop();

		if (desktop != null) {
			desktop.buscaAutomaticaApos(grupoApos, container);
		}
	}

	@Override
	public Dimension getDimensoes() {
		return getSize();
	}

	@Override
	public void setTitulo(String titulo) {
		setTitle(titulo);
	}

	@Override
	public void selecionar(boolean b) {
		try {
			setSelected(b);
		} catch (PropertyVetoException e) {
			LOG.log(Level.FINEST, "{0}", b);
		}
	}

	public boolean ehTabela(TabelaBuscaAuto tabela) {
		return getApelido().equalsIgnoreCase(tabela.getApelido())
				&& container.getObjeto().getTabela2().equalsIgnoreCase(tabela.getNome());
	}

	public boolean ehTabela(TabelaBuscaAutoApos tabela) {
		return getApelido().equalsIgnoreCase(tabela.getApelido())
				&& container.getObjeto().getTabela2().equalsIgnoreCase(tabela.getNome());
	}

	public boolean ehTabela(TabelaLinkAuto tabela) {
		return getApelido().equalsIgnoreCase(tabela.getApelido())
				&& container.getObjeto().getTabela2().equalsIgnoreCase(tabela.getNome());
	}

	public boolean ehTabela(Objeto objeto) {
		return container.getObjeto().getTabela2().equalsIgnoreCase(objeto.getTabela2());
	}

	public boolean ehTabela(ConfigArquivo config) {
		return config.isChecarApelido()
				? getApelido().equalsIgnoreCase(config.getApelido())
						&& container.getObjeto().getTabela2().equalsIgnoreCase(config.getTabela())
				: container.getObjeto().getTabela2().equalsIgnoreCase(config.getTabela());
	}

	public ObjetoContainer getObjetoContainer() {
		return container;
	}

	public void buscaAutomatica(String campo, String argumentos) {
		container.buscaAutomatica(campo, argumentos);
	}

	public void buscaAutomaticaApos() {
		container.buscaAutomaticaApos();
	}

	public void linkAutomatico(String campo, String argumento) {
		container.linkAutomatico(campo, argumento);
	}

	public void atualizarFormulario() {
		container.atualizarFormulario();
	}

	public void limpar() {
		container.limpar();
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

	public String getApelido() {
		if (apelido == null) {
			apelido = Constantes.VAZIO;
		}

		return apelido;
	}

	public void setApelido(String apelido) {
		this.apelido = apelido;
	}

	public void selecionarConexao(Conexao conexao) {
		container.selecionarConexao(conexao);
	}

	public void ajusteObjetoFormulario(boolean aoObjeto, int deltaX, int deltaY) {
		Objeto objeto = container.getObjeto();

		if (objeto != null) {
			if (aoObjeto) {
				setLocation(objeto.getX() + deltaX, objeto.getY() + deltaY);
			} else {
				objeto.setX(getX() - deltaX);
				objeto.setY(getY() - deltaY);
			}
		}
	}

	public void aplicarConfigArquivo(ConfigArquivo config) {
		if (config != null && ehTabela(config)) {
			container.aplicarConfigArquivo(config);
		}
	}
}