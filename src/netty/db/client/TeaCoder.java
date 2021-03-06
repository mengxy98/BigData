package netty.db.client;


/** */
/**
 * Tea算法 每次操作可以处理8个字节数据 KEY为16字节,应为包含4个int型数的int[]，一个int为4个字节
 * 加密解密轮数应为8的倍数，推荐加密轮数为64轮
 * */
public class TeaCoder {
	
	private static int [] KEY;
	static {
		byte[] bb= "Pvezwx8eZPwblXfD".getBytes();
		int i=0;
		byte[] bb1={bb[i++],bb[i++],bb[i++],bb[i++]};
		byte[] bb2={bb[i++],bb[i++],bb[i++],bb[i++]};
		byte[] bb3={bb[i++],bb[i++],bb[i++],bb[i++]};
		byte[] bb4={bb[i++],bb[i++],bb[i++],bb[i++]};
		
		byteArrayToInt(bb1);
	
		KEY = new int[] {// 加密解密所用的KEY
				byteArrayToInt(bb1), byteArrayToInt(bb2), byteArrayToInt(bb3), byteArrayToInt(bb4) };			
	}
	// 加密
	public byte[] encrypt(byte[] content, int offset, int[] key, int times) {// times为加密轮数
		int[] tempInt = byteToInt(content, offset);
		int y = tempInt[0], z = tempInt[1], sum = 0, i;
		int delta = 0x9e3779b9; // 这是算法标准给的值
		int a = key[0], b = key[1], c = key[2], d = key[3];

		for (i = 0; i < times; i++) {
			sum += delta;
			y += ((z << 4) + a) ^ (z + sum) ^ ((z >> 5) + b);
			z += ((y << 4) + c) ^ (y + sum) ^ ((y >> 5) + d);
		}
		tempInt[0] = y;
		tempInt[1] = z;
		return intToByte(tempInt, 0);
	}

	// 解密
	public byte[] decrypt(byte[] encryptContent, int offset, int[] key,
			int times) {
		int[] tempInt = byteToInt(encryptContent, offset);
		int y = tempInt[0], z = tempInt[1], sum = 0xC6EF3720, i;
		int delta = 0x9e3779b9; // 这是算法标准给的值
		int a = key[0], b = key[1], c = key[2], d = key[3];

		for (i = 0; i < times; i++) {
			z -= ((y << 4) + c) ^ (y + sum) ^ ((y >> 5) + d);
			y -= ((z << 4) + a) ^ (z + sum) ^ ((z >> 5) + b);
			sum -= delta;
		}
		tempInt[0] = y;
		tempInt[1] = z;

		return intToByte(tempInt, 0);
	}

	// byte[]型数据转成int[]型数据
	private int[] byteToInt(byte[] content, int offset) {

		int[] result = new int[content.length >> 2]; // 除以2的n次方 == 右移n位 即
														// content.length / 4 ==
														// content.length >> 2
		for (int i = 0, j = offset; j < content.length; i++, j += 4) {
			result[i] = transform(content[j + 3])
					| transform(content[j + 2]) << 8
					| transform(content[j + 1]) << 16 | (int) content[j] << 24;
		}
		return result;

	}

