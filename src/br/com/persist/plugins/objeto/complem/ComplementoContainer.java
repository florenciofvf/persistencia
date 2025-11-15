package br.com.persist.plugins.objeto.complem;

import static br.com.persist.componente.BarraButtonEnum.APLICAR;
import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.EXCLUIR;
import static br.com.persist.componente.BarraButtonEnum.LIMPAR;
import static br.com.persist.componente.BarraButtonEnum.SALVAR;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JList;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;

import br.com.persist.abstrato.PluginBasico;
import br.com.persist.arquivo.ArquivoUtil;
import br.com.persist.assistencia.ColecaoStringModelo;
import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Janela;
import br.com.persist.componente.Nil;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.TextEditor;
import br.com.persist.componente.TextEditorLine;

public class ComplementoContainer extends Panel implements PluginBasico {
	private final ToolbarLista toolbarLista = new ToolbarLista();
	private final ToolbarArea toolbarArea = new ToolbarArea();
	private File fileComplementos = new File("complementos");
	private final TextEditor textEditor = new TextEditor();
	private final transient ComplementoListener listener;
	private static final Logger LOG = Logger.getGlobal();
	private static final long serialVersionUID = 1L;
	private final Toolbar toolbar = new Toolbar();
	private final JList<String> listaComplementos;

	public ComplementoContainer(Janela janela, ComplementoListener listener) {
		listaComplementos = new JList<>(new ColecaoStringModelo(listener.getColecaoComplemento()));
		listaComplementos.addMouseListener(mouseListenerInner);
		textEditor.setText(listener.getComplemento());
		this.listener = listener;
		toolbar.ini(janela);
		montarLayout();
	}

