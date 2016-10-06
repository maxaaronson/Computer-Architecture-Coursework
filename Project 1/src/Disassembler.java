/* 
 * CS472
 * Project 1
 * Max Aaronson
 * 9/19/15
 */


public class Disassembler {

	public static void main(String[] args) {
		
		int instructions[] = {0x022DA822, 0x8EF30018, 0x12A70004, 0x02689820,
				0xAD930018, 0x02697824, 0xAD8FFFF4, 0x018C6020, 0x02A4A825,
				0x158FFFF6, 0x8E59FFF0};
		int instruction;
		String hex;
		String bin;
		int opcode;
		int rs;
		int rt;
		int rd;
		int shamt;
		int funct;
		short pcoffset;
		int target;
		int startAddress = 0x7A060;
		int currentAddress = startAddress;
		String operation = "";  // just to initialize the string.  will be set once opcode/funct is calculated
		
		for (int i = 0; i<instructions.length; i++){
			System.out.println("Now on instruction " + i);
			if (i != 0){currentAddress += 4;}
			hex = "00000000";
			hex += (Integer.toHexString(instructions[i]).toUpperCase());  // formating for hex string
			hex = hex.substring(hex.length()-8, hex.length());
			System.out.println("In Hex: " + hex);  // prints out current hex instruction
			
			
			bin = Integer.toBinaryString(instructions[i]);  // display instruction as binary.  This is just to verify outputs
			while (bin.length()<32){
				bin = "0" + bin;  // adds leading 0s
			}
			System.out.println("In Binary: " + bin);
			
			
			instruction = instructions[i];
			opcode = (instruction & 0xFC000000) >>> 26;
			rs = (instruction & 0x03E00000) >>> 21;
			rt = (instruction & 0x001F0000) >>> 16;
			
			
			System.out.println("Opcode: " + opcode);
			System.out.println("rs: " + rs);
			System.out.println("rt: " + rt);
			
			
			if (opcode == 0){ // Handles R-Format cases
				System.out.println("R-Format");
				
				 rd = (instruction & 0x0000F800) >>> 11;
				 shamt = 0;  // can always be assumed to be 0
				 funct = (instruction & 0x0000003F);
				 System.out.println("rd: " + rd);
				 System.out.println("shamt: " + shamt);
				 System.out.println("funct: " + funct);
				 
				 switch(funct){
				 
				 case 32:
					 operation = "Add";
					 break;
				case 34:
					operation = "Sub";
					 break;
				case 36:
					operation = "AND";
					break;
				case 37:
					operation = "OR";
					break;
				case 42:
					operation = "slt";
					break;
				 }
				 
				 
				 System.out.println("RESULT: "+Integer.toHexString(currentAddress).toUpperCase()+
							 " "+operation+" $"+rd+", $"+rs+", $"+rt);
				 
			}
			else { // I-Format
				System.out.println("I-Format");
				pcoffset = (short) (instruction & 0x0000FFFF);
				System.out.println("PC Offset: " + pcoffset);
				target = currentAddress + 4 + (pcoffset << 2);
				
				switch(opcode){
				
				case 35:
					operation = "lw";
					System.out.println("RESULT: "+Integer.toHexString(currentAddress).toUpperCase()+
							 " "+operation+" $"+rt+", "+pcoffset+"("+"$"+rs+")");
					break;
				case 43:
					operation = "sw";
					System.out.println("RESULT: "+Integer.toHexString(currentAddress).toUpperCase()+
							 " "+operation+" $"+rt+", "+pcoffset+"("+"$"+rs+")");
					break;
				case 4:
					operation = "beq";
					System.out.println("RESULT: "+Integer.toHexString(currentAddress).toUpperCase()+
							" "+operation+" $"+rt+", $"+rs+", Address " + Integer.toHexString(target).toUpperCase());
					break;
				case 5:
					operation = "bne";
					System.out.println("RESULT: "+Integer.toHexString(currentAddress).toUpperCase()+
							 " "+operation+" $"+rt+", $"+rs+", Address " + Integer.toHexString(target).toUpperCase());
					break;
				}
				
				
			}
			
			
			System.out.println("--------------------------------------------------------");
		}

	}

}
