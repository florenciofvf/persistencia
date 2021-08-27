package br.com.persist.abstrato;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.KeyStroke;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import br.com.persist.assistencia.Constantes;
import br.com.persist.assistencia.Preferencias;
import br.com.persist.componente.Janela;

public abstract class AbstratoInternalFrame extends JInternalFrame implements Janela {
	private static final long serialVersionUID = 1L;

	public AbstratoInternalFrame(String titulo) {
		super(titulo, true, true, true, true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLayout(new BorderLayout());
		setSize(Constantes.SIZE);
		setActionESC();
		configurar();
	}

	@Override
	public final void fechar() {
		dispose();
	}

	private void setActionESC() {
		JComponent component = (JComponent) getContentPane();
		InputMap inputMap = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), Constantes.ESC);
		ActionMap actionMap = component.getActionMap();
		actionMap.put(Constantes.ESC, actionEsc());
	}

	private Action actionEsc() {
		return new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (Preferencias.isFecharComESCInternal()) {
					fechar();
				}
			}
		};
	}

	private void configurar() {
		addInternalFrameListener(new InternalFrameAdapter() {
			@Override
			public void internalFrameActivated(InternalFrameEvent e) {
				executarAoAtivarFormulario();
			}

			@Override
			public void internalFrameOpened(InternalFrameEvent e) {
				executarAoAbrirFormulario();
			}

			@Override
			public void internalFrameClosing(InternalFrameEvent e) {
				executarAoFecharFormulario();
			}
		});
	}

	public void executarAoAtivarFormulario() {
	}

	public void executarAoAbrirFormulario() {
	}

	public void executarAoFecharFormulario() {
	}

	public void excluirContainer() {
	}
}