	private void montarLayout() {
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, criarPanelTextArea(), criarPanelLista());
		listaComplementos.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listaComplementos.setCellRenderer(new ComplementoCellRenderer());
		split.setDividerLocation(Constantes.SIZE.height / 2);
		split.setOneTouchExpandable(true);
		split.setContinuousLayout(true);
		add(BorderLayout.NORTH, toolbar);
		add(BorderLayout.CENTER, split);
	}

	private Panel criarPanelTextArea() {
		Panel panel = new Panel();
		panel.add(BorderLayout.NORTH, toolbarArea);
		ScrollPane scrollPane = new ScrollPane(textEditor);
		scrollPane.setRowHeaderView(new TextEditorLine(textEditor));
		Panel panelScroll = new Panel();
		panelScroll.add(BorderLayout.CENTER, scrollPane);
		panel.add(BorderLayout.CENTER, new ScrollPane(panelScroll));
		return panel;
	}

	private Panel criarPanelLista() {
		Panel panel = new Panel();
		panel.add(BorderLayout.NORTH, toolbarLista);
		panel.add(BorderLayout.CENTER, new ScrollPane(listaComplementos));
		return panel;
	}

	private transient MouseListener mouseListenerInner = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			String sel = listaComplementos.getSelectedValue();
			if (Util.isEmpty(sel) || sel.startsWith("#")) {
				return;
			}
			String string = textEditor.getText();
			if (Util.isEmpty(string)) {
				textEditor.setText(sel);
			} else {
				textEditor.setText(string + " " + sel);
			}
		}
	};

	private class Toolbar extends BarraButton {
		private static final long serialVersionUID = 1L;

		public void ini(Janela janela) {
			super.ini(janela, APLICAR);
		}

		@Override
		protected void aplicar() {
			listener.processarComplemento(textEditor.getText());
			fechar();
		}
	}

	private class ToolbarArea extends BarraButton {
		private static final long serialVersionUID = 1L;

		private ToolbarArea() {
			super.ini(new Nil(), LIMPAR, COPIAR, COLAR);
		}

		@Override
		protected void limpar() {
			textEditor.setText(Constantes.VAZIO);
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
	}

	private class ToolbarLista extends BarraButton {
		private Action limparComplementosAcao = Action
				.acaoIcon(ComplementoMensagens.getString("label.limpar_complementos"), Icones.NOVO);
		private Action adicionar2Acao = actionIcon("label.adicionar", Icones.CRIAR2);
		private Action adicionarAcao = actionIcon("label.criar", Icones.CRIAR);
		private static final long serialVersionUID = 1L;

		private ToolbarLista() {
			super.ini(new Nil(), BAIXAR, SALVAR, EXCLUIR);
			addButton(adicionarAcao);
			addButton(adicionar2Acao);
			addButton(limparComplementosAcao);
			limparComplementosAcao.setActionListener(e -> limparComplementos());
			adicionar2Acao.setActionListener(e -> adicionar2());
			adicionarAcao.setActionListener(e -> adicionar());
		}

		private void limparComplementos() {
			if (listener.getColecaoComplemento().isEmpty() && listaComplementos.getModel().getSize() == 0) {
				return;
			}
			if (Util.confirmar(ComplementoContainer.this,
					ComplementoMensagens.getString("msg.confirma_exclusao_complementos"), false)) {
				listener.getColecaoComplemento().clear();
				listaComplementos.setModel(new ColecaoStringModelo(listener.getColecaoComplemento()));
			}
		}

		@Override
		protected void baixar() {
			try {
				if (fileComplementos.exists() && fileComplementos.canRead()) {
					List<String> list = ArquivoUtil.lerArquivo(fileComplementos, true);
					listaComplementos.setModel(new ColecaoStringModelo(list));
				}
			} catch (Exception e) {
				LOG.log(Level.SEVERE, Constantes.ERRO, e);
			}
		}

		@Override
		protected void salvar() {
			if (Util.confirmar(ComplementoContainer.this,
					ComplementoMensagens.getString("msg.confirma_salvar_complementos"), false)) {
				try {
					ColecaoStringModelo modelo = (ColecaoStringModelo) listaComplementos.getModel();
					ArquivoUtil.salvar(modelo.getLista(), fileComplementos);
					salvoMensagem();
				} catch (Exception e) {
					LOG.log(Level.SEVERE, Constantes.ERRO, e);
				}
			}
		}

		@Override
		protected void excluir() {
			int i = listaComplementos.getSelectedIndex();
			if (i == -1) {
				Util.mensagem(ComplementoContainer.this,
						ComplementoMensagens.getString("msg.selecione_um_item_para_exclusao"));
				return;
			}
			if (Util.confirmar(ComplementoContainer.this, ComplementoMensagens.getString("msg.confirma_exclusao_item"),
					false)) {
				ColecaoStringModelo modelo = (ColecaoStringModelo) listaComplementos.getModel();
				modelo.excluir(i);
				listaComplementos.setModel(new ColecaoStringModelo(modelo.getLista()));
			}
		}

		private void adicionar() {
			int i = listaComplementos.getSelectedIndex();
			if (i == -1) {
				Util.mensagem(ComplementoContainer.this,
						ComplementoMensagens.getString("msg.selecione_um_item_para_inclusao"));
				return;
			}
			Object resp = Util.getValorInputDialog(ComplementoContainer.this, "label.atencao",
					ComplementoMensagens.getString("msg.texto_inclusao"), null);
			if (resp != null) {
				ColecaoStringModelo modelo = (ColecaoStringModelo) listaComplementos.getModel();
				modelo.incluir(i, resp.toString());
				listaComplementos.setModel(new ColecaoStringModelo(modelo.getLista()));
			}
		}

		private void adicionar2() {
			Object resp = Util.getValorInputDialog(ComplementoContainer.this, "label.atencao",
					ComplementoMensagens.getString("msg.texto_inclusao"), null);
			if (resp != null) {
				ColecaoStringModelo modelo = (ColecaoStringModelo) listaComplementos.getModel();
				modelo.adicionar(resp.toString());
				listaComplementos.setModel(new ColecaoStringModelo(modelo.getLista()));
			}
		}
	}
}