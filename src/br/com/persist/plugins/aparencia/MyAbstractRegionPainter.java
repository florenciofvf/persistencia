package br.com.persist.plugins.aparencia;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.LinearGradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.VolatileImage;
import java.awt.print.PrinterGraphics;
import java.lang.reflect.Method;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.Painter;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import sun.reflect.misc.MethodUtil;

public abstract class MyAbstractRegionPainter implements Painter<JComponent> {
	private PaintContext ctx;
	private float f;
	private float leftWidth;
	private float topHeight;
	private float centerWidth;
	private float centerHeight;
	private float rightWidth;
	private float bottomHeight;
	private float leftScale;
	private float topScale;
	private float centerHScale;
	private float centerVScale;
	private float rightScale;
	private float bottomScale;

	protected MyAbstractRegionPainter() {
	}

	@Override
	public final void paint(Graphics2D g, JComponent c, int w, int h) {
		if (w <= 0 || h <= 0) {
			return;
		}

		Object[] extendedCacheKeys = getExtendedCacheKeys(c);
		ctx = getPaintContext();
		PaintContext.CacheMode cacheMode = ctx == null ? PaintContext.CacheMode.NO_CACHING : ctx.cacheMode;
		if (cacheMode == PaintContext.CacheMode.NO_CACHING || !MyImageCache.getInstance().isImageCachable(w, h)
				|| g instanceof PrinterGraphics) {
			paint0(g, c, w, h, extendedCacheKeys);
		} else if (cacheMode == PaintContext.CacheMode.FIXED_SIZES) {
			paintWithFixedSizeCaching(g, c, w, h, extendedCacheKeys);
		} else {
			paintWith9SquareCaching(g, ctx, c, w, h, extendedCacheKeys);
		}
	}

	protected Object[] getExtendedCacheKeys(JComponent c) {
		return null;
	}

	protected abstract PaintContext getPaintContext();

	protected void configureGraphics(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	}

	protected abstract void doPaint(Graphics2D g, JComponent c, int width, int height, Object[] extendedCacheKeys);

	protected final float decodeX(float x) {
		if (x >= 0 && x <= 1) {
			return x * leftWidth;
		} else if (x > 1 && x < 2) {
			return ((x - 1) * centerWidth) + leftWidth;
		} else if (x >= 2 && x <= 3) {
			return ((x - 2) * rightWidth) + leftWidth + centerWidth;
		} else {
			throw new IllegalArgumentException("Invalid x");
		}
	}

	protected final float decodeY(float y) {
		if (y >= 0 && y <= 1) {
			return y * topHeight;
		} else if (y > 1 && y < 2) {
			return ((y - 1) * centerHeight) + topHeight;
		} else if (y >= 2 && y <= 3) {
			return ((y - 2) * bottomHeight) + topHeight + centerHeight;
		} else {
			throw new IllegalArgumentException("Invalid y");
		}
	}

	protected final float decodeAnchorX(float x, float dx) {
		if (x >= 0 && x <= 1) {
			return decodeX(x) + (dx * leftScale);
		} else if (x > 1 && x < 2) {
			return decodeX(x) + (dx * centerHScale);
		} else if (x >= 2 && x <= 3) {
			return decodeX(x) + (dx * rightScale);
		} else {
			throw new IllegalArgumentException("Invalid x");
		}
	}

	protected final float decodeAnchorY(float y, float dy) {
		if (y >= 0 && y <= 1) {
			return decodeY(y) + (dy * topScale);
		} else if (y > 1 && y < 2) {
			return decodeY(y) + (dy * centerVScale);
		} else if (y >= 2 && y <= 3) {
			return decodeY(y) + (dy * bottomScale);
		} else {
			throw new IllegalArgumentException("Invalid y");
		}
	}

