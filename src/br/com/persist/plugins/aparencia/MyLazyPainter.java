package br.com.persist.plugins.aparencia;

import java.awt.Dimension;
import java.awt.Insets;
import java.lang.reflect.Constructor;

import javax.swing.UIDefaults;

public class MyLazyPainter implements UIDefaults.LazyValue {
	private int which;
	private MyAbstractRegionPainter.PaintContext ctx;
	private String className;

	MyLazyPainter(String className, int which, Insets insets, Dimension canvasSize, boolean inverted) {
		if (className == null) {
			throw new IllegalArgumentException("The className must be specified");
		}
		this.className = className;
		this.which = which;
		this.ctx = new MyAbstractRegionPainter.PaintContext(insets, canvasSize, inverted);
	}

	MyLazyPainter(String className, int which, Insets insets, Dimension canvasSize, boolean inverted,
			MyAbstractRegionPainter.PaintContext.CacheMode cacheMode, double maxH, double maxV) {
		if (className == null) {
			throw new IllegalArgumentException("The className must be specified");
		}
		this.className = className;
		this.which = which;
		this.ctx = new MyAbstractRegionPainter.PaintContext(insets, canvasSize, inverted, cacheMode, maxH, maxV);
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object createValue(UIDefaults table) {
		try {
			Class c;
			Object cl;
			if (table == null || !((cl = table.get("ClassLoader")) instanceof ClassLoader)) {
				cl = Thread.currentThread().getContextClassLoader();
				if (cl == null) {
					cl = ClassLoader.getSystemClassLoader();
				}
			}
			c = Class.forName(className, true, (ClassLoader) cl);
			Constructor constructor = c.getConstructor(MyAbstractRegionPainter.PaintContext.class, int.class);
			if (constructor == null) {
				throw new NullPointerException("Failed to find the constructor for the class: " + className);
			}
			return constructor.newInstance(ctx, which);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}