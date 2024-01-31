package com.android.nQuant;

public interface Ditherable {
	public int getColorIndex(final int c);
	
	public short nearestColorIndex(final Integer[] palette, final int c, final int pos);
}
