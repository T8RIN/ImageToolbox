package com.android.nQuant;

public interface Ditherable {
	int getColorIndex(final int c);

	short nearestColorIndex(final Integer[] palette, final int c, final int pos);
}
