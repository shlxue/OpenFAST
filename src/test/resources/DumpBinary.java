package com.nywe.test;

public class DumpBinary 
{
    public static void dump ( byte[] byteArray )
    {
        int line = 0;
    
        for ( int i=0; i<byteArray.length; i++)
        {
           byte tByte = byteArray[i];
        
           for ( int j=0; j<8; j++)
           {
                byte tst =(byte)(0x80 & tByte);
                                    
                String ss = ( Math.abs(tst) == 0x80 ) ? "1" : "0";
                tByte = (byte)(tByte << 1);
                
            System.err.print(ss);
           }
        
           if ( line > 3 )
           {
               System.err.println();
               line = 0;
           }
           else
           {
               System.err.print(" ");
               line++;
           }
        
        }
    }
}

