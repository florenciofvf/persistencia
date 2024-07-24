package br.com.persist.plugins.instrucao.biblionativo;

import java.math.BigInteger;
import java.util.Objects;

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

	public void addLista(Lista lista) {
		if (lista != null) {
			long size = lista.size().longValue();
			for (long i = 0; i < size; i++) {
				Object o = lista.get(i);
				add(o);
			}
		}
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
	public java.lang.String toString() {
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

	@Override
	public int hashCode() {
		int hashCode = 1;
		No no = cabeca;
		while (no != null) {
			hashCode = 31 * hashCode + no.hashCode();
			no = no.proximo;
		}
		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		if (obj instanceof Lista) {
			Lista other = (Lista) obj;
			if (!comprimento.equals(other.comprimento)) {
				return false;
			}
			No no = cabeca;
			No noOther = other.cabeca;
			while (no != null && noOther != null) {
				if (!no.equals(noOther)) {
					return false;
				}
				noOther = noOther.proximo;
				no = no.proximo;
			}
			return no == null && noOther == null;
		}
		return false;
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

		@Override
		public int hashCode() {
			return Objects.hash(valor);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			} else if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			if (obj instanceof No) {
				No other = (No) obj;
				return Objects.equals(valor, other.valor);
			}
			return false;
		}
	}
}