
public class IF_ID_Register {

	int instruction;

	@Override
	public String toString() {
		String m = Integer.toHexString(instruction);
		String m2 = ("00000000" + m).substring(m.length());
		return "instruction= " + m2;
	}
	

}
