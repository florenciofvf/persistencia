package br.com.persist.plugins.mapa.organiza;

import br.com.persist.assistencia.ArgumentoException;
import br.com.persist.plugins.mapa.Objeto;

/*
 * organizadorParametros="grau deslocamento"
 */
public class OrganizadorBola extends Organizador {
	private int deslocamentoDelta;
	private int deslocamento;
	private int grau = 15;
	private int estagio;
	private int soma;

	public OrganizadorBola() {
		super("bola");
		reiniciar();
	}

	@Override
	public void parametros(String string) throws ArgumentoException {
		try {
			String[] strings = string.split(" ");
			grau = Integer.parseInt(strings[0]);
			deslocamentoDelta = Integer.parseInt(strings[1]);
		} catch (Exception e) {
			throw new ArgumentoException("O organizador bola necessita do parametro grau e deslocamento.");
		}
	}

	@Override
	public void organizar(Objeto objeto) {
		if (estagio == 0) {
			objeto.getVetor().rotacaoZ(deslocamento);
			objeto.getVetor().rotacaoY(soma);
			soma += grau;
			if (soma > 340) {
				soma = 0;
				estagio++;
				deslocamento += deslocamentoDelta;
			}
		} else if (estagio == 1) {
			objeto.getVetor().rotacaoY(deslocamento);
			objeto.getVetor().rotacaoZ(soma);
			soma += grau;
			if (soma > 340) {
				soma = 0;
				estagio--;
				deslocamento += deslocamentoDelta;
			}
		}
	}

	@Override
	public void reiniciar() {
		deslocamento = 0;
		estagio = 0;
		soma = 0;
	}
}