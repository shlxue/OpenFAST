package com.nywe.test;
/**
 * Name: OpraFeed.java
 * 
 * Description: A little test program to read a FAST FIX encoded OPRA 
 *              File and decode it using the OPEN FAST Java decoder. The 
 *              File is provided by SIAC to help an application developer test 
 *              their FAST FIX implementation. There is 4 byte lenght field 
 *              in front of each packet in the file, there are also
 *              some control characters and a one byte length field in front of
 *              each encoded msg ( see the code, pretty simple ).
 * 
 * 
 * Author: Tom Clark
 * 
 * 
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.openfast.Context;
import org.openfast.Message;
import org.openfast.codec.FastDecoder;
import org.openfast.template.MessageTemplate;
import org.openfast.template.loader.MessageTemplateLoader;
import org.openfast.template.loader.XMLMessageTemplateLoader;
import org.w3c.dom.Document;

public class OpraFeed
{
private int mMsgsRead = 0;
private MessageTemplate mTemplate = null;
private InputStream mFileInputStream = null;
private DataInput mDataInputStream;
private Context mContext;
private String  mOPRAEncodedFile;

    public static void main ( String args[] )
    {
        if ( args.length < 2 )
        {
            System.err.println("Invalid Args, usage: [OPRAEncodedFile OPRATemplateXMLFile");
            System.exit(-1);
        }
        
        new OpraFeed (args[0],args[1]);
        
    }
    
    /**
     * 
     * Constructor:
     * 
     * @param pOPRAEncodedFile
     * @param pFASTFIXTemplateFile
     */
    
    public OpraFeed ( String pOPRAEncodedFile,
                      String pFASTFIXTemplateFile )
    {
        mOPRAEncodedFile = pOPRAEncodedFile;
        
        System.out.println("Starting Fast FIX OPRA Test\n" +
                           "Using Encoded File " + mOPRAEncodedFile + "\n" +
                           "Using FAST Template " + pFASTFIXTemplateFile );
    
        mFileInputStream = null;
        //OutputStream tFileOutputStream = null;
    
        try
        {
            mFileInputStream = new FileInputStream ( pOPRAEncodedFile );
            //tFileOutputStream = new FileOutputStream ( args[0]+ ".out" );
        }
        catch ( Throwable t )
        {
            System.err.println("Error Opening File " + pOPRAEncodedFile);
            t.printStackTrace();
            System.exit(-1);
        }
        
        // Wrap the FileInputStream wityh a DataInputStream to allow for easy reading of the stream
        mDataInputStream = new DataInputStream (  mFileInputStream );
 
         
        mTemplate = getTemplateFromXMLFile ( pFASTFIXTemplateFile );
        mTemplate.setId("0");
    
        mContext = new Context();
        mContext.registerTemplate(0, mTemplate);
        
        readAndDecodeFile();
    
    }    
    
    /**
     * 
     * This is the guts of the program, this is where
     * we read and decode the file
     * 
     */
    
    public void readAndDecodeFile()
    {
        int tLen = 0;
        
        try
        {
            while ( true )
            {
                tLen = mDataInputStream.readInt(); // Read 4 byte packet len, Multiple msgs are packed into a single packet.
                System.err.println("Packet Read, Len = " + tLen );
      
                byte tSOH = mDataInputStream.readByte(); // Soh
                tLen--;
      
                if ( tSOH != 1 ) // Every packet message must start with an SOH.
                {
                    System.err.println("Invalid Message, No SOH");
                    System.exit(-1);
                    break;
                }
      
                //
                // Loop thru the messages in the packet.
                //
      
                // mContext.reset(); // should reset between packets
              
                while ( tLen > 0  )
                {
                    // one byte field denotes size of decoded data.
                    
                    int tMsgLen = (int)mDataInputStream.readByte(); // len      
                    tMsgLen = (0x000000FF & tMsgLen ); // unsigned.
                    
                    tLen--;
          
                    System.err.println("Len of decoded data msg = " + tMsgLen );

                   
                    if ( tMsgLen == 3 ) // ETX
                    {
                        System.err.println("ETX Reached, End Of Packet Data");
                        break;
                    }
                    
                    tLen -= tMsgLen;
          
                    byte[] byteArray = new byte[tMsgLen];
                    
                   
                    mDataInputStream.readFully(byteArray); // We are reading the "Encoded Message" here!!!!
          
                    InputStream tInput = null;
                    FastDecoder tDecoder = null;
                    Message tMessage  = null;
         
                    //HexDump.dumpHexData("FAST FIX Dump ",byteArray);
                    //DumpBinary.dump(byteArray);
      
                    //tFileOutputStream.write(byteArray); // write raw encoded data file
                    tInput =  new ByteArrayInputStream( byteArray );
                    tDecoder = new FastDecoder(mContext, tInput);
                    tMessage  = tDecoder.readMessage();
               
                    dumpMessage( tMessage );
                }
              
              }
          }
          catch ( EOFException e )
          {
              System.err.println("End Of File Reached " + mMsgsRead + " Messages Decoded" );
              System.exit(-1);
          }
          catch ( Throwable t )
          {
              System.err.println("Error Reading File " + mOPRAEncodedFile + " Error = " + t.getMessage() );
              t.printStackTrace();
              System.exit(-1);
          }
    }
    
    /**
     * 
     *  Method: dumpMessage()
     *  
     * 
     * @param pMessage
     */

    protected void dumpMessage ( Message pMessage )
    {
        System.err.println("---------  Message # [" + (++mMsgsRead) + "]------------ ");
    
        for ( int i=0; i<pMessage.getFieldCount(); i++ )
        {
            if ( pMessage.isDefined(i) )
            {
                if (i == 1 || i == 2 ) // 1 = CATEGORY 2 = MESSAGE TYPE
                    System.err.println("["+i+"] " + mTemplate.getField(i).getName()+ " = " + (char)Byte.parseByte(pMessage.getValue(i).toString()));
                else
                    System.err.println("["+i+"] " + mTemplate.getField(i).getName()+ " = " +  pMessage.getValue(i).toString());    
            }
        }
    }
   
    /**
     * 
     * Method: getTemplateFromXMLFile
     * 
     * @param pFASTFIXTemplateFile
     * @return
     */
    
    protected MessageTemplate getTemplateFromXMLFile ( String pFASTFIXTemplateFile )
    {
        try
        {
            Document tDoc = loadDocFromFile ( pFASTFIXTemplateFile );
            byte[] templateXml = docToByteArray( tDoc );

            MessageTemplateLoader loader = new XMLMessageTemplateLoader();
            MessageTemplate[] templates = loader.load(new ByteArrayInputStream(templateXml));
         
            return templates[0]; // Only one Template Defined
        }
        catch ( Throwable t )
        {
            System.err.println("Error Loading Template " + t.getMessage());
            t.printStackTrace();
            System.exit(-1);
        }
        
        return null; // never reached, keep compiler happy.
    }

    /**
     * 
     * 
     * @param pXMLFilename
     * @return
     * @throws Exception
     */
   
    
    public static Document loadDocFromFile ( String pXMLFilename ) throws Exception
    {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();

        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

        Document doc = docBuilder.parse (new File(pXMLFilename));

        return doc;

    }
    
    /**
     * 
     * 
     * @param pDoc
     * @return
     * @throws Exception
     */

    public static byte[] docToByteArray( Document pDoc ) throws Exception
    {
        // TODO: look into doing this only once!!!
        // TODO: Performance check!!!!
        TransformerFactory xformFactory = TransformerFactory.newInstance();
        Transformer idTransform = xformFactory.newTransformer();

        Source input = new DOMSource(pDoc);

        ByteArrayOutputStream tArray = new ByteArrayOutputStream();
        Result output = new StreamResult( tArray  );

        idTransform.transform(input, output);

        return tArray.toByteArray();

    }



}
