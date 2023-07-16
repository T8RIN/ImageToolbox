
#include <stdio.h>
    
int main()
{
  __int64 off=0;

  _fseeki64(NULL, off, SEEK_SET);
        
  return 0;
}
