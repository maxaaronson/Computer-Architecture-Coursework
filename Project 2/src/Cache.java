
public class Cache {
	
	
	CacheSlot[] slot;
	
	public Cache(){
		slot = new CacheSlot[0x10];
		for (int i = 0; i < 0x10; i++){
			slot[i] = new CacheSlot();
			slot[i].slotNum = i;
			slot[i].tag = 0;
			slot[i].valid = 0;
			slot[i].dirtyBit = 0;
		}
	}
	

	public void displayCache(){
		System.out.println("Slot  Valid  Dirty  Tag       Data");
		for (CacheSlot slot: slot){
			System.out.print(Integer.toHexString(slot.slotNum) + "      " + Integer.toHexString(slot.valid)
						+ "      " + Integer.toHexString(slot.dirtyBit) + "      " + Integer.toHexString(slot.tag) + "        ");
			for (int j: slot.data){
				System.out.print((Integer.toHexString(j) + "  ").substring(0, 3));
				//System.out.print(Integer.toHexString(slot.data[j]) + " ");
			};
			System.out.print("\n");
		}
	}
	


}
