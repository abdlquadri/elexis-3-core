package ch.elexis.core.data.util;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import ch.elexis.data.Artikel;
import ch.elexis.data.Patient;
import ch.elexis.data.Prescription;
import ch.elexis.data.Query;

public class ScriptExtractFixMedi {

	public String run(String targetfile){
		try{
			File out=new File(targetfile);
			if(!out.canWrite()){
				return "can't write "+targetfile;
			}
			FileWriter fw=new FileWriter(out);
			List<Patient> all =new Query<Patient>(Patient.class).execute();
			for(Patient pat:all){
				Prescription[] medis=pat.getFixmedikation();
				for(Prescription pre:medis){
					Artikel art=Artikel.load(pre.get(Prescription.FLD_ARTICLE_ID));
					StringBuilder sb=new StringBuilder();
					sb.append(pat.getId()).append(",")
						.append(art.getEAN()).append(",")
						.append(art.getPharmaCode()).append(",")
						.append(pre.getDosis()).append(",")
						.append(pre.getBemerkung()).append("\n");
					fw.write(sb.toString());	
				}
				fw.flush();
			}
			fw.close();
			
		}catch(Exception ex){
			return "Caught exception "+ex.getMessage();
		}
		return "";
	}
}
