package br.com.persist.componente;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JSplitPane;

public class SplitPane extends JSplitPane {
	private static final long serialVersionUID = 1L;

	public SplitPane(int orientacao) {
		super(orientacao);
		setContinuousLayout(true);
		setOneTouchExpandable(true);
		setBorder(BorderFactory.createEmptyBorder());
	}

	public SplitPane(int newOrientation, Component newLeftComponent, Component newRightComponent) {
		super(newOrientation, newLeftComponent, newRightComponent);
		setContinuousLayout(true);
		setOneTouchExpandable(true);
		setBorder(BorderFactory.createEmptyBorder());
	}
}