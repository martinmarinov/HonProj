package martin.translatortest;

import martin.translatortest.QCGate.type;

public class TranslatorRunner {

	private final static String translator_path = "C:\\Users\\Martin\\Desktop\\data\\einar\\ParallelQC.exe";
	
	public static void main(String[] args) throws Exception {
		System.out.println(QCGate.translateToMBQCRaw(translator_path, new QCGate(type.ZZ, 1, 2), new QCGate(type.ZZ, 2, 3), new QCGate(type.H, 3)));
	}
	


}