	protected final Color decodeColor(String key, float hOffset, float sOffset, float bOffset, int aOffset) {
		if (UIManager.getLookAndFeel() instanceof NimbusLookAndFeel) {
			NimbusLookAndFeel laf = (NimbusLookAndFeel) UIManager.getLookAndFeel();
			return laf.getDerivedColor(key, hOffset, sOffset, bOffset, aOffset, true);
		} else {
			return Color.getHSBColor(hOffset, sOffset, bOffset);
		}
	}

	protected final Color decodeColor(Color color1, Color color2, float midPoint) {
		return new Color(deriveARGB(color1, color2, midPoint));
	}

	static int deriveARGB(Color color1, Color color2, float midPoint) {
		int r = color1.getRed() + Math.round((color2.getRed() - color1.getRed()) * midPoint);
		int g = color1.getGreen() + Math.round((color2.getGreen() - color1.getGreen()) * midPoint);
		int b = color1.getBlue() + Math.round((color2.getBlue() - color1.getBlue()) * midPoint);
		int a = color1.getAlpha() + Math.round((color2.getAlpha() - color1.getAlpha()) * midPoint);
		return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
	}

	protected final LinearGradientPaint decodeGradient(float x1, float y1, float x2, float y2, float[] midpoints,
			Color[] colors) {
		if (x1 == x2 && y1 == y2) {
			y2 += .00001f;
		}
		return new LinearGradientPaint(x1, y1, x2, y2, midpoints, colors);
	}

	protected final RadialGradientPaint decodeRadialGradient(float x, float y, float r, float[] midpoints,
			Color[] colors) {
		if (r == 0f) {
			r = .00001f;
		}
		return new RadialGradientPaint(x, y, r, midpoints, colors);
	}

	@SuppressWarnings("rawtypes")
	protected final Color getComponentColor(JComponent c, String property, Color defaultColor, float saturationOffset,
			float brightnessOffset, int alphaOffset) {
		Color color = null;
		if (c != null) {
			if ("background".equals(property)) {
				color = c.getBackground();
			} else if ("foreground".equals(property)) {
				color = c.getForeground();
			} else if (c instanceof JList && "selectionForeground".equals(property)) {
				color = ((JList) c).getSelectionForeground();
			} else if (c instanceof JList && "selectionBackground".equals(property)) {
				color = ((JList) c).getSelectionBackground();
			} else if (c instanceof JTable && "selectionForeground".equals(property)) {
				color = ((JTable) c).getSelectionForeground();
			} else if (c instanceof JTable && "selectionBackground".equals(property)) {
				color = ((JTable) c).getSelectionBackground();
			} else {
				String s = "get" + Character.toUpperCase(property.charAt(0)) + property.substring(1);
				try {
					Method method = MethodUtil.getMethod(c.getClass(), s, null);
					color = (Color) MethodUtil.invoke(method, c, null);
				} catch (Exception e) {
				}
				if (color == null) {
					Object value = c.getClientProperty(property);
					if (value instanceof Color) {
						color = (Color) value;
					}
				}
			}
		}
		if (color == null || color instanceof UIResource) {
			return defaultColor;
		} else if (saturationOffset != 0 || brightnessOffset != 0 || alphaOffset != 0) {
			float[] tmp = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
			tmp[1] = clamp(tmp[1] + saturationOffset);
			tmp[2] = clamp(tmp[2] + brightnessOffset);
			int alpha = clamp(color.getAlpha() + alphaOffset);
			return new Color((Color.HSBtoRGB(tmp[0], tmp[1], tmp[2]) & 0xFFFFFF) | (alpha << 24));
		} else {
			return color;
		}
	}

	protected static class PaintContext {
		protected static enum CacheMode {
			NO_CACHING, FIXED_SIZES, NINE_SQUARE_SCALE
		}

		private static Insets EMPTY_INSETS = new Insets(0, 0, 0, 0);

		private Insets stretchingInsets;
		private Dimension canvasSize;
		private boolean inverted;
		private CacheMode cacheMode;
		private double maxHorizontalScaleFactor;
		private double maxVerticalScaleFactor;

