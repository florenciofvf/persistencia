package br.com.persist.assistencia;

import java.math.BigInteger;

public class Lista {
	private BigInteger comprimento;
	private No cabeca;
	private No cauda;

	public Lista() {
		clear();
	}

	public Object head() {
		check();
		return cabeca.valor;
	}

	private void check() {
		if (comprimento.intValue() == 0) {
			throw new IllegalStateException("Lista Vazia.");
		}
	}

	public Lista tail() {
		check();
		Lista resposta = new Lista();
		if (comprimento.intValue() == 1) {
			return resposta;
		}
		No no = cabeca.proximo;
		while (no != null) {
			resposta.add(no.valor);
			no = no.proximo;
		}
		return resposta;
	}

	public BigInteger size() {
		return comprimento;
	}

	public BigInteger empty() {
		return comprimento.intValue() == 0 ? createTrue() : createFalse();
	}

	private BigInteger createFalse() {
		return BigInteger.valueOf(0);
	}

	private BigInteger createTrue() {
		return BigInteger.valueOf(1);
	}

	public BigInteger notContains(Object o) {
		BigInteger respo = contains(o);
		BigInteger falso = createFalse();
		BigInteger verda = createTrue();
		return respo.equals(verda) ? falso : verda;
	}

	public BigInteger contains(Object o) {
		if (comprimento.intValue() == 0) {
			return createFalse();
		}
		No no = cabeca;
		if (o == null) {
			while (no != null) {
				if (no.valor == null) {
					return createTrue();
				}
				no = no.proximo;
			}
		} else {
			while (no != null) {
				if (o.equals(no.valor)) {
					return createTrue();
				}
				no = no.proximo;
			}
		}
		return createFalse();
	}

	public Object add(Object o) {
		No no = new No(o);
		if (cabeca == null) {
			cabeca = no;
		}
		if (cauda != null) {
			cauda.proximo = no;
		}
		cauda = no;
		comprimento = comprimento.add(BigInteger.valueOf(1));
		return o;
	}

	private No getNo(long indice) {
		check(indice);
		long c = 0;
		No no = cabeca;
		while (c < indice) {
			no = no.proximo;
			c++;
		}
		return no;
	}

	public Object get(long indice) {
		return getNo(indice).valor;
	}

	public Object set(long indice, Object valor) {
		getNo(indice).valor = valor;
		return valor;
	}

	public BigInteger clear() {
		comprimento = BigInteger.valueOf(0);
		cabeca = null;
		cauda = null;
		return createTrue();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		No no = cabeca;
		while (no != null) {
			if (sb.length() > 0) {
				sb.append(", ");
			}
			sb.append(no.valor);
			no = no.proximo;
		}
		sb.insert(0, "[");
		sb.insert(sb.length(), "]");
		return sb.toString();
	}

	private void check(long indice) {
		if (indice < 0 || indice >= comprimento.longValue()) {
			throw new IndexOutOfBoundsException("indice=" + indice + ", size=" + comprimento);
		}
	}

	private class No {
		Object valor;
		No proximo;

		private No(Object valor, No proximo) {
			this.proximo = proximo;
			this.valor = valor;
		}

		private No(Object valor) {
			this(valor, null);
		}
	}
}