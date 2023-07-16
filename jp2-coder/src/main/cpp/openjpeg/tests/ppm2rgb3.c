/*
 * Copyright (c) 2014, Mathieu Malaterre <mathieu.malaterre@voxxl.com>
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

/*
 * Technically on UNIX, one can simply call `ppmtorgb3`, but on my system it
 * did not work. So I had to write my own.
 */

#include <stdio.h>  /* fprintf */
#include <string.h> /* strcmp */
#include <stdlib.h> /* malloc */

static const char magic[] = "P6";

static int readheader( FILE *ppm, int *X, int *Y, int *bpp )
{
  char buffer[256];
  char strbuffer[256];
  char *line;
  int n;

  *X = *Y = *bpp = 0;

  line = fgets(buffer, sizeof(buffer), ppm);
  if( !line ) return 0;
  n = sscanf(buffer, "%255[^\r\n]", strbuffer);
  if( n != 1 ) return 0;
  if( strcmp(strbuffer, magic ) != 0 ) return 0;

  /* skip comments */
  while( fgets(buffer, sizeof(buffer), ppm) && *buffer == '#' )
    {
    }
  n = sscanf(buffer, "%d %d", X,Y);
  if( n != 2 ) return 0;
  line = fgets(buffer, sizeof(buffer), ppm);
  if( !line ) return 0;
  n = sscanf(buffer, "%d", bpp);
  if( n != 1 ) return 0;
  if( *bpp != 255 ) return 0;

  return 1;
}

static int writeoutput( const char *fn, FILE *ppm, int X, int Y, int bpp )
{
  FILE *outf[] = {NULL, NULL, NULL};
  int i, x, y = 0;
  char outfn[256];
  static const char *exts[3] = {
    "red",
    "grn",
    "blu"
  };
  char *image_line = NULL;
  int ok = 0;

  /* write single comp as PGM: P5 */
  for( i = 0; i < 3; ++i )
    {
#ifdef _MSC_VER
#define snprintf _snprintf /* Visual Studio */
#endif
    snprintf( outfn, sizeof(outfn), "%s.%s.pgm", fn, exts[i] );
    outf[i] = fopen( outfn, "wb" );
    if( !outf[i] ) goto cleanup;
    /* write header */
    fprintf( outf[i], "P5\n" );
    fprintf( outf[i], "%d %d\n", X, Y );
    fprintf( outf[i], "%d\n", bpp );
    }

  /* write pixel data */
  image_line = (char*)malloc( (size_t)X * 3 * sizeof(char) );
  if( !image_line ) goto cleanup;
  while( fread(image_line, sizeof(char), (size_t)X * 3, ppm) == (size_t)X * 3 )
    {
    for( x = 0; x < X; ++x )
      for( i = 0; i < 3; ++i )
        if( fputc( image_line[3*x+i], outf[i] ) == EOF ) goto cleanup;
    ++y;
    }
  if( y == Y )
    ok = 1;
cleanup:
  free(image_line);
  for( i = 0; i < 3; ++i )
    if( outf[i] ) fclose( outf[i] );

  return ok;
}

int main(int argc, char *argv[])
{
  const char *fn;
  FILE *ppm = NULL;
  int X, Y, bpp;
  int ok = 0;

  if( argc < 2 )
    {
    fprintf( stderr, "%s input.ppm\n", argv[0] );
    goto cleanup;
    }
  fn = argv[1];
  ppm = fopen( fn, "rb" );

  if( !readheader( ppm, &X, &Y, &bpp ) ) goto cleanup;

  if( !writeoutput(fn, ppm, X, Y, bpp ) ) goto cleanup;

  ok = 1;
cleanup:
  if(ppm) fclose(ppm);
  return ok ? EXIT_SUCCESS : EXIT_FAILURE;
}
