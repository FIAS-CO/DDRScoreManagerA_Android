package jp.linanfine.dsma.value._enum;

public enum MusicRank {
	AAA,
	AAp,
	AA,
	AAm,
	Ap,
	A,
	Am,
	Bp,
	B,
	Bm,
	Cp,
	C,
	Cm,
	Dp,
	D,
	E,
	Noplay;
	public String toString(){
		switch(this){
		case AAA: return "AAA";
		case AAp: return "AA+";
		case AA: return "AA";
		case AAm: return "AA-";
		case Ap: return "A+";
		case A: return "A";
		case Am: return "A-";
		case Bp: return "B+";
		case B: return "B";
		case Bm: return "B-";
		case Cp: return "C+";
		case C: return "C";
		case Cm: return "C-";
		case Dp: return "D+";
		case D: return "D";
		case E: return "E";
		default: return "NoPlay";
		}
	}
}
