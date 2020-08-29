package br.com.persist.instrucao;

import java.awt.BorderLayout;

import br.com.persist.formulario.AbstratoInternalFrame;

public class InstrucaoContainerFormularioInterno extends AbstratoInternalFrame {
	private static final long serialVersionUID = 1L;
	private final transient InstrucaoContainerFormularioListener listener;
	private final InstrucaoContainer container;

	private InstrucaoContainerFormularioInterno(Instrucao instrucao, InstrucaoContainerFormularioListener listener) {
		super(instrucao.getNome());
		container = new InstrucaoContainer(this, instrucao, this::excluirInstrucao);
		this.listener = listener;
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	public void excluirInstrucao(Instrucao instrucao) {
		listener.excluirInstrucao(instrucao);
		fechar();
	}

	public static InstrucaoContainerFormularioInterno criar(Instrucao instrucao,
			InstrucaoContainerFormularioListener listener) {
		return new InstrucaoContainerFormularioInterno(instrucao, listener);
	}
}