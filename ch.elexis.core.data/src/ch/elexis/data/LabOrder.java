package ch.elexis.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.elexis.data.LabItem.typ;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.JdbcLink.Stm;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.VersionInfo;

public class LabOrder extends PersistentObject implements Comparable<LabOrder> {
	
	public static final String FLD_USER = "userid"; //$NON-NLS-1$
	public static final String FLD_MANDANT = "mandant"; //$NON-NLS-1$
	public static final String FLD_PATIENT = "patient"; //$NON-NLS-1$
	public static final String FLD_ITEM = "item"; //$NON-NLS-1$
	public static final String FLD_RESULT = "result"; //$NON-NLS-1$
	public static final String FLD_TIME = "time"; //$NON-NLS-1$
	public static final String FLD_OBSERVATIONTIME = "observationtime"; //$NON-NLS-1$
	public static final String FLD_STATE = "state"; //$NON-NLS-1$
	public static final String FLD_ORDERID = "orderid"; //$NON-NLS-1$
	public static final String FLD_GROUPNAME = "groupname"; //$NON-NLS-1$
	
	public static final String VERSIONID = "VERSION"; //$NON-NLS-1$
	private static final String TABLENAME = "LABORDER"; //$NON-NLS-1$
	public static final String VERSION = "1.3.0"; //$NON-NLS-1$
	public static final String VERSION110 = "1.1.0"; //$NON-NLS-1$
	public static final String VERSION120 = "1.2.0"; //$NON-NLS-1$
	public static final String VERSION130 = "1.3.0"; //$NON-NLS-1$
	private static final String UPD110 = "ALTER TABLE " + TABLENAME //$NON-NLS-1$
		+ " ADD groupname VARCHAR(255);"; //$NON-NLS-1$
	private static final String UPD120 = "ALTER TABLE " + TABLENAME //$NON-NLS-1$
		+ " ADD observationtime VARCHAR(24);"; //$NON-NLS-1$
	private static final String UPD130 =
		"CREATE INDEX laborder4 ON " + TABLENAME + " (" + FLD_ORDERID + ");";//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		
	// do not change order, as we save the ordinal to the db, only adding new state is allowed
	public enum State {
		ORDERED, DONE, DONE_IMPORT;
	}
	
	public static String getStateLabel(State state){
		switch (state) {
		case ORDERED:
			return Messages.LabOrder_stateOrdered;
		case DONE:
			return Messages.LabOrder_stateDone;
		case DONE_IMPORT:
			return Messages.LabOrder_stateImported;
		default:
			return "???"; //$NON-NLS-1$
		}
	}
	
	// @formatter:off
	static final String create = 
			"CREATE TABLE " + TABLENAME + " (" + //$NON-NLS-1$ //$NON-NLS-2$
			"ID VARCHAR(25) primary key, " + //$NON-NLS-1$
			"lastupdate BIGINT," + //$NON-NLS-1$
			"deleted CHAR(1) default '0'," + //$NON-NLS-1$
			
			"userid VARCHAR(128)," + //$NON-NLS-1$
			"mandant VARCHAR(128)," + //$NON-NLS-1$
			"patient VARCHAR(128)," + //$NON-NLS-1$
			"item VARCHAR(128)," + //$NON-NLS-1$
			"result VARCHAR(128)," + //$NON-NLS-1$
			"orderid VARCHAR(128)," + //$NON-NLS-1$
			"groupname VARCHAR(255)," + //$NON-NLS-1$
			"time VARCHAR(24)," + //$NON-NLS-1$
			"observationtime VARCHAR(24)," + //$NON-NLS-1$
			"state CHAR(1)" + //$NON-NLS-1$		
			");" + //$NON-NLS-1$
			"CREATE INDEX laborder1 ON " + TABLENAME + " (" + FLD_TIME + ");" + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			"CREATE INDEX laborder2 ON " + TABLENAME + " (" + FLD_MANDANT + ");" + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			"CREATE INDEX laborder3 ON " + TABLENAME + " (" + FLD_PATIENT + ");" + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			"CREATE INDEX laborder4 ON " + TABLENAME + " (" + FLD_ORDERID + ");" + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			"INSERT INTO " + TABLENAME + " (ID," + FLD_USER + ") VALUES (" + JdbcLink.wrap(VERSIONID) + "," + JdbcLink.wrap(VERSION) + ");"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
	// @formatter:on
	
