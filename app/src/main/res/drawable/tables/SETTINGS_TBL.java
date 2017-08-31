package com.bizonesoft.ace.tables;

public class SETTINGS_TBL {
	private SETTINGS_TBL(){
		
	};
	public static String TABLE_NAME="SETTINGS_TBL";
	public static String STB_STNGS_NM="STB_STNGS_NM";
	public static String STB_STNGS_VAL="STB_STNGS_VAL";
	
	private static final String STNGS_TBL_CREATE =
		   	(new StringBuffer()).append("create table " ).append( TABLE_NAME ).append(" ( " ).append( 
		   			STB_STNGS_NM ).append( " text primary key , " ).append(
		   					STB_STNGS_VAL).append( " text not null);").toString();

	private static final String STTNGS_TBL_DROP = "DROP TABLE IF EXISTS " + TABLE_NAME; 
	
	public static String createTable(){
		return STNGS_TBL_CREATE;
	}
	public static String dropTable(){
		return STTNGS_TBL_DROP;
	}
}
