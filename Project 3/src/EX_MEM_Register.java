
public class EX_MEM_Register {

	int MemRead;
	int MemWrite;
	int RegWrite;
	int MemToReg;
	int AluResult;
	int SWValue;
	int WriteRegNum;
	
	String inst;
	
	@Override
	public String toString() {
		return "MemRead=" + MemRead + ", MemWrite=" + MemWrite
				+ ", RegWrite=" + RegWrite + ", MemToReg=" + MemToReg
				+ ",\n AluResult=" + Integer.toHexString(AluResult) + ", SWValue=" + Integer.toHexString(SWValue)
				+ ", WriteRegNum=" + WriteRegNum ;
	}
}
