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
 * Extract all JP2 files contained within a PDF file.
 *
 * Technically you could simply use mutool, eg:
 *
 * $ mutool show -be -o obj58.jp2 Bug691816.pdf 58
 *
 * to extract a given JP2 file from within a PDF
 * However it happens sometimes that the PDF is itself corrupted, this tools is
 * a lame PDF parser which only extract stream contained in JPXDecode box
 * only work on linux since I need memmem function
 */

/*
 * Add support for other signatures:
 * 
 * obj<</Subtype/Image/Length 110494/Filter/JPXDecode/BitsPerComponent 8/ColorSpace/DeviceRGB/Width 712/Height 1052>>stream
 */
#define _GNU_SOURCE
#include <string.h>
#include <stdio.h>
#include <stddef.h>
#include <assert.h>

int main(int argc, char *argv[])
{
#define NUMJP2 32
  int i, c = 0;
  long offets[NUMJP2];
  char buffer[512];
#define BUFLEN 4096
  int cont = 1;
  FILE *f;
  size_t nread;
  char haystack[BUFLEN];
  const char needle[] = "JPXDecode";

  const size_t nlen = strlen( needle );
  const size_t flen = BUFLEN - nlen;
  char *fpos = haystack + nlen;
  const char *filename;
  if( argc < 2 ) return 1;

  filename = argv[1];

  memset( haystack, 0, nlen );

  f = fopen( filename, "rb" );
  while( cont )
    {
    const char *ret;
    size_t hlen;
    nread = fread(fpos, 1, flen, f);
    hlen = nlen + nread;
    ret = memmem( haystack, hlen, needle, nlen);
    if( ret )
      {
      const long cpos = ftell(f);
      const ptrdiff_t diff = ret - haystack;
      assert( diff >= 0 );
      /*fprintf( stdout, "Found it: %lx\n", (ptrdiff_t)cpos - (ptrdiff_t)hlen + diff);*/
      offets[c++] = (ptrdiff_t)cpos - (ptrdiff_t)hlen + diff;
      }
    cont = (nread == flen);
    memcpy( haystack, haystack + nread, nlen );
    }

  assert( feof( f ) );

  for( i = 0; i < c; ++i )
    {
    int s, len = 0;
    char *r;
    const int ret = fseek(f, offets[i], SEEK_SET);
    assert( ret == 0 );
    r = fgets(buffer, sizeof(buffer), f);
    assert( r );
    /*fprintf( stderr, "DEBUG: %s", r );*/
    s = sscanf(r, "JPXDecode]/Length  %d/Width %*d/BitsPerComponent %*d/Height %*d", &len);
    if( s == 0 )
      { // try again harder
      const int ret = fseek(f, offets[i] - 40, SEEK_SET); // 40 is magic number
      assert( ret == 0 );
      r = fgets(buffer, sizeof(buffer), f);
      assert( r );
      const char needle2[] = "/Length";
      char * s2 = strstr(buffer, needle2);
      s = sscanf(s2, "/Length  %d/", &len);
      }
    if( s == 1 )
      {
      FILE *jp2;
      int j;
      char jp2fn[512];
      sprintf( jp2fn, "%s.%d.jp2", filename, i );
      jp2 = fopen( jp2fn, "wb" );
      for( j = 0; j < len; ++j )
        {
        int v = fgetc(f);
        int ret2 = fputc(v, jp2);
        assert( ret2 != EOF );
        }
      fclose( jp2 );
#if 0
      /* TODO need to check we reached endstream */
      r = fgets(buffer, sizeof(buffer), f);
      fprintf( stderr, "DEBUG: [%s]", r );
      r = fgets(buffer, sizeof(buffer), f);
      fprintf( stderr, "DEBUG: [%s]", r );
#endif
      }
    }
  fclose(f);

  return 0;
}
