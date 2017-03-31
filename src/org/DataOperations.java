package org;

import java.io.*;

import org.util.misc;

public class DataOperations {

    public static InputStream streamFromPath(String path) throws IOException {
        return new BufferedInputStream(new FileInputStream(path));
    }

    public static void readFromPath(String path, byte abyte0[], int length) throws IOException {
        InputStream inputstream = streamFromPath(path);
        DataInputStream datainputstream = new DataInputStream(inputstream);
        try {
            datainputstream.readFully(abyte0, 0, length);
        }
        catch (EOFException _ex) {
        }
        datainputstream.close();
    }

    public static int getUnsignedByte(byte byte0) {
        return byte0 & 0xff;
    }

    public static int getUnsigned2Bytes(byte abyte0[], int i) {
        return ((abyte0[i] & 0xff) << 8) + (abyte0[i + 1] & 0xff);
    }

    public static int getUnsigned4Bytes(byte abyte0[], int i) {
        return ((abyte0[i] & 0xff) << 24) + ((abyte0[i + 1] & 0xff) << 16) + ((abyte0[i + 2] & 0xff) << 8) + (abyte0[i + 3] & 0xff);
    }

    public static long getUnsigned8Bytes(byte abyte0[], int i) {
        return (((long) getUnsigned4Bytes(abyte0, i) & 0xffffffffL) << 32) + ((long) getUnsigned4Bytes(abyte0, i + 4) & 0xffffffffL);
    }

    public static int readInt(byte abyte0[], int i) {
        return ((abyte0[i] & 0xff) << 24) | ((abyte0[i + 1] & 0xff) << 16) | ((abyte0[i + 2] & 0xff) << 8) | (abyte0[i + 3] & 0xff);
    }

    public static int getSigned2Bytes(byte abyte0[], int i) {
        int j = getUnsignedByte(abyte0[i]) * 256 + getUnsignedByte(abyte0[i + 1]);
        if (j > 32767) {
            j -= 0x10000;
        }
        return j;
    }

    public static int getSigned4Bytes(byte abyte0[], int i) {
        if ((abyte0[i] & 0xff) < 128) {
            return abyte0[i];
        } else {
            return ((abyte0[i] & 0xff) - 128 << 24) + ((abyte0[i + 1] & 0xff) << 16) + ((abyte0[i + 2] & 0xff) << 8) + (abyte0[i + 3] & 0xff);
        }
    }

    public static int getIntFromByteArray(byte byteArray[], int offset, int length) {
        int bitOffset = offset >> 3;
        int bitMod = 8 - (offset & 7);
        int i1 = 0;
        for (; length > bitMod; bitMod = 8) {
            i1 += (byteArray[bitOffset++] & baseLengthArray[bitMod]) << length - bitMod;
            length -= bitMod;
        }

        if (length == bitMod)
            i1 += byteArray[bitOffset] & baseLengthArray[bitMod];
        else
            i1 += byteArray[bitOffset] >> bitMod - length & baseLengthArray[length];
        return i1;
    }

    public static String addCharacters(String s, int i) {
        String s1 = "";
        for (int j = 0; j < i; j++)
            if (j >= s.length()) {
                s1 = s1 + " ";
            } else {
                char c = s.charAt(j);
                if (c >= 'a' && c <= 'z')
                    s1 = s1 + c;
                else if (c >= 'A' && c <= 'Z')
                    s1 = s1 + c;
                else if (c >= '0' && c <= '9')
                    s1 = s1 + c;
                else
                    s1 = s1 + '_';
            }

        return s1;
    }

