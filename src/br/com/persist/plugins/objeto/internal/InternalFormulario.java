package br.com.persist.plugins.objeto.internal;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicInternalFrameUI;

import br.com.persist.abstrato.AbstratoInternalFrame;
import br.com.persist.abstrato.DesktopAlinhamento;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.componente.SetLista.Coletor;
import br.com.persist.fichario.Fichario;
import br.com.persist.formulario.Formulario;
import br.com.persist.plugins.conexao.Conexao;
import br.com.persist.plugins.objeto.Desktop;
import br.com.persist.plugins.objeto.Objeto;
import br.com.persist.plugins.objeto.ObjetoConstantes;
import br.com.persist.plugins.objeto.ObjetoSuperficie;
import br.com.persist.plugins.objeto.Relacao;
import br.com.persist.plugins.objeto.vinculo.Pesquisa;
import br.com.persist.plugins.objeto.vinculo.Referencia;
import br.com.persist.plugins.objeto.vinculo.Vinculacao;
import br.com.persist.plugins.variaveis.Variavel;
import br.com.persist.plugins.variaveis.VariavelProvedor;

public class InternalFormulario extends AbstratoInternalFrame {
	private static final Logger LOG = Logger.getGlobal();
	private static final long serialVersionUID = 1L;
	private final InternalContainer container;
	private boolean processadoPesquisa;
	private Desktop desktop;

