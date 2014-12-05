package easy.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;

import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageDecoder;
import com.sun.imageio.plugins.bmp.BMPImageReader;
import com.sun.imageio.plugins.gif.GIFImageReader;
import com.sun.imageio.plugins.jpeg.JPEGImageReader;
import com.sun.imageio.plugins.png.PNGImageReader;
import com.sun.imageio.plugins.wbmp.WBMPImageReader;

public class Images
{

	public Images()
	{
	}

	/**
	 * 存jpg文件
	 * @param tagFileName
	 * @param image
	 * @param compressionquality 图片质量
	 * @throws IOException
	 */
	public static void toJpg(final String tagFileName,BufferedImage image,float compressionquality) throws IOException
	{
		ImageWriter writer = null;
		Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpg");
		if (iter.hasNext())
		{
			writer = (ImageWriter) iter.next();
		}

		// 准备输出文件
		// JFile.delete(tagFileName);
		BufferedOutputStream ofile = new BufferedOutputStream(new FileOutputStream(tagFileName, false));
		ImageOutputStream ios = ImageIO.createImageOutputStream(ofile);
		writer.setOutput(ios);

		// 设置压缩比
		ImageWriteParam iwparam = writer.getDefaultWriteParam();
		iwparam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		iwparam.setProgressiveMode(ImageWriteParam.MODE_DISABLED);
		iwparam.setCompressionQuality(compressionquality);

		// 写图片
		writer.write(null, new IIOImage(image, null, null), iwparam);

		// 最后清理
		ios.flush();
		writer.dispose();
		ios.close();
	}

	/**
	 * 图片修改尺寸
	 * 
	 * @param srcFileName
	 * @param tagFileName
	 * @param width
	 * @param height
	 * @throws IOException
	 */
	public static void resize(String srcFileName, String tagFileName,
					int width, int height) throws IOException
	{
		if (width <= 0 || height <= 0)
		{
			ImageReader r = getReader(srcFileName);
			int w = r.getWidth(0);
			int h = r.getHeight(0);

			if (width <= 0)
			{
				width = (int) (w * ((double) height / h));
			}
			else if (height <= 0)
			{
				height = (int) (h * ((double) width / w));

			}
			// System.out.println(width+" "+height);
		}

		String ext = Format.getFileExtName(srcFileName).toLowerCase();
		
		if (ext.equals(""))
		{
			ext = "jpg";
		}

		boolean isjpg = false;
		//System.setProperty("java.awt.headless", "true");

		if (ext.equals("jpg"))
		{
			JPEGImageDecoder decoder = JPEGCodec
							.createJPEGDecoder(new FileInputStream(srcFileName)); // 先把流转一遍
			try
			{
				BufferedImage bi = decoder.decodeAsBufferedImage();
				// Builder<BufferedImage> bu = Thumbnails.of(image).size(width,
				// hight);

				BufferedImage tag = new BufferedImage(width, height,BufferedImage.TYPE_INT_BGR);
				Graphics2D g = (Graphics2D) tag.getGraphics();
				//g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				//g.drawImage(bi, 0, 0, width, height, null);
				g.drawImage(bi.getScaledInstance(width, height,
	                            Image.SCALE_AREA_AVERAGING), 0, 0, width, height, null);
				/*
				 * tag.getGraphics().drawImage(
                    bi.getScaledInstance(width, height,
                            Image.SCALE_AREA_AVERAGING), 0, 0, null);
				 */

				toJpg(tagFileName,tag,0.9f);

				isjpg = true;
			}
			catch (ImageFormatException e)
			{
				// Log.OutException(e);
			}
		}

		if (isjpg == false)
		{
			BufferedImage bi = ImageIO.read(new File(srcFileName));

			BufferedImage tag = new BufferedImage(width, height,
							BufferedImage.TYPE_INT_BGR);
			Graphics2D g = (Graphics2D) tag.getGraphics();
			g.drawImage(bi, 0, 0, width, height, null);

			if (ext.equals("jpg"))
			{
				toJpg(tagFileName,tag,0.9f);
			}
			else
			{
				ImageIO.write(tag, ext, new File(tagFileName));
			}
		}
		// BufferedImage bi = ImageIO.read(new File(srcFileName));
		// http://img04.taobaocdn.com/bao/uploaded/i4/T1ofTwFEJdXXXXXXXX_!!0-item_pic.jpg
	}

