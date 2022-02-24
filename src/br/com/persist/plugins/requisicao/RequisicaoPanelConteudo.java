package br.com.persist.plugins.requisicao;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JToolBar;

import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Button;
import br.com.persist.componente.Label;
import br.com.persist.componente.Panel;
import br.com.persist.parser.Tipo;
import br.com.persist.plugins.requisicao.conteudo.RequisicaoConteudo;
import br.com.persist.plugins.requisicao.conteudo.RequisicaoConteudoListener;

public class RequisicaoPanelConteudo extends Panel {
	private static final long serialVersionUID = 1L;
	private transient RequisicaoConteudoListener requisicaoConteudoListener;
	private transient RequisicaoRota requisicaoRota;
	private final RequisicaoPagina requisicaoPagina;
	private final transient Tipo parametros;
	private String titulo = "Bytes";
	private final byte[] bytes;

	public RequisicaoPanelConteudo(RequisicaoPagina requisicaoPagina, InputStream is, Tipo parametros)
			throws IOException {
		this.requisicaoPagina = requisicaoPagina;
		bytes = Util.getArrayBytes(is);
		this.parametros = parametros;
	}

	public void setRequisicaoConteudoListener(RequisicaoConteudoListener requisicaoConteudoListener) {
		this.requisicaoConteudoListener = requisicaoConteudoListener;
	}

	public RequisicaoConteudoListener getRequisicaoConteudoListener() {
		return requisicaoConteudoListener;
	}

	public void setRequisicaoRota(RequisicaoRota requisicaoRota) {
		this.requisicaoRota = requisicaoRota;
	}

	public RequisicaoRota getRequisicaoRota() {
		return requisicaoRota;
	}

	public Icon getIcone() {
		return Icones.ICON;
	}

	public String getTitulo() {
		return titulo;
	}

	public void configuracoes(String uri, String mime) {
		BarraInfo barraInfo = new BarraInfo(uri, mime);
		add(BorderLayout.NORTH, barraInfo);
		barraInfo.configurar();
		barraInfo.checarView();
	}

	private class BarraInfo extends JToolBar {
		private static final long serialVersionUID = 1L;
		private JComboBox<RequisicaoConteudo> cmbVisualizador = new JComboBox<>();
		private Label labelVisualizador = new Label("label.visualizador");
		private Button btnBaixar = new Button("label.baixar");
		private Label labelMime = new Label();
		private Label labelURI = new Label();
		private final String mime;

		private BarraInfo(String uri, String mime) {
			labelURI.setText("[" + uri + "]");
			labelMime.setText(" - " + mime);
			this.mime = mime;
			add(labelURI);
			add(labelMime);
			addSeparator();
			add(btnBaixar);
			addSeparator();
			add(labelVisualizador);
			add(cmbVisualizador);
			btnBaixar.addActionListener(e -> baixar());
			cmbVisualizador.addItemListener(BarraInfo.this::processarVisualizador);
		}

		private void configurar() {
			cmbVisualizador.setModel(new DefaultComboBoxModel<>(requisicaoPagina.getVisualizadores()));
		}

		private void processarVisualizador(ItemEvent e) {
			if (ItemEvent.SELECTED == e.getStateChange()) {
				RequisicaoConteudo requisicaoConteudo = (RequisicaoConteudo) cmbVisualizador.getSelectedItem();
				if (requisicaoConteudo == null) {
					return;
				}
				requisicaoConteudo.setRequisicaoRota(requisicaoRota);
				requisicaoConteudo.setRequisicaoConteudoListener(requisicaoConteudoListener);
				Component visualizador = requisicaoConteudo.exibidor(RequisicaoPanelConteudo.this, bytes, parametros);
				if (visualizador != null) {
					RequisicaoPanelConteudo.this.add(BorderLayout.CENTER, visualizador);
					requisicaoPagina.associarMimeVisualizador(mime, requisicaoConteudo);
				}
			}
		}

		private void checarView() {
			RequisicaoConteudo requisicaoConteudo = requisicaoPagina.getVisualizador(mime);
			cmbVisualizador.setSelectedItem(requisicaoConteudo);
		}

		private void baixar() {
			JFileChooser fileChooser = Util.criarFileChooser(null, false);
			int opcao = fileChooser.showSaveDialog(this);
			if (opcao == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				if (file != null) {
					try {
						Util.salvar(file, bytes);
						Util.mensagem(this, "Sucesso!");
					} catch (IOException e) {
						Util.mensagem(this, e.getMessage());
					}
				}
			}
		}
	}
}