	public InternalFormulario(Conexao padrao, Objeto objeto, boolean buscaAuto) {
		super(objeto.getId());
		container = new InternalContainer(this, padrao, objeto, buscaAuto);
		container.setConfiguraAlturaListener(InternalFormulario.this::configurarAltura);
		container.setRelacaoObjetoListener(InternalFormulario.this::listarRelacoes);
		container.setAlinhamentoListener(InternalFormulario.this::alinhar);
		container.setSelecaoListener(InternalFormulario.this::selecionar);
		container.setDimensaoListener(InternalFormulario.this::getSize);
		container.setTituloListener(InternalFormulario.this::setTitle);
		container.setLarguraListener(InternalFormulario.this::mesma);
		container.setVisibilidadeListener(visibilidadeListener);
		container.setComponenteListener(componenteListener);
		container.setVinculoListener(vinculoListener);
		setFrameIcon(Icones.VAZIO);
		montarLayout();
		configurar2();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	private void configurar2() {
		addPropertyChangeListener(IS_MAXIMUM_PROPERTY, evt -> checarMaximizado(evt.getNewValue()));
		addMouseWheelListener(e -> checarAltura(e.getY(), e.getWheelRotation()));
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				checarRedimensionamento();
			}
		});
	}

	private void checarMaximizado(Object valor) {
		checarDesktop();
		if (desktop != null) {
			desktop.setAjusteAutomatico(Boolean.FALSE.equals(valor));
		}
	}

	private void checarRedimensionamento() {
		checarDesktop();
		if (desktop != null && desktop.isAjusteAutomatico() && desktop.isAjusteAutomaticoForm()) {
			configurarAjustes(false);
		}
	}

	private void checarAltura(int y, int precisao) {
		if (y < 21) {
			int altura = getHeight();
			if (precisao > Constantes.QUATRO) {
				processarNorte(altura, true);
			} else if (precisao < -Constantes.QUATRO) {
				processarSul(altura, true);
			}
			if (desktop != null) {
				desktop.repaint();
			}
		}
	}

	private void processarNorte(int alturaAtual, boolean update) {
		Variavel varMinimoAltura = VariavelProvedor
				.getVariavel(ObjetoConstantes.ALTURMA_MINIMA_FORMULARIO_SEM_REGISTROS);
		int novaAltura = Constantes.TRINTA;
		if (varMinimoAltura != null) {
			novaAltura = varMinimoAltura.getInteiro(Constantes.TRINTA);
		}
		if (novaAltura != alturaAtual) {
			setSize(getWidth(), novaAltura);
			if (update) {
				SwingUtilities.updateComponentTreeUI(this);
			}
		}
	}

	private void processarSul(int alturaAtual, boolean update) {
		int totalRegistros = container.getTotalRegistros();
		if (totalRegistros < 1) {
			int alturaTitulo = getAlturaTitulo();
			int alturaToolbar = container.getAlturaToolbar();
			int alturaHeader = container.getAlturaTableHeader();
			int novaAltura = alturaTitulo + alturaToolbar + alturaHeader + Constantes.DEZ;
			if (novaAltura != alturaAtual) {
				setSize(getWidth(), novaAltura);
				if (update) {
					SwingUtilities.updateComponentTreeUI(this);
				}
			}
		} else {
			configurarAltura(totalRegistros, true, update);
		}
	}

	private int getAlturaTitulo() {
		JComponent c = ((BasicInternalFrameUI) getUI()).getNorthPane();
		return c == null ? 0 : c.getHeight();
	}

	public static InternalFormulario criar(Conexao padrao, Objeto objeto, boolean buscaAuto) {
		return new InternalFormulario(padrao, objeto, buscaAuto);
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

	public void configurarAltura(int total, boolean norteSemRegistros, boolean update) {
		boolean salvar = false;
		Variavel varMaximoRegistro = VariavelProvedor
				.getVariavel(ObjetoConstantes.ALTURMA_MINIMA_FORMULARIO_MAXIMO_DE_REGISTROS);
		if (varMaximoRegistro == null) {
			varMaximoRegistro = new Variavel(ObjetoConstantes.ALTURMA_MINIMA_FORMULARIO_MAXIMO_DE_REGISTROS,
					Constantes.VAZIO + Constantes.DEZ);
			VariavelProvedor.adicionar(varMaximoRegistro);
			salvar = true;
		}
		Variavel varMinimoAltura = VariavelProvedor
				.getVariavel(ObjetoConstantes.ALTURMA_MINIMA_FORMULARIO_SEM_REGISTROS);
		if (varMinimoAltura == null) {
			varMinimoAltura = new Variavel(ObjetoConstantes.ALTURMA_MINIMA_FORMULARIO_SEM_REGISTROS,
					Constantes.VAZIO + Constantes.TRINTA);
			VariavelProvedor.adicionar(varMinimoAltura);
			salvar = true;
		}
		checarAtualizarVariavelProvedor(salvar);
		if (total < 1) {
			if (norteSemRegistros) {
				processarNorte(getHeight(), false);
			} else {
				processarSul(getHeight(), false);
			}
		} else {
			int alturaTitulo = getAlturaTitulo();
			int alturaToolbar = container.getAlturaToolbar();
			int alturaHeader = container.getAlturaTableHeader();
			int novaAltura = alturaTitulo + alturaToolbar + alturaHeader + Constantes.DEZ;
			int maximoRegistros = varMaximoRegistro.getInteiro(Constantes.DEZ);
			if (total > maximoRegistros) {
				total = maximoRegistros;
			}
			total++;
			setSize(getWidth(), novaAltura + total * container.getAlturaTableRegistro());
		}
		if (update) {
			SwingUtilities.updateComponentTreeUI(this);
		}
	}

	private void checarAtualizarVariavelProvedor(boolean salvar) {
		if (salvar) {
			try {
				VariavelProvedor.salvar();
				VariavelProvedor.inicializar();
			} catch (Exception e) {
				LOG.log(Level.SEVERE, Constantes.ERRO, e);
			}
		}
	}

	private transient InternalListener.Vinculo vinculoListener = new InternalListener.Vinculo() {
		@Override
		public void pesquisar(Conexao conexao, Pesquisa pesquisa, String argumentos) {
			checarDesktop();
			if (desktop != null) {
				desktop.pesquisar(conexao, pesquisa, argumentos);
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

		public void selecionarCampo(Objeto objeto, Coletor coletor, Component c) {
			checarDesktop();
			if (desktop instanceof ObjetoSuperficie) {
				((ObjetoSuperficie) desktop).selecionarCampo(objeto, coletor, c);
			}
		}

		public void atualizarComplemento(Objeto objeto) {
			checarDesktop();
			if (desktop instanceof ObjetoSuperficie) {
				((ObjetoSuperficie) desktop).atualizarComplemento(objeto);
			}
		}

		public List<Objeto> objetosComTabela() {
			checarDesktop();
			if (desktop instanceof ObjetoSuperficie) {
				return ((ObjetoSuperficie) desktop).objetosComTabela();
			}
			return new ArrayList<>();
		}

		public boolean validoInvisibilidade() {
			checarDesktop();
			if (desktop instanceof ObjetoSuperficie) {
				return ((ObjetoSuperficie) desktop).getIdOrigens().size() < 2;
			}
			return true;
		}

		public void adicionarHierarquico(Conexao conexao, Objeto objeto, Map<String, Object> mapaRef) {
			checarDesktop();
			if (desktop instanceof ObjetoSuperficie) {
				((ObjetoSuperficie) desktop).adicionarHierarquico(conexao, objeto, mapaRef);
			}
		}

		public void preencherVinculacao(Vinculacao vinculacao) {
			checarDesktop();
			if (desktop instanceof ObjetoSuperficie) {
				((ObjetoSuperficie) desktop).preencherVinculacao(vinculacao);
			}
		}

		public void salvarVinculacao(Vinculacao vinculacao) {
			checarDesktop();
			if (desktop instanceof ObjetoSuperficie) {
				((ObjetoSuperficie) desktop).salvarVinculacao(vinculacao);
			}
		}
	};

	private transient InternalListener.Visibilidade visibilidadeListener = new InternalListener.Visibilidade() {
		@Override
		public void setVisible(boolean b) {
			InternalFormulario.this.setVisible(b);
		}

		@Override
		public void checarLargura(InternalContainer invocador) {
			checarDesktop();
			if (desktop instanceof ObjetoSuperficie) {
				((ObjetoSuperficie) desktop).checarLargura(invocador);
			}
		}

		@Override
		public void limparOutros(InternalContainer invocador) {
			checarDesktop();
			if (desktop != null) {
				desktop.limparOutros(invocador);
			}
		}
	};

	private transient InternalListener.Componente componenteListener = new InternalListener.Componente() {
		@Override
		public void getFormulario(AtomicReference<Formulario> ref) {
			checarDesktop();
			if (desktop != null) {
				desktop.setFormulario(ref);
			}
		}

		@Override
		public Component getComponente() {
			return InternalFormulario.this;
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

	public List<Relacao> listarRelacoes(Objeto objeto) {
		checarDesktop();
		if (desktop instanceof ObjetoSuperficie) {
			return ((ObjetoSuperficie) desktop).getRelacoes(objeto);
		}
		return new ArrayList<>();
	}

	public void selecionar(boolean b) {
		LOG.log(Level.FINEST, "{0}", b);
	}

	public boolean ehReferencia(Referencia referencia) {
		return referencia.igual(container.getObjeto());
	}

	public boolean ehTabela(InternalConfig config) {
		return config.igual(container.getObjeto());
	}

	public boolean ehTabela(Objeto objeto) {
		return objeto.igual(container.getObjeto());
	}

	public InternalContainer getInternalContainer() {
		return container;
	}

	public void setReferenciaPesquisa(Referencia referencia) {
		container.getObjeto().setReferenciaPesquisa(referencia);
	}

	public void pesquisar(Conexao conexao, Referencia referencia, String argumentos) {
		container.pesquisar(conexao, referencia, argumentos);
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

	public void atualizarComplemento(Objeto objeto) {
		container.atualizarComplemento(objeto);
	}

	public void limpar2() {
		container.limpar2();
	}

	public void limparOutros(InternalContainer invocador) {
		container.limparOutros(invocador);
	}

	public boolean ehObjeto(Objeto objeto) {
		return container.getObjeto().equals(objeto);
	}

	public String getComplementoChaves(boolean and, Conexao conexao) {
		return container.getComplementoChaves(and, conexao);
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

	public void aplicar(InternalConfig config) {
		if (config != null && ehTabela(config)) {
			container.aplicarConfig(config);
		}
	}

	public void invertidoNoFichario(Fichario fichario) {
		container.invertidoNoFichario(fichario);
	}

	public boolean isProcessadoPesquisa() {
		return processadoPesquisa;
	}

	public void setProcessadoPesquisa(boolean processadoPesquisa) {
		this.processadoPesquisa = processadoPesquisa;
	}

	@Override
	public void executarAoAbrirFormulario() {
		container.processar(getGraphics());
		container.formularioVisivel();
	}
}