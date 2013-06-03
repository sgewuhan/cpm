package com.sg.widget.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Drawable;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import com.mongodb.DBObject;


//import org.eclipse.swt.graphics.GC;

public class ImageUtil {
//	public static final String THUMB_120_90 = "thumb120_90@";
//	public static final String SCALE_640_480 = "thumb640_480@";
//	public static final int bigWidth = 2048;
//	public static final int bigHeight = 1536;
	// public static final int bigWidth = 1024;
	// public static final int bigHeight = 768;
//	private static Map<String, Image> mainPageImageCache = new ConcurrentHashMap<String, Image>();
//	private static Map<String, Image> gallaryImageCache = new ConcurrentHashMap<String, Image>();

	// private static Map<String, BufferedImage> imageBufferCache = new
	// ConcurrentHashMap<String, BufferedImage>();

	public static Image getImageFromFile(Device display, String name) {
		InputStream stream = null;
		try {
			stream = new FileInputStream(name);
			Image image = new Image(display, stream);

			return image;

		} catch (FileNotFoundException e) {
		} finally {
			try {
				if (stream != null) {
					stream.close();
				}
			} catch (IOException unexpected) {
			}
		}

		return null;
	}

	public static BufferedImage getBufferedImageFromFile(String name) {

		BufferedImage image = null;
		try {
			image = ImageIO.read(new File(name));
		} catch (IOException e) {
		}
		return image;
	}
	
	public static BufferedImage getBufferedImageFromInputStream(InputStream is) {

		BufferedImage image = null;
		try {
			image = ImageIO.read(is);
		} catch (IOException e) {
		}
		return image;
	}
	
	public static BufferedImage getBufferedImageFromDB(String namespace, String oid) {
		try {
			InputStream is = FileUtil.getInputSteamFromGridFS(namespace, oid);
			BufferedImage image = null;
			image = ImageIO.read(is);
			is.close();
			return image;
		} catch (IOException e) {
		}
		return null;
	}
	
	public static Image getImageFromDB(Device display,String namespace, String oid) {
		try {
			InputStream stream = FileUtil.getInputSteamFromGridFS(namespace, oid);
			if(stream==null) return null;
			Image image = new Image(display, stream);
			stream.close();
			return image;
		} catch (IOException e) {
		}
		return null;
	}
	
	
	public static Image getImageFromDB(Display display,DBObject imgData, Point size) {

		BufferedImage bf = ImageUtil.getBufferedImageFromDB((String) imgData.get("namespace"), imgData.get("_id").toString());
		if (size != null)
			bf = ImageUtil.fitImage(bf, size.x, size.y);

		if (bf != null) {
			return ImageUtil.getImage(display, bf);
		}
		return null;
	}
	

	public static Image scaleFitImage(Image image, int width, int height) {
		Rectangle sourceBounds = image.getBounds();
		if (sourceBounds.width > width || sourceBounds.height > height) {// 需要进行截取
			float ratioW = ((float) width) / ((float) sourceBounds.width);
			float ratioH = ((float) height) / ((float) sourceBounds.height);
			// 取最小的作为放缩因子
			float ratio = Math.max(ratioW, ratioH);

			ImageData imageData = image.getImageData();

			int nwidth = (int) (sourceBounds.width * ratio);
			int nheight = (int) (sourceBounds.height * ratio);
			image = new Image(image.getDevice(), imageData.scaledTo(nwidth,
					nheight));
		}
		return image;
	}
	
	public static Image scaleFitImage2(Image image, int width, int height) {
		Rectangle sourceBounds = image.getBounds();
		if (sourceBounds.width > width || sourceBounds.height > height) {// 需要进行截取
			float ratioW = ((float) width) / ((float) sourceBounds.width);
			float ratioH = ((float) height) / ((float) sourceBounds.height);
			// 取最小的作为放缩因子
			float ratio = Math.min(ratioW, ratioH);

			ImageData imageData = image.getImageData();

			int nwidth = (int) (sourceBounds.width * ratio);
			int nheight = (int) (sourceBounds.height * ratio);
			image = new Image(image.getDevice(), imageData.scaledTo(nwidth,
					nheight));
		}
		return image;
	}

