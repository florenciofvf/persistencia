package br.com.persist.plugins.objeto.internal;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.plaf.basic.BasicInternalFrameUI;

import br.com.persist.abstrato.AbstratoInternalFrame;
import br.com.persist.abstrato.DesktopAlinhamento;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.SetLista.Coletor;
import br.com.persist.fichario.Fichario;
import br.com.persist.formulario.Formulario;
import br.com.persist.marca.XMLException;
import br.com.persist.plugins.conexao.Conexao;
import br.com.persist.plugins.metadado.MetadadoException;
import br.com.persist.plugins.objeto.Desktop;
import br.com.persist.plugins.objeto.Objeto;
import br.com.persist.plugins.objeto.ObjetoConstantes;
import br.com.persist.plugins.objeto.ObjetoException;
import br.com.persist.plugins.objeto.ObjetoPreferencia;
import br.com.persist.plugins.objeto.ObjetoSuperficie;
import br.com.persist.plugins.objeto.ObjetoSuperficieUtil;
import br.com.persist.plugins.objeto.Relacao;
import br.com.persist.plugins.objeto.internal.InternalListener.ConfiguraAlturaSemRegistros;
import br.com.persist.plugins.objeto.vinculo.Pesquisa;
import br.com.persist.plugins.objeto.vinculo.Referencia;
import br.com.persist.plugins.objeto.vinculo.Vinculacao;
import br.com.persist.plugins.variaveis.Variavel;
import br.com.persist.plugins.variaveis.VariavelProvedor;

