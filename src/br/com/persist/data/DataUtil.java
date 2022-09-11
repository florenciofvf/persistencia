package br.com.persist.data;

public class DataUtil {
	private DataUtil() {
	}

	public static String toString(Tipo tipo) {
		ContainerStringBuilder container = new ContainerStringBuilder();
		if (tipo != null) {
			tipo.export(container, 0);
		}
		return container.toString();
	}
}