package br.com.persist.plugins.ouvinte;

import static br.com.persist.componente.BarraButtonEnum.ABRIR_EM_FORMULARO;
import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.DESTACAR_EM_FORMULARIO;
import static br.com.persist.componente.BarraButtonEnum.LIMPAR;
import static br.com.persist.componente.BarraButtonEnum.RETORNAR_AO_FICHARIO;
import static br.com.persist.componente.BarraButtonEnum.SALVAR;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JFileChooser;

import br.com.persist.abstrato.AbstratoContainer;
import br.com.persist.abstrato.AbstratoTitulo;
import br.com.persist.abstrato.PluginBasico;
import br.com.persist.assistencia.ArquivoUtil;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Selecao;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.Janela;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.TextEditor;
import br.com.persist.componente.TextEditorLine;
import br.com.persist.fichario.Fichario;
import br.com.persist.fichario.Titulo;
import br.com.persist.formulario.Formulario;

public class OuvinteContainer extends AbstratoContainer implements PluginBasico {
	private TextEditor textEditor = new TextEditor();
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private OuvinteFormulario ouvinteFormulario;
	private OuvinteDialogo ouvinteDialogo;
	private File arquivo;

	public OuvinteContainer(Janela janela, Formulario formulario) {
		super(formulario);
		toolbar.ini(janela);
		montarLayout();
	}

	public OuvinteDialogo getOuvinteDialogo() {
		return ouvinteDialogo;
	}

	public void setOuvinteDialogo(OuvinteDialogo ouvinteDialogo) {
		this.ouvinteDialogo = ouvinteDialogo;
		if (ouvinteDialogo != null) {
			ouvinteFormulario = null;
		}
	}

	public OuvinteFormulario getOuvinteFormulario() {
		return ouvinteFormulario;
	}

	public void setOuvinteFormulario(OuvinteFormulario ouvinteFormulario) {
		this.ouvinteFormulario = ouvinteFormulario;
		if (ouvinteFormulario != null) {
			ouvinteDialogo = null;
		}
	}

	private void montarLayout() {
		add(BorderLayout.NORTH, toolbar);
		ScrollPane scrollPane = new ScrollPane(textEditor);
		scrollPane.setRowHeaderView(new TextEditorLine(textEditor));
		Panel panelScroll = new Panel();
		panelScroll.add(BorderLayout.CENTER, scrollPane);
		add(BorderLayout.CENTER, new ScrollPane(panelScroll));
		textEditor.setListener(TextEditor.newTextEditorAdapter(toolbar::focusInputPesquisar, toolbar::salvar, null));
	}

	@Override
	public void setJanela(Janela janela) {
		toolbar.setJanela(janela);
	}

	@Override
	public void processar(Formulario formulario, Map<String, Object> args) {
		String string = (String) args.get(OuvinteEvento.GET_STRING);
		if (!Util.isEmpty(string)) {
			textEditor.setText(string);
			if (toolbar.chkAtivar.isSelected() && ouvinteFormulario != null) {
				ouvinteFormulario.toFront();
				ouvinteFormulario.requestFocus();
			} else if (toolbar.chkAtivar.isSelected()) {
				int indice = formulario.getIndicePagina(this);
				formulario.selecionarPagina(indice);
			}
			args.put(OuvinteEvento.GET_RESULT, OuvinteEvento.GET_RESULT);
		}
	}

	private class Toolbar extends BarraButton implements ActionListener {
		private final CheckBox chkAtivar = new CheckBox();
		private static final long serialVersionUID = 1L;
		private transient Selecao selecao;

		public void ini(Janela janela) {
			super.ini(janela, DESTACAR_EM_FORMULARIO, RETORNAR_AO_FICHARIO, ABRIR_EM_FORMULARO, LIMPAR, SALVAR, COPIAR,
					COLAR);
			chkAtivar.setToolTipText(OuvinteMensagens.getString("hint.formulario_top_mensagem"));
			txtPesquisa.addActionListener(this);
			add(chkAtivar);
			add(txtPesquisa);
			add(label);
		}

		@Override
		protected void destacarEmFormulario() {
			if (formulario.excluirPagina(OuvinteContainer.this)) {
				OuvinteFormulario.criar(formulario, OuvinteContainer.this);
			} else if (ouvinteDialogo != null) {
				ouvinteDialogo.excluirContainer();
				OuvinteFormulario.criar(formulario, OuvinteContainer.this);
			}
		}

		@Override
		protected void retornarAoFichario() {
			if (ouvinteFormulario != null) {
				ouvinteFormulario.excluirContainer();
				formulario.adicionarPagina(OuvinteContainer.this);
			} else if (ouvinteDialogo != null) {
				ouvinteDialogo.excluirContainer();
				formulario.adicionarPagina(OuvinteContainer.this);
			}
		}

		@Override
		protected void abrirEmFormulario() {
			if (ouvinteDialogo != null) {
				ouvinteDialogo.excluirContainer();
			}
			OuvinteFormulario.criar(formulario);
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
		protected void limpar() {
			textEditor.limpar();
		}

		@Override
		protected void copiar() {
			String string = Util.getString(textEditor);
			Util.setContentTransfered(string);
			copiarMensagem(string);
			textEditor.requestFocus();
		}

		@Override
		protected void colar(boolean numeros, boolean letras) {
			Util.getContentTransfered(textEditor, numeros, letras);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!Util.isEmpty(txtPesquisa.getText())) {
				selecao = Util.getSelecao(textEditor, selecao, txtPesquisa.getText());
				selecao.selecionar(label);
			} else {
				label.limpar();
			}
		}

		@Override
		protected void salvar() {
			JFileChooser fileChooser = Util.criarFileChooser(arquivo, false);
			int opcao = fileChooser.showSaveDialog(formulario);
			if (opcao == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				if (file != null) {
					salvar(file);
					arquivo = file;
					setTitulo();
				}
			}
		}

		private void salvar(File file) {
			if (Util.confirmaSalvar(OuvinteContainer.this)) {
				try {
					ArquivoUtil.salvar(textEditor, file);
					salvoMensagem();
				} catch (Exception e) {
					Util.mensagem(OuvinteContainer.this, e.getMessage());
				}
			}
		}

		private void setTitulo() {
			if (ouvinteFormulario == null) {
				int indice = formulario.getIndicePagina(OuvinteContainer.this);
				if (indice != -1) {
					formulario.setHintTitlePagina(indice, arquivo.getAbsolutePath(), arquivo.getName());
				}
			} else {
				ouvinteFormulario.setTitle(arquivo.getName());
			}
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
	public String getStringPersistencia() {
		return Constantes.VAZIO;
	}

	@Override
	public Class<?> getClasseFabrica() {
		return OuvinteFabrica.class;
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
				return OuvinteMensagens.getString(OuvinteConstantes.LABEL_OUVINTE_MIN);
			}

			@Override
			public String getTitulo() {
				return OuvinteMensagens.getString(OuvinteConstantes.LABEL_OUVINTE);
			}

			@Override
			public String getHint() {
				return OuvinteMensagens.getString(OuvinteConstantes.LABEL_OUVINTE);
			}

			@Override
			public Icon getIcone() {
				return Icones.PANEL;
			}
		};
	}
}