package br.com.persist.plugins.aparencia;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;

public class MyInternalFramePainter extends MyAbstractRegionPainter {
	static final int BACKGROUND_ENABLED = 1;
	static final int BACKGROUND_ENABLED_WINDOWFOCUSED = 2;

	private int state;
	private PaintContext ctx;

	private Path2D path = new Path2D.Float();
	private RoundRectangle2D roundRect = new RoundRectangle2D.Float(0, 0, 0, 0, 0, 0);
	protected Ellipse2D ellipse = new Ellipse2D.Float(0, 0, 0, 0);

	private Color color5 = decodeColor("nimbusBlueGrey", 0.0f, -0.023821115f, -0.06666666f, 0);
	private Color color7 = decodeColor("nimbusBlueGrey", -0.006944418f, -0.07399663f, 0.11372548f, 0);
	private Color color9 = new Color(255, 200, 0, 255);
	private Color color10 = decodeColor("nimbusBase", 0.004681647f, -0.6274498f, 0.39999998f, 0);
	private Color color11 = decodeColor("nimbusBase", 0.032459438f, -0.5934608f, 0.2862745f, 0);
	private Color color12 = new Color(204, 207, 213, 255);

	protected Object[] componentColors;

	public MyInternalFramePainter(PaintContext ctx, int state) {
		super();
		this.state = state;
		this.ctx = ctx;
	}

	@Override
	protected void doPaint(Graphics2D g, JComponent c, int width, int height, Object[] extendedCacheKeys) {
		componentColors = extendedCacheKeys;
		switch (state) {
		case BACKGROUND_ENABLED:
			paintBackgroundEnabled(g);
			break;
		case BACKGROUND_ENABLED_WINDOWFOCUSED:
			paintBackgroundEnabledAndWindowFocused(g);
			break;
		}
	}

	@Override
	protected final PaintContext getPaintContext() {
		return ctx;
	}

	private void paintBackgroundEnabled(Graphics2D g) {
		roundRect = decodeRoundRect1();
		g.setPaint(color5);
		g.fill(roundRect);
	}

	private void paintBackgroundEnabledAndWindowFocused(Graphics2D g) {
		roundRect = decodeRoundRect2();
		g.setPaint(color7);
		g.fill(roundRect);
		path = decodePath5();
		g.setPaint(color9);
		g.fill(path);
		path = decodePath1();
		g.setPaint(decodeGradient2(path));
		g.fill(path);
		path = decodePath6();
		g.setPaint(color12);
		g.fill(path);
	}

	private RoundRectangle2D decodeRoundRect1() {
		roundRect.setRoundRect(decodeX(0.0f), decodeY(0.0f), decodeX(3.0f) - decodeX(0.0f),
				decodeY(3.0f) - decodeY(0.0f), 4.6666665f, 4.6666665f);
		return roundRect;
	}

	private Path2D decodePath1() {
		path.reset();
		path.moveTo(decodeX(0.16666667f), decodeY(0.12f));
		path.curveTo(decodeAnchorX(0.1666666716337204f, 0.0f), decodeAnchorY(0.11999999731779099f, -1.0f),
				decodeAnchorX(0.5f, -1.0f), decodeAnchorY(0.03999999910593033f, 0.0f), decodeX(0.5f), decodeY(0.04f));
		path.curveTo(decodeAnchorX(0.5f, 1.0f), decodeAnchorY(0.03999999910593033f, 0.0f), decodeAnchorX(2.5f, -1.0f),
				decodeAnchorY(0.03999999910593033f, 0.0f), decodeX(2.5f), decodeY(0.04f));
		path.curveTo(decodeAnchorX(2.5f, 1.0f), decodeAnchorY(0.03999999910593033f, 0.0f),
				decodeAnchorX(2.8333332538604736f, 0.0f), decodeAnchorY(0.11999999731779099f, -1.0f),
				decodeX(2.8333333f), decodeY(0.12f));
		path.curveTo(decodeAnchorX(2.8333332538604736f, 0.0f), decodeAnchorY(0.11999999731779099f, 1.0f),
				decodeAnchorX(2.8333332538604736f, 0.0f), decodeAnchorY(0.9599999785423279f, 0.0f), decodeX(2.8333333f),
				decodeY(0.96f));
		path.lineTo(decodeX(0.16666667f), decodeY(0.96f));
		path.curveTo(decodeAnchorX(0.1666666716337204f, 0.0f), decodeAnchorY(0.9599999785423279f, 0.0f),
				decodeAnchorX(0.1666666716337204f, 0.0f), decodeAnchorY(0.11999999731779099f, 1.0f),
				decodeX(0.16666667f), decodeY(0.12f));
		path.closePath();
		return path;
	}

