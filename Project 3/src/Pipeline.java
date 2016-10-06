/*  Max Aaronson
 *  CS472
 *  Project 3
 *  11/25/15
 */


public class Pipeline {
	
	IF_ID_Register IF_ID_Write;
	IF_ID_Register IF_ID_Read;
	ID_EX_Register ID_EX_Write;
	ID_EX_Register ID_EX_Read;
	EX_MEM_Register EX_MEM_Write;
	EX_MEM_Register EX_MEM_Read;
	MEM_WB_Register MEM_WB_Write;
	MEM_WB_Register MEM_WB_Read;

	int Regs[];
	int Main_Mem[];
	
	int instructions[] = {
			0xa1020000,
			0x810afffc,
			0x00831820,
			0x01263820,
			0x01224820,
			0x81180000,
			0x81510010,
			0x00624022,
			0x00000000,
			0x00000000,
			0x00000000,
			0x00000000
			};
	
	
	public static void main(String[] args) {
		
		Pipeline pipeline = new Pipeline();
		pipeline.run();
	}
	
	public void run(){
		Main_Mem = new int[0x400];
		
		// fill the main memory array
		int value = 0;
		for (int i = 0; i < 0x400; i++){
			if (value == 0x100){  
				value = 0;
			}
			Main_Mem[i] = value;		
			value++;
		}
		
		Regs = new int[32];
		Regs[0] = 0;
		for (int i = 1; i < 32; i++){
			Regs[i] = 0x100 + i;
		}
		
		IF_ID_Write = new IF_ID_Register();
		IF_ID_Read = new IF_ID_Register();
		ID_EX_Write = new ID_EX_Register();
		ID_EX_Read = new ID_EX_Register();
		EX_MEM_Write = new EX_MEM_Register();
		EX_MEM_Read = new EX_MEM_Register();
		MEM_WB_Write = new MEM_WB_Register();
		MEM_WB_Read	= new MEM_WB_Register();
		
		ID_EX_Write.inst = "NOP";
		ID_EX_Read.inst = "NOP";
		EX_MEM_Write.inst = "NOP";
		EX_MEM_Read.inst = "NOP";
		MEM_WB_Write.inst = "NOP";
		MEM_WB_Read.inst = "NOP";
	
		
		for (int i = 0; i < instructions.length; i++){
			IF_stage(i);
			ID_stage();
			EX_stage();
			MEM_stage();
			WB_stage();
			Print_out_everything(i);
			Copy_write_to_read();
		}
	}

	private void IF_stage(int i) {
		// fetch instruction from memory and put it in IF_ID_Write
		int inst = instructions[i];
		IF_ID_Write.instruction = inst;
		
	}

	private void ID_stage() {
		// fetch instruction from IF_ID_Read
		int inst = IF_ID_Read.instruction;
		
		// decode instruction
		int opcode = (inst & 0xFC000000) >>> 26;
		int rs = (inst & 0x03E00000) >>> 21;
		int rt = (inst & 0x001F0000) >>> 16;
		int rd = (inst & 0x0000F800) >>> 11;
	    int funct = (inst & 0x0000003F);
	    int offset = (inst & 0x0000FFFF);
	    
	    if (offset >>> 15 == 1){  // check if leading bit is a 1
	    	offset += 0xffff0000;  // sign extend to 32 bits with leading 1's to preserve negative value
	    }

		switch(opcode){ 

		case 0:// Handles R-Format cases
			if (funct == 0){
				ID_EX_Write.RegDst = 0;
				ID_EX_Write.ALUSrc = 0;
				ID_EX_Write.MemToReg = 0;
				ID_EX_Write.RegWrite = 0;
				ID_EX_Write.MemRead = 0;
				ID_EX_Write.MemWrite = 0;
				ID_EX_Write.ALUOp = 0; 
				ID_EX_Write.inst = "NOP";
			}
			else {
				ID_EX_Write.RegDst = 1;
				ID_EX_Write.ALUSrc = 0;
				ID_EX_Write.MemToReg = 0;
				ID_EX_Write.RegWrite = 1;
				ID_EX_Write.MemRead = 0;
				ID_EX_Write.MemWrite = 0;
				ID_EX_Write.ALUOp = 2;  // Both ALUOp bits are set to 1
				
				switch(funct){
				 
				 case 0x20:
					 ID_EX_Write.inst = "Add";
					 break;
				case 0x22:
					 ID_EX_Write.inst = "Sub";
					 break;
				}
			}
			
			 break;
			 
		case 0x20:
			ID_EX_Write.inst = "Lb";
			ID_EX_Write.RegDst = 0;
			ID_EX_Write.ALUSrc = 1;
			ID_EX_Write.MemToReg = 1;
			ID_EX_Write.RegWrite = 1;
			ID_EX_Write.MemRead = 1;
			ID_EX_Write.MemWrite = 0;
			ID_EX_Write.ALUOp = 0;
			break;
			
		case 0x28:
			ID_EX_Write.inst = "Sb";
			// ID_EX_Write.RegDst is ignored
			ID_EX_Write.ALUSrc = 1;
			//  ID_EX_Write.MemToReg is ignored
			ID_EX_Write.RegWrite = 0;
			ID_EX_Write.MemRead = 0;
			ID_EX_Write.MemWrite = 1;
			ID_EX_Write.ALUOp = 0;
			break;
		}		
		
		// read register values
		ID_EX_Write.ReadReg1Value = Regs[rs];
		ID_EX_Write.ReadReg2Value = Regs[rt];
		ID_EX_Write.SWValue = Regs[rt];
		
		// write other information
		ID_EX_Write.rt = rt;
		ID_EX_Write.rd = rd;
		ID_EX_Write.funct = funct; 
		ID_EX_Write.SEOffset = offset;
		ID_EX_Write.SWValue = Regs[rt];
	}

