package br.com.persist.plugins.anotacao;

import static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO;
import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.DESTACAR_EM_FORMULARIO;
import static br.com.persist.componente.BarraButtonEnum.RETORNAR_AO_FICHARIO;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;

import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.abstrato.PluginArvore;
import br.com.persist.arquivo.ArquivoPesquisa;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;
import br.com.persist.marca.XMLException;

public class AnotacaoContainer extends AbstratoContainer implements PluginArvore {
	private static final Logger LOG = Logger.getGlobal();
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private AnotacaoFormulario anotacaoFormulario;
	private AnotacaoDialogo anotacaoDialogo;
	private final AnotacaoSplit split;

	public AnotacaoContainer(Janela janela, Formulario formulario) {
		super(formulario);
		split = new AnotacaoSplit();
		split.inicializar(this);
		toolbar.ini(janela);
		montarLayout();
	}

	public AnotacaoDialogo getAnotacaoDialogo() {
		return anotacaoDialogo;
	}

	public void setAnotacaoDialogo(AnotacaoDialogo anotacaoDialogo) {
		this.anotacaoDialogo = anotacaoDialogo;
		if (anotacaoDialogo != null) {
			anotacaoFormulario = null;
		}
	}

	public AnotacaoFormulario getAnotacaoFormulario() {
		return anotacaoFormulario;
	}

	public void setAnotacaoFormulario(AnotacaoFormulario anotacaoFormulario) {
		this.anotacaoFormulario = anotacaoFormulario;
		if (anotacaoFormulario != null) {
			anotacaoDialogo = null;
		}
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, split);
	}

	@Override
	public void setJanela(Janela janela) {
		toolbar.setJanela(janela);
	}

	public void focusInputPesquisarTree() {
		toolbar.focusInputPesquisar();
	}

	private class Toolbar extends BarraButton implements ActionListener {
		private static final long serialVersionUID = 1L;
		private transient ArquivoPesquisa pesquisa;

		public void ini(Janela janela) {
			super.ini(janela, BAIXAR, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, ABRIR_EM_FORMULARO);
			txtPesquisa.addActionListener(this);
			add(txtPesquisa);
			add(chkPorParte);
			add(chkPsqConteudo);
			add(label);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!Util.isEmpty(txtPesquisa.getText())) {
				if (chkPsqConteudo.isSelected()) {
					Set<String> set = new LinkedHashSet<>();
					split.contemConteudo(set, txtPesquisa.getText(), chkPorParte.isSelected());
					Util.mensagem(AnotacaoContainer.this, Util.getString(set));
				} else {
					pesquisa = split.getTree().getPesquisa(pesquisa, txtPesquisa.getText(), chkPorParte.isSelected());
					pesquisa.selecionar(label);
				}
			} else {
				label.limpar();
			}
		}

		@Override
		protected void destacarEmFormulario() {
			if (formulario.excluirPagina(AnotacaoContainer.this)) {
				AnotacaoFormulario.criar(formulario, AnotacaoContainer.this);
			} else if (anotacaoDialogo != null) {
				anotacaoDialogo.excluirContainer();
				AnotacaoFormulario.criar(formulario, AnotacaoContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (anotacaoFormulario != null) {
				anotacaoFormulario.excluirContainer();
				formulario.adicionarPagina(AnotacaoContainer.this);
			} else if (anotacaoDialogo != null) {
				anotacaoDialogo.excluirContainer();
				formulario.adicionarPagina(AnotacaoContainer.this);
			}
		}

		@Override
		protected void abrirEmFormulario() {
			if (anotacaoDialogo != null) {
				anotacaoDialogo.excluirContainer();
			}
			AnotacaoFormulario.criar(formulario);
		}

		@Override
		public void windowOpenedHandler(Window window) {
			buttonDestacar.estadoFormulario();
		}

		@Override
		public void dialogOpenedHandler(Dialog dialog) {
			buttonDestacar.estadoDialogo();
		}

		void adicionadoAoFichario() {
			buttonDestacar.estadoFichario();
		}

		@Override
		protected void baixar() {
			split.inicializar(AnotacaoContainer.this);
		}
	}

	@Override
	public void adicionadoAoFichario(Fichario fichario) {
		toolbar.adicionadoAoFichario();
	}

	@Override
	public void windowOpenedHandler(Window window) {
		toolbar.windowOpenedHandler(window);
	}

	@Override
	public void dialogOpenedHandler(Dialog dialog) {
		toolbar.dialogOpenedHandler(dialog);
	}

	@Override
	public String getStringPersistencia() {
		try {
			split.salvar();
		} catch (XMLException e) {
			LOG.log(Level.SEVERE, e.getMessage());
		}
		return Constantes.VAZIO;
	}

	@Override
	public Class<?> getClasseFabrica() {
		return AnotacaoFabrica.class;
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public Titulo getTitulo() {
		return new AbstratoTitulo() {
			@Override
			public String getTituloMin() {
				return AnotacaoMensagens.getString(AnotacaoConstantes.LABEL_ANOTACOES_MIN);
			}

			@Override
			public String getTitulo() {
				return AnotacaoMensagens.getString(AnotacaoConstantes.LABEL_ANOTACOES);
			}

			@Override
			public String getHint() {
				return AnotacaoMensagens.getString(AnotacaoConstantes.LABEL_ANOTACOES);
			}

			@Override
			public Icon getIcone() {
				return Icones.PANEL4;
			}
		};
	}
}