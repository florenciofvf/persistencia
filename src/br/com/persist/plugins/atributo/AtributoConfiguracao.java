package br.com.persist.plugins.atributo;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;

import br.com.persist.abstrato.AbstratoConfiguracao;
import br.com.persist.arquivo.ArquivoUtil;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Muro;
import br.com.persist.assistencia.SwingConstantes;
import br.com.persist.componente.Button;
import br.com.persist.componente.ButtonGrupo;
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.FileChooser;
import br.com.persist.componente.Label;
import br.com.persist.componente.PanelCenter;
import br.com.persist.componente.RadioButton;
import br.com.persist.componente.TextField;
import br.com.persist.formulario.Formulario;

public class AtributoConfiguracao extends AbstratoConfiguracao {
	private final CheckBox chkExibirArqIgnorados = criarCheckBox("label.exibir_arq_ignorados");
	private final Button btnDirPadraoSelArquivos = new Button("label.diretorio");
	private final TextField txtDirPadraoSelArquivos = new TextField();
	private static final long serialVersionUID = 1L;

	private final transient NomeValor[] posicoes = {
			new NomeValor("label.acima", SwingConstantes.TOP, NomeValor.POSICAO_ABA),
			new NomeValor("label.esquerdo", SwingConstantes.LEFT, NomeValor.POSICAO_ABA),
			new NomeValor("label.abaixo", SwingConstantes.BOTTOM, NomeValor.POSICAO_ABA),
			new NomeValor("label.direito", SwingConstantes.RIGHT, NomeValor.POSICAO_ABA) };

	public AtributoConfiguracao(Formulario formulario) {
		super(formulario, AtributoMensagens.getString("label.plugin_atributo"));
		montarLayout();
		configurar();
	}

	private void montarLayout() {
		PanelCenter panelPosicoes = criarPainelGrupo(posicoes, AtributoPreferencia.getAtributoPosicaoAbaFichario());
		txtDirPadraoSelArquivos.setText(AtributoPreferencia.getDirPadraoSelecaoArquivos());
		chkExibirArqIgnorados.setSelected(AtributoPreferencia.isExibirArqIgnorados());

		Muro muro = new Muro();
		muro.camada(Muro.panelGrid(new PanelCenter(criarLabel("label.dir_padrao_arquivos"), btnDirPadraoSelArquivos)));
		muro.camada(txtDirPadraoSelArquivos);
		Label tituloLocalAbas = criarLabelTituloRotulo("label.local_abas");
		muro.camada(Muro.panelGridBorderBottom(tituloLocalAbas, panelPosicoes));
		muro.camada(Muro.panelGrid(chkExibirArqIgnorados));
		add(BorderLayout.CENTER, muro);
	}

	private void configurar() {
		chkExibirArqIgnorados
				.addActionListener(e -> AtributoPreferencia.setExibirArqIgnorados(chkExibirArqIgnorados.isSelected()));
		txtDirPadraoSelArquivos.addActionListener(
				e -> AtributoPreferencia.setDirPadraoSelecaoArquivos(txtDirPadraoSelArquivos.getText()));
		txtDirPadraoSelArquivos.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				AtributoPreferencia.setDirPadraoSelecaoArquivos(txtDirPadraoSelArquivos.getText());
			}
		});
		btnDirPadraoSelArquivos.addActionListener(e -> {
			FileChooser fileChooser = new FileChooser(ArquivoUtil.getValido(txtDirPadraoSelArquivos.getText()));
			fileChooser.setFileSelectionMode(FileChooser.DIRECTORIES);
			int i = fileChooser.showOpenDialog(AtributoConfiguracao.this);
			if (i == FileChooser.APPROVE) {
				File sel = fileChooser.getSelectedFile();
				txtDirPadraoSelArquivos.setText(sel.getAbsolutePath());
				AtributoPreferencia.setDirPadraoSelecaoArquivos(txtDirPadraoSelArquivos.getText());
			}
		});
	}

	private Label criarLabelTituloRotulo(String rotulo) {
		return criarLabelTitulo(AtributoMensagens.getString(rotulo), false);
	}

	private Label criarLabelTitulo(String rotulo, boolean chaveRotulo) {
		Label label = new Label(rotulo, chaveRotulo);
		label.setHorizontalAlignment(SwingConstantes.CENTER);
		return label;
	}

	private class NomeValor {
		private static final byte POSICAO_ABA = 1;
		private final String nome;
		private final int valor;
		private final int tipo;

		private NomeValor(String chave, int valor, int tipo) {
			this.nome = Mensagens.getString(chave);
			this.valor = valor;
			this.tipo = tipo;
		}
	}

	private PanelCenter criarPainelGrupo(NomeValor[] nomeValores, int padrao) {
		PanelCenter panel = new PanelCenter();
		ButtonGrupo grupo = new ButtonGrupo();
		for (int i = 0; i < nomeValores.length; i++) {
			RadioPosicao radio = new RadioPosicao(nomeValores[i]);
			radio.setSelected(radio.nomeValor.valor == padrao);
			radio.setMargin(new Insets(5, 10, 5, 5));
			panel.add(radio);
			grupo.add(radio);
		}
		return panel;
	}

	private class RadioPosicao extends RadioButton {
		private static final long serialVersionUID = 1L;
		private final transient NomeValor nomeValor;

		private RadioPosicao(NomeValor nomeValor) {
			super(nomeValor.nome);
			this.nomeValor = nomeValor;
			addActionListener(e -> {
				if (nomeValor.tipo == NomeValor.POSICAO_ABA) {
					AtributoPreferencia.setAtributoPosicaoAbaFichario(nomeValor.valor);
				}
			});
		}
	}

	static CheckBox criarCheckBox(String chaveRotulo) {
		return new CheckBox(AtributoMensagens.getString(chaveRotulo), false);
	}

	public Label criarLabel(String chaveRotulo) {
		return new Label(AtributoMensagens.getString(chaveRotulo), false);
	}
}