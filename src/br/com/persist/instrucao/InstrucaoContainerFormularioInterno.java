package br.com.persist.instrucao;

import java.awt.BorderLayout;

import br.com.persist.formulario.AbstratoInternalFrame;

public class InstrucaoContainerFormularioInterno extends AbstratoInternalFrame implements InstrucaoContainerListener {
	private static final long serialVersionUID = 1L;
	private final transient InstrucaoContainerFormularioListener listener;
	private final InstrucaoContainer container;

	private InstrucaoContainerFormularioInterno(Instrucao instrucao, InstrucaoContainerFormularioListener listener) {
		super(instrucao.getNome());
		container = new InstrucaoContainer(this, instrucao, this);
		this.listener = listener;
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	@Override
	public void excluirInstrucao(Instrucao instrucao) {
		listener.excluirInstrucao(instrucao);
		fechar();
	}

	public static InstrucaoContainerFormularioInterno criar(Instrucao instrucao,
			InstrucaoContainerFormularioListener listener) {
		return new InstrucaoContainerFormularioInterno(instrucao, listener);
	}
}