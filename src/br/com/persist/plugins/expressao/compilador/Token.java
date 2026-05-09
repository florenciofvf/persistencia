package br.com.persist.plugins.expressao.compilador;

import java.util.Objects;

import br.com.persist.plugins.expressao.condicional.IFContexto;
import br.com.persist.plugins.expressao.constante.ConstanteContexto;
import br.com.persist.plugins.expressao.funcao.FuncaoContexto;
import br.com.persist.plugins.expressao.funcao.FuncaoNativaContexto;
import br.com.persist.plugins.expressao.loop.WhileContexto;
import br.com.persist.plugins.expressao.organiza.AliasContexto;
import br.com.persist.plugins.expressao.organiza.PacoteContexto;
import br.com.persist.plugins.expressao.retorno.RetornoContexto;

public class Token {
	public static final int CONSTANTE = 1;
	public static final int PARAMETRO = 2;
	private boolean consumido;
	final String string;
	final int indice;
	String original;
	final Tipo tipo;
	int style;

	public Token(String string, Tipo tipo, int indice) {
		this.string = Objects.requireNonNull(string);
		this.tipo = Objects.requireNonNull(tipo);
		this.indice = indice;
	}

	public int getIndice() {
		return indice;
	}

	public boolean isConsumido() {
		return consumido;
	}

	public void setConsumido(boolean consumido) {
		this.consumido = consumido;
	}

	public void setOriginal(String original) {
		this.original = original;
	}

	public String getOriginal() {
		return original != null ? original : string;
	}

	public String getString() {
		return string;
	}

	public boolean isStyle(int tipo) {
		return style == tipo;
	}

	public void setStyle(int style) {
		this.style = style;
	}

	public enum Tipo {
		CHAVE("[a-z][A-Z][0-9]"), CHAVE2("chave.chave"), CHAVEN("chave.chave.chave"), PONTO_E_VIRGULA(";"),
		ABRE_PARENTESE("("), FECHA_PARENTESE(")"), ABRE_COLCHETE("["), FECHA_COLCHETE("]"), ABRE_CHAVE("{"),
		FECHA_CHAVE("}"), ATRIBUICAO("="), VIRGULA(","), OPERADOR(""), STRING("'xyz'"), INTEIRO("[0-9]"),
		FLUTUANTE("[0-9].[0-9]"), COMENTARIO("// ou /*cmt*/"), VIRTUAL(""), EL("${lista:head > pessoa.nome}");

		private String desc;

		Tipo(String desc) {
			this.desc = desc;
		}

		public String getDesc() {
			return desc;
		}
	}

	public boolean isEspecial() {
		return isAbreChave() || isFechaChave() || isAbreParentese() || isFechaParentese() || isAbreColchete()
				|| isFechaColchete();
	}

	@Override
	public String toString() {
		return string + "@" + tipo;
	}

	public boolean isPontoEVirgula() {
		return tipo == Tipo.PONTO_E_VIRGULA;
	}

	public boolean isAbreParentese() {
		return tipo == Tipo.ABRE_PARENTESE;
	}

	public boolean isFechaParentese() {
		return tipo == Tipo.FECHA_PARENTESE;
	}

	public boolean isAbreColchete() {
		return tipo == Tipo.ABRE_COLCHETE;
	}

	public boolean isFechaColchete() {
		return tipo == Tipo.FECHA_COLCHETE;
	}

	public boolean isAbreChave() {
		return tipo == Tipo.ABRE_CHAVE;
	}

	public boolean isFechaChave() {
		return tipo == Tipo.FECHA_CHAVE;
	}

	public boolean isComentario() {
		return tipo == Tipo.COMENTARIO;
	}

	public boolean isAtribuicao() {
		return tipo == Tipo.ATRIBUICAO;
	}

	public boolean isFlutuante() {
		return tipo == Tipo.FLUTUANTE;
	}

	public boolean isOperador() {
		return tipo == Tipo.OPERADOR;
	}

	public boolean isVirgula() {
		return tipo == Tipo.VIRGULA;
	}

	public boolean isInteiro() {
		return tipo == Tipo.INTEIRO;
	}

	public boolean isString() {
		return tipo == Tipo.STRING;
	}

	public boolean isVirtual() {
		return tipo == Tipo.VIRTUAL;
	}

	public boolean isChave() {
		return tipo == Tipo.CHAVE;
	}

	public boolean isChave2() {
		return tipo == Tipo.CHAVE2;
	}

	public boolean isChaveN() {
		return tipo == Tipo.CHAVEN;
	}

	public boolean isEL() {
		return tipo == Tipo.EL;
	}

	public boolean chave() {
		return isChave() || isChave2() || isChaveN();
	}

	public boolean isNativo() {
		return isString() || isInteiro() || isFlutuante();
	}

	public boolean isOperadorMOuM() {
		return isOperador() && ("+".equals(string) || "-".equals(string));
	}

	public boolean isReservado() {
		return ConstanteContexto.CONST.equals(string) || FuncaoNativaContexto.DEFUN_NATIVE.equals(string)
				|| RetornoContexto.RETURN.equals(string) || PacoteContexto.PACKAGE.equals(string)
				|| FuncaoContexto.DEFUN.equals(string) || WhileContexto.WHILE.equals(string)
				|| IFContexto.ELSEIF.equals(string) || IFContexto.ELSE.equals(string) || IFContexto.IF.equals(string)
				|| AliasContexto.ALIAS.equals(string);
	}
}