    public static long stringLength12ToLong(String s) {
        String s1 = "";
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c >= 'a' && c <= 'z')
                s1 = s1 + c;
            else if (c >= 'A' && c <= 'Z')
                s1 = s1 + (char) ((c + 97) - 65);
            else if (c >= '0' && c <= '9')
                s1 = s1 + c;
            else
                s1 = s1 + ' ';
        }

        s1 = s1.trim();
        if (s1.length() > 12)
            s1 = s1.substring(0, 12);
        long l = 0L;
        for (int j = 0; j < s1.length(); j++) {
            char c1 = s1.charAt(j);
            l *= 37L;
            if (c1 >= 'a' && c1 <= 'z')
                l += (1 + c1) - 97;
            else if (c1 >= '0' && c1 <= '9')
                l += (27 + c1) - 48;
        }

        return l;
    }

    public static String longToString(long l) {
        if (l < 0L)
            return "invalid_name";
        String s = "";
        while (l != 0L) {
            int i = (int) (l % 37L);
            l /= 37L;
            if (i == 0)
                s = " " + s;
            else if (i < 27) {
                if (l % 37L == 0L)
                    s = (char) ((i + 65) - 1) + s;
                else
                    s = (char) ((i + 97) - 1) + s;
            } else {
                s = (char) ((i + 48) - 27) + s;
            }
        }
        return s;
    }
    
    public static int storeDB(String entry, byte database[]) {
        int nDBEntries = getUnsigned2Bytes(database, 0);
        int entryIdentifier = 0;
        entry = entry.toUpperCase();
        for (int k = 0; k < entry.length(); k++)
            entryIdentifier = (entryIdentifier * 61 + entry.charAt(k)) - 32;
        int offset = 2 + nDBEntries * 10;
        long dbEntryIdentifier;
        int dbEntryLength;
        byte[] out;
        for (int i = 0; i < nDBEntries; i++) {
            dbEntryIdentifier = ((database[i * 10 + 2] & 0xff) << 24)
            		+ ((database[i * 10 + 3] & 0xff) << 16)
            		+ ((database[i * 10 + 4] & 0xff) << 8)
            		+ (database[i * 10 + 5] & 0xff);
            dbEntryLength = ((database[i * 10 + 9] & 0xff) << 16)
            		+ ((database[i * 10 + 10] & 0xff) << 8)
            		+ (database[i * 10 + 11] & 0xff);
            out = new byte[dbEntryLength];
            for (int j = 0; j < dbEntryLength; ++j)
            	out[j] = (byte)(database[offset + j] >> 1);
            /*
            try {
            	//misc.writeToFile(out, "src/org/conf/utils/cache/data/sounds1/sound"+i+".pcm");
            	//misc.writeToFile(out, "src/org/conf/utils/cache/data/models36/object"+i+".ob3");
            } catch (IOException ioe) {ioe.printStackTrace();}
            */
            offset += dbEntryLength;
        }

        return 0;
    }
    
    private static int getIdentifier(String entry)
    {
        int entryIdentifier = 0;
        entry = entry.toUpperCase();
        for (int k = 0; k < entry.length(); k++)
            entryIdentifier = (entryIdentifier * 61 + entry.charAt(k)) - 32;
        return entryIdentifier;
    }
    
    /**
     * A database has the following layout (name(bytes)):
     * HEADER:
     * nbr_of_entries(2), identifier0(4), length0(3), length0(3),
     * identifier1(4), length1(3), length1(3),
     * identifier2(4), length2(3), length2(3),
     * ...
     * identifierN(4), lengthN(3), lengthN(3)
     * 
     * DATA:
     * data0(length0)
     * data1(length1)
     * data2(length2)
     * ...
     * dataN(lengthN)
     * 
     * @param entry A sting containing the sought entry
     * @param database The database where the entry should be found.
     * @return the offset (i.e. index) in the database where the entry is stored.
     */
    public static int getEntryOffset(String entry, byte database[]) {
        int nDBEntries = getUnsigned2Bytes(database, 0);
        int entryIdentifier = getIdentifier(entry);
        int offset = 2 + nDBEntries * 10;
        int dbEntryIdentifier, dbEntryLength;
        /*
        if (entry.startsWith("Legends")
        		&& (entry.endsWith(".ob3") || entry.endsWith(".OB3")))
        {
        	storeDB(entry, database);
        	System.out.printf("Printing "+nDBEntries+" entries to database\n");
        }*//*
        if (entry.endsWith(".pcm") || entry.endsWith(".PCM"))
        {
        	storeDB(entry, database);
        	System.out.printf("Printing to database\n");
        }*/
        for (int i = 0; i < nDBEntries; i++) {
            dbEntryIdentifier = ((database[i * 10 + 2] & 0xff) << 24)
            		+ ((database[i * 10 + 3] & 0xff) << 16)
            		+ ((database[i * 10 + 4] & 0xff) << 8)
            		+ (database[i * 10 + 5] & 0xff);
            dbEntryLength = ((database[i * 10 + 9] & 0xff) << 16)
            		+ ((database[i * 10 + 10] & 0xff) << 8)
            		+ (database[i * 10 + 11] & 0xff);
            if (dbEntryIdentifier == entryIdentifier)
                return offset;
            offset += dbEntryLength;
        }

        return 0;
    }
    
    /**
     * A database has the following layout (name(bytes)):
     * HEADER:
     * nbr_of_entries(2), identifier0(4), length0(3), length0(3),
     * identifier1(4), length1(3), length1(3),
     * identifier2(4), length2(3), length2(3),
     * ...
     * identifierN(4), lengthN(3), lengthN(3)
     * 
     * DATA:
     * data0(length0)
     * data1(length1)
     * data2(length2)
     * ...
     * dataN(lengthN)
     * 
     * @param entry A sting containing the sought entry
     * @param database The database where the entry should be found.
     * @return the the length (in bytes) of the entry.
     */
    public static int getEntryLength(String entry, byte database[]) {
        int nDBEntries = getUnsigned2Bytes(database, 0);
        int entryIdentifier = getIdentifier(entry);

        int offset = 2 + nDBEntries * 10;
        for (int i1 = 0; i1 < nDBEntries; i1++) {
            int dbEntryIdentifier = ((database[i1 * 10 + 2] & 0xff) << 24)
            		+ ((database[i1 * 10 + 3] & 0xff) << 16)
            		+ ((database[i1 * 10 + 4] & 0xff) << 8)
            		+ (database[i1 * 10 + 5] & 0xff);
            int dbEntryLength = ((database[i1 * 10 + 6] & 0xff) << 16)
            		+ ((database[i1 * 10 + 7] & 0xff) << 8)
            		+ (database[i1 * 10 + 8] & 0xff);
            // seems to always be the same as dbEntryLength
            int dbEntryLength2 = ((database[i1 * 10 + 9] & 0xff) << 16)
            		+ ((database[i1 * 10 + 10] & 0xff) << 8)
            		+ (database[i1 * 10 + 11] & 0xff);
            if (dbEntryIdentifier == entryIdentifier)
                return dbEntryLength;
            offset += dbEntryLength2;
        }

        return 0;
    }

    private static int baseLengthArray[] = {
            0, 1, 3, 7, 15, 31, 63, 127, 255, 511,
            1023, 2047, 4095, 8191, 16383, 32767, 65535, 0x1ffff, 0x3ffff, 0x7ffff,
            0xfffff, 0x1fffff, 0x3fffff, 0x7fffff, 0xffffff, 0x1ffffff, 0x3ffffff, 0x7ffffff, 0xfffffff, 0x1fffffff,
            0x3fffffff, 0x7fffffff, -1
    };

}
