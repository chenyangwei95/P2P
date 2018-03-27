package dataModels;

public class Utilities {
	
	public static byte type2Value(String type) {
		switch (type.toUpperCase()) {
			case "CHOKE":
				return 0;
			case "UNCHOKE":
				return 1;
			case "INTERESTED":
				return 2;
			case "NOTINTERESTED":
				return 3;
			case "HAVE":
				return 4;
			case "BITFIELD":
				return 5;
			case "REQUEST":
				return 6;
			case "PIECE":
				return 7;
			default:
				return -1;
		}
	}
	
	public static String value2Type(byte type) {
		switch (type) {
			case 0:
				return "CHOKE";
			case 1:
				return "UNCHOKE";
			case 2:
				return "INTERESTED";
			case 3:
				return "NOTINTERESTED";
			case 4:
				return "HAVE";
			case 5:
				return "BITFIELD";
			case 6:
				return "REQUEST";
			case 7:
				return "PIECE";
			default:
				return null;
		}
	}

	public static byte[] int2Bytes(int integer) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte)((integer >> 24) & 0xFF);
		bytes[1] = (byte)((integer >> 16) & 0xFF);
		bytes[2] = (byte)((integer >> 8) & 0xFF);
		bytes[3] = (byte)(integer & 0xFF);
        return bytes;
	}
	
	public static int bytes2Int(byte[] bytes) {
        int integer = (int)( ((bytes[0] & 0xFF) << 24) 
        					| ((bytes[1] & 0xFF) << 16) 
        					| ((bytes[2] & 0xFF) << 8)
                			| (bytes[3] & 0xFF));
        return integer;
    }

	
	public static byte[] mergeTwoByteArrays(byte[] a, byte[] b) {
        byte[] res = new byte[a.length + b.length];
        int i = 0;
        while (i < a.length) {
        	res[i] = a[i];
        	i++;
        }
        int j = 0;
        while (i < res.length && j < b.length) {
        	res[i] = b[j];
        	i++;
        	j++;
        }
        return res;
    }
	
	public static Boolean int2Boolean (int i)
	{
		if (i==1)
			return true;
		else
			return false;
	}
	
	public static boolean CompareByteArrary(byte[] a, byte[] b) {
        if (a.length == b.length){
            for (int i = 0; i < a.length; i++) {
                if (a[i] != b[i]){
                    return false;
                }
            }
        } 
        else {
            return false;
        }
        return true;
    }

    public static String printOut(byte[] out) {
    	System.out.println("printing..., out length: " + out.length);
    	byte[] len = new byte[4];
    	for (int i = 0; i <= 3; i++) {
    		System.out.println("printing in loop 1...: " + out[i]);
    		len[i] = out[i];
    	}
    	int length = bytes2Int(len);
    	byte ty = out[4];
    	String type = value2Type(ty);
    	byte[] load = new byte[4];
    	for (int i = 5; i <= 8; i++) {
    		System.out.println("printing in loop 2...: " + out[i]);
    		load[i - 5] = out[i];
    	}
    	int pieceIndex = bytes2Int(load);
    	System.out.println("printed...");
    	return "Length: " + length + ", type: " + type + ", pieceIndex: " + pieceIndex;
    }
}