	static {
		addMapping(TABLENAME, FLD_ID, FLD_USER, FLD_MANDANT, FLD_PATIENT, FLD_ITEM, FLD_RESULT,
			FLD_ORDERID, FLD_GROUPNAME, FLD_TIME, FLD_STATE, FLD_OBSERVATIONTIME);
		
		if (!tableExists(TABLENAME)) {
			createOrModifyTable(create);
		} else {
			LabOrder version = load(VERSIONID);
			VersionInfo vi = new VersionInfo(version.get(FLD_USER));
			if (vi.isOlder(VERSION)) {
				if (vi.isOlder(new VersionInfo(VERSION110))) {
					createOrModifyTable(UPD110);
				}
				if (vi.isOlder(new VersionInfo(VERSION120))) {
					createOrModifyTable(UPD120);
				}
				if (vi.isOlder(new VersionInfo(VERSION130))) {
					createOrModifyTable(UPD130);
				}
				version.set(FLD_USER, VERSION);
			}
		}
	}
	
	@Override
	protected String getTableName(){
		return TABLENAME;
	}
	
	public LabOrder(){
		// TODO Auto-generated constructor stub
	}
	
	public LabOrder(String id){
		super(id);
	}
	
	public static LabOrder load(final String id){
		return new LabOrder(id);
	}
	
	public LabOrder(Anwender user, Mandant mandant, Patient patient, LabItem item,
		LabResult result, String orderId, String groupname, TimeTool time){
		create(null);
		set(new String[] {
			FLD_USER, FLD_MANDANT, FLD_PATIENT, FLD_ITEM, FLD_TIME, FLD_OBSERVATIONTIME,
			FLD_ORDERID, FLD_GROUPNAME
		}, user.getId(), mandant.getId(), patient.getId(), item.getId(),
			time.toString(TimeTool.TIMESTAMP), time.toString(TimeTool.TIMESTAMP), orderId,
			groupname);
		setState(State.ORDERED);
		if (result != null) {
			set(FLD_RESULT, result.getId());
		}
		if (item.getTyp() == typ.FORMULA) {
			createResult();
			setState(State.DONE);
		}
	}

	public LabResult createResult(){
		LabResult result = new LabResult(getPatient(), null, getLabItem(), "", null);
		result.setObservationTime(getObservationTime());
		setLabResult(result);
		return result;
	}
	
	public LabResult createResult(Kontakt origin){
		LabResult result = new LabResult(getPatient(), null, getLabItem(), "", null, origin);
		result.setObservationTime(getObservationTime());
		setLabResult(result);
		return result;
	}
	
	public void setState(State state){
		set(FLD_STATE, Integer.toString(state.ordinal()));
		// check if there is a reminder to set done ...
		if (state != State.ORDERED) {
			if (isOrderDone()) {
				closeOrderReminder();
			}
		}
	}
	
	public void setObservationTime(TimeTool time){
		set(FLD_OBSERVATIONTIME, time.toString(TimeTool.TIMESTAMP));
	}

	public void setObservationTimeWithResults(TimeTool time){
		setObservationTime(time);
		List<LabResult> results = getLabResults();
		for (LabResult labResult : results) {
			labResult.setObservationTime(time);
		}
	}

	/**
	 * Test if all the orders with this order id are done
	 * 
	 * @return
	 */
	private boolean isOrderDone(){
		return getLabOrders(getPatient(), null, null, null, get(FLD_ORDERID), null, State.ORDERED) != null;
	}

	/**
	 * Close all {@link Reminder} which have the order id set as params field. Setting the params
	 * field happens in LaborVerordnungDialog.
	 * 
	 */
	private void closeOrderReminder(){
		List<Reminder> reminders = Reminder.findForPatient(getPatient(), null);
		for (Reminder reminder : reminders) {
			String params = reminder.get("Params"); //$NON-NLS-1$
			if (params.startsWith(LabOrder.FLD_ORDERID)) {
				String[] parts = params.split("="); //$NON-NLS-1$
				if (parts.length == 2) {
					if (parts[1].equals(get(FLD_ORDERID))) {
						reminder.setStatus(Reminder.Status.STATE_DONE);
					}
				}
			}
		}
	}

