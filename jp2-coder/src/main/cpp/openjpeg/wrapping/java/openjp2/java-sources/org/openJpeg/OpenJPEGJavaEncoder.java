/*
 * Copyright (c) 2002-2014, Universite catholique de Louvain (UCL), Belgium
 * Copyright (c) 2002-2014, Professor Benoit Macq
 * Copyright (c) 2002-2007, Patrick Piscaglia, Telemis s.a.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS `AS IS'
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */  
package org.openJpeg;

import java.io.File;
import java.util.Vector;

/** This class encodes one image into the J2K format, 
 * using the OpenJPEG.org library.
 * To be able to log messages, the called must register a IJavaJ2KEncoderLogger object.
 */
public class OpenJPEGJavaEncoder {

	public interface IJavaJ2KEncoderLogger {
		public void logEncoderMessage(String message);
		public void logEncoderError(String message);
	}
	
    private static boolean isInitialized = false;
    
	// ===== Compression parameters =============>
	// These value may be changed for each image
    private String[] encoder_arguments = null;
	/** number of resolutions decompositions */
	private int nbResolutions = -1;
	/** the quality layers, expressed as compression rate */
	private float[] ratioLayers = null;
	/** the quality layers, expressed as PSNR values. This variable, if defined, has priority over the ratioLayers variable */
	private float[] psnrLayers = null;
	
	/** Contains the 8 bpp version of the image. May NOT be filled together with image16 or image24.<P>
	 * We store the 8 or 16 bpp version of the original image while the encoder uses a 32 bpp version, because <UL>
	 * <LI> the storage capacity required is smaller
	 * <LI> the transfer Java --> C will be faster
	 * <LI> the conversion byte/short ==> int will be done faster by the C
	 * </UL>*/
	private byte[] image8 = null;
	/** Contains the 16 bpp version of the image. May NOT be filled together with image8 or image24*/
	private short[] image16 = null;
	/** Contains the 24 bpp version of the image. May NOT be filled together with image8 or image16 */
	private int[] image24 = null;
	/** Holds the result of the compression, i.e. the J2K compressed bytecode */
    private byte compressedStream[] = null;
    /** Holds the compressed stream length, which may be smaller than compressedStream.length if this byte[] is pre-allocated */
    private long compressedStreamLength = -1;
    /** Holds the compressed version of the index file, returned by the encoder */
    private byte compressedIndex[] = null;
    /** Width and Height of the image */
    private int width = -1;
    private int height = -1;
    private int depth = -1;
    /** Tile size. We suppose the same size for the horizontal and vertical tiles.
     * If size == -1 ==> no tiling */
    private int tileSize = -1;
    // <===== Compression parameters =============
    
    private Vector<IJavaJ2KEncoderLogger> loggers = new Vector();

    public OpenJPEGJavaEncoder(String openJPEGlibraryFullPathAndName, IJavaJ2KEncoderLogger messagesAndErrorsLogger) throws ExceptionInInitializerError
    {
    	this(openJPEGlibraryFullPathAndName);
    	loggers.addElement(messagesAndErrorsLogger);
    }

    public OpenJPEGJavaEncoder(String openJPEGlibraryFullPathAndName) throws ExceptionInInitializerError
    {
    	if (!isInitialized) {
    		try {
    			String absolutePath = (new File(openJPEGlibraryFullPathAndName)).getCanonicalPath();
    			System.load(absolutePath);
    			isInitialized = true;
    		} catch (Throwable t) {
    			t.printStackTrace();
    			throw new ExceptionInInitializerError("OpenJPEG Java Encoder: probably impossible to find the C library");
    		}
    	}
    }
    
    public void addLogger(IJavaJ2KEncoderLogger messagesAndErrorsLogger) {
    	loggers.addElement(messagesAndErrorsLogger);
    }
    
    public void removeLogger(IJavaJ2KEncoderLogger messagesAndErrorsLogger) {
    	loggers.removeElement(messagesAndErrorsLogger);
    }
    
    /** This method compresses the given image.<P>
     * It returns the compressed J2K codestream into the compressedStream byte[].<P>
     * It also returns the compression index as a compressed form, into the compressedIndex byte[].<P>
     * One of the image8, image16 or image24 arrays must be correctly initialized and filled.<P>
     * The width, height and depth variables must be correctly filled.<P>
     * The nbResolutions, nbLayers and if needed the float[] psnrLayers or ratioLayers must also be filled before calling this method.
     */
    public void encodeImageToJ2K() {
		// Need to allocate / reallocate the compressed stream buffer ? (size = max possible size = original image size)
		if (compressedStream== null || (compressedStream.length != width*height*depth/8)) {
			logMessage("OpenJPEGJavaEncoder.encodeImageToJ2K: (re-)allocating " + (width*height*depth/8) + " bytes for the compressedStream");
			compressedStream = new byte[width*height*depth/8];
		}
		// Arguments = 
		// - number of resolutions "-n 5" : 2
		// - size of tile "-t 512,512" : 2
		// 
		// Image width, height, depth and pixels are directly fetched by C from the Java class
		int nbArgs = 2 + (tileSize == -1 ? 0 : 2) + (encoder_arguments != null ? encoder_arguments.length : 0);
		if (psnrLayers != null && psnrLayers.length>0 && psnrLayers[0] != 0)
			// If psnrLayers is defined and doesn't just express "lossless"
			nbArgs += 2;
		else if (ratioLayers != null && ratioLayers.length>0 && ratioLayers[0]!=0.0)
			nbArgs += 2;
		String[] arguments = new String[nbArgs];
		int offset = 0;
		arguments[offset] = "-n"; arguments[offset+1] = "" + nbResolutions; offset += 2;
		if (tileSize!= -1) {
			arguments[offset++] = "-t"; 
			arguments[offset++] = "" + tileSize + "," + tileSize;
		}
		// If PSNR layers are defined, use them to encode the images
		if (psnrLayers != null && psnrLayers.length>0 && psnrLayers[0]!=-1) {
			arguments[offset++] = "-q";
			String s = "";
			for (int i=0; i<psnrLayers.length; i++)
				s += psnrLayers[i] + ",";
			arguments[offset++] = s.substring(0, s.length()-1);
		} else if (ratioLayers != null && ratioLayers.length>0 && ratioLayers[0]!=0.0) {
			// Specify quality ratioLayers, as compression ratios
			arguments[offset++] = "-r";
			String s = "";
			for (int i=0; i<ratioLayers.length; i++)
				s += ratioLayers[i] + ",";
			arguments[offset++] = s.substring(0, s.length()-1);
		}
		if (encoder_arguments != null) {
			for (int i=0; i<encoder_arguments.length; i++) {
				arguments[i+offset] = encoder_arguments[i];
			}
		}
		logMessage("Encoder additional arguments = " + arrayToString(arguments));
		long startTime = (new java.util.Date()).getTime();
		compressedStreamLength = internalEncodeImageToJ2K(arguments);
		logMessage("compression time = " + ((new java.util.Date()).getTime() - startTime) + " msec");
    }
    
