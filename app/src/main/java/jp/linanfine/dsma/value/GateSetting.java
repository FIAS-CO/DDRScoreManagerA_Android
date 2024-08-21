package jp.linanfine.dsma.value;

public class GateSetting {
	public int SetPfcScore;
	public boolean OverWriteLife4;
	public boolean OverWriteFullCombo;
	public boolean OverWriteLowerScores;
	public boolean FromNewSite;
	public SiteVersion FromSite;

	public enum SiteVersion {
		WORLD,
		A3,
		A20PLUS;

		public static SiteVersion fromString(String version) {
			try {
				return valueOf(version.toUpperCase());
			} catch (IllegalArgumentException e) {
				return WORLD;
			}
		}
	}
}
