package br.com.persist.plugins.mapa.organiza;

import br.com.persist.plugins.mapa.forma.Forma;

/*
 * 
 * organizador="bola" organizadorParametros="grau deslocamento"
 * 
 */
public class OrganizadorBola implements Organizador {
	private int deslocamentoDelta;
	private int deslocamento;
	private int grau = 15;
	private int estagio;
	private int soma;

	public OrganizadorBola() {
		reiniciar();
	}

	@Override
	public void parametros(String string) {
		try {
			String[] strings = string.split(" ");
			grau = Integer.parseInt(strings[0]);
			deslocamentoDelta = Integer.parseInt(strings[1]);
		} catch (Exception e) {
			throw new IllegalArgumentException("O organizador bola necessita do parametro grau e deslocamento.");
		}
	}

	@Override
	public void organizar(Forma forma) {
		if (estagio == 0) {
			forma.vetor.rotacaoZ(deslocamento);
			forma.vetor.rotacaoY(soma);
			soma += grau;
			if (soma > 340) {
				soma = 0;
				estagio++;
				deslocamento += deslocamentoDelta;
			}
		} else if (estagio == 1) {
			forma.vetor.rotacaoY(deslocamento);
			forma.vetor.rotacaoZ(soma);
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