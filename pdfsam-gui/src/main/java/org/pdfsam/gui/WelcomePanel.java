/*
 * Created on 05/apr/2012
 * Copyright 2010 by Andrea Vacondio (andrea.vacondio@gmail.com).
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License as published by the Free Software Foundation; 
 * either version 2 of the License.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program; 
 * if not, write to the Free Software Foundation, Inc., 
 *  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.pdfsam.gui;

import javax.swing.JPanel;

import org.pdfsam.context.DefaultI18nContext;
import org.pdfsam.module.ModuleCategory;
import org.pdfsam.module.ModuleDescriptor;

/**
 * @author Andrea Vacondio
 * 
 */
public class WelcomePanel implements Module {

    private JPanel modulePanel = new JPanel();
    private ModuleDescriptor descriptor = new ModuleDescriptor(WelcomePanel.class.getCanonicalName(),
            ModuleCategory.OTHER, DefaultI18nContext.getInstance().i18n("Welcome"), null);

    @Override
    public ModuleDescriptor getDescriptor() {
        return descriptor;
    }

    @Override
    public JPanel getModulePanel() {
        return modulePanel;
    }

}