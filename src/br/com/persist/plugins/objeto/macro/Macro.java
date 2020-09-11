package br.com.persist.plugins.objeto.macro;

import java.awt.Color;

import br.com.persist.plugins.objeto.Objeto;
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

	static class Quebrado extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			relacao.setQuebrado((Boolean) valor);
		}

		@Override
		public void executar(Objeto objeto) {
			throw new UnsupportedOperationException();
		}
	}

	static class ColunaInfo extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setColunaInfo((Boolean) valor);
		}
	}

	static class AbrirAuto extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setAbrirAuto((Boolean) valor);
		}
	}

	static class LinkAuto extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setLinkAuto((Boolean) valor);
		}
	}

	static class Ccsc extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setCcsc((Boolean) valor);
		}
	}

	static class Bpnt extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setBpnt((Boolean) valor);
		}
	}

	static class Transparente extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setTransparente((Boolean) valor);
		}
	}

	static class CopiarDestacado extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setCopiarDestacado((Boolean) valor);
		}
	}

	static class AjusteAutoForm extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setAjusteAutoForm((Boolean) valor);
		}
	}

	static class AjusteAutoEnter extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setAjusteAutoEnter((Boolean) valor);
		}
	}

	static class PontoDestino extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			relacao.setPontoDestino((Boolean) valor);
		}

		@Override
		public void executar(Objeto objeto) {
			throw new UnsupportedOperationException();
		}
	}

	static class PontoOrigem extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			relacao.setPontoOrigem((Boolean) valor);
		}

		@Override
		public void executar(Objeto objeto) {
			throw new UnsupportedOperationException();
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
		public void executar(Relacao relacao) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void executar(Objeto objeto) {
			if (valor != null) {
				objeto.setIcone(valor.toString());
			} else {
				objeto.limparIcone();
			}
		}
	}

	static class Cor extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			relacao.setCor((Color) valor);
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setCor((Color) valor);
		}
	}

	static class XPos extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setX((Integer) valor);
		}
	}

	static class YPos extends Instrucao {
		@Override
		public void executar(Relacao relacao) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void executar(Objeto objeto) {
			objeto.setY((Integer) valor);
		}
	}
}