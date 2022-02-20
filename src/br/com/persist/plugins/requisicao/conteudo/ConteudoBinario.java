package br.com.persist.plugins.requisicao.conteudo;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.Icon;
import javax.swing.JFileChooser;

import br.com.persist.assistencia.Icones;
import br.com.persist.assistencia.Muro;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Button;
import br.com.persist.componente.Panel;
import br.com.persist.parser.Tipo;
import br.com.persist.plugins.requisicao.RequisicaoException;

public class ConteudoBinario extends AbstratoRequisicaoConteudo {

	@Override
	public Component exibir(InputStream is, Tipo parametros, String uri) throws RequisicaoException, IOException {
		byte[] bytes = Util.getArrayBytes(is);

		Panel panel = new Panel();
		panel.add(BorderLayout.NORTH, criarToolbarPesquisa(uri));
		panel.add(BorderLayout.CENTER, new PanelBinario(bytes));

		return panel;
	}

	private class PanelBinario extends Panel {
		private static final long serialVersionUID = 1L;
		private Button btnBaixar = new Button("label.baixar");
		private final byte[] bytes;

		private PanelBinario(byte[] bytes) {
			setLayout(new GridBagLayout());
			this.bytes = bytes;
			Muro muro = new Muro();
			muro.camada(btnBaixar);
			add(muro);
			btnBaixar.addActionListener(e -> baixar());
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

	@Override
	public String titulo() {
		return "Bytes";
	}

	@Override
	public Icon icone() {
		return Icones.ICON;
	}
}