package ch.elexis.core.ui.medication.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import ch.elexis.core.ui.medication.views.MedicationTableViewerItem;
import ch.elexis.core.ui.medication.views.MedicationView;
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
				
				if (presc != null && !(presc.getEntryType() == EntryType.RESERVE_MEDICATION)) {
					Prescription reserveMedi = new Prescription(presc);
					reserveMedi.setEntryType(EntryType.RESERVE_MEDICATION);
					presc.stop(null);
					presc.setStopReason("Umgestellt auf Reserve Medikation");
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