	public State getState(){
		String stateStr = checkNull(get(FLD_STATE));
		if (stateStr.isEmpty()) {
			return State.ORDERED;
		} else {
			return State.values()[Integer.parseInt(stateStr.trim())];
		}
	}
	
	public TimeTool getObservationTime(){
		String string = get(FLD_OBSERVATIONTIME);
		if (string != null && !string.isEmpty()) {
			return new TimeTool(string);
		} else {
			return null;
		}
	}

	public LabItem getLabItem(){
		LabItem ret = new LabItem(get(FLD_ITEM));
		if (ret.exists()) {
			return ret;
		} else {
			return null;
		}
	}
	
	public void setLabItem(LabItem item){
		set(FLD_ITEM, item.getId());
	}
	
	public LabResult getLabResult(){
		LabResult ret = new LabResult(get(FLD_RESULT));
		if (ret.exists()) {
			return ret;
		} else {
			return null;
		}
	}
	
	public void setLabResult(LabResult result){
		if (result != null) {
			set(FLD_RESULT, result.getId());
		} else {
			set(FLD_RESULT, null);
		}
	}
	
	public TimeTool getTime(){
		String string = get(FLD_TIME);
		if (string != null && !string.isEmpty()) {
			return new TimeTool(string);
		} else {
			return null;
		}
	}
	
	public Patient getPatient(){
		return Patient.load(get(FLD_PATIENT));
	}
	
	@Override
	public int compareTo(LabOrder other){
		State otherState = other.getState();
		if (otherState.ordinal() < getState().ordinal()) {
			return -1;
		} else if (otherState.ordinal() > getState().ordinal()) {
			return 1;
		}
		return getLabItem().getLabel().compareTo(other.getLabItem().getLabel());
	}
	
	@Override
	public String getLabel(){
		StringBuilder sb = new StringBuilder();
		sb.append("[" + get(FLD_ORDERID) + "] - "); //$NON-NLS-1$ //$NON-NLS-2$
		sb.append(getStateLabel(getState()));
		sb.append(" - "); //$NON-NLS-1$
		sb.append(getLabItem().getLabel());
		if (getLabResult() != null) {
			sb.append(" - "); //$NON-NLS-1$
			sb.append(getLabResult().getResult());
		}
		return sb.toString();
	}
	
	/**
	 * Get a list of all results of the orders with the same orderId.
	 * 
	 * @return
	 */
	public List<LabResult> getLabResults(){
		ArrayList<LabResult> ret = new ArrayList<LabResult>();
		List<LabOrder> orders = getLabOrders(null, null, null, null, get(FLD_ORDERID), null, null);
		if (orders != null) {
			for (LabOrder labOrder : orders) {
				if (labOrder.getLabResult() != null) {
					ret.add(labOrder.getLabResult());
				}
			}
		}
		return ret;
	}
	
	/**
	 * Get the {@link Kontakt} used if result is entered manual.
	 * 
	 * @return
	 */
	public static Kontakt getOrCreateManualLabor(){
		String identifier = Messages.LabOrder_contactOwnLabName;
		Labor labor = null;
		Query<Labor> qbe = new Query<Labor>(Labor.class);
		qbe.add(Kontakt.FLD_SHORT_LABEL, Query.LIKE, "%" + identifier + "%"); //$NON-NLS-1$ //$NON-NLS-2$
		qbe.or();
		qbe.add(Kontakt.FLD_NAME1, Query.LIKE, "%" + identifier + "%"); //$NON-NLS-1$ //$NON-NLS-2$
		List<Labor> results = qbe.execute();
		if (results.isEmpty()) {
			labor = new Labor(identifier, "Labor " + identifier); //$NON-NLS-1$
		} else {
			labor = results.get(0);
		}
		return labor;
	}
	
	public static List<LabOrder> getLabOrdersByOrderId(String orderId){
		Query<LabOrder> qlo = new Query<LabOrder>(LabOrder.class);
		qlo.add(FLD_ORDERID, Query.EQUALS, orderId);
		qlo.add(FLD_ID, Query.NOT_EQUAL, VERSIONID);
		List<LabOrder> orders = qlo.execute();
		if (orders.isEmpty()) {
			return null;
		} else {
			return orders;
		}
	}
	