	public static Rectangle getFitableBounds(float sourceWidth,
			float sourceHeight, float targetWidth, float targetHeight) {

		int srcX;
		int srcY;
		int srcWidth;
		int srcHeight;
		// 图片适应到画布,保证长宽比
		float targetWHRatio = targetWidth / targetHeight;// 获得目标的宽高比
		float sourceWHRatio = sourceWidth / sourceHeight;// 获得原图像的宽高比

		// 如果目标的宽高比>=原图的宽高比，原图的x坐标不变，改变y坐标
		if (targetWHRatio >= sourceWHRatio) {
			srcWidth = (int) sourceWidth;
			srcX = 0;
			srcHeight = Math.round(sourceWidth / targetWHRatio);
			srcY = Math.round((sourceHeight - sourceWidth / targetWHRatio) / 2);
		} else {
			srcHeight = (int) sourceHeight;
			srcY = 0;
			srcWidth = Math.round(srcHeight * targetWHRatio);
			srcX = Math.round((sourceWidth - srcHeight * targetWHRatio) / 2);

		}
		return new Rectangle(srcX, srcY, srcWidth, srcHeight);
	}

	/**
	 * 直接使用GC进行操作
	 * @param image
	 * @param targetWidth
	 * @param targetHeight
	 * @param drawable 
	 * @return
	 */
	public static void drawImage(Drawable drawable,Image image, int targetWidth, int targetHeight) {
		Rectangle bounds = getFitableBounds(image.getBounds().width,
				image.getBounds().height, targetWidth, targetHeight);
		GC gc = new GC(drawable);
		gc.drawImage(image, bounds.x, bounds.y, bounds.width, bounds.height, 0, 0, targetWidth, targetHeight);
		gc.dispose();
	}
	
	/**
	 * 将图片适应到对目标尺寸
	 * 
	 * @param image
	 * @param targetWidth
	 * @param targetHeight
	 * @return
	 */
	public static Image fitImage(Image image, int targetWidth, int targetHeight) {

		BufferedImage awtSrcImage = convertToAWT(image.getImageData());
		Rectangle bounds = getFitableBounds(image.getBounds().width,
				image.getBounds().height, targetWidth, targetHeight);
		// 剪裁至合适的尺寸
		BufferedImage awtClipImage = awtSrcImage.getSubimage(bounds.x,
				bounds.y, bounds.width, bounds.height);
		// 缩放至对应尺寸
		BufferedImage awtTgtImage = new BufferedImage(targetWidth,
				targetHeight, awtSrcImage.getType());
		java.awt.Image awtimage = awtClipImage.getScaledInstance(targetWidth,
				targetHeight, java.awt.Image.SCALE_SMOOTH);
		Graphics g = awtTgtImage.getGraphics();
		g.drawImage(awtimage, 0, 0, null); // 绘制目标图
		g.dispose();

		// 测试使用
		// try {
		// saveImage(awtTgtImage, "d:/temp/" + System.currentTimeMillis()
		// + ".jpg", "jpg");
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

		ImageData imageData = convertToSWT(awtTgtImage);

		return new Image(null, imageData);
	}

	/**
	 * 将图片适应到对目标尺寸
	 * 
	 * @param image
	 * @param targetWidth
	 * @param targetHeight
	 * @return
	 */
	public static BufferedImage fitImage(BufferedImage image, int targetWidth,
			int targetHeight) {

		Rectangle bounds = getFitableBounds(image.getWidth(),
				image.getHeight(), targetWidth, targetHeight);

		// 剪裁至合适的尺寸
		BufferedImage awtClipImage = image.getSubimage(bounds.x, bounds.y,
				bounds.width, bounds.height);

		if (bounds.width == targetWidth || bounds.height == targetHeight) {
			return awtClipImage;
		}


		BufferedImage targetImage = new BufferedImage(targetWidth, targetHeight,BufferedImage.TYPE_INT_RGB);
		targetImage.getGraphics().drawImage(awtClipImage.getScaledInstance(targetWidth, targetHeight,  java.awt.Image.SCALE_SMOOTH), 0, 0,  null);   
		
		return targetImage;
//		double ratio = ((double) targetHeight / (double) bounds.height); // 缩放比例
//		AffineTransformOp op = new AffineTransformOp(
//				AffineTransform.getScaleInstance(ratio, ratio), null);
//		BufferedImage awtTgtImage = op.filter(awtClipImage, null);
//		return awtTgtImage;
	}

