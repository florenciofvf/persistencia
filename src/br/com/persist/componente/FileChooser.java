package br.com.persist.componente;

import java.io.File;

import javax.swing.JFileChooser;

public class FileChooser extends JFileChooser {
	public static final int DIRECTORIES = JFileChooser.DIRECTORIES_ONLY;
	private static final long serialVersionUID = 6638442146727886569L;
	public static final int APPROVE = JFileChooser.APPROVE_OPTION;

	public FileChooser(String currentDirectoryPath) {
		super(currentDirectoryPath);
	}

	public FileChooser(File currentDirectory) {
		super(currentDirectory);
	}

	public FileChooser() {
		super();
	}
}