	// int[]型数据转成byte[]型数据
	private byte[] intToByte(int[] content, int offset) {
		byte[] result = new byte[content.length << 2]; // 乘以2的n次方 == 左移n位 即
														// content.length * 4 ==
														// content.length << 2
		for (int i = 0, j = offset; j < result.length; i++, j += 4) {
			result[j + 3] = (byte) (content[i] & 0xff);
			result[j + 2] = (byte) ((content[i] >> 8) & 0xff);
			result[j + 1] = (byte) ((content[i] >> 16) & 0xff);
			result[j] = (byte) ((content[i] >> 24) & 0xff);
		}
		return result;
	}

	
	//通过TEA算法加密信息
    public byte[] encryptByTea(String info){
        byte[] temp = info.getBytes();
        int n = 8 - temp.length % 8;//若temp的位数不足8的倍数,需要填充的位数
        byte[] encryptStr = new byte[temp.length + n];
        encryptStr[0] = (byte)n;
        System.arraycopy(temp, 0, encryptStr, n, temp.length);
        byte[] result = new byte[encryptStr.length];
        for(int offset = 0; offset < result.length; offset += 8){
            byte[] tempEncrpt = encrypt(encryptStr, offset, KEY, 32);
            System.arraycopy(tempEncrpt, 0, result, offset, 8);
        }
        return result;
    }
  //通过TEA算法解密信息
    public String decryptByTea(byte[] secretInfo){
        byte[] decryptStr = null;
        byte[] tempDecrypt = new byte[secretInfo.length];
        for(int offset = 0; offset < secretInfo.length; offset += 8){
            decryptStr = decrypt(secretInfo, offset, KEY, 32);
            System.arraycopy(decryptStr, 0, tempDecrypt, offset, 8);
        }
        
        int n = tempDecrypt[0];
        return new String(tempDecrypt, n, decryptStr.length - n);
        
    }	
 // 若某字节被解释成负的则需将其转成无符号正数
	private static int transform(byte temp) {
		int tempInt = (int) temp;
		if (tempInt < 0) {
			tempInt += 256;
		}
		return tempInt;
	}

	
	private  static int byteArrayToInt(byte[] bytes) {
		int value = 0;
		
		// 由高位到低位
		int len = bytes.length;
//		int iSign = getBit(bytes[len-1],7);
////		if (iSign == 1)
			
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			if(i>=len)
			 break;
			value += (int)(bytes[i] & 0x000000FF) << shift;// 往高位游
		}
		int shift = 4-len;
		if (shift >0)
		{
			shift = shift*8;
			value = value >> shift;
		}
		return value;
	}
	public static void main(String[] args) {

//		int[] KEY = new int[] {// 加密解密所用的KEY
//		0x789f5645, 0xf68bd5a4, 0x81963ffa, 0x458fac58 };
		
//		byte[] bb= "Pvezwx8eZPwblXfD".getBytes();
//		int i=0;
//		byte[] bb1={bb[i++],bb[i++],bb[i++],bb[i++]};
//		byte[] bb2={bb[i++],bb[i++],bb[i++],bb[i++]};
//		byte[] bb3={bb[i++],bb[i++],bb[i++],bb[i++]};
//		byte[] bb4={bb[i++],bb[i++],bb[i++],bb[i++]};
//		
//		byteArrayToInt(bb1);
//	
//		int[] KEY = new int[] {// 鍔犲瘑瑙ｅ瘑鎵�鐢ㄧ殑KEY
//				byteArrayToInt(bb1), byteArrayToInt(bb2), byteArrayToInt(bb3), byteArrayToInt(bb4) };		
		
		
		TeaCoder tea = new TeaCoder();

		byte[] info = new byte[] {

		1, 2, 3, 4, 5, 6, 7, 8 };
		System.out.print("原数据：");
		for (byte j : info)
			System.out.print(j + " ");
		System.out.println();

		byte[] secretInfo = tea.encrypt(info, 0, KEY, 32);
		System.out.print("加密后的数据：");
		for (byte j : secretInfo)
			System.out.print(j + " ");
		System.out.println();

		byte[] decryptInfo = tea.decrypt(secretInfo, 0, KEY, 32);
		System.out.print("解密后的数据：");
		for (byte j : decryptInfo)
			System.out.print(j + " ");
		
		System.out.println();
		
		String infostr = " __jda=122270672.0xe9c8a161fbe88622.1447241545055.1447241545055.1447241545055.1; __jdu=52809338767131030; __jdv=238571484|direct|-|none|-; __tra=1736810.1167376060.1447241549.1447241549.1447241904.1; __tru=e3d0287e-1457-4f59-bab9-81cbf41975f3; __trv=1736810%7Cdirect%7C-%7Cnone%7C-; __wga=1448421443354.1448421443354.1447382490958.1443479958861.1.90; buy_uin=876753872; cartNum=5; cid=2; jdAddrId=; jdAddrName=; jdpin=wdFgjaaGFxsuRV; mba_muid=1447241548294-96da0c9a378e40da6f; mobilev=html5; mt_subsite=122%252C1447241549%7C%7C; network=wifi; nickname=%u5927%u558A%u633A%u54E5%u4FDD%u5E73%u5B89; openid1=EABE483D41C1CF8E2CE810A62D670057D66A126056D7A271B156040AA2363E3FB971DCEFA7BFB81F6BC69584909C930D; picture_url=http%3A%2F%2Fq.qlogo.cn%2Fqqapp%2F100273020%2F00000000000000000000000018A98150%2F40; pinId=kXqUuxxAaCdEg3yL6089zw; pinsign=750c768d14877921480e22402f6a80cd; PPRD_P=EA.17052.1.1-UUID.0xe9c8a161fbe88622; retina=1; sid=AWWkvACm3bBPascRsfiZI1eS; sk_history=%u534E%u4E3A%u8363%u80006; sq_open_id=00000000000000000000000018A98150; TrackID=rjJWQ1vSJWsBpXPJdlbDVylsHtewhQzHPHHVfqlE5Z1hgh";
        System.out.println("原数据：" + infostr);
        for(byte j : infostr.getBytes())
            System.out.print(j + " ");
        
        
        byte[] encryptInfo = tea.encryptByTea(infostr);
        System.out.println();
        System.out.println("加密后的数据：");
        for(byte j : encryptInfo)
            System.out.print(j + " ");
        System.out.println();
        
        String decryptInfoStr = tea.decryptByTea(encryptInfo);
        System.out.print("解密后的数据：");
        System.out.println(decryptInfoStr);
        for(byte j : decryptInfoStr.getBytes())
            System.out.print(j + " ");
        System.out.println();
	}

}
