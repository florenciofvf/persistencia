package br.com.persist.plugins.objeto;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;

import br.com.persist.assistencia.AssistenciaException;
import br.com.persist.assistencia.Util;
import br.com.persist.plugins.objeto.macro.MacroDialogo;
import br.com.persist.plugins.objeto.macro.MacroException;
import br.com.persist.plugins.objeto.macro.MacroProvedor;

public class MacroManager {
	final ObjetoSuperficie superficie;

	MacroManager(ObjetoSuperficie superficie) {
		this.superficie = superficie;
	}

	javax.swing.Action threadProcessar = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			ObjetoSuperficieUtil.processar(superficie);
		}
	};

	javax.swing.Action threadDesativar = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			ObjetoSuperficieUtil.desativar(superficie);
		}
	};

	javax.swing.Action macroLista = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (MacroProvedor.isEmpty()) {
				return;
			}
			MacroDialogo.criar(superficie.container.getFrame());
		}
	};

	javax.swing.Action macro = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			List<MacroProvedor.Instrucao> instrucoes = MacroProvedor.getInstrucoes();
			if (instrucoes.isEmpty()) {
				return;
			}
			try {
				macroObjetos(instrucoes);
				macroRelacoes(instrucoes);
				superficie.repaint();
			} catch (AssistenciaException ex) {
				Util.mensagem(superficie, ex.getMessage());
			}
		}

		private void macroObjetos(List<MacroProvedor.Instrucao> instrucoes) throws AssistenciaException {
			for (Objeto objeto : superficie.getObjetos()) {
				if (objeto.isSelecionado()) {
					for (MacroProvedor.Instrucao instrucao : instrucoes) {
						try {
							instrucao.executar(objeto);
							instrucao.posExecutar(superficie, objeto, null);
						} catch (MacroException ex) {
							Util.mensagem(superficie, ex.getMessage());
						}
					}
				}
			}
		}

		private void macroRelacoes(List<MacroProvedor.Instrucao> instrucoes) {
			for (Relacao relacao : superficie.getRelacoes()) {
				if (relacao.isSelecionado()) {
					for (MacroProvedor.Instrucao instrucao : instrucoes) {
						try {
							instrucao.executar(relacao);
							instrucao.posExecutar(superficie, null, relacao);
						} catch (MacroException ex) {
							Util.mensagem(superficie, ex.getMessage());
						}
					}
				}
			}
		}
	};

	javax.swing.Action zoomMenos = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			for (Objeto objeto : superficie.getObjetos()) {
				objeto.zoomMenos();
			}
			superficie.repaint();
		}
	};

	javax.swing.Action zoomMais = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			for (Objeto objeto : superficie.getObjetos()) {
				objeto.zoomMais();
			}
			superficie.repaint();
		}
	};
}