public class InternalFormulario extends AbstratoInternalFrame {
	private transient AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
			ObjetoPreferencia.getNivelTransparencia());
	private static final Logger LOG = Logger.getGlobal();
	private transient AlphaComposite compositeDestacar;
	private static final long serialVersionUID = 1L;
	private final InternalContainer container;
	private Dimension dimensionDestacar;
	private boolean processadoPesquisa;
	private boolean processado;
	private Desktop desktop;
	private long ultimo;

	public InternalFormulario(Formulario formulario, Conexao padrao, Objeto objeto, boolean buscaAuto) {
		super(formulario, objeto.getId());
		container = new InternalContainer(this, padrao, objeto, buscaAuto);
		container.setRelacaoObjetoListener(InternalFormulario.this::listarRelacoes);
		container.setAlinhamentoListener(InternalFormulario.this::alinhar);
		container.setDimensaoListener(InternalFormulario.this::getSize);
		container.setTituloListener(InternalFormulario.this::setTitle);
		container.setLarguraListener(InternalFormulario.this::mesma);
		container.setVisibilidadeListener(visibilidadeListener);
		container.setConfiguraAlturaListener(alturaListener);
		container.setComponenteListener(componenteListener);
		container.setVinculoListener(vinculoListener);
		container.setSelecaoListener(selecaoListener);
		setFrameIcon(Icones.VAZIO);
		montarLayout();
		configurar2();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	private void configurar2() {
		addMouseWheelListener(e -> checarAltura(e.getY(), e.getWheelRotation(), System.currentTimeMillis()));
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				checarRedimensionamento();
			}
		});
	}

	public void checarRedimensionamento() {
		checarDesktop();
		if (desktop != null && desktop.isAjusteAutoEmpilhaForm()) {
			configurarAjustes(false);
		}
	}

	private synchronized void checarAltura(int y, int precisao, long time) {
		if (precisao == 0 || container.getObjeto().isIgnorar()) {
			return;
		}
		long diff = time - ultimo;
		ultimo = time;
		if (diff < 90) {
			return;
		}
		if (y < 21) {
			int altura = getHeight();
			if (precisao > 0) {
				processarNorte(altura, true);
			} else if (precisao < 0) {
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
				Util.updateComponentTreeUI(this);
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
					Util.updateComponentTreeUI(this);
				}
			}
		} else {
			processarAltura(ConfiguraAlturaSemRegistros.SCROLL_SUL, alturaAtual, update);
		}
	}

	private int getAlturaTitulo() {
		JComponent c = ((BasicInternalFrameUI) getUI()).getNorthPane();
		if (c != null) {
			Dimension preferredSize = c.getPreferredSize();
			if (preferredSize != null) {
				return (int) preferredSize.getHeight();
			}
		}
		return 0;
	}

	public static InternalFormulario criar(Formulario formulario, Conexao padrao, Objeto objeto, boolean buscaAuto) {
		return new InternalFormulario(formulario, padrao, objeto, buscaAuto);
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
			desktop.getAjuste().aproximarObjetoFormulario(true, updateTree, null);
			desktop.getAjustar().usarFormularios(false);
		}
	}

	private void processarAltura(ConfiguraAlturaSemRegistros semRegistros, int alturaAtual, boolean update) {
		int totalRegistros = container.getTotalRegistros();
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

		if (totalRegistros < 1) {
			processarSemRegistros(semRegistros, alturaAtual, update);
		} else {
			int alturaTitulo = getAlturaTitulo();
			int alturaToolbar = container.getAlturaToolbar();
			int alturaHeader = container.getAlturaTableHeader();
			int novaAltura = alturaTitulo + alturaToolbar + alturaHeader + Constantes.DEZ;
			int maximoRegistros = varMaximoRegistro.getInteiro(Constantes.DEZ);
			if (totalRegistros > maximoRegistros) {
				totalRegistros = maximoRegistros;
			}
			if (container.scrollVisivel()) {
				totalRegistros++;
			}
			int alturaFinal = novaAltura + totalRegistros * container.getAlturaTableRegistro();
			if (alturaFinal != alturaAtual) {
				setSize(getWidth(), alturaFinal);
				if (update) {
					Util.updateComponentTreeUI(this);
				}
			}
		}
	}

	private void processarSemRegistros(ConfiguraAlturaSemRegistros semRegistros, int alturaAtual, boolean update) {
		if (ConfiguraAlturaSemRegistros.SCROLL_NORTE == semRegistros) {
			processarNorte(alturaAtual, update);
		} else if (ConfiguraAlturaSemRegistros.SCROLL_SUL == semRegistros) {
			processarSul(alturaAtual, update);
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
		public void pesquisar(Conexao conexao, Pesquisa pesquisa, Argumento argumento, boolean soTotal, boolean emForms)
				throws ObjetoException {
			checarDesktop();
			if (desktop != null) {
				desktop.pesquisar(conexao, pesquisa, argumento, soTotal, emForms);
			}
		}

		@Override
		public void pesquisarApos(Objeto fonte, Pesquisa pesquisa) {
			checarDesktop();
			if (desktop != null) {
				desktop.pesquisarApos(fonte, pesquisa);
			}
		}

		@Override
		public void pesquisarLink(List<Referencia> refs, String argumentos) {
			checarDesktop();
			if (desktop != null) {
				desktop.pesquisarLink(refs, argumentos);
			}
		}

		public void selecionarCampo(Objeto objeto, Coletor coletor, Component c, String selecionarItem) {
			checarDesktop();
			if (desktop instanceof ObjetoSuperficie) {
				((ObjetoSuperficie) desktop).selecionarCampo(objeto, coletor, c, selecionarItem);
			}
		}

		public void listarNomeBiblio(List<String> lista, Component c) {
			checarDesktop();
			if (desktop instanceof ObjetoSuperficie) {
				((ObjetoSuperficie) desktop).listarNomeBiblio(lista);
			}
		}

		public void atualizarComplemento(Objeto objeto) {
			checarDesktop();
			if (desktop instanceof ObjetoSuperficie) {
				ObjetoSuperficieUtil.atualizarComplemento((ObjetoSuperficie) desktop, objeto);
			}
		}

		public Objeto getObjeto(Referencia ref) {
			checarDesktop();
			if (desktop instanceof ObjetoSuperficie) {
				return ObjetoSuperficieUtil.getObjeto((ObjetoSuperficie) desktop, ref);
			}
			return null;
		}

		public List<Objeto> objetosComTabela() {
			checarDesktop();
			if (desktop instanceof ObjetoSuperficie) {
				return ObjetoSuperficieUtil.objetosComTabela((ObjetoSuperficie) desktop);
			}
			return new ArrayList<>();
		}

		public boolean validoInvisibilidade() {
			checarDesktop();
			if (desktop instanceof ObjetoSuperficie) {
				return ObjetoSuperficieUtil.getIdOrigens((ObjetoSuperficie) desktop).size() < 2;
			}
			return true;
		}

		public void adicionarHierarquico(Conexao conexao, Objeto objeto, Map<String, Object> mapaRef)
				throws MetadadoException, ObjetoException {
			checarDesktop();
			if (desktop instanceof ObjetoSuperficie) {
				((ObjetoSuperficie) desktop).adicionarHierarquico(conexao, objeto, mapaRef);
			}
		}

		public void adicionarHierarquicoInvisivelAbaixo(Conexao conexao, Objeto objeto) {
			checarDesktop();
			if (desktop instanceof ObjetoSuperficie) {
				((ObjetoSuperficie) desktop).adicionarHierarquicoInvisivelAbaixo(getLocation());
			}
		}

		public void adicionarHierarquicoInvisivelAcima(Conexao conexao, Objeto objeto) {
			checarDesktop();
			if (desktop instanceof ObjetoSuperficie) {
				((ObjetoSuperficie) desktop).adicionarHierarquicoInvisivelAcima(getLocation());
			}
		}

		public void adicionarHierarquicoAvulsoAbaixo(Conexao conexao, Objeto objeto) {
			checarDesktop();
			if (desktop instanceof ObjetoSuperficie) {
				((ObjetoSuperficie) desktop).adicionarHierarquicoAvulsoAbaixo(conexao, objeto);
			}
		}

		public void adicionarHierarquicoAvulsoAcima(Conexao conexao, Objeto objeto) {
			checarDesktop();
			if (desktop instanceof ObjetoSuperficie) {
				((ObjetoSuperficie) desktop).adicionarHierarquicoAvulsoAcima(conexao, objeto);
			}
		}

		public void getMetadado(AtomicReference<Object> ref, Objeto objeto) {
			checarDesktop();
			if (desktop instanceof ObjetoSuperficie) {
				((ObjetoSuperficie) desktop).getMetadado(ref, objeto);
			}
		}

		public void preencherVinculacao(Vinculacao vinculacao) throws XMLException {
			checarDesktop();
			if (desktop instanceof ObjetoSuperficie) {
				ObjetoSuperficieUtil.preencherVinculacao((ObjetoSuperficie) desktop, vinculacao);
			}
		}

		public void salvarVinculacao(Vinculacao vinculacao) {
			checarDesktop();
			if (desktop instanceof ObjetoSuperficie) {
				ObjetoSuperficieUtil.salvarVinculacao((ObjetoSuperficie) desktop, vinculacao);
			}
		}
	};

	private transient InternalListener.Visibilidade visibilidadeListener = new InternalListener.Visibilidade() {
		@Override
		public void checarRedimensionamento() {
			InternalFormulario.this.checarRedimensionamento();
		}

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

	private transient InternalListener.ConfiguraAltura alturaListener = new InternalListener.ConfiguraAltura() {
		@Override
		public void configurarAltura(ConfiguraAlturaSemRegistros semRegistros, boolean update) {
			processarAltura(semRegistros, getAlturaAtual(), update);
		}

		@Override
		public int getAlturaAtual() {
			return getHeight();
		}
	};

	private transient InternalListener.Componente componenteListener = new InternalListener.Componente() {
		@Override
		public Formulario getFormulario() {
			return formulario;
		}

		@Override
		public Component getComponente() {
			return InternalFormulario.this;
		}
	};

	private transient InternalListener.Selecao selecaoListener = new InternalListener.Selecao() {
		@Override
		public void visibilidade(boolean b) {
			if (isEnabled() && isVisible()) {
				compositeDestacar = b ? composite : null;
				repaint();
			}
		}

		@Override
		public void corFundo(boolean b) {
			if (isEnabled() && isVisible()) {
				dimensionDestacar = b ? getSize() : null;
				repaint();
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

	public List<Relacao> listarRelacoes(Objeto objeto) {
		checarDesktop();
		if (desktop instanceof ObjetoSuperficie) {
			return ObjetoSuperficieUtil.getRelacoes((ObjetoSuperficie) desktop, objeto);
		}
		return new ArrayList<>();
	}

	public void setNivelTransparencia(float nivel) {
		if (nivel >= 0.0f && nivel <= 1.0f) {
			composite = composite.derive(nivel);
		}
	}

	@Override
	public void paint(Graphics g) {
		if (compositeDestacar != null || container.getObjeto().isIgnorar()) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setComposite(compositeDestacar != null ? compositeDestacar : composite);
		}
		super.paint(g);
		if (dimensionDestacar != null) {
			g.setColor(Color.ORANGE);
			g.fillRect(0, 0, dimensionDestacar.width, dimensionDestacar.height);
		}
	}

	public boolean ehReferencia(Referencia referencia) {
		return referencia.igual(container.getObjeto());
	}

	public boolean coringa(Referencia referencia) {
		return referencia.coringa(container.getObjeto());
	}

	public boolean ehTabela(Objeto objeto) {
		return objeto.igual(container.getObjeto());
	}

	public InternalContainer getInternalContainer() {
		return container;
	}

	public List<String> getNomeColunas() {
		return container.getNomeColunas();
	}

	public void setReferenciaPesquisa(Referencia referencia) {
		container.getObjeto().setReferenciaPesquisa(referencia);
	}

	public void pesquisar(Conexao conexao, Pesquisa pesquisa, Referencia referencia, Argumento argumento,
			boolean soTotal, boolean emForms) {
		container.pesquisar(conexao, pesquisa, referencia, argumento, soTotal, emForms);
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

	public void labelTotalRegistros(long total) {
		container.labelTotalRegistros(total);
	}

	public void configuracaoDinamica(Objeto objeto) {
		container.configuracaoDinamica(objeto);
	}

	public void limpar3() {
		container.limpar3();
	}

	public void limpar2() {
		container.limpar2();
	}

	public boolean isAjustarLargura() {
		return container.isAjustarLargura();
	}

	public boolean contemExcecao() {
		return container.contemExcecao();
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
			objeto.configLocalAssociado();
		}
	}

	public void aproximarFormularioAoObjeto(int deltaX, int deltaY) {
		Objeto objeto = container.getObjeto();
		if (objeto != null) {
			setLocation(objeto.getX() + deltaX, objeto.getY() + deltaY);
			objeto.configLocalAssociado();
		}
	}

	public void setInternalConfig(InternalConfig config) {
		container.setInternalConfig(config);
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
	public void windowInternalActivatedHandler(JInternalFrame internal) {
		if (!processado) {
			processado = true;
			container.windowInternalActivatedHandler(this);
		}
	}

	public AlphaComposite getCompositeDestacar() {
		return compositeDestacar;
	}

	public void setCompositeDestacar(AlphaComposite compositeDestacar) {
		this.compositeDestacar = compositeDestacar;
	}
}