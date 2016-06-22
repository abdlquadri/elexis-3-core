/*******************************************************************************
 * Copyright (c) 2016, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/
package ch.elexis.core.ui.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Just a host for the category DocumentHandling
 * @author gerry
 *
 */
public class DocMgrCategoryHost extends PreferencePage implements IWorkbenchPreferencePage {
	
	public DocMgrCategoryHost(){
		// TODO Auto-generated constructor stub
	}
	
	public DocMgrCategoryHost(String title){
		super(title);
		// TODO Auto-generated constructor stub
	}
	
	public DocMgrCategoryHost(String title, ImageDescriptor image){
		super(title, image);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void init(IWorkbench workbench){
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected Control createContents(Composite parent){
		// TODO Auto-generated method stub
		return null;
	}
	
}
