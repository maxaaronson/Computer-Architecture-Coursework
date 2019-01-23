/*
 *  By Max Aaronson
 *  CS472
 *  Project 2
 *  10/10/2015
 */


public class CacheSimulator {
	
	
	static int tag = 0;
	static int slotNum = 0;
	static int offset = 0;
	static int blockStart;

	static short mainMem[];
	static Cache cache;
	
	static int instructions[][] = {  // the first column of the array determines read (0), write (1) or display (2)
									 // second column is address, third is value to be written
		{0, 0x5},
		{0, 0x6},
		{0, 0x7},
		{0, 0x14c},
		{0, 0x14d},
		{0, 0x14e},
		{0, 0x14f},
		{0, 0x150},
		{0, 0x151},
		{0, 0x3a6},
		{0, 0x4c3},
		{2},  
		{1, 0x14c, 0x99},
		{1, 0x63b, 0x7},
		{0, 0x582},
		{2},
		{0, 0x348},
		{0, 0x3f},
		{2},
		{0, 0x14b},
		{0, 0x14c},
		{0, 0x63f},
		{0, 0x83},
		{2}
		};
	
	
	public static void main(String[] args) {
		
		//  0x800 = 2048 decimal
		mainMem = new short[0x800];
		
		// fill the main memory array
		short value = 0;
		for (int i = 0; i < 0x800; i++){
			if (value == 0x100){  
				value = 0;
			}
			mainMem[i] = value;		
		
			value++;
		}
		
		cache = new Cache();
		
		for (int i = 0; i < instructions.length; i++){  // read the instructions in the array one by one
			switch (instructions[i][0]){
			
			case 0:  // read
				System.out.println("Instruction: Read");
				System.out.println("Address: " + Integer.toHexString(instructions[i][1]));
				read(instructions[i][1]);
				break;
			case 1:  // write
				System.out.println("Instruction: Write");
				System.out.println("Address: " + Integer.toHexString(instructions[i][1]));
				System.out.println("New Value: " + Integer.toHexString(instructions[i][2]));
				write(instructions[i][1], instructions[i][2]);
				break;
			case 2:  // display
				System.out.println("Instruction: Display");
				cache.displayCache();
			}
		}
		

	}
	
	static void write(int address, int value) {
		// break address into parts
		offset = (address & 0x0000000f);
		
		slotNum = address & 0x000000f0;
		slotNum = slotNum >> 4;
		
		tag = address & 0xffffff00;
		tag = tag >> 8;
		
		blockStart = address & 0xfffffff0;
		 
		// check the valid bit for the slot
		// if valid bit = 0, bring block into cache, set valid bit = 1, write new value, set dirty bit = 1, and return cache miss
		if (cache.slot[slotNum].valid == 0){
			fetchBlock(blockStart, tag, slotNum);
			cache.slot[slotNum].valid = 1;
			writeToCache(slotNum, offset, value);
			System.out.println("At that byte, the value " + Integer.toHexString(cache.slot[slotNum].data[offset]) + " "
					+ "has been written (Cache Miss)");
		}
		else {  // if valid bit is 1, check if tag in address equals tag in the slot
			
			if(tag == cache.slot[slotNum].tag){ 
				// if tags match, write new value to cache, set dirty bit to 1, return cache hit
				writeToCache(slotNum, offset, value);
				System.out.println("At that byte, the value " + Integer.toHexString(cache.slot[slotNum].data[offset]) + " "
						+ "has been written (Cache Hit)");
			}
			else {  // if tags don't match, check dirty bit to see if a value must be written back to mainMem
				
				if (cache.slot[slotNum].dirtyBit == 1){ // if dirty bit is 1, write the block at that slot number back to mainMem,
					writeBack(slotNum);                                    
				}
				// then fetch new block, write new values to that block in the cache,
				// keep dirty bit = 1, return cache miss
				fetchBlock(blockStart, tag, slotNum);
				writeToCache(slotNum, offset, value);
				System.out.println("At that byte, the value " + Integer.toHexString(cache.slot[slotNum].data[offset]) + ""
						+ "has been written (Cache Miss)");
			}
		}
	}

	static void writeBack(int slotNum) {
		// calculate start address in memory of the block currently in the cache
		int memAddress;
		memAddress = cache.slot[slotNum].tag << 8;
		memAddress += slotNum << 4;
		for (int i = 0; i < 0x10; i++){
			mainMem[memAddress + i] = (short) cache.slot[slotNum].data[i];
		}
		System.out.println("Conflict Miss: Block in slot written back to Main Memory starting at address " + Integer.toHexString(memAddress));
	}

	static void writeToCache(int slotNum, int offset, int value) {
		cache.slot[slotNum].data[offset] = value;
		cache.slot[slotNum].dirtyBit = 1;
	}

	public static void read(int address){
		// break address into parts
		offset = (address & 0x0000000f);

		slotNum = address & 0x000000f0;
		slotNum = slotNum >> 4;

		tag = address & 0xffffff00;
		tag = tag >> 8;
		
		blockStart = address & 0xfffffff0;
		
		// check the valid bit for the slot
		// if valid bit = 0, bring block into cache, set valid bit = 1, and return cache miss
		if (cache.slot[slotNum].valid == 0){
			fetchBlock(blockStart, tag, slotNum);
			cache.slot[slotNum].valid = 1;
			System.out.println("At that byte, the value is " + Integer.toHexString(cache.slot[slotNum].data[offset]) + " (Cache Miss)");
		}
		
		else {  // if valid bit is 1
			    // check if tag in address equals tag in the slot
			
			if(tag == cache.slot[slotNum].tag){ 	// if true, return cache hit
				System.out.println("At that byte, the value is " + Integer.toHexString(cache.slot[slotNum].data[offset]) + " (Cache Hit)");
			}
			else{  // first check dirty bit to see if current block in the cache must be written back to mainMem,
				   // if so, write values to mainMem
				   // then bring block starting at blockStart into cache, set dirty bit = 0, and return cache miss
				if (cache.slot[slotNum].dirtyBit == 1){
					writeBack(slotNum);  
					
				}
				
				fetchBlock(blockStart, tag, slotNum);
				System.out.println("At that byte, the value is " + Integer.toHexString(cache.slot[slotNum].data[offset]) + " (Cache Miss)");
			}
		}
	}

	static void fetchBlock(int blockStart, int tag, int slotNum) { // bring 16 bytes into cache
		for (int i = 0; i < 0x10; i++){
			cache.slot[slotNum].data[i] = mainMem[blockStart + i];
		}
		cache.slot[slotNum].tag = tag;  // update tag
		cache.slot[slotNum].dirtyBit = 0;  // reset dirty bit
	}

}
