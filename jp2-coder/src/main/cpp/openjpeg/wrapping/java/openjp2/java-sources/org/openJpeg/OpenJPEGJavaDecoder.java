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

import java.util.Vector;

/** This class decodes one J2K codestream into an image (width + height + depth + pixels[], 
 * using the OpenJPEG.org library.
 * To be able to log messages, the called must register a IJavaJ2KDecoderLogger object.
 */
public class OpenJPEGJavaDecoder {

	public interface IJavaJ2KDecoderLogger {
		public void logDecoderMessage(String message);
		public void logDecoderError(String message);
	}
	
    private static boolean isInitialized = false;
    
	// ===== decompression parameters =============>
	// These value may be changed for each image
    private String[] decoder_arguments = null;
	/** number of resolutions decompositions */
	private int nbResolutions = -1;
	/** the quality layers */
	private int[] layers = null;

	/** Contains the 8 bpp version of the image. May NOT be filled together with image16 or image24.<P>
	 * We store in Java the 8 or 16 bpp version of the image while the decoder uses a 32 bpp version, because <UL>
	 * <LI> the storage capacity required is smaller
	 * <LI> the transfer Java <-- C will be faster
	 * <LI> the conversion byte/short ==> int will be done faster by the C
	 * </UL>*/
	private byte[] image8 = null;
	/** Contains the 16 bpp version of the image. May NOT be filled together with image8 or image24*/
	private short[] image16 = null;
	/** Contains the 24 bpp version of the image. May NOT be filled together with image8 or image16 */
	private int[] image24 = null;
	/** Holds the J2K compressed bytecode to decode */
    private byte compressedStream[] = null;
    /** Holds the compressed version of the index file, to be used by the decoder */
    private byte compressedIndex[] = null;
    /** Width and Height of the image */
    private int width = -1;
    private int height = -1;
    private int depth = -1;
    /** This parameter is never used in Java but is read by the C library to know the number of resolutions to skip when decoding, 
     * i.e. if there are 5 resolutions and skipped=1 ==> decode until resolution 4.  */
    private int skippedResolutions = 0;
    
    private Vector<IJavaJ2KDecoderLogger> loggers = new Vector();


    public OpenJPEGJavaDecoder(String openJPEGlibraryFullPathAndName, IJavaJ2KDecoderLogger messagesAndErrorsLogger) throws ExceptionInInitializerError
    {
    	this(openJPEGlibraryFullPathAndName);
    	loggers.addElement(messagesAndErrorsLogger);
    }

    public OpenJPEGJavaDecoder(String openJPEGlibraryFullPathAndName) throws ExceptionInInitializerError
    {
    	if (!isInitialized) {
    		try {
    			System.load(openJPEGlibraryFullPathAndName);
    			isInitialized = true;
    		} catch (Throwable t) {
    			throw new ExceptionInInitializerError("OpenJPEG Java Decoder: probably impossible to find the C library");
    		}
    	}
    }
    
    public void addLogger(IJavaJ2KDecoderLogger messagesAndErrorsLogger) {
    	loggers.addElement(messagesAndErrorsLogger);
    }
    
    public void removeLogger(IJavaJ2KDecoderLogger messagesAndErrorsLogger) {
    	loggers.removeElement(messagesAndErrorsLogger);
    }
    
    public int  decodeJ2KtoImage() {
		if ((image16 == null || (image16 != null && image16.length != width*height)) && (depth==-1 || depth==16)) {
			image16 = new short[width*height];
			logMessage("OpenJPEGJavaDecoder.decompressImage: image16 length = " + image16.length + " (" + width + " x " + height + ") ");
		}
		if ((image8 == null || (image8 != null && image8.length != width*height)) && (depth==-1 || depth==8)) {
			image8 = new byte[width*height];
			logMessage("OpenJPEGJavaDecoder.decompressImage: image8 length = " + image8.length + " (" + width + " x " + height + ") ");
		}
		if ((image24 == null || (image24 != null && image24.length != width*height)) && (depth==-1 || depth==24)) {
			image24 = new int[width*height];
			logMessage("OpenJPEGJavaDecoder.decompressImage: image24 length = " + image24.length + " (" + width + " x " + height + ") ");
		}
		
		String[] arguments = new String[0 + (decoder_arguments != null ? decoder_arguments.length : 0)];
		int offset = 0;
		if (decoder_arguments != null) {
			for (int i=0; i<decoder_arguments.length; i++) {
				arguments[i+offset] = decoder_arguments[i];
			}
		}

		return internalDecodeJ2KtoImage(arguments);
    }
    
    /** 
     * Decode the j2k stream given in the codestream byte[] and fills the image8, image16 or image24 array, according to the bit depth.
     */
    private native int internalDecodeJ2KtoImage(String[] parameters);

    /** Image depth in bpp */
	public int getDepth() {
		return depth;
	}

    /** Image depth in bpp */
	public void setDepth(int depth) {
		this.depth = depth;
	}

	/** Image height in pixels */
	public int getHeight() {
		return height;
	}

	/** Image height in pixels */
	public void setHeight(int height) {
		this.height = height;
	}

	/** Number of resolutions contained in the image */
	public int getNbResolutions() {
		return nbResolutions;
	}

	/** Number of resolutions contained in the image */
	public void setNbResolutions(int nbResolutions) {
		this.nbResolutions = nbResolutions;
	}

	/** Width of the image in pixels */
	public int getWidth() {
		return width;
	}

	/** Width of the image in pixels */
	public void setWidth(int width) {
		this.width = width;
	}

	/** Contains the decompressed version of the image, if the depth in is [9,16] bpp.
	 * Returns NULL otherwise.
	 */
	public short[] getImage16() {
		return image16;
	}

	/** Contains the decompressed version of the image, if the depth in is [17,24] bpp and the image is in color.
	 * Returns NULL otherwise.
	 */
	public int[] getImage24() {
		return image24;
	}

	/** Contains the decompressed version of the image, if the depth in is [1,8] bpp.
	 * Returns NULL otherwise.
	 */
	public byte[] getImage8() {
		return image8;
	}

	/** Sets the compressed version of the index file for this image.
	 * This index file is used by the decompressor
	 */
	public void setCompressedIndex(byte[] compressedIndex) {
		this.compressedIndex = compressedIndex;
	}

	/** Sets the codestream to be decoded */
	public void setCompressedStream(byte[] compressedStream) {
		this.compressedStream = compressedStream;
	}

	/** @return the compressed code stream length, or -1 if not defined */
	public long getCodestreamLength() {
		if (compressedStream == null)
			return -1;
		else return compressedStream.length;
	}
	
	/** This method is called either directly or by the C methods */
	public void logMessage(String message) {
		for (IJavaJ2KDecoderLogger logger:loggers)
			logger.logDecoderMessage(message);
	}
	
	/** This method is called either directly or by the C methods */
	public void logError(String error) {
		for (IJavaJ2KDecoderLogger logger:loggers)
			logger.logDecoderError(error);
	}

	public void reset() {
		nbResolutions = -1;
		layers = null;
		image8 = null;
		image16 = null;
		image24 = null;
		compressedStream = null;
	    compressedIndex = null;
	    width = -1;
	    height = -1;
	    depth = -1;
	}

	public void setSkippedResolutions(int numberOfSkippedResolutions) {
		skippedResolutions = numberOfSkippedResolutions;
	}

	/** Contains all the decoding arguments other than the input/output file */
	public void setDecoderArguments(String[] argumentsForTheDecoder) {
		decoder_arguments = argumentsForTheDecoder;
	}


}
