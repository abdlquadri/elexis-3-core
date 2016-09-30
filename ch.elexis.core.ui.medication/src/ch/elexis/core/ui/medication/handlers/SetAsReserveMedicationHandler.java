package ch.elexis.core.ui.medication.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.medication.views.MedicationTableViewerItem;
import ch.elexis.core.ui.medication.views.MedicationView;
import ch.elexis.data.ArticleDefaultSignature;
import ch.elexis.data.Artikel;
import ch.elexis.data.Patient;
import ch.elexis.data.Prescription;
import ch.elexis.data.Prescription.EntryType;

public class SetAsReserveMedicationHandler extends AbstractHandler {
	
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
				
				// is no ReserveMedication yet
				if (presc != null && !presc.isReserveMedication()) {
					Artikel article = presc.getArtikel();
					String dose = presc.getDosis();
					String remark = presc.getBemerkung();
					String disposalComment = presc.getDisposalComment();
					
					if (dose.isEmpty() && remark.isEmpty()) {
						ArticleDefaultSignature defSig =
							ArticleDefaultSignature.getDefaultsignatureForArticle(article);
						if (defSig != null) {
							dose = defSig.getSignatureAsDosisString();
							remark = defSig.getSignatureComment();
						}
					}
					
					// create ReserveMedication
					Prescription reserveMedi = new Prescription(article,
						(Patient) ElexisEventDispatcher.getSelected(Patient.class), dose, remark);
					reserveMedi.setEntryType(EntryType.RESERVE_MEDICATION);
					// add disposal comment if present
					if (disposalComment != null && !disposalComment.isEmpty())
						reserveMedi.setDisposalComment(disposalComment);
						
					// if selection is FixMedication -> stop it
					if (presc.isFixedMediation()) {
						String stopDose = StringConstants.ZERO;
						presc.addTerm(null, stopDose);
						presc.setStopReason("Umgestellt auf ReserveMedikation");
					}
					
					MedicationView medicationView =
						(MedicationView) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getActivePage().findView(MedicationView.PART_ID);
					medicationView.refresh();
				}
			}
		}
		return null;
	}
	
}