		private float a;
		private float b;
		private float c;
		private float d;
		private float aPercent;
		private float bPercent;
		private float cPercent;
		private float dPercent;

		public PaintContext(Insets insets, Dimension canvasSize, boolean inverted) {
			this(insets, canvasSize, inverted, null, 1, 1);
		}

		public PaintContext(Insets insets, Dimension canvasSize, boolean inverted, CacheMode cacheMode, double maxH,
				double maxV) {
			if (maxH < 1 || maxH < 1) {
				throw new IllegalArgumentException("Both maxH and maxV must be >= 1");
			}

			this.stretchingInsets = insets == null ? EMPTY_INSETS : insets;
			this.canvasSize = canvasSize;
			this.inverted = inverted;
			this.cacheMode = cacheMode == null ? CacheMode.NO_CACHING : cacheMode;
			this.maxHorizontalScaleFactor = maxH;
			this.maxVerticalScaleFactor = maxV;

			if (canvasSize != null) {
				a = stretchingInsets.left;
				b = canvasSize.width - stretchingInsets.right;
				c = stretchingInsets.top;
				d = canvasSize.height - stretchingInsets.bottom;
				this.canvasSize = canvasSize;
				this.inverted = inverted;
				if (inverted) {
					float available = canvasSize.width - (b - a);
					aPercent = available > 0f ? a / available : 0f;
					bPercent = available > 0f ? b / available : 0f;
					available = canvasSize.height - (d - c);
					cPercent = available > 0f ? c / available : 0f;
					dPercent = available > 0f ? d / available : 0f;
				}
			}
		}
	}

	private void prepare(float w, float h) {
		if (ctx == null || ctx.canvasSize == null) {
			f = 1f;
			leftWidth = centerWidth = rightWidth = 0f;
			topHeight = centerHeight = bottomHeight = 0f;
			leftScale = centerHScale = rightScale = 0f;
			topScale = centerVScale = bottomScale = 0f;
			return;
		}

		Number scale = (Number) UIManager.get("scale");
		f = scale == null ? 1f : scale.floatValue();

		if (ctx.inverted) {
			centerWidth = (ctx.b - ctx.a) * f;
			float availableSpace = w - centerWidth;
			leftWidth = availableSpace * ctx.aPercent;
			rightWidth = availableSpace * ctx.bPercent;
			centerHeight = (ctx.d - ctx.c) * f;
			availableSpace = h - centerHeight;
			topHeight = availableSpace * ctx.cPercent;
			bottomHeight = availableSpace * ctx.dPercent;
		} else {
			leftWidth = ctx.a * f;
			rightWidth = (float) (ctx.canvasSize.getWidth() - ctx.b) * f;
			centerWidth = w - leftWidth - rightWidth;
			topHeight = ctx.c * f;
			bottomHeight = (float) (ctx.canvasSize.getHeight() - ctx.d) * f;
			centerHeight = h - topHeight - bottomHeight;
		}

		leftScale = ctx.a == 0f ? 0f : leftWidth / ctx.a;
		centerHScale = (ctx.b - ctx.a) == 0f ? 0f : centerWidth / (ctx.b - ctx.a);
		rightScale = (ctx.canvasSize.width - ctx.b) == 0f ? 0f : rightWidth / (ctx.canvasSize.width - ctx.b);
		topScale = ctx.c == 0f ? 0f : topHeight / ctx.c;
		centerVScale = (ctx.d - ctx.c) == 0f ? 0f : centerHeight / (ctx.d - ctx.c);
		bottomScale = (ctx.canvasSize.height - ctx.d) == 0f ? 0f : bottomHeight / (ctx.canvasSize.height - ctx.d);
	}

