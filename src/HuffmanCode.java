import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

public class HuffmanCode {
    static Map<Byte,String>huffmanCodes=new HashMap<>();

    public static byte[] huffmanZip(byte[] contentbytes){
        List<treeNode> contentlist=getList(contentbytes);//Convert the byte array into a List of Node
        treeNode root=createhuffmantree(contentlist);
        root.preOreder();
        huffmanCodes=getCodes(root);
        byte[] zipCodes=zip(contentbytes,huffmanCodes);
        return zipCodes;
    }


    public static void zipFile(String srcFile, String dstFile) {
        //Create file input stream
        FileInputStream is=null;
        //Create file output stream
        OutputStream os=null;
        ObjectOutputStream oos=null;
        try {
            is=new FileInputStream(srcFile);
            byte[] b=new byte[is.available()];//Create a byte[] array with the same size as the file
            is.read(b);//Read the content of the file and copy it to b
            byte[] huffmanBytes=huffmanZip(b);//Get the complete Huffman binary long string into byte form for transmission
            os=new FileOutputStream(dstFile);//Create file output stream, store compressed files
            oos=new ObjectOutputStream(os);//Create an ObjectOutputStream associated with the file output stream
            oos.writeObject(huffmanBytes);//Write in the form of object stream
            oos.writeObject(huffmanCodes);//Be sure to pay attention here, you must pass in the Huffman coding mapping table, otherwise it cannot be decompressed

        }catch (Exception e){
        }finally {
            try {
                oos.close();//The order of closing the stream should be opposite to the order of building the stream
                os.close();
                is.close();
            }catch (Exception e){
            }
        }
    }

    public static void unZipFile(String zipFile, String dstFile) {
        InputStream is=null;
        ObjectInputStream ois=null;
        OutputStream os=null;
        try{
            is=new FileInputStream(zipFile);
            ois=new ObjectInputStream(is);
            byte[] huffmanBytes=(byte[]) ois.readObject();
            Map<Byte,String>huffmanMap=(Map<Byte,String>)ois.readObject();
            byte[] finalBytes=decode(huffmanMap,huffmanBytes);//decoding
            os=new FileOutputStream(dstFile);
            os.write(finalBytes);
        }catch (Exception e){
        }finally {
            try {
                os.close();
                ois.close();
                is.close();
            }catch (Exception e){
            }
        }
    }

    public static List<treeNode> getList(byte[] contentbytes){
        ArrayList<treeNode> nodes=new ArrayList<treeNode>();
        //Traverse bytes, count the number of occurrences of each byte
        Map<Byte,Integer>counts=new HashMap<>();
        for(byte item:contentbytes){
            Integer count=counts.get(item);//Find the value corresponding to the item in the Map
            if(count==null){//Indicates that this count does not yet exist
                counts.put(item,1);
            }else {
                counts.put(item,count+1);
            }
        }

        for(Map.Entry<Byte,Integer>entry:counts.entrySet()){
            nodes.add(new treeNode(entry.getKey(),entry.getValue()));
        }
        return nodes;

    }

    //Build the Huffman tree
    public static treeNode createhuffmantree(List<treeNode> nodes){
        while(nodes.size()>1){
            Collections.sort(nodes);
            treeNode leftNode=nodes.get(0);
            treeNode rightNode=nodes.get(1);
            treeNode parentNode=new treeNode(null,leftNode.value+rightNode.value);//Non-leaf nodes only have weights, no data
            parentNode.left=leftNode;
            parentNode.right=rightNode;
            nodes.remove(0);
            nodes.remove(0);
            nodes.add(parentNode);
        }
        return nodes.get(0);
    }

    static Map<Byte,String>huffmancodes=new HashMap<>();//Such as a->1001
    static StringBuilder stringBuilder=new StringBuilder();//Used to splice strings