	public static Image[] getImagePack(Device device, String namespace, String oid) {
//		String key = oid+"@"+namespace;
//		
////		System.out.println("load image:"+key);
//		Image image = mainPageImageCache.get(key);
//		Image thumb640_480 = mainPageImageCache.get(SCALE_640_480 + key);
//		Image thumb120_90 = mainPageImageCache.get(THUMB_120_90 + key);
//		if (image == null) {
//
//			BufferedImage bimage = getBufferedImageFromDB(namespace,oid);
//			image = getImage(device, bimage);
//			mainPageImageCache.put(key, image);
//
//			thumb640_480 = scaleFitImage(image, 640, 480);
//			mainPageImageCache.put(SCALE_640_480 + key, thumb640_480);
//
//			BufferedImage bthumb120_90 = fitImage(bimage, 120, 90);
//			thumb120_90 = getImage(device, bthumb120_90);
//			mainPageImageCache.put(THUMB_120_90 + key, thumb120_90);
//		}
//		return new Image[] { image, thumb640_480, thumb120_90 };
		return null;
	}
	

	public static Image getGallaryImage(Display display, String namespace, String oid) {
//		String key = oid+"@"+namespace;
//		Image image = gallaryImageCache.get(key);
//		if(image==null){
//			image = getImageFromDB(display, namespace, oid);
//			if(image==null){
//				return null;
//			}
//			gallaryImageCache.put(key, image);
//		}
//		
//		return image;
		return null;
	}

	// private static BufferedImage[] getCacheImage2(String imagePath) {
	// BufferedImage bimage = imageBufferCache.get(imagePath);
	// // BufferedImage thumb640_480 = imageBufferCache.get(SCALE_640_480 +
	// imagePath);
	// BufferedImage bthumb120_90 = imageBufferCache.get(THUMB_120_90 +
	// imagePath);
	// if (bimage == null) {
	// bimage = getBufferedImage(imagePath);
	// imageBufferCache.put(imagePath, bimage);
	// }
	// // if (thumb640_480 == null) {
	// // thumb640_480 = fitImage(image, 640, 480);
	// // imageBufferCache.put(SCALE_640_480 + imagePath, thumb640_480);
	// // }
	// if (bthumb120_90 == null) {
	// bthumb120_90 = fitImage(bimage, 120, 90);
	// imageBufferCache.put(THUMB_120_90 + imagePath, bthumb120_90);
	// }
	//
	// return new BufferedImage[] { bimage, bthumb120_90 };
	// }

	public static Image getImage(Device device, BufferedImage image) {
		ImageData imageData = convertToSWT(image);
		return new Image(device, imageData);
	}

