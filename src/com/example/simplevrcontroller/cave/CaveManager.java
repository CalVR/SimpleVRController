package com.example.simplevrcontroller.cave;

import java.util.ArrayList;

public class CaveManager {
	
	private static CaveManager manager;
	
	private ArrayList<Cave> caves;
	
	static  {
		manager = new CaveManager();
	}
	
	private CaveManager(){
		caves = new ArrayList<Cave>();
	}
	
	public Cave getCave(String name){
		for(Cave c : caves)
			if(c.getName().equals(name))
				return c;
		return null;
	}
	
	/**
	 * Trys to add the given cave to the manager.
	 * @param cave
	 * @return
	 */
	public boolean addCave(Cave cave){
		if(caves.contains(cave))
			return false;
		
		caves.add(cave);
		
		return true;
	}
	
	
	
	public static CaveManager getCaveManager(){
		return manager;
	}
	
}