	private RoundRectangle2D decodeRoundRect2() {
		roundRect.setRoundRect(decodeX(0.0f), decodeY(0.0f), decodeX(3.0f) - decodeX(0.0f),
				decodeY(3.0f) - decodeY(0.0f), 4.8333335f, 4.8333335f);
		return roundRect;
	}

	private Path2D decodePath5() {
		path.reset();
		path.moveTo(decodeX(0.16666667f), decodeY(0.08f));
		path.curveTo(decodeAnchorX(0.1666666716337204f, 0.0f), decodeAnchorY(0.07999999821186066f, 1.0f),
				decodeAnchorX(0.1666666716337204f, 0.0f), decodeAnchorY(0.07999999821186066f, -1.0f),
				decodeX(0.16666667f), decodeY(0.08f));
		path.closePath();
		return path;
	}

	private Path2D decodePath6() {
		path.reset();
		path.moveTo(decodeX(0.5f), decodeY(0.96f));
		path.lineTo(decodeX(0.16666667f), decodeY(0.96f));
		path.curveTo(decodeAnchorX(0.1666666716337204f, 0.0f), decodeAnchorY(0.9599999785423279f, 0.0f),
				decodeAnchorX(0.1666666716337204f, 0.0f), decodeAnchorY(2.5f, -1.0f), decodeX(0.16666667f),
				decodeY(2.5f));
		path.curveTo(decodeAnchorX(0.1666666716337204f, 0.0f), decodeAnchorY(2.5f, 1.0f), decodeAnchorX(0.5f, -1.0f),
				decodeAnchorY(2.8333332538604736f, 0.0f), decodeX(0.5f), decodeY(2.8333333f));
		path.curveTo(decodeAnchorX(0.5f, 1.0f), decodeAnchorY(2.8333332538604736f, 0.0f), decodeAnchorX(2.5f, -1.0f),
				decodeAnchorY(2.8333332538604736f, 0.0f), decodeX(2.5f), decodeY(2.8333333f));
		path.curveTo(decodeAnchorX(2.5f, 1.0f), decodeAnchorY(2.8333332538604736f, 0.0f),
				decodeAnchorX(2.8333332538604736f, 0.0f), decodeAnchorY(2.5f, 1.0f), decodeX(2.8333333f),
				decodeY(2.5f));
		path.curveTo(decodeAnchorX(2.8333332538604736f, 0.0f), decodeAnchorY(2.5f, -1.0f),
				decodeAnchorX(2.8333332538604736f, 0.0f), decodeAnchorY(0.9599999785423279f, 0.0f), decodeX(2.8333333f),
				decodeY(0.96f));
		path.lineTo(decodeX(2.5f), decodeY(0.96f));
		path.lineTo(decodeX(2.5f), decodeY(2.5f));
		path.lineTo(decodeX(0.5f), decodeY(2.5f));
		path.lineTo(decodeX(0.5f), decodeY(0.96f));
		path.closePath();
		return path;
	}

	private Paint decodeGradient2(Shape s) {
		Rectangle2D bounds = s.getBounds2D();
		float x = (float) bounds.getX();
		float y = (float) bounds.getY();
		float w = (float) bounds.getWidth();
		float h = (float) bounds.getHeight();
		return decodeGradient((0.5f * w) + x, (0.0f * h) + y, (0.5f * w) + x, (1.0f * h) + y,
				new float[] { 0.0f, 0.5f, 1.0f },
				new Color[] { color10, decodeColor(color10, color11, 0.5f), color11 });
	}
}