	/**
	 * 剪裁图片
	 * 
	 * @param srcFileName
	 * @param tagFileName
	 * @param width
	 * @param height
	 * @throws IOException
	 */
	public static void cut(String srcFileName, String tagFileName, int x,
					int y, int width, int height) throws IOException
	{
		String ext = Format.getFileExtName(srcFileName);

		// BufferedImage bi = ImageIO.read(new File(srcFileName));
		BufferedImage tag = new BufferedImage(width, height,
						BufferedImage.TYPE_INT_RGB);

		ImageReader reader = getReader(srcFileName);
		ImageInputStream iis = ImageIO.createImageInputStream(new File(
						srcFileName));
		reader.setInput(iis, true);

		Rectangle rect = new Rectangle(x, y, width, height);
		ImageReadParam param = reader.getDefaultReadParam();
		param.setSourceRegion(rect);
		BufferedImage bi = reader.read(0, param);

		tag.getGraphics().drawImage(bi, 0, 0, width, height, null);

		ImageIO.write(tag, ext, new File(tagFileName));
	}
	
	public static String getImageType(final String localpath) throws IOException
	{
		FileInputStream fis = new FileInputStream(localpath);
		
	    int leng = fis.available();  
	    BufferedInputStream buff = new BufferedInputStream(fis);  
		byte[] mapObj = new byte[leng];
	    buff.read(mapObj, 0, leng);  

	    String type = "";  
	    ByteArrayInputStream bais = new ByteArrayInputStream(mapObj);
	    MemoryCacheImageInputStream mcis = new MemoryCacheImageInputStream(bais);
		
		Iterator<ImageReader> itr = ImageIO.getImageReaders(mcis);
		while (itr.hasNext())
		{
			ImageReader reader = (ImageReader) itr.next();
			if (reader instanceof GIFImageReader)
			{
				type = "gif";
			}
			else if (reader instanceof JPEGImageReader)
			{
				type = "jpg";
			}
			else if (reader instanceof PNGImageReader)
			{
				type = "png";
			}
			else if (reader instanceof BMPImageReader)
			{
				type = "bmp";
			}
			else if (reader instanceof WBMPImageReader)
			{
				type = "bmp";
			}
		}
		fis.close();
		return type;
	}

	/**
	 * 返回图片信息
	 * 
	 * @param localpath
	 * @return
	 * @throws IOException
	 */
	public static ImageReader getReader(final String localpath)
					throws IOException
	{
		//String ext = Format.getFileExtName(localpath);
		ImageReader reader = null;
		String ext = getImageType(localpath);

		Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName(ext);
		if (readers.hasNext())
		{
			reader = readers.next();
			File file = new File(localpath);
			ImageInputStream input = ImageIO.createImageInputStream(file);
			reader.setInput(input, true);
		}
		// int width = reader.getWidth(0);
		// int height = reader.getHeight(0);

		return reader;
	}

	// /*
	// * 改改变尺寸如果不合适就进行剪裁到合适尺寸
	// *
	// */
	// public static void resizecut(String srcFileName, String tagFileName,
	// int width, int height) throws IOException
	// {
	//
	// }

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			Images.resize("/Users/Neo/Desktop/a.jpg",
							"/Users/Neo/Desktop/b.jpg", 350, 0);
//			Images.resize("/Users/Neo/Desktop/a.",
//							"/Users/Neo/Desktop/bb.jpg", 350, 0);
			// Images.resize("/Users/Neo/Desktop/a.png",
			// "/Users/Neo/Desktop/bb.png", 350, 0);
		}
		catch (IOException e)
		{
			Log.OutException(e);
		}

		// try
		// {
		// ImageReader r =
		// getReader("/Users/Neo/Desktop/down/2014-04-14/59a35c174e7298d1dedbb9f2f0a067bb.jpg");
		//
		// System.out.println(r.getWidth(0) + " " + r.getHeight(0));
		//
		//
		// resize("/Users/Neo/Desktop/down/2014-04-14/59a35c174e7298d1dedbb9f2f0a067bb.jpg","/Users/Neo/Desktop/down/2014-04-14/aaa.jpg",1200,1200);
		// //resizePro("/Users/Neo/Desktop/down/2014-04-14/59a35c174e7298d1dedbb9f2f0a067bb.jpg","/Users/Neo/Desktop/down/2014-04-14/bbb.jpg",0,300);
		// cut("/Users/Neo/Desktop/down/2014-04-14/59a35c174e7298d1dedbb9f2f0a067bb.jpg","/Users/Neo/Desktop/down/2014-04-14/ccc.jpg",0,200,800,400);
		// resizecut("/Users/Neo/Desktop/down/2014-04-14/59a35c174e7298d1dedbb9f2f0a067bb.jpg","/Users/Neo/Desktop/down/2014-04-14/ccc.jpg",0,200,800,400);
		//
		// }
		// catch (IOException e)
		// {
		// Log.OutException(e);
		// }
	}
}
