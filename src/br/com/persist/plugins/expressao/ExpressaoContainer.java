package br.com.persist.plugins.expressao;

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

public class ExpressaoContainer extends AbstratoContainer implements PluginArvore {
	private static final Logger LOG = Logger.getGlobal();
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private ExpressaoFormulario expressaoFormulario;
	private ExpressaoDialogo expressaoDialogo;
	private final ExpressaoSplit split;

	public ExpressaoContainer(Janela janela, Formulario formulario) {
		super(formulario);
		split = new ExpressaoSplit();
		split.inicializar(this);
		toolbar.ini(janela);
		montarLayout();
	}

	public ExpressaoDialogo getExpressaoDialogo() {
		return expressaoDialogo;
	}

	public void setExpressaoDialogo(ExpressaoDialogo expressaoDialogo) {
		this.expressaoDialogo = expressaoDialogo;
		if (expressaoDialogo != null) {
			expressaoFormulario = null;
		}
	}

	public ExpressaoFormulario getExpressaoFormulario() {
		return expressaoFormulario;
	}

	public void setExpressaoFormulario(ExpressaoFormulario expressaoFormulario) {
		this.expressaoFormulario = expressaoFormulario;
		if (expressaoFormulario != null) {
			expressaoDialogo = null;
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
					Util.mensagem(ExpressaoContainer.this, Util.getString(set));
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
			if (formulario.excluirPagina(ExpressaoContainer.this)) {
				ExpressaoFormulario.criar(formulario, ExpressaoContainer.this);
			} else if (expressaoDialogo != null) {
				expressaoDialogo.excluirContainer();
				ExpressaoFormulario.criar(formulario, ExpressaoContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (expressaoFormulario != null) {
				expressaoFormulario.excluirContainer();
				formulario.adicionarPagina(ExpressaoContainer.this);
			} else if (expressaoDialogo != null) {
				expressaoDialogo.excluirContainer();
				formulario.adicionarPagina(ExpressaoContainer.this);
			}
		}

		@Override
		protected void abrirEmFormulario() {
			if (expressaoDialogo != null) {
				expressaoDialogo.excluirContainer();
			}
			ExpressaoFormulario.criar(formulario);
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
			split.inicializar(ExpressaoContainer.this);
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
		return ExpressaoFabrica.class;
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
				return ExpressaoMensagens.getString(ExpressaoConstantes.LABEL_EXPRESSAO_MIN);
			}

			@Override
			public String getTitulo() {
				return ExpressaoMensagens.getString(ExpressaoConstantes.LABEL_EXPRESSAO);
			}

			@Override
			public String getHint() {
				return ExpressaoMensagens.getString(ExpressaoConstantes.LABEL_EXPRESSAO);
			}

			@Override
			public Icon getIcone() {
				return Icones.ESTRELA;
			}
		};
	}
}