    public static void getCodes(treeNode node,String curcode,StringBuilder stringBuilder){
        StringBuilder stringBuilder2=new StringBuilder(stringBuilder);
        stringBuilder2.append(curcode);
        if(node!=null){//Do not process if node is empty
            //Determine whether it is a leaf node
            if(node.data==null){//Description is a non-leaf node
                getCodes(node.left,"0",stringBuilder2);
                getCodes(node.right,"1",stringBuilder2);
            }else{//Is a leaf node
                huffmancodes.put(node.data,stringBuilder2.toString());
            }

        }
    }

    public static Map<Byte,String> getCodes(treeNode node){
        if(node==null){
            return null;
        }
        getCodes(node.left,"0",stringBuilder);
        getCodes(node,"1",stringBuilder);
        return huffmancodes;
    }

    public static byte[] zip(byte[] bytes,Map<Byte,String>huffmanCodes){
        //Get the complete binary Huffman code
        StringBuilder stringBuilder=new StringBuilder();
        for(byte b:bytes){
            stringBuilder.append(huffmanCodes.get(b));
        }


        int len=0;
        if(stringBuilder.length()%8==0){
            len=stringBuilder.length()/8;
        }else{
            len=stringBuilder.length()/8+1;//May not be divisible
        }

        //Create compressed byte[] array
        int index=0;
        byte[] huffmanCodesByte=new byte[len];
        for (int i = 0; i <stringBuilder.length(); i=i+8) {
            String curString;
            if(stringBuilder.length()<i+8){
                curString=stringBuilder.substring(i);
            }else{
                curString=stringBuilder.substring(i,i+8);//substring coordinate index is not included before and after
            }
            huffmanCodesByte[index]=(byte)Integer.parseInt(curString,2);//By default curString is in binary format, convert it to decimal Int, and then convert the type to byte
            index++;
        }
        return huffmanCodesByte;
    }

    public static String byteToBitString(boolean flag,byte b){
        int temp=b;// convert b to int
        if (flag) {
            temp |=256;
        }
        String str=Integer.toBinaryString(temp);
        if(flag){
            return str.substring(str.length()-8);
        }else{
            return str;
        }
    }

    public static byte[] decode(Map<Byte,String>huffmanCodes,byte[] huffmanCodesByte){
        StringBuilder stringBuilder1=new StringBuilder();
        for (int i = 0; i <huffmanCodesByte.length; i++) {
            byte b=huffmanCodesByte[i];
            boolean flag=(i==huffmanCodesByte.length-1);
            stringBuilder1.append(byteToBitString(!flag,b));
        }


        Map<String,Byte>map=new HashMap<String,Byte>();//key is a binary string, value is the byte corresponding to the decoded character
        for(Map.Entry<Byte,String>entry:huffmanCodes.entrySet()){
            map.put(entry.getValue(),entry.getKey());
        }
        //Create a collection to store byte
        List<Byte> list=new ArrayList<>();
        for (int i = 0; i <stringBuilder1.length() ; ) {
            int count=0;//Internal counter, because the number of Huffman digits corresponding to each scanned character is uncertain, each time the search is successful, the counter is cleared
            boolean flag=true;
            while(flag){
                String search=stringBuilder1.substring(i,i+count);
                Byte b=map.get(search);
                if(b==null){
                    count++;
                }else{
                    flag=false;
                    list.add(b);
                }
            }
            i=i+count;
        }
        //Store the data in the list into byte[]
        byte[] listToByte=new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            listToByte[i]=list.get(i);
        }
        return listToByte;
    }
}



//Build node class
class treeNode implements Comparable<treeNode>{
    Byte data;//Stored data
    int value;//Node weight
    treeNode left;//Left child node
    treeNode right;//Right child node

    public treeNode(Byte data, int value) {
        this.data = data;
        this.value = value;
    }

    @Override
    public String toString() {
        return "treeNode{" +
                "data=" + data +
                ", value=" + value +
                '}';
    }

    @Override
    public int compareTo(treeNode o) {
        return this.value-o.value;
    }

    //Preorder traversal
    public void preOreder(){
        if(this.left!=null){
            this.left.preOreder();
        }
        if(this.right!=null){
            this.right.preOreder();
        }

    }
}