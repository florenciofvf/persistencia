package br.com.persist.plugins.robo;

import java.awt.Robot;
import java.util.ArrayList;
import java.util.List;
import static java.lang.Integer.parseInt;

public class RoboProvedor {
	private static final List<Robo> lista = new ArrayList<>();

	private RoboProvedor() {
	}

	public static Robo getRobo(String nome) {
		for (Robo obj : lista) {
			if (obj.getNome().equals(nome)) {
				return obj;
			}
		}
		return null;
	}

	static {
		lista.add(new MouseRelease());
		lista.add(new MouseWheel());
		lista.add(new KeyRelease());
		lista.add(new MousePress());
		lista.add(new MouseMove());
		lista.add(new KeyPress());
		lista.add(new Delay());
		lista.add(new Break());
	}
}

class MouseMove extends Robo {
	protected MouseMove() {
		super("mouseMove");
	}

	@Override
	void processar(Robot robot, String[] params) {
		if (params.length == 3) {
			robot.delay(DELAY);
			robot.mouseMove(parseInt(params[1]), parseInt(params[2]));
		}
	}
}

class MousePress extends Robo {
	protected MousePress() {
		super("mousePress");
	}

	@Override
	void processar(Robot robot, String[] params) {
		if (params.length == 2) {
			robot.delay(DELAY);
			robot.mousePress(parseInt(params[1]));
		}
	}
}

class MouseRelease extends Robo {
	protected MouseRelease() {
		super("mouseRelease");
	}

	@Override
	void processar(Robot robot, String[] params) {
		if (params.length == 2) {
			robot.delay(DELAY);
			robot.mouseRelease(parseInt(params[1]));
		}
	}
}

class MouseWheel extends Robo {
	protected MouseWheel() {
		super("mouseWheel");
	}

	@Override
	void processar(Robot robot, String[] params) {
		if (params.length == 2) {
			robot.delay(DELAY);
			robot.mouseWheel(parseInt(params[1]));
		}
	}
}

class KeyPress extends Robo {
	protected KeyPress() {
		super("keyPress");
	}

	@Override
	void processar(Robot robot, String[] params) {
		if (params.length == 2) {
			robot.delay(DELAY);
			robot.keyPress(parseInt(params[1]));
		}
	}
}

class KeyRelease extends Robo {
	protected KeyRelease() {
		super("keyRelease");
	}

	@Override
	void processar(Robot robot, String[] params) {
		if (params.length == 2) {
			robot.delay(DELAY);
			robot.keyRelease(parseInt(params[1]));
		}
	}
}

class Delay extends Robo {
	protected Delay() {
		super("delay");
	}

	@Override
	void processar(Robot robot, String[] params) {
		if (params.length == 2) {
			robot.delay(parseInt(params[1]));
		} else {
			robot.delay(DELAY);
		}
	}
}

class Break extends Robo {
	protected Break() {
		super("break");
	}

	@Override
	void processar(Robot robot, String[] params) {
		//
	}
}