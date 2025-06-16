package br.com.persist.plugins.objeto.macro;

import java.awt.Color;

import br.com.persist.assistencia.AssistenciaException;
import br.com.persist.plugins.objeto.Objeto;
import br.com.persist.plugins.objeto.ObjetoSuperficie;
import br.com.persist.plugins.objeto.Relacao;
import br.com.persist.plugins.objeto.macro.MacroProvedor.Instrucao;

public class Macro {
	private Macro() {
	}

	static class DeslocamentoXIdDescricao extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			relacao.setDeslocamentoXDesc((Integer) valor);
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setDeslocamentoXId((Integer) valor);
		}
	}

	static class DeslocamentoYIdDescricao extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			relacao.setDeslocamentoYDesc((Integer) valor);
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setDeslocamentoYId((Integer) valor);
		}
	}

	static class DesenharIdDescricao extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			relacao.setDesenharDescricao((Boolean) valor);
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setDesenharId((Boolean) valor);
		}
	}

	static class PrefixoNomeTabela extends Instrucao {
		@Override
		public void executar(Relacao relacao) throws MacroException {
			throw new MacroException();
		}

		@Override
		public void executar(Objeto objeto) {
			if (valor != null) {
				objeto.setPrefixoNomeTabela(valor.toString());
			}
		}
	}

	static class MargemInferior extends Instrucao {
		@Override
		public void executar(Relacao relacao) throws MacroException {
			throw new MacroException();
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setMargemInferior((Integer) valor);
		}
	}

	static class Intervalo extends Instrucao {
		@Override
		public void executar(Relacao relacao) throws MacroException {
			throw new MacroException();
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setIntervalo((Integer) valor);
		}
	}

	static class Quebrado extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			relacao.setQuebrado((Boolean) valor);
		}

		@Override
		public void executar(Objeto objeto) throws MacroException {
			throw new MacroException();
		}
	}

	static class ColunaInfo extends Instrucao {
		@Override
		public void executar(Relacao relacao) throws MacroException {
			throw new MacroException();
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setColunaInfo((Boolean) valor);
		}
	}

	static class AbrirAuto extends Instrucao {
		@Override
		public void executar(Relacao relacao) throws MacroException {
			throw new MacroException();
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setAbrirAuto((Boolean) valor);
		}
	}

	static class LinkAuto extends Instrucao {
		@Override
		public void executar(Relacao relacao) throws MacroException {
			throw new MacroException();
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setLinkAuto((Boolean) valor);
		}
	}

	static class LarguraRotulos extends Instrucao {
		@Override
		public void executar(Relacao relacao) throws MacroException {
			throw new MacroException();
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setLarguraRotulos((Boolean) valor);
		}
	}

	static class Ignorar extends Instrucao {
		@Override
		public void executar(Relacao relacao) throws MacroException {
			throw new MacroException();
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setIgnorar((Boolean) valor);
		}
	}

	static class Grupo extends Instrucao {
		@Override
		public void executar(Relacao relacao) throws MacroException {
			throw new MacroException();
		}

		@Override
		public void executar(Objeto objeto) {
			if (valor != null) {
				objeto.setGrupo(valor.toString());
			}
		}
	}

	static class Ccsc extends Instrucao {
		@Override
		public void executar(Relacao relacao) throws MacroException {
			throw new MacroException();
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setCcsc((Boolean) valor);
		}
	}

	static class Sane extends Instrucao {
		@Override
		public void executar(Relacao relacao) throws MacroException {
			throw new MacroException();
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setSane((Boolean) valor);
		}
	}

	static class Bpnt extends Instrucao {
		@Override
		public void executar(Relacao relacao) throws MacroException {
			throw new MacroException();
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setBpnt((Boolean) valor);
		}
	}

	static class Transparente extends Instrucao {
		@Override
		public void executar(Relacao relacao) throws MacroException {
			throw new MacroException();
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setTransparente((Boolean) valor);
		}
	}

	static class ClonarAoDestacar extends Instrucao {
		@Override
		public void executar(Relacao relacao) throws MacroException {
			throw new MacroException();
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setClonarAoDestacar((Boolean) valor);
		}
	}

	static class AjusteAutoForm extends Instrucao {
		@Override
		public void executar(Relacao relacao) throws MacroException {
			throw new MacroException();
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setAjusteAutoForm((Boolean) valor);
		}
	}

	static class AjusteLargForm extends Instrucao {
		@Override
		public void executar(Relacao relacao) throws MacroException {
			throw new MacroException();
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setAjustarLargura((Boolean) valor);
		}
	}

	static class Complemento extends Instrucao {
		@Override
		public void executar(Relacao relacao) throws MacroException {
			throw new MacroException();
		}

		@Override
		public void executar(Objeto objeto) {
			if (valor != null) {
				objeto.setComplemento(valor.toString());
			}
		}
	}

	static class PontoDestino extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			relacao.setPontoDestino((Boolean) valor);
		}

		@Override
		public void executar(Objeto objeto) throws MacroException {
			throw new MacroException();
		}
	}

	static class PontoOrigem extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			relacao.setPontoOrigem((Boolean) valor);
		}

		@Override
		public void executar(Objeto objeto) throws MacroException {
			throw new MacroException();
		}
	}

	static class CorFundo extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			relacao.setCor((Color) valor);
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setCor((Color) valor);
		}
	}

	static class CorFonte extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			relacao.setCorFonte((Color) valor);
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setCorFonte((Color) valor);
		}
	}

	static class Icone extends Instrucao {
		@Override
		public void executar(Relacao relacao) throws MacroException {
			throw new MacroException();
		}

		@Override
		public void executar(Objeto objeto) throws AssistenciaException {
			if (valor != null) {
				objeto.setIcone(valor.toString());
			} else {
				objeto.limparIcone();
			}
		}
	}

	static class XPos extends Instrucao {
		@Override
		public void executar(Relacao relacao) throws MacroException {
			throw new MacroException();
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setX((Integer) valor);
		}

		@Override
		public void posExecutar(ObjetoSuperficie superficie, Objeto objeto, Relacao relacao) {
			if (superficie != null && objeto != null) {
				superficie.localizarInternalFormulario(objeto);
			}
		}
	}

	static class YPos extends Instrucao {
		@Override
		public void executar(Relacao relacao) throws MacroException {
			throw new MacroException();
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setY((Integer) valor);
		}
	}
}