	/**
	 * SWT转换成AWT
	 * 
	 * @param org
	 *            .eclipse.swt.graphics.ImageData;
	 * @return java.awt.image.BufferedImage;
	 */
	private static BufferedImage convertToAWT(ImageData data) {
		ColorModel colorModel = null;
		PaletteData palette = data.palette;
		if (palette.isDirect) {
			colorModel = new DirectColorModel(data.depth, palette.redMask,
					palette.greenMask, palette.blueMask);
			BufferedImage bufferedImage = new BufferedImage(colorModel,
					colorModel.createCompatibleWritableRaster(data.width,
							data.height), false, null);
			WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[3];
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					int pixel = data.getPixel(x, y);
					RGB rgb = palette.getRGB(pixel);
					pixelArray[0] = rgb.red;
					pixelArray[1] = rgb.green;
					pixelArray[2] = rgb.blue;
					raster.setPixels(x, y, 1, 1, pixelArray);
				}
			}
			return bufferedImage;
		} else {
			RGB[] rgbs = palette.getRGBs();
			byte[] red = new byte[rgbs.length];
			byte[] green = new byte[rgbs.length];
			byte[] blue = new byte[rgbs.length];
			for (int i = 0; i < rgbs.length; i++) {
				RGB rgb = rgbs[i];
				red[i] = (byte) rgb.red;
				green[i] = (byte) rgb.green;
				blue[i] = (byte) rgb.blue;
			}
			if (data.transparentPixel != -1) {
				colorModel = new IndexColorModel(data.depth, rgbs.length, red,
						green, blue, data.transparentPixel);
			} else {
				colorModel = new IndexColorModel(data.depth, rgbs.length, red,
						green, blue);
			}
			BufferedImage bufferedImage = new BufferedImage(colorModel,
					colorModel.createCompatibleWritableRaster(data.width,
							data.height), false, null);
			WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[1];
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					int pixel = data.getPixel(x, y);
					pixelArray[0] = pixel;
					raster.setPixel(x, y, pixelArray);
				}
			}
			return bufferedImage;
		}
	}

	/**
	 * AWT转换成SWT
	 * 
	 * @param java
	 *            .awt.image.BufferedImage;
	 * @return org.eclipse.swt.graphics.ImageData;
	 */
	private static ImageData convertToSWT(BufferedImage bufferedImage) {
		ColorModel colorModel = bufferedImage.getColorModel();

		if (colorModel instanceof DirectColorModel) {
			DirectColorModel cm = (DirectColorModel) colorModel;
			PaletteData palette = new PaletteData(cm.getRedMask(),
					cm.getGreenMask(), cm.getBlueMask());
			ImageData data = new ImageData(bufferedImage.getWidth(),
					bufferedImage.getHeight(), cm.getPixelSize(), palette);
			WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[3];
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					try{
						raster.getPixel(x, y, pixelArray);
						int pixel = palette.getPixel(new RGB(pixelArray[0],
								pixelArray[1], pixelArray[2]));
						data.setPixel(x, y, pixel);
						
					}catch(Exception e){}
				}
			}
			return data;
		} else if (colorModel instanceof IndexColorModel) {
			IndexColorModel cm = (IndexColorModel) colorModel;
			int size = cm.getMapSize();
			byte[] reds = new byte[size];
			byte[] greens = new byte[size];
			byte[] blues = new byte[size];
			cm.getReds(reds);
			cm.getGreens(greens);
			cm.getBlues(blues);
			RGB[] rgbs = new RGB[size];
			for (int i = 0; i < rgbs.length; i++) {
				rgbs[i] = new RGB(reds[i] & 0xFF, greens[i] & 0xFF,
						blues[i] & 0xFF);
			}
			PaletteData palette = new PaletteData(rgbs);
			ImageData data = new ImageData(bufferedImage.getWidth(),
					bufferedImage.getHeight(), cm.getPixelSize(), palette);
			data.transparentPixel = cm.getTransparentPixel();
			WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[1];
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					raster.getPixel(x, y, pixelArray);
					data.setPixel(x, y, pixelArray[0]);
				}
			}
			return data;
		} else if (colorModel instanceof ComponentColorModel) {
			// ComponentColorModel cm = (ComponentColorModel) colorModel;
			// PaletteData palette = new PaletteData(cm.getRedMask(),
			// cm.getGreenMask(), cm.getBlueMask());
			// ImageData data = new ImageData(bufferedImage.getWidth(),
			// bufferedImage.getHeight(),
			// cm.getPixelSize(), palette);
			// WritableRaster raster = bufferedImage.getRaster();
			// int[] pixelArray =colorModel.getComponentSize();
			// for (int y = 0; y < data.height; y++) {
			// for (int x = 0; x < data.width; x++) {
			// raster.getPixel(x, y, pixelArray);
			// int pixel = palette.getPixel(new RGB(pixelArray[0],
			// pixelArray[1], pixelArray[2]));
			// data.setPixel(x, y, pixel);
			// }
			// }
			// return data;

			// ASSUMES: 3 BYTE BGR IMAGE TYPE

			PaletteData palette = new PaletteData(0x0000FF, 0x00FF00, 0xFF0000);
			ImageData data = new ImageData(bufferedImage.getWidth(),
					bufferedImage.getHeight(), colorModel.getPixelSize(),
					palette);

			// This is valid because we are using a 3-byte Data model with no
			// transparent pixels
			data.transparentPixel = -1;

			WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[3];
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					raster.getPixel(x, y, pixelArray);
					int pixel = palette.getPixel(new RGB(pixelArray[0],
							pixelArray[1], pixelArray[2]));
					data.setPixel(x, y, pixel);
				}
			}
			return data;

		}
		return null;
	}

	public static void saveImage(RenderedImage image, String fileName,
			String formatName) {
		// 写文件
		FileOutputStream out = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ImageIO.write(image, formatName, bos);// 输出到bos
			out = new FileOutputStream(fileName);
			out.write(bos.toByteArray()); // 写文件
		} catch (IOException e) {
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
			}
		}
	}

	public static void genarateNumber(int from, int to, int sizeX, int sizeY, String destFolder) {
		if (to < from) {
			return;
		}
		Color[] colors = { Color.ORANGE, Color.BLACK };
		for (int i = from; i <= to; i++) {
			BufferedImage bi = new BufferedImage(sizeX, sizeY,
					BufferedImage.TYPE_INT_BGR);
			Graphics2D g2d = (Graphics2D) bi.getGraphics();
			Font f = new Font("Arial", Font.BOLD, 48);
			g2d.setFont(f);
			g2d.setColor(colors[i % 2]);
			g2d.fillRect(0, 0, sizeX, sizeY);
			g2d.setColor(Color.WHITE);
			g2d.drawString(" "+i, 50, 100);
			
			DecimalFormat nf = new DecimalFormat("000");
			saveImage(bi,destFolder+"/"+nf.format(i)+".jpg","jpg");
		}
	}

	public static void main(String[] args) {
		// 对某个库里面的图片进行居中并剪材适应大小
		String destFolder = "D:/temp";
		String sourceFolder = "D:/lib/picture/source/search2_files";
//		transfer(sourceFolder, destFolder, 150, 150);
		 transfer(sourceFolder,destFolder,148,70);
//		genarateNumber(0,48,150,150,destFolder);
	}
	
	
	public static InputStream transfer(InputStream is, int width,
			int height) {
			BufferedImage bf = getBufferedImageFromInputStream(is);
			try {
				is.close();
			} catch (IOException e1) {
			}
			if (bf == null) {
				return null;
			}
			int sourceWidth = bf.getWidth();
			int sourceHeight = bf.getWidth();
			// 如果图片小于预订尺寸跳过
			if (sourceWidth < width || sourceHeight < height) {
				return null;
			}

			BufferedImage bf2 = fitImage(bf, width, height);
			try {
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				ImageIO.write(bf2, "jpeg", os);
				InputStream is2 = new ByteArrayInputStream(os.toByteArray());
				os.close();
				return is2;
			} catch (IOException e) {
			}
			return null;
	}

	private static void transfer(String sourcePath, String destPath, int width,
			int height) {
		String targetPath = destPath + "/" + width + "_" + height + "/";
		File targetFolder = new File(targetPath);
		if (!targetFolder.exists()) {
			targetFolder.mkdirs();
		}

		File folder = new File(sourcePath);
		File[] files = folder.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
//			System.out.print("trasfer file " + file.getName() + "。。。");
			BufferedImage bf = getBufferedImageFromFile(file.getPath());
			if (bf == null) {
//				System.out.println(" not a valid image file");
				continue;
			}
			int sourceWidth = bf.getWidth();
			int sourceHeight = bf.getWidth();
			// 如果图片小于预订尺寸跳过
			if (sourceWidth < width || sourceHeight < height) {
//				System.out.println(" image file is too small");
				continue;
			}

			BufferedImage bf2 = fitImage(bf, width, height);
			String fileName = targetPath + file.getName();
			saveImage(bf2, fileName,
					fileName.substring(fileName.lastIndexOf(".") + 1));
//			System.out.println(" image file saved to " + fileName);
		}
	}


}
