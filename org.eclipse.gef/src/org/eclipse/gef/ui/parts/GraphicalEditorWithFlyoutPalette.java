/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.gef.ui.parts;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite.FlyoutPreferences;
import org.eclipse.gef.ui.views.palette.PalettePage;
import org.eclipse.gef.ui.views.palette.PaletteViewerPage;

/**
 * This class serves as a quick starting point for clients who are new to GEF. It will
 * create an Editor with a flyout palette.  The flyout palette will only be visible when
 * the palette view is not open.
 * <p>
 * <EM>IMPORTANT</EM>This class should only be used as a reference for creating your own
 * EditorPart implementation. This class will not suit everyone's needs, and may change in
 * the future. Clients may copy the implementation.
 * 
 * @author Pratik Shah
 * @since 3.0
 */
public abstract class GraphicalEditorWithFlyoutPalette
	extends GraphicalEditor
{
	
private PaletteViewerProvider provider;
private FlyoutPaletteComposite splitter;
private CustomPalettePage page;

/**
 * @see	GraphicalEditor#initializeGraphicalViewer()
 */
protected void initializeGraphicalViewer() {
	splitter.hookDropTargetListener(getGraphicalViewer());
}

/**
 * Creates a PaletteViewerProvider that will be used to create palettes for the view
 * and the flyout.
 * 
 * @return	the palette provider
 */
protected PaletteViewerProvider createPaletteViewerProvider() {
	return new PaletteViewerProvider(getGraphicalViewer().getEditDomain());
}

protected CustomPalettePage createPalettePage() {
	return new CustomPalettePage(getPaletteViewerProvider());
}

/**
 * @see	GraphicalEditor#createPartControl(Composite)
 */
public void createPartControl(Composite parent) {
	splitter = new FlyoutPaletteComposite(parent, SWT.NONE, getSite().getPage(),
			getPaletteViewerProvider(), getPalettePreferences());
	super.createPartControl(splitter);
	splitter.setGraphicalControl(getGraphicalControl());
	if (page != null) {
		splitter.setExternalViewer(page.getPaletteViewer());
		page = null;
	}
}

/**
 * @see	org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
 */
public Object getAdapter(Class type) {
	if (type == PalettePage.class) {
		if (splitter == null) {
			page = createPalettePage();
			return page;
		}
		return createPalettePage();
	}
	return super.getAdapter(type);
}

/**
 * @return	the graphical viewer's control
 */
protected Control getGraphicalControl() {
	return getGraphicalViewer().getControl();
}

/**
 * @return	the FlyoutPreferences object used to save the flyout palette's preferences 
 */
protected abstract FlyoutPreferences getPalettePreferences();
	
/**
 * Returns the PaletteRoot for the palette viewer.
 * @return the palette root
 */
protected abstract PaletteRoot getPaletteRoot();

/**
 * Returns the palette viewer provider that is used to create palettes for the view and
 * the flyout.  Creates one if it doesn't already exist.
 * 
 * @see	#createPaletteViewerProvider()
 */
protected final PaletteViewerProvider getPaletteViewerProvider() {
	if (provider == null)
		provider = createPaletteViewerProvider();
	return provider;
}

/**
 * Sets the edit domain for this editor.
 * 
 * @param	ed	The new EditDomain
 */
protected void setEditDomain(DefaultEditDomain ed) {
	super.setEditDomain(ed);
	getEditDomain().setPaletteRoot(getPaletteRoot());
}

protected class CustomPalettePage extends PaletteViewerPage {
	public CustomPalettePage(PaletteViewerProvider provider) {
		super(provider);
	}
	public void createControl(Composite parent) {
		super.createControl(parent);
		if (splitter != null)
			splitter.setExternalViewer(viewer);
	}
	public void dispose() {
		if (splitter != null)
			splitter.setExternalViewer(null);
		super.dispose();
	}
	public PaletteViewer getPaletteViewer() {
		return viewer;
	}
}

}