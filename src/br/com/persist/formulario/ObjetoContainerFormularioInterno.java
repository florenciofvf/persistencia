package br.com.persist.formulario;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import br.com.persist.banco.Conexao;
import br.com.persist.banco.ConexaoProvedor;
import br.com.persist.container.ObjetoContainer;
import br.com.persist.desktop.Desktop;
import br.com.persist.desktop.Objeto;
import br.com.persist.listener.ObjetoContainerListener;
import br.com.persist.modelo.VariaveisModelo;
import br.com.persist.util.BuscaAuto.Grupo;
import br.com.persist.util.BuscaAuto.Tabela;
import br.com.persist.util.ChaveValor;
import br.com.persist.util.Constantes;
import br.com.persist.util.LinkAuto.Link;
import br.com.persist.util.Preferencias;
import br.com.persist.util.IIni;
import br.com.persist.util.IJanela;

public class ObjetoContainerFormularioInterno extends AbstratoInternalFrame
		implements IJanela, ObjetoContainerListener, IIni {
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

				if (desktop != null && desktop.isAjusteAutomatico() && Preferencias.isAjusteAutomatico()) {
					configAjustes(false);
				}
			}
		});
	}

	public void configAjustes(boolean updateTree) {
		if (desktop != null) {
			desktop.ajusteFormulario();
			desktop.ajusteObjetoFormulario(false, updateTree);
			desktop.ajusteDimension();
		}
	}

	@Override
	public void configAlturaAutomatica(int total) {
		Dimension d = getDimensoes();

		boolean salvar = false;

		ChaveValor cvMinimoRegistros = VariaveisModelo.get(Constantes.ALTURMA_MINIMA_FORMULARIO_REGISTROS);
		ChaveValor cvMinimoDados = VariaveisModelo.get(Constantes.ALTURMA_MINIMA_FORMULARIO_DADOS);
		ChaveValor cvMinimo = VariaveisModelo.get(Constantes.ALTURMA_MINIMA_FORMULARIO);

		if (cvMinimoRegistros == null) {
			cvMinimoRegistros = new ChaveValor(Constantes.ALTURMA_MINIMA_FORMULARIO_REGISTROS, "" + Constantes.DEZ);
			VariaveisModelo.adicionar(cvMinimoRegistros);
			salvar = true;
		}

		if (cvMinimoDados == null) {
			cvMinimoDados = new ChaveValor(Constantes.ALTURMA_MINIMA_FORMULARIO_DADOS, "" + Constantes.SETENTA);
			VariaveisModelo.adicionar(cvMinimoDados);
			salvar = true;
		}

		if (cvMinimo == null) {
			cvMinimo = new ChaveValor(Constantes.ALTURMA_MINIMA_FORMULARIO, "" + Constantes.TRINTA);
			VariaveisModelo.adicionar(cvMinimo);
			salvar = true;
		}

		if (salvar) {
			VariaveisModelo.salvar();
			VariaveisModelo.inicializar();
		}

		int minimoRegistros = cvMinimoRegistros.getInteiro(Constantes.DEZ);
		int minimoDados = cvMinimoDados.getInteiro(Constantes.SETENTA);
		int minimo = cvMinimo.getInteiro(Constantes.TRINTA);

		if (total < 1) {
			setSize(d.width, minimo);
		} else if (total <= minimoRegistros) {
			setSize(d.width, minimoDados + total * 20 + (total == 1 ? 5 : -total));
		} else {
			setSize(d.width, minimoDados + minimoRegistros * 20 + (-minimoRegistros));
		}

		checarDesktop();
		configAjustes(true);
	}

	@Override
	public void buscaAutomatica(Grupo grupo, String argumentos) {
		checarDesktop();

		if (desktop != null) {
			desktop.buscaAutomatica(grupo, argumentos, container);
		}
	}

	@Override
	public void linkAutomatico(Link link, String argumento) {
		checarDesktop();

		if (desktop != null) {
			desktop.linkAutomatico(link, argumento, container);
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

	public boolean ehTabela(Tabela tabela) {
		return getApelido().equalsIgnoreCase(tabela.getApelido())
				&& container.getObjeto().getTabela2().equalsIgnoreCase(tabela.getNome());
	}

	public boolean ehTabela(br.com.persist.util.LinkAuto.Tabela tabela) {
		return getApelido().equalsIgnoreCase(tabela.getApelido())
				&& container.getObjeto().getTabela2().equalsIgnoreCase(tabela.getNome());
	}

	public ObjetoContainer getObjetoContainer() {
		return container;
	}

	public void buscaAutomatica(String campo, String argumentos) {
		container.buscaAutomatica(campo, argumentos);
	}

	public void linkAutomatico(String campo, String argumento) {
		container.linkAutomatico(campo, argumento);
	}

	public void atualizarFormulario() {
		container.atualizarFormulario();
	}

	public String getApelido() {
		if (apelido == null) {
			apelido = "";
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
}