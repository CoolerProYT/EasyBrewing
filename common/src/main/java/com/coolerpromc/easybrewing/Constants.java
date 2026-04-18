package com.coolerpromc.easybrewing;

import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {
	public static final String MODID = "easybrewing";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	public static Identifier id(String path){
		return Identifier.fromNamespaceAndPath(MODID, path);
	}
}