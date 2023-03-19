package br.com.persist.plugins.mapa;

import static br.com.persist.componente.BarraButtonEnum.BAIXAR;
import static br.com.persist.componente.BarraButtonEnum.COLAR;
import static br.com.persist.componente.BarraButtonEnum.COPIAR;
import static br.com.persist.componente.BarraButtonEnum.LIMPAR;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.Icon;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Selecao;
import br.com.persist.assistencia.Util;
import br.com.persist.componente.Action;
import br.com.persist.componente.BarraButton;
import br.com.persist.componente.Nil;
import br.com.persist.componente.Panel;
import br.com.persist.componente.ScrollPane;
import br.com.persist.componente.TextField;

public class MapaPagina extends Panel {
	private static final long serialVersionUID = 1L;
	private final JTabbedPane tabbedPane = new JTabbedPane();
	private final AbaText abaText = new AbaText();
	private final AbaView abaView = new AbaView();
	private final File file;

	public MapaPagina(File file) {
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		this.file = file;
		montarLayout();
		abrir();
	}

	private void montarLayout() {
		tabbedPane.addTab("Text", abaText);
		tabbedPane.addTab("View", abaView);
		add(BorderLayout.CENTER, tabbedPane);
	}

	static Action actionMenu(String chave) {
		return Action.acaoMenu(MapaMensagens.getString(chave), null);
	}

	static Action actionIcon(String chave, Icon icon) {
		return Action.acaoIcon(MapaMensagens.getString(chave), icon);
	}

	class AbaText extends Panel {
		private static final long serialVersionUID = 1L;
		private final ToolbarParametro toolbar = new ToolbarParametro();
		private final JTextPane textArea = new JTextPane();
		private ScrollPane scrollPane;

		AbaText() {
			montarLayout();
		}

		void montarLayout() {
			add(BorderLayout.NORTH, toolbar);
			Panel panelArea = new Panel();
			panelArea.add(BorderLayout.CENTER, textArea);
			scrollPane = new ScrollPane(panelArea);
			add(BorderLayout.CENTER, scrollPane);
		}

		int getValueScrollPane() {
			return scrollPane.getVerticalScrollBar().getValue();
		}

		void setValueScrollPane(int value) {
			SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(value));
		}

		String getConteudo() {
			return textArea.getText();
		}

		void abrir() {
			textArea.setText(Constantes.VAZIO);
			if (file.exists()) {
				try (BufferedReader br = new BufferedReader(
						new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
					StringBuilder sb = new StringBuilder();
					int value = getValueScrollPane();
					String linha = br.readLine();
					while (linha != null) {
						sb.append(linha + Constantes.QL);
						linha = br.readLine();
					}
					textArea.setText(sb.toString());
					setValueScrollPane(value);
				} catch (Exception ex) {
					Util.stackTraceAndMessage(MapaConstantes.PAINEL_MAPA, ex, this);
				}
			}
		}

		private class ToolbarParametro extends BarraButton implements ActionListener {
			private static final long serialVersionUID = 1L;
			private final TextField txtPesquisa = new TextField(35);
			private transient Selecao selecao;

			private ToolbarParametro() {
				super.ini(new Nil(), LIMPAR, BAIXAR, COPIAR, COLAR);
				txtPesquisa.setToolTipText(Mensagens.getString("label.pesquisar"));
				txtPesquisa.addActionListener(this);
				add(txtPesquisa);
				add(label);
			}

			@Override
			protected void limpar() {
				textArea.setText(Constantes.VAZIO);
			}

			@Override
			protected void baixar() {
				abrir();
				selecao = null;
				label.limpar();
			}

			@Override
			protected void copiar() {
				String string = Util.getString(textArea);
				Util.setContentTransfered(string);
				copiarMensagem(string);
				textArea.requestFocus();
			}

			@Override
			protected void colar(boolean numeros, boolean letras) {
				Util.getContentTransfered(textArea, numeros, letras);
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				if (!Util.estaVazio(txtPesquisa.getText())) {
					selecao = Util.getSelecao(textArea, selecao, txtPesquisa.getText());
					selecao.selecionar(label);
				} else {
					label.limpar();
				}
			}
		}
	}

	public String getConteudo() {
		return abaText.getConteudo();
	}

	public void setConteudo(String string) {
		abaText.textArea.setText(string);
	}

	public String getNome() {
		return file.getName();
	}

	private void abrir() {
		abaText.abrir();
	}

	public void excluir() {
		if (file.exists()) {
			Path path = FileSystems.getDefault().getPath(file.getAbsolutePath());
			try {
				Files.delete(path);
			} catch (IOException e) {
				Util.stackTraceAndMessage(MapaConstantes.PAINEL_MAPA, e, this);
			}
		}
	}

	public void salvar(AtomicBoolean atomic) {
		if (!Util.confirmaSalvarMsg(this, Constantes.TRES, MapaMensagens.getString("msg.confirmar_salvar_ativa"))) {
			return;
		}
		try (PrintWriter pw = new PrintWriter(file, StandardCharsets.UTF_8.name())) {
			pw.print(abaText.textArea.getText());
			atomic.set(true);
		} catch (Exception ex) {
			Util.stackTraceAndMessage(MapaConstantes.PAINEL_MAPA, ex, this);
		}
	}
}