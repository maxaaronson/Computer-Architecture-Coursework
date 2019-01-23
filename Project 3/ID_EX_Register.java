
public class ID_EX_Register {

	int RegDst;
	@Override
	public String toString() {
		return "RegDst=" + RegDst + ", ALUOp=" + Integer.toBinaryString(ALUOp)
				+ ", ALUSrc=" + ALUSrc + ", MemRead=" + MemRead + ", MemWrite="
				+ MemWrite + ", RegWrite=" + RegWrite + ", MemToReg="
				+ MemToReg + ",\n ReadReg1Value=" + Integer.toHexString(ReadReg1Value)
				+ ", ReadReg2Value=" + Integer.toHexString(ReadReg2Value) + ", rt=" + rt + ", rd="
				+ rd + ", funct=" + Integer.toHexString(funct) + ", SEOffset=" + Integer.toHexString(SEOffset)
				+ ", SWValue=" + Integer.toHexString(SWValue);
	}

	int ALUOp;
	int ALUSrc;
	int MemRead;
	int MemWrite;
	int RegWrite;
	int MemToReg;
	int ReadReg1Value;
	int ReadReg2Value;
	int rt;
	int rd;
	int funct;
	int SEOffset;
	int SWValue;

	String inst;
	
}
