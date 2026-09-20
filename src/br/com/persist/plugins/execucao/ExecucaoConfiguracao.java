package br.com.persist.plugins.execucao;

import java.awt.BorderLayout;
import java.awt.Insets;

import br.com.persist.abstrato.AbstratoConfiguracao;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Muro;
import br.com.persist.assistencia.SwingConstantes;
import br.com.persist.componente.ButtonGrupo;
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.Label;
import br.com.persist.componente.PanelCenter;
import br.com.persist.componente.RadioButton;
import br.com.persist.formulario.Formulario;

public class ExecucaoConfiguracao extends AbstratoConfiguracao {
	private final CheckBox chkExibirArqIgnorados = criarCheckBox("label.exibir_arq_ignorados");
	private static final long serialVersionUID = 1L;

	private final transient NomeValor[] posicoes = {
			new NomeValor("label.acima", SwingConstantes.TOP, NomeValor.POSICAO_ABA),
			new NomeValor("label.esquerdo", SwingConstantes.LEFT, NomeValor.POSICAO_ABA),
			new NomeValor("label.abaixo", SwingConstantes.BOTTOM, NomeValor.POSICAO_ABA),
			new NomeValor("label.direito", SwingConstantes.RIGHT, NomeValor.POSICAO_ABA) };

	public ExecucaoConfiguracao(Formulario formulario) {
		super(formulario, ExecucaoMensagens.getString("label.plugin_execucao"));
		montarLayout();
		configurar();
	}

	private void montarLayout() {
		PanelCenter panelPosicoes = criarPainelGrupo(posicoes, ExecucaoPreferencia.getExecucaoPosicaoAbaFichario());
		chkExibirArqIgnorados.setSelected(ExecucaoPreferencia.isExibirArqIgnorados());

		Muro muro = new Muro();
		Label tituloLocalAbas = criarLabelTituloRotulo("label.local_abas");
		muro.camada(Muro.panelGridBorderBottom(tituloLocalAbas, panelPosicoes));
		muro.camada(Muro.panelGrid(chkExibirArqIgnorados));
		add(BorderLayout.CENTER, muro);
	}

	private void configurar() {
		chkExibirArqIgnorados
				.addActionListener(e -> ExecucaoPreferencia.setExibirArqIgnorados(chkExibirArqIgnorados.isSelected()));
	}

	private Label criarLabelTituloRotulo(String rotulo) {
		return criarLabelTitulo(ExecucaoMensagens.getString(rotulo), false);
	}

	private Label criarLabelTitulo(String rotulo, boolean chaveRotulo) {
		Label label = new Label(rotulo, chaveRotulo);
		label.setHorizontalAlignment(SwingConstantes.CENTER);
		return label;
	}

	public Label criarLabel(String chaveRotulo) {
		return new Label(ExecucaoMensagens.getString(chaveRotulo), false);
	}

	static CheckBox criarCheckBox(String chaveRotulo) {
		return new CheckBox(ExecucaoMensagens.getString(chaveRotulo), false);
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
					ExecucaoPreferencia.setExecucaoPosicaoAbaFichario(nomeValor.valor);
				}
			});
		}
	}
}
