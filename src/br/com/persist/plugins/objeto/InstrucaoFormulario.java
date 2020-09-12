package br.com.persist.plugins.objeto;

import java.awt.BorderLayout;

import br.com.persist.abstrato.AbstratoInternalFrame;

public class InstrucaoFormulario extends AbstratoInternalFrame {
	private static final long serialVersionUID = 1L;
	private final transient InstrucaoFormularioListener listener;
	private final InstrucaoContainer container;

	private InstrucaoFormulario(Instrucao instrucao, InstrucaoFormularioListener listener) {
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

	public static InstrucaoFormulario criar(Instrucao instrucao, InstrucaoFormularioListener listener) {
		return new InstrucaoFormulario(instrucao, listener);
	}
}