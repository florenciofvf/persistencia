package br.com.persist.plugins.projeto;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JColorChooser;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

import br.com.persist.abstrato.AbstratoConfiguracao;
import br.com.persist.assistencia.Mensagens;
import br.com.persist.assistencia.Muro;
import br.com.persist.componente.CheckBox;
import br.com.persist.componente.Label;
import br.com.persist.componente.Panel;
import br.com.persist.componente.PanelCenter;
import br.com.persist.formulario.Formulario;

public class ProjetoConfiguracao extends AbstratoConfiguracao {
	private final CheckBox chkExibirArqIgnorados = criarCheckBox("label.exibir_arq_ignorados");
	private static final long serialVersionUID = 1L;

	private final transient NomeValor[] posicoes = {
			new NomeValor("label.acima", SwingConstants.TOP, NomeValor.POSICAO_ABA),
			new NomeValor("label.esquerdo", SwingConstants.LEFT, NomeValor.POSICAO_ABA),
			new NomeValor("label.abaixo", SwingConstants.BOTTOM, NomeValor.POSICAO_ABA),
			new NomeValor("label.direito", SwingConstants.RIGHT, NomeValor.POSICAO_ABA) };

	public ProjetoConfiguracao(Formulario formulario) {
		super(formulario, ProjetoMensagens.getString("label.plugin_projeto"));
		montarLayout();
		configurar();
	}

	private void montarLayout() {
		PanelCenter panelPosicoes = criarPainelGrupo(posicoes, ProjetoPreferencia.getProjetoPosicaoAbaFichario());
		chkExibirArqIgnorados.setSelected(ProjetoPreferencia.isExibirArqIgnorados());

		Muro muro = new Muro();
		Label tituloLocalAbas = criarLabelTituloRotulo("label.local_abas");
		muro.camada(Muro.panelGridBorderBottom(tituloLocalAbas, panelPosicoes));
		muro.camada(Muro.panelGridBorderBottom(criarLabelTitulo("label.titulo_painel_elemento_final_rest"),
				new PainelElementoFinalRest()));
		muro.camada(Muro.panelGrid(chkExibirArqIgnorados));
		add(BorderLayout.CENTER, muro);
	}

	private void configurar() {
		chkExibirArqIgnorados
				.addActionListener(e -> ProjetoPreferencia.setExibirArqIgnorados(chkExibirArqIgnorados.isSelected()));
	}

	private class PainelElementoFinalRest extends Panel {
		private Label labelCorAtual = new Label(ProjetoMensagens.getString("label.cor_atual"), false);
		private transient MouseInner mouseInner = new MouseInner();
		private static final long serialVersionUID = 1L;

		private PainelElementoFinalRest() {
			super(new GridLayout(0, 1));
			add(labelCorAtual);
			labelCorAtual.setForeground(ProjetoPreferencia.getCorElementoFinalRest());
			labelCorAtual.setBorder(BorderFactory.createLineBorder(Color.GRAY));
			labelCorAtual.setHorizontalAlignment(SwingConstants.CENTER);
			labelCorAtual.addMouseListener(mouseInner);
		}

		private class MouseInner extends MouseAdapter {
			@Override
			public void mouseClicked(MouseEvent e) {
				Label label = (Label) e.getSource();
				Color color = JColorChooser.showDialog(ProjetoConfiguracao.this, label.getText(),
						label.getForeground());
				if (color == null) {
					return;
				}
				label.setForeground(color);
				ProjetoPreferencia.setCorElementoFinalRest(color);
			}
		}
	}

	private Label criarLabelTituloRotulo(String rotulo) {
		return criarLabelTitulo(ProjetoMensagens.getString(rotulo), false);
	}

	private Label criarLabelTitulo(String chaveRotulo) {
		Label label = new Label(ProjetoMensagens.getString(chaveRotulo), false);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		return label;
	}

	private Label criarLabelTitulo(String rotulo, boolean chaveRotulo) {
		Label label = new Label(rotulo, chaveRotulo);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		return label;
	}

	public Label criarLabel(String chaveRotulo) {
		return new Label(ProjetoMensagens.getString(chaveRotulo), false);
	}

	static CheckBox criarCheckBox(String chaveRotulo) {
		return new CheckBox(ProjetoMensagens.getString(chaveRotulo), false);
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
					ProjetoPreferencia.setProjetoPosicaoAbaFichario(nomeValor.valor);
				}
			});
		}
	}
}