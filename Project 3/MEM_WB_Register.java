
public class MEM_WB_Register {

	int RegWrite;
	int MemToReg;
	int AluResult;
	int RegWriteNum;
	int LWDataValue;
	
	String inst;
	
	@Override
	public String toString() {
		return "RegWrite=" + RegWrite + ", MemToReg="
				+ MemToReg + ",\n AluResult=" + Integer.toHexString(AluResult) + ", RegWriteNum="
				+ RegWriteNum + ", LWDataValue=" + Integer.toHexString(LWDataValue);
	}
}
