package br.com.persist.plugins.mapa;

import java.awt.BorderLayout;
import java.awt.Insets;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

import br.com.persist.abstrato.AbstratoConfiguracao;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Muro;
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.Label;
import br.com.persist.componente.PanelCenter;
import br.com.persist.formulario.Formulario;

public class MapaConfiguracao extends AbstratoConfiguracao {
	private static final long serialVersionUID = 1L;
	private final CheckBox chkExibirArqIgnorados = criarCheckBox("label.exibir_arq_ignorados");

	private final transient NomeValor[] posicoes = {
			new NomeValor("label.acima", SwingConstants.TOP, NomeValor.POSICAO_ABA),
			new NomeValor("label.esquerdo", SwingConstants.LEFT, NomeValor.POSICAO_ABA),
			new NomeValor("label.abaixo", SwingConstants.BOTTOM, NomeValor.POSICAO_ABA),
			new NomeValor("label.direito", SwingConstants.RIGHT, NomeValor.POSICAO_ABA) };

	public MapaConfiguracao(Formulario formulario) {
		super(formulario, MapaMensagens.getString("label.plugin_mapa"));
		montarLayout();
		configurar();
	}

	private void montarLayout() {
		PanelCenter panelPosicoes = criarPainelGrupo(posicoes, MapaPreferencia.getMapaPosicaoAbaFichario());
		chkExibirArqIgnorados.setSelected(MapaPreferencia.isExibirArqInvisivel());

		Muro muro = new Muro();
		Label tituloLocalAbas = criarLabelTituloRotulo("label.local_abas");
		muro.camada(Muro.panelGridBorderBottom(tituloLocalAbas, panelPosicoes));
		muro.camada(Muro.panelGrid(chkExibirArqIgnorados));
		add(BorderLayout.CENTER, muro);
	}

	private void configurar() {
		chkExibirArqIgnorados
				.addActionListener(e -> MapaPreferencia.setExibirArqInvisivel(chkExibirArqIgnorados.isSelected()));
	}

	private Label criarLabelTituloRotulo(String rotulo) {
		return criarLabelTitulo(MapaMensagens.getString(rotulo), false);
	}

	private Label criarLabelTitulo(String rotulo, boolean chaveRotulo) {
		Label label = new Label(rotulo, chaveRotulo);
		label.setHorizontalAlignment(Label.CENTER);
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
		ButtonGroup grupo = new ButtonGroup();
		for (int i = 0; i < nomeValores.length; i++) {
			RadioPosicao radio = new RadioPosicao(nomeValores[i]);
			radio.setSelected(radio.nomeValor.valor == padrao);
			radio.setMargin(new Insets(5, 10, 5, 5));
			panel.add(radio);
			grupo.add(radio);
		}
		return panel;
	}

	private class RadioPosicao extends JRadioButton {
		private static final long serialVersionUID = 1L;
		private final transient NomeValor nomeValor;

		private RadioPosicao(NomeValor nomeValor) {
			super(nomeValor.nome);
			this.nomeValor = nomeValor;
			addActionListener(e -> {
				if (nomeValor.tipo == NomeValor.POSICAO_ABA) {
					MapaPreferencia.setMapaPosicaoAbaFichario(nomeValor.valor);
				}
			});
		}
	}

	static CheckBox criarCheckBox(String chaveRotulo) {
		return new CheckBox(MapaMensagens.getString(chaveRotulo), false);
	}

	public Label criarLabel(String chaveRotulo) {
		return new Label(MapaMensagens.getString(chaveRotulo), false);
	}
}