	private void EX_stage() {
		
		int input1 = ID_EX_Read.ReadReg1Value;
		int input2;
		int ALUResult = 0;
		
		switch (ID_EX_Read.ALUOp){
		case 2:
			
			input2 = ID_EX_Read.ReadReg2Value;
			
			switch (ID_EX_Read.funct){
			case 32:
				// operation = Add;
				ALUResult =  input1 + input2;
				
				 break;
			case 34:
				//operation = Sub;
				ALUResult = input1 - input2;
				
				 break;
			}
			break;
			
		case 0:
			input2 = ID_EX_Read.SEOffset;
			ALUResult = input1 + input2;
			
			break;
		}
		
		if (ID_EX_Read.RegDst == 1){
			EX_MEM_Write.WriteRegNum = ID_EX_Read.rd;  // indicates rd is the destination register
		}
		else {
			EX_MEM_Write.WriteRegNum = ID_EX_Read.rt;  // indicates rt is the destination register
		}
		
		// write to EX_MEM_Write
		EX_MEM_Write.AluResult = ALUResult;
		EX_MEM_Write.MemRead = ID_EX_Read.MemRead;
		EX_MEM_Write.MemWrite = ID_EX_Read.MemWrite;
		EX_MEM_Write.RegWrite = ID_EX_Read.RegWrite;
		EX_MEM_Write.MemToReg = ID_EX_Read.MemToReg;
		EX_MEM_Write.SWValue = ID_EX_Read.SWValue;
		EX_MEM_Write.inst = ID_EX_Read.inst;
		

		
	}

	private void MEM_stage() {
		int loadedValue = 0;
		if (EX_MEM_Read.MemRead == 1 && EX_MEM_Read.MemWrite == 0 && EX_MEM_Read.MemToReg == 1 && EX_MEM_Read.RegWrite == 1){
			loadedValue = Main_Mem[EX_MEM_Read.AluResult];  // load byte
		}
		if (EX_MEM_Read.MemRead == 0 && EX_MEM_Read.MemWrite == 1 && EX_MEM_Read.RegWrite == 0){
			Main_Mem[EX_MEM_Read.AluResult] = EX_MEM_Read.SWValue;  // store byte
		}
		
		MEM_WB_Write.AluResult = EX_MEM_Read.AluResult;
		MEM_WB_Write.LWDataValue = loadedValue;
		MEM_WB_Write.RegWrite = EX_MEM_Read.RegWrite;
		MEM_WB_Write.MemToReg = EX_MEM_Read.MemToReg;
		MEM_WB_Write.RegWriteNum = EX_MEM_Read.WriteRegNum;
		MEM_WB_Write.inst = EX_MEM_Read.inst;
	}

