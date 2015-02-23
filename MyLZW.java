/*************************************************************************
 * Modified By: David Bickford
 * Email: DRB56@pitt.edu
 * 
 *  Compilation:  javac LZW.java
 *  Execution:    java LZW - < input.txt   (compress)
 *  Execution:    java LZW + < input.txt   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *
 *  Compress or expand binary input from standard input using LZW.
 *
 *  WARNING: STARTING WITH ORACLE JAVA 6, UPDATE 7 the SUBSTRING
 *  METHOD TAKES TIME AND SPACE LINEAR IN THE SIZE OF THE EXTRACTED
 *  SUBSTRING (INSTEAD OF CONSTANT SPACE AND TIME AS IN EARLIER
 *  IMPLEMENTATIONS).
 *
 *  See <a href = "http://java-performance.info/changes-to-string-java-1-7-0_06/">this article</a>
 *  for more details.
 *
 *************************************************************************/

public class MyLZW {
    private static final int R = 256;        // number of input chars
    private static int L = 512;       // number of codewords = 2^W
    private static int W = 9;         // codeword width
    //two new variables: size of string consumed, size of bits

    public static void compress(String compressionType) {
        int amount = 0;
        int dataRead = 0;
        int dataWritten = 0;
        double currRatio = 0.0;
        double oldRatio = 0.0;
        String input = BinaryStdIn.readString();
        char[] charArray = input.toCharArray();
        dataRead = charArray.length*8;
        TST<Integer> st = new TST<Integer>();
        for (int i = 0; i < R; i++)
            st.put("" + (char) i, i); //initializes codebook and creates new codes for ascii characters
        int code = R+1;  // R is codeword for EOF(257th character, "End Of File")
        
        while (input.length() > 0) {
            String s = st.longestPrefixOf(input);  // Find max prefix match s.
            
            if(compressionType.equals("n"))
            {
                if(amount == 0)
                {
                    //writing 'n' to the file
                    BinaryStdOut.write('n');
                }
                
                BinaryStdOut.write(st.get(s), W);      // Print s's encoding.
                int t = s.length();
                
                if (t < input.length() && code < L)
                {   
                    // Add s to symbol table.
                    st.put(input.substring(0, t + 1), code++);
                }
                else if(t < input.length() && W < 16)
                {
                    //if W hasn't reached it's max then increases W and L
                    W++;
                    L *= 2;
                    st.put(input.substring(0, t + 1), code++);
                }
                input = input.substring(t);            // Scan past s in input.
                amount++;
            }
            else if(compressionType.equals("r"))
            {
                if(amount == 0)
                {
                    //writing 'r' to the file
                    BinaryStdOut.write('r');
                }
                
                BinaryStdOut.write(st.get(s), W);      // Print s's encoding.
                int t = s.length();
                if (t < input.length() && code < L)
                {   
                    // Add s to symbol table.
                    st.put(input.substring(0, t + 1), code++);
                }
                else if(t < input.length() && W < 16)
                {
                    W++;
                    L *= 2;
                    st.put(input.substring(0, t + 1), code++);
                }
                else if(t < input.length() && W == 16 && L == 65535)
                {
                    //creating a new TST if type is 'r' and reseting it
                    st = new TST<Integer>();
                    for (int i = 0; i < R; i++)
                    {
                        st.put("" + (char) i, i);
                    }
                    //setting W and L to 9 and 512 respecively
                    W = 9;
                    L = 512;
                    st.put(input.substring(0, t + 1), code++);
                }
                input = input.substring(t);
                amount++;
            }
            else if(compressionType.equals("m"))
            {
                if(amount == 0)
                {
                    //writing 'm' to the file
                   BinaryStdOut.write('m');
                }
                BinaryStdOut.write(st.get(s), W);      // Print s's encoding.
                dataWritten += W;
                currRatio = dataRead/dataWritten;
                int t = s.length();
                
                if(oldRatio/currRatio >= 1.1)
                {
                    //creating a new TST if the ratio is greater than 1.1
                    st = new TST<Integer>();
                    for (int i = 0; i < R; i++)
                    {
                        st.put("" + (char) i, i);
                    }
                    //reseting W and L
                    W = 9;
                    L = 512;
                    st.put(input.substring(0, t + 1), code++);
                }
                else
                {
                    if (t < input.length() && code < L)
                    {   
                        // Add s to symbol table.
                        st.put(input.substring(0, t + 1), code++);
                    }
                    else if(t < input.length() && W < 16)
                    {
                        W++;
                        L *= 2;
                        st.put(input.substring(0, t + 1), code++);
                    }
                }
                input = input.substring(t);
                amount++;
            } 
        }
        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    } 

    
    public static void expand() {
        String[] st = new String[65536];
        int i; // next available codeword value
        char type = BinaryStdIn.readChar();
        int written = 0;
        int read = 0;
        // initialize symbol table with all 1-character strings
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;           // rebuilding codebook
        st[i++] = "";                        // (unused) lookahead for EOF

        int codeword = BinaryStdIn.readInt(W);  //reads W bits in the file
        
        if (codeword == R) return;           // expanded message is empty string
        String val = st[codeword];

        while (true) {
            //if the type of compression is n do this.
            if(type == 'n')
            {
                BinaryStdOut.write(val);
                codeword = BinaryStdIn.readInt(W);
                if (codeword == R) break;
                String s = st[codeword];
                if (i == codeword)
                {
                    s = val + val.charAt(0);
                }   // special case hack for first letter

                if (i < L-1)
                {
                    st[i++] = val + s.charAt(0);
                } 
                else if(W < 16)
                {
                    W++;
                    L *= 2;
                    st[i++] = val + s.charAt(0);
                }
                val = s;
            }
            //if the type of compression is r do this
            else if(type == 'r')
            {
                //checking if W and L have reached the max
                if(W == 16 && L == 65535)
                {
                    W = 9;
                    L = 512;
                    
                    BinaryStdOut.write(val);
                    codeword = BinaryStdIn.readInt(W);
                    if (codeword == R) break;
                    String s = st[codeword];
                    if (i == codeword)
                    {
                        s = val + val.charAt(0);
                    }   // special case hack for first letter

                    if (i < L-1)
                    {
                        st[i++] = val + s.charAt(0);
                    } 
                    else if(W < 16)
                    {
                        W++;
                        L *= 2;
                        st[i++] = val + s.charAt(0);
                    }
                    val = s;
                }
                else
                {
                    //if W and L hasn't reached their max do this
                    BinaryStdOut.write(val);
                    codeword = BinaryStdIn.readInt(W);
                    if (codeword == R) break;
                    String s = st[codeword];
                    if (i == codeword)
                    {
                        s = val + val.charAt(0);
                    }   // special case hack for first letter

                    if (i < L-1)
                    {
                        st[i++] = val + s.charAt(0);
                    } 
                    else if(W < 16)
                    {
                        W++;
                        L *= 2;
                        st[i++] = val + s.charAt(0);
                    }
                    val = s;
                }
            }
            else if(type == 'm')
            {
                BinaryStdOut.write(val);
                codeword = BinaryStdIn.readInt(W);
                if (codeword == R) break;
                String s = st[codeword];
                if (i == codeword)
                {
                    s = val + val.charAt(0);
                }   // special case hack for first letter

                if (i < L-1)
                {
                    st[i++] = val + s.charAt(0);
                } 
                else if(W < 16)
                {
                    W++;
                    L *= 2;
                    st[i++] = val + s.charAt(0);
                }
                val = s;
            }
        }
        BinaryStdOut.close();
    }

    public static void main(String[] args) {
        if      (args[0].equals("-")) compress(args[1]);
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }

}