	/**
	 * Returns all LabOrders matching the criteria. If a parameter is null it will be ignored.
	 * 
	 * @param patient
	 * @param mandant
	 * @param labItem
	 * @param orderId
	 * @param time
	 * @return
	 */
	public static List<LabOrder> getLabOrders(Patient patient, Mandant mandant, LabItem labItem,
		LabResult result, String orderId, TimeTool time, State state){
		Query<LabOrder> qlo = new Query<LabOrder>(LabOrder.class);
		qlo.add(FLD_ID, Query.NOT_EQUAL, VERSIONID);
		
		if (patient != null) {
			qlo.add(FLD_PATIENT, "=", patient.getId()); //$NON-NLS-1$
		}
		
		if (mandant != null) {
			qlo.add(FLD_MANDANT, "=", mandant.getId()); //$NON-NLS-1$
		}
		
		if (labItem != null) {
			qlo.add(FLD_ITEM, "=", labItem.getId()); //$NON-NLS-1$
		}
		
		if (result != null) {
			qlo.add(FLD_RESULT, "=", result.getId()); //$NON-NLS-1$
		}
		
		if (orderId != null && !orderId.isEmpty()) {
			qlo.add(FLD_ORDERID, "=", orderId); //$NON-NLS-1$
		}
		
		if (time != null) {
			qlo.add(FLD_TIME, "LIKE", time.toString(TimeTool.DATE_COMPACT) + "%"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		if (state != null) {
			qlo.add(FLD_STATE, Query.EQUALS, Integer.toString(state.ordinal()));
		}
		
		List<LabOrder> orders = qlo.execute();
		if (orders.isEmpty()) {
			return null;
		} else {
			return orders;
		}
	}
	
	public static Map<String, List<LabOrder>> getMapByOrderId(){
		HashMap<String, List<LabOrder>> ret = new HashMap<String, List<LabOrder>>();
		Query<LabOrder> qlo = new Query<LabOrder>(LabOrder.class);
		List<LabOrder> orders = qlo.execute();
		for (LabOrder labOrder : orders) {
			List<LabOrder> groupedList = ret.get(labOrder.get(LabOrder.FLD_ORDERID));
			if (groupedList != null) {
				groupedList.add(labOrder);
			} else {
				groupedList = new ArrayList<LabOrder>();
				groupedList.add(labOrder);
				ret.put(labOrder.get(LabOrder.FLD_ORDERID), groupedList);
			}
		}
		return ret;
	}
	
	public synchronized static String getNextOrderId(){
		LabOrder version = load(VERSIONID);
		String orderId = version.get(FLD_ORDERID);
		String nextOrderId = "-1";
		if (orderId != null && !orderId.isEmpty()) {
			int intNextOrderId = Integer.parseInt(orderId) + 1;
			// make sure it is still free
			List<LabOrder> existing = getLabOrdersByOrderId(Integer.toString(intNextOrderId));
			while (existing != null) {
				intNextOrderId++;
				existing = getLabOrdersByOrderId(Integer.toString(intNextOrderId));
			}
			nextOrderId = Integer.toString(intNextOrderId);
		} else {
			// fallback to old method
			nextOrderId = getNextOrderIdOld();
		}
		version.set(FLD_ORDERID, nextOrderId);
		return nextOrderId;
	}
	
	/**
	 * Old version of getting next order id.
	 * 
	 * @return
	 */
	private static String getNextOrderIdOld(){
		JdbcLink link = PersistentObject.getConnection();
		Stm statement = link.getStatement();
		ResultSet result =
			statement
				.query("SELECT COUNT(DISTINCT " + FLD_ORDERID + ") AS total FROM " + TABLENAME); //$NON-NLS-1$ //$NON-NLS-2$
		int numberOfIds = 0;
		try {
			if (result.next()) {
				numberOfIds = result.getInt("total"); //$NON-NLS-1$
			}
		} catch (SQLException ex) {
			ExHandler.handle(ex);
		} finally {
			link.releaseStatement(statement);
		}
		int orderId = numberOfIds + 1;
		List<LabOrder> existing =
			getLabOrders(null, null, null, null, Integer.toString(orderId), null, null);
		while (existing != null) {
			orderId++;
			existing = getLabOrders(null, null, null, null, Integer.toString(orderId), null, null);
		}
		return Integer.toString(orderId);
	}
}