	private void WB_stage() {
		if (MEM_WB_Read.MemToReg == 0 && MEM_WB_Read.RegWrite == 1){
			Regs[MEM_WB_Read.RegWriteNum] = MEM_WB_Read.AluResult;
		}
		if (MEM_WB_Read.MemToReg == 1 && MEM_WB_Read.RegWrite == 1){
			Regs[MEM_WB_Read.RegWriteNum] = MEM_WB_Read.LWDataValue;
		}
		
	}

	private void Print_out_everything(int i) {
		System.out.println("------------------------------------------------");
		System.out.println("Clock Cycle " + i);

		System.out.println("\nIF_ID Write: \n" + IF_ID_Write);
		System.out.println("\nIF_ID Read: \n" + IF_ID_Read);
		
		System.out.println("\nID_EX Write: (" + ID_EX_Write.inst + ")\n" + ID_EX_Write);
		System.out.println("\nID_EX Read: (" + ID_EX_Read.inst + ")\n" + ID_EX_Read);
		System.out.println("\nEX_MEM Write: (" + EX_MEM_Write.inst + ")\n" + EX_MEM_Write);
		System.out.println("\nEX_MEM Read: (" + EX_MEM_Read.inst + ")\n" + EX_MEM_Read);
		System.out.println("\nMEM_WB Write: (" + MEM_WB_Write.inst + ")\n" + MEM_WB_Write);
		System.out.println("\nMEM_WB Read: (" + MEM_WB_Read.inst + ")\n" + MEM_WB_Read);
		
		System.out.println("\nRegister Values:");
		for (int j = 0; j < Regs.length; j++){
			System.out.format("Value at Register %02d", j); 
			System.out.print(": " + Integer.toHexString(Regs[j]) + "\n");
		}
	}

	private void Copy_write_to_read() {

		IF_ID_Read.instruction = IF_ID_Write.instruction;
		
		ID_EX_Read.ReadReg1Value = ID_EX_Write.ReadReg1Value;
		ID_EX_Read.ReadReg2Value = ID_EX_Write.ReadReg2Value;
		ID_EX_Read.rt = ID_EX_Write.rt;
		ID_EX_Read.rd = ID_EX_Write.rd;
		ID_EX_Read.funct = ID_EX_Write.funct; 
		ID_EX_Read.SEOffset = ID_EX_Write.SEOffset;
		ID_EX_Read.SWValue = ID_EX_Write.SWValue;
		ID_EX_Read.RegDst = ID_EX_Write.RegDst;
		ID_EX_Read.ALUSrc = ID_EX_Write.ALUSrc;
		ID_EX_Read.MemToReg = ID_EX_Write.MemToReg;
		ID_EX_Read.RegWrite = ID_EX_Write.RegWrite;
		ID_EX_Read.MemRead = ID_EX_Write.MemRead;
		ID_EX_Read.MemWrite = ID_EX_Write.MemWrite;
		ID_EX_Read.ALUOp = ID_EX_Write.ALUOp;
		ID_EX_Read.inst = ID_EX_Write.inst;
		
		EX_MEM_Read.AluResult = EX_MEM_Write.AluResult;
		EX_MEM_Read.WriteRegNum = EX_MEM_Write.WriteRegNum;
		EX_MEM_Read.SWValue = EX_MEM_Write.SWValue;
		EX_MEM_Read.MemRead = EX_MEM_Write.MemRead;
		EX_MEM_Read.MemWrite = EX_MEM_Write.MemWrite;
		EX_MEM_Read.RegWrite = EX_MEM_Write.RegWrite;
		EX_MEM_Read.MemToReg = EX_MEM_Write.MemToReg;
		EX_MEM_Read.inst = EX_MEM_Write.inst;
		
		MEM_WB_Read.AluResult = MEM_WB_Write.AluResult;
		MEM_WB_Read.LWDataValue = MEM_WB_Write.LWDataValue;
		MEM_WB_Read.RegWrite = MEM_WB_Write.RegWrite;
		MEM_WB_Read.MemToReg = MEM_WB_Write.MemToReg;
		MEM_WB_Read.RegWriteNum = MEM_WB_Write.RegWriteNum;
		MEM_WB_Read.inst = MEM_WB_Write.inst;
		
	}


}
