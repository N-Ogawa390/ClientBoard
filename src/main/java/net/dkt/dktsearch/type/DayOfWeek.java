package net.dkt.dktsearch.type;

public enum DayOfWeek {
	MONDAY("月", "月曜日"),
	TUESDAY("火", "火曜日"),
	WEDNESDAY("水", "水曜日"),
	THURSDAY("木", "木曜日"),
	FRIDAY("金", "金曜日"),
	SATURDAY("土", "土曜日"),
	SUNDAY("日", "日曜日");
	
	private final String alias;
	private final String name;
	
	private DayOfWeek(String alias, String name) {
		this.alias = alias;
		this.name = name;
	}
	
	public String getAlias() {
		return this.alias;
	}
	
	public String getName() {
		return this.name;
	}
}
