package br.com.persist.plugins.instrucao.compilador;

import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;

public class IFEqContexto extends Container implements Salto {
	public static final String IF_EQ = "ifeq";
	int posicao;

	@Override
	public void indexar(AtomicInteger atomic) {
		super.indexar(atomic);
		indice = atomic.getAndIncrement();
	}

	@Override
	public void configPontoDesvio() {
		IFContexto ifContexto = (IFContexto) getPai();
		if (ifContexto.getUltimo() instanceof ElseContexto) {
			ElseContexto elseContexto = (ElseContexto) ifContexto.getUltimo();
			CorpoContexto corpo = elseContexto.getCorpo();
			if (corpo.isEmpty()) {
				configDesvio(ifContexto);
			} else {
				corpo.indexar(this);
			}
		} else {
			configDesvio(ifContexto);
		}
	}

	private void configDesvio(IFContexto ifContexto) {
		//
	}

	@Override
	public void salvar(PrintWriter pw) {
		super.salvar(pw);
		print(pw, IF_EQ, "" + posicao);
	}

	@Override
	public int getPosicao() {
		return posicao;
	}

	@Override
	public void setPosicao(int i) {
		posicao = i;
	}
}