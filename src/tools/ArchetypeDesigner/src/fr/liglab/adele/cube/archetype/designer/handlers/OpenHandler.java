/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Lars Vogel <lars.Vogel@gmail.com> - Bug 419770
 *******************************************************************************/
package fr.liglab.adele.cube.archetype.designer.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import fr.liglab.adele.cube.archetype.designer.model.Archetype;
import fr.liglab.adele.cube.archetype.designer.model.IArchetypeService;
public class OpenHandler {

	@Inject
	IArchetypeService arch;
	
	@Execute
	public void execute(Shell shell){
		FileDialog dialog = new FileDialog(shell);
		dialog.setFilterExtensions(new String[] { "*.arch", "*.xml" });
		String filename = dialog.open();
		if (filename != null) {
			Archetype a = this.arch.loadArchetype(filename);
			if (a == null) {
				MessageDialog.openInformation(shell, "Error", "Invalid Archetype file!");
			} else {
				this.arch.setCurrentArchetype(a);
			}
		} else {
			MessageDialog.openInformation(shell, "Error", "You should choose a valid Archetype file!");
		}
	}
}
