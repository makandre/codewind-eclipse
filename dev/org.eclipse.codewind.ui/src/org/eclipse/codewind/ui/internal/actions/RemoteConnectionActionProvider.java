/*******************************************************************************
 * Copyright (c) 2019, 2020 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *	 IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.codewind.ui.internal.actions;

import org.eclipse.codewind.core.internal.connection.RemoteConnection;
import org.eclipse.codewind.ui.internal.views.ViewHelper;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.SelectionProviderAction;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;

/**
 * Action provider for a Codewind connection.
 */
public class RemoteConnectionActionProvider extends CommonActionProvider {
	
	private ISelectionProvider selProvider;
	private NewProjectAction newProjectAction;
	private BindAction bindAction;
	private ManageRegistriesAction manageRegistriesAction;
	private ManageReposAction manageReposAction;
	private ConnectDisconnectAction connectDisconnectAction;
	private EditConnectionAction editConnectionAction;
	private RemoveConnectionAction removeConnectionAction;
	private RemoteDoubleClickAction remoteDoubleClickAction;
	private OpenTektonDashboardAction openTektonDashboardAction;
	private LogLevelAction logLevelAction;
	private DiagnosticsAction diagnosticsAction;
	
	@Override
	public void init(ICommonActionExtensionSite aSite) {
		super.init(aSite);
		selProvider = aSite.getStructuredViewer();
		newProjectAction = new NewProjectAction(selProvider);
		bindAction = new BindAction(selProvider);
		manageRegistriesAction = new ManageRegistriesAction(selProvider);
		manageReposAction = new ManageReposAction(selProvider);
		connectDisconnectAction = new ConnectDisconnectAction(selProvider);
		editConnectionAction = new EditConnectionAction(selProvider);
		removeConnectionAction = new RemoveConnectionAction(selProvider);
		remoteDoubleClickAction = new RemoteDoubleClickAction(selProvider);
		openTektonDashboardAction = new OpenTektonDashboardAction(selProvider);
		logLevelAction = new LogLevelAction(selProvider);
		diagnosticsAction = new DiagnosticsAction(selProvider);
	}
	
	@Override
	public void fillContextMenu(IMenuManager menu) {
		selProvider.setSelection(selProvider.getSelection());
		menu.add(newProjectAction);
		menu.add(bindAction);
		menu.add(new Separator());
		menu.add(manageRegistriesAction);
		menu.add(manageReposAction);
		menu.add(new Separator());
		if (openTektonDashboardAction.showAction()) {
			menu.add(openTektonDashboardAction);
			menu.add(new Separator());
		}
		menu.add(connectDisconnectAction);
		menu.add(editConnectionAction);
		menu.add(removeConnectionAction);
		boolean supportSeparator = false;
		if (logLevelAction.showAction()) {
			menu.add(new Separator());
			supportSeparator = true;
			menu.add(logLevelAction);
		}
		if (diagnosticsAction.showAction()) {
			if (!supportSeparator) {
				menu.add(new Separator());
			}
			menu.add(diagnosticsAction);
		}
	}
    
	@Override
	public void fillActionBars(IActionBars actionBars) {
		super.fillActionBars(actionBars);
		actionBars.setGlobalActionHandler(ICommonActionConstants.OPEN, remoteDoubleClickAction);
	}
	
	private static class RemoteDoubleClickAction extends SelectionProviderAction {
		
		RemoteConnection connection = null;
		
		public RemoteDoubleClickAction(ISelectionProvider selectionProvider) {
			super(selectionProvider, "");
			selectionChanged(getStructuredSelection());
		}

		@Override
		public void selectionChanged(IStructuredSelection sel) {
			if (sel.size() == 1) {
				Object obj = sel.getFirstElement();
				if (obj instanceof RemoteConnection) {
					connection = (RemoteConnection) obj;
					return;
				}
			}
			connection = null;
		}

		@Override
		public void run() {
			if (connection != null) {
				if (!connection.isConnected()) {
					ConnectDisconnectAction.connectRemoteCodewind(connection);
				} else {
					ViewHelper.expandConnection(connection);
				}
			}
		}
	}

}
