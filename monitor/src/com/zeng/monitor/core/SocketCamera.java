package com.zeng.monitor.core;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.apache.http.conn.EofSensorInputStream;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

/**
 * A CameraSource implementation that obtains its bitmaps via a TCP connection
 * to a remote host on a specified address/port.
 * 
 * @author Tom Gibara
 * 
 */

public class SocketCamera implements CameraSource {

	private static final int SOCKET_TIMEOUT = 1000;

	private final String address;
	private final int port;
	private final Rect bounds;
	private final boolean preserveAspectRatio;
	private final Paint paint = new Paint();

	private Socket socket = null;

	public SocketCamera(String address, int port, int width, int height,
			boolean preserveAspectRatio) {
		this.address = address;
		this.port = port;
		bounds = new Rect(0, 0, width, height);
		this.preserveAspectRatio = preserveAspectRatio;

		paint.setFilterBitmap(true);
		paint.setAntiAlias(true);
	}

	public int getWidth() {
		return bounds.right;
	}

	public int getHeight() {
		return bounds.bottom;
	}

	public boolean open() {
		/* nothing to do */
		return true;
	}

	public boolean capture(Canvas canvas) {
		if (canvas == null){
			//throw new IllegalArgumentException("null canvas");
		}
		try {
			socket = new Socket();
			socket.bind(null);
			socket.setSoTimeout(SOCKET_TIMEOUT);
			socket.connect(new InetSocketAddress(address, port), SOCKET_TIMEOUT);

			// obtain the bitmap
			InputStream in = socket.getInputStream();
			Bitmap bitmap = BitmapFactory.decodeStream(in);

			// render it to canvas, scaling if necessary
			if (bounds.right == bitmap.getWidth()
					&& bounds.bottom == bitmap.getHeight()) {
				canvas.drawBitmap(bitmap, 0, 0, null);
			} else {
				Rect dest;
				if (preserveAspectRatio) {
					dest = new Rect(bounds);
					dest.bottom = bitmap.getHeight() * bounds.right
							/ bitmap.getWidth();
					dest.offset(0, (bounds.bottom - dest.bottom) / 2);
				} else {
					dest = bounds;
				}
				canvas.drawBitmap(bitmap, null, dest, paint);
			}

		} catch (RuntimeException e) {
			Log.i(LOG_TAG, "Failed to obtain image over network", e);
			return false;
		} catch (IOException e) {
			Log.i(LOG_TAG, "Failed to obtain image over network", e);
			return false;
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				/* ignore */
			}
		}
		return true;
	}

	public void close() {
		/* nothing to do */
	}

	public boolean saveImage(String savePath, String fileName) {
		FileOutputStream fos = null;
		// obtain the bitmap
		try {

			socket = new Socket();
			//socket.bind(null);
			socket.setSoTimeout(SOCKET_TIMEOUT);
			socket.connect(new InetSocketAddress(address, port),SOCKET_TIMEOUT);

			InputStream in = socket.getInputStream();
			
			byte[] byte_Buffer = new byte[1024] ;
			int int_BufferSize ;
			
			while ((int_BufferSize = in.read(byte_Buffer)) != -1)
			{
				String string_Buffer = "" ;
				
				for (int i = 0 ;i < byte_Buffer.length; ++i) {
					string_Buffer += (char)byte_Buffer[i]; 
				}
				Log.v("SocketCamera", string_Buffer,null);
				//Log.v("SocketCamer", byte_Buffer, null) ;
			}
			Bitmap bitmap = BitmapFactory.decodeStream(in);
			if (bounds.right == bitmap.getWidth()
					&& bounds.bottom == bitmap.getHeight()) {
				return false;
			} else {
				fos = new FileOutputStream(savePath + "/" + fileName);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos);
				return true;
			}
		} catch (RuntimeException e) {
			Log.i(LOG_TAG, "Failed to obtain image over network", e);
			return false;
		} catch(EOFException e) {
			e.printStackTrace() ;
			return false ;
		} catch(FileNotFoundException e){
			e.printStackTrace() ;
			return false ;
		} catch (IOException e) {
			e.printStackTrace();

			return false;

		} finally {
			if (fos != null) {
				try {
					fos.flush();
					fos.close();
					socket.close();
					
				} catch (IOException e) {
					// swallow
					e.printStackTrace();
					return false;
				}
			}
		}

		//return true;
	}

}
