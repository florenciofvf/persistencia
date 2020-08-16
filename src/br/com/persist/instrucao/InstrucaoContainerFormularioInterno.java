package br.com.persist.instrucao;

import java.awt.BorderLayout;

import br.com.persist.formulario.AbstratoInternalFrame;
import br.com.persist.util.IJanela;

public class InstrucaoContainerFormularioInterno extends AbstratoInternalFrame
		implements IJanela, InstrucaoContainerListener {
	private static final long serialVersionUID = 1L;
	private final transient InstrucaoContainerFormularioListener listener;
	private final InstrucaoContainer container;

	public InstrucaoContainerFormularioInterno(Instrucao instrucao, InstrucaoContainerFormularioListener listener) {
		super(instrucao.getNome());
		container = new InstrucaoContainer(this, instrucao, this);
		this.listener = listener;
		montarLayout();
	}

	private void montarLayout() {
		add(BorderLayout.CENTER, container);
	}

	@Override
	public void fechar() {
		dispose();
	}

	@Override
	public void excluirInstrucao(Instrucao instrucao) {
		listener.excluirInstrucao(instrucao);
		fechar();
	}
}