	private void paintWith9SquareCaching(Graphics2D g, PaintContext ctx, JComponent c, int w, int h,
			Object[] extendedCacheKeys) {
		Dimension canvas = ctx.canvasSize;
		Insets insets = ctx.stretchingInsets;
		if (w <= (canvas.width * ctx.maxHorizontalScaleFactor) && h <= (canvas.height * ctx.maxVerticalScaleFactor)) {
			VolatileImage img = getImage(g.getDeviceConfiguration(), c, canvas.width, canvas.height, extendedCacheKeys);
			if (img != null) {
				Insets dstInsets;
				if (ctx.inverted) {
					int leftRight = (w - (canvas.width - (insets.left + insets.right))) / 2;
					int topBottom = (h - (canvas.height - (insets.top + insets.bottom))) / 2;
					dstInsets = new Insets(topBottom, leftRight, topBottom, leftRight);
				} else {
					dstInsets = insets;
				}
				Object oldScaleingHints = g.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				MyImageScalingHelper.paint(g, 0, 0, w, h, img, insets, dstInsets,
						MyImageScalingHelper.PaintType.PAINT9_STRETCH, MyImageScalingHelper.PAINT_ALL);
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, oldScaleingHints != null ? oldScaleingHints
						: RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			} else {
				paint0(g, c, w, h, extendedCacheKeys);
			}
		} else {
			paint0(g, c, w, h, extendedCacheKeys);
		}
	}

	private void paintWithFixedSizeCaching(Graphics2D g, JComponent c, int w, int h, Object[] extendedCacheKeys) {
		VolatileImage img = getImage(g.getDeviceConfiguration(), c, w, h, extendedCacheKeys);
		if (img != null) {
			g.drawImage(img, 0, 0, null);
		} else {
			paint0(g, c, w, h, extendedCacheKeys);
		}
	}

	private VolatileImage getImage(GraphicsConfiguration config, JComponent c, int w, int h,
			Object[] extendedCacheKeys) {
		MyImageCache imageCache = MyImageCache.getInstance();
		VolatileImage buffer = (VolatileImage) imageCache.getImage(config, w, h, this, extendedCacheKeys);

		int renderCounter = 0;
		do {
			int bufferStatus = VolatileImage.IMAGE_INCOMPATIBLE;
			if (buffer != null) {
				bufferStatus = buffer.validate(config);
			}

			if (bufferStatus == VolatileImage.IMAGE_INCOMPATIBLE || bufferStatus == VolatileImage.IMAGE_RESTORED) {
				if (buffer == null || buffer.getWidth() != w || buffer.getHeight() != h
						|| bufferStatus == VolatileImage.IMAGE_INCOMPATIBLE) {
					if (buffer != null) {
						buffer.flush();
						buffer = null;
					}
					buffer = config.createCompatibleVolatileImage(w, h, Transparency.TRANSLUCENT);
					imageCache.setImage(buffer, config, w, h, this, extendedCacheKeys);
				}
				Graphics2D bg = buffer.createGraphics();
				bg.setComposite(AlphaComposite.Clear);
				bg.fillRect(0, 0, w, h);
				bg.setComposite(AlphaComposite.SrcOver);
				configureGraphics(bg);
				paint0(bg, c, w, h, extendedCacheKeys);
				bg.dispose();
			}
		} while (buffer.contentsLost() && renderCounter++ < 3);
		if (renderCounter == 3)
			return null;
		return buffer;
	}

	private void paint0(Graphics2D g, JComponent c, int width, int height, Object[] extendedCacheKeys) {
		prepare(width, height);
		g = (Graphics2D) g.create();
		configureGraphics(g);
		doPaint(g, c, width, height, extendedCacheKeys);
		g.dispose();
	}

	private float clamp(float value) {
		if (value < 0) {
			value = 0;
		} else if (value > 1) {
			value = 1;
		}
		return value;
	}

	private int clamp(int value) {
		if (value < 0) {
			value = 0;
		} else if (value > 255) {
			value = 255;
		}
		return value;
	}
}