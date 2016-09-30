package ch.elexis.core.ui.medication.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.medication.views.MedicationTableViewerItem;
import ch.elexis.data.ArticleDefaultSignature;
import ch.elexis.data.Artikel;
import ch.elexis.data.Patient;
import ch.elexis.data.Prescription;
import ch.elexis.data.Prescription.EntryType;

public class SetAsFixMedicationHandler extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		ISelection selection =
			HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();
		if (selection != null) {
			IStructuredSelection strucSelection = (IStructuredSelection) selection;
			Object firstElement = strucSelection.getFirstElement();
			
			if (firstElement instanceof MedicationTableViewerItem) {
				MedicationTableViewerItem mtvItem = (MedicationTableViewerItem) firstElement;
				Prescription presc = mtvItem.getPrescription();
				
				if (presc != null && (!presc.isFixedMediation()
					|| presc.getEntryType() == EntryType.RESERVE_MEDICATION)) {
					Artikel article = presc.getArtikel();
					String dosage = presc.getDosis();
					String remark = presc.getBemerkung();
					String disposalComment = presc.getDisposalComment();
					
					if (dosage.isEmpty() && remark.isEmpty()) {
						ArticleDefaultSignature defSig =
							ArticleDefaultSignature.getDefaultsignatureForArticle(article);
						if (defSig != null) {
							dosage = defSig.getSignatureAsDosisString();
							remark = defSig.getSignatureComment();
						}
					}
					
					Prescription fixMediPresc = new Prescription(article,
						(Patient) ElexisEventDispatcher.getSelected(Patient.class), dosage, remark);
					fixMediPresc.setEntryType(EntryType.FIXED_MEDICATION);
					
					if (disposalComment != null && !disposalComment.isEmpty())
						fixMediPresc.setDisposalComment(disposalComment);
					
					// if selection is FixMedication -> stop it
					if (presc.isFixedMediation()) {
						String stopDose = StringConstants.ZERO;
						presc.addTerm(null, stopDose);
						presc.setStopReason("Umgestellt auf Fix Medikation");
					}
				}
			}
		}
		return null;
	}
}