    /** 
     * Fills the compressedStream byte[] and the compressedIndex byte[]
     * @return the codestream length.
     */
    private native long internalEncodeImageToJ2K(String[] parameters);

    /** Image depth in bpp */
	public int getDepth() {
		return depth;
	}

    /** Image depth in bpp */
	public void setDepth(int depth) {
		this.depth = depth;
	}

	/** Image height in pixels  */
	public int getHeight() {
		return height;
	}

	/** Image height in pixels  */
	public void setHeight(int height) {
		this.height = height;
	}

	/** This method must be called in depth in [9,16].
	 * @param an array of shorts, containing width*height values
	 */
	public void setImage16(short[] image16) {
		this.image16 = image16;
	}

	/** This method must be called in depth in [17,24] for RGB images.
	 * @param an array of int, containing width*height values
	 */
	public void setImage24(int[] image24) {
		this.image24 = image24;
	}

	/** This method must be called in depth in [1,8].
	 * @param an array of bytes, containing width*height values
	 */
	public void setImage8(byte[] image8) {
		this.image8 = image8;
	}

	/** Return the ratioLayers, i.e. the compression ratio for each quality layer.
	 * If the last value is 0.0, last layer is lossless compressed.
	 */
	public float[] getRatioLayers() {
		return ratioLayers;
	}

	/**
	 * sets the quality layers.
	 * At least one level.
	 * Each level is expressed as a compression ratio (float).
	 * If the last value is 0.0, the last layer will be losslessly compressed
	 */
	public void setRatioLayers(float[] layers) {
		this.ratioLayers = layers;
	}

	/** Return the PSNR Layers, i.e. the target PSNR for each quality layer.
	 * If the last value is -1, last layer is lossless compressed.
	 */
	public float[] getPsnrLayers() {
		return psnrLayers;
	}

	/**
	 * sets the quality layers.
	 * At least one level.
	 * Each level is expressed as a target PSNR (float).
	 * If the last value is -1, the last layer will be losslessly compressed
	 */
	public void setPsnrLayers(float[] layers) {
		this.psnrLayers = layers;
	}

	/** Set the number of resolutions that must be created */
	public void setNbResolutions(int nbResolutions) {
		this.nbResolutions = nbResolutions;
	}

	public int getWidth() {
		return width;
	}

	/** Width of the image, in pixels */
	public void setWidth(int width) {
		this.width = width;
	}

	/** Return the compressed index file.
	 * Syntax: TODO PP:
	 */
	public byte[] getCompressedIndex() {
		return compressedIndex;
	}
	
	public void setCompressedIndex(byte[] index) {
		compressedIndex = index;
	}

	public byte[] getCompressedStream() {
		return compressedStream;
	}

	public void reset() {
		nbResolutions = -1;
		ratioLayers = null;
		psnrLayers = null;
		image8 = null;
		image16 = null;
		image24 = null;
		compressedStream = null;
	    compressedIndex = null;
	    width = -1;
	    height = -1;
	    depth = -1;
	}

	public short[] getImage16() {
		return image16;
	}

	public int[] getImage24() {
		return image24;
	}

	public byte[] getImage8() {
		return image8;
	}
	
	/** Sets the size of the tiles. We assume square tiles */
	public void setTileSize(int tileSize) {
		this.tileSize = tileSize;
	}
	
	/** Contains all the encoding arguments other than the input/output file, compression ratio, tile size */
	public void setEncoderArguments(String[] argumentsForTheEncoder) {
		encoder_arguments = argumentsForTheEncoder;
	}

	public void logMessage(String message) {
		for (IJavaJ2KEncoderLogger logger:loggers)
			logger.logEncoderMessage(message);
	}
	
	public void logError(String error) {
		for (IJavaJ2KEncoderLogger logger:loggers)
			logger.logEncoderError(error);
	}

	public long getCompressedStreamLength() {
		return compressedStreamLength;
	}
	
	private String arrayToString(String[] array) {
		if (array == null)
			return "NULL";
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<array.length; i++)
			sb.append(array[i]).append(" ");
		sb.delete(sb.length()-1, sb.length());
		return sb.toString();
	}
}
