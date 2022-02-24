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
import br.com.persist.plugins.requisicao.visualizador.RequisicaoVisualizador;
import br.com.persist.plugins.requisicao.visualizador.RequisicaoVisualizadorListener;

public class RequisicaoPanelBytes extends Panel {
	private static final long serialVersionUID = 1L;
	private transient RequisicaoVisualizadorListener requisicaoVisualizadorListener;
	private transient RequisicaoRota requisicaoRota;
	private final RequisicaoPagina requisicaoPagina;
	private final transient Tipo parametros;
	private String titulo = "Bytes";
	private final byte[] bytes;

	public RequisicaoPanelBytes(RequisicaoPagina requisicaoPagina, InputStream is, Tipo parametros) throws IOException {
		this.requisicaoPagina = requisicaoPagina;
		bytes = Util.getArrayBytes(is);
		this.parametros = parametros;
	}

	public void setRequisicaoVisualizadorListener(RequisicaoVisualizadorListener requisicaoVisualizadorListener) {
		this.requisicaoVisualizadorListener = requisicaoVisualizadorListener;
	}

	public RequisicaoVisualizadorListener getRequisicaoVisualizadorListener() {
		return requisicaoVisualizadorListener;
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
		private JComboBox<RequisicaoVisualizador> cmbVisualizador = new JComboBox<>();
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
				RequisicaoVisualizador requisicaoVisualizador = (RequisicaoVisualizador) cmbVisualizador
						.getSelectedItem();
				if (requisicaoVisualizador == null) {
					return;
				}
				requisicaoVisualizador.setRequisicaoRota(requisicaoRota);
				requisicaoVisualizador.setRequisicaoVisualizadorListener(requisicaoVisualizadorListener);
				Component visualizador = requisicaoVisualizador.exibidor(RequisicaoPanelBytes.this, bytes, parametros);
				if (visualizador != null) {
					RequisicaoPanelBytes.this.add(BorderLayout.CENTER, visualizador);
					requisicaoPagina.associarMimeVisualizador(mime, requisicaoVisualizador);
				}
			}
		}

		private void checarView() {
			RequisicaoVisualizador requisicaoVisualizador = requisicaoPagina.getVisualizador(mime);
			cmbVisualizador.setSelectedItem(requisicaoVisualizador);
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