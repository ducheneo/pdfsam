/* 
 * This file is part of the PDF Split And Merge source code
 * Created on 29/ago/2014
 * Copyright 2013-2014 by Andrea Vacondio (andrea.vacondio@gmail.com).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.pdfsam.ui.dashboard.preference;

import static org.sejda.eventstudio.StaticStudio.eventStudio;
import javafx.scene.control.Button;

import javax.inject.Named;

import org.pdfsam.i18n.DefaultI18nContext;
import org.pdfsam.module.ClearUsageRequestEvent;
import org.pdfsam.ui.support.Style;

/**
 * Button sending a request to clear usage statistics
 * 
 * @author Andrea Vacondio
 *
 */
@Named
class ClearStatisticsButton extends Button {

    ClearStatisticsButton() {
        super(DefaultI18nContext.getInstance().i18n("Clear usage statistics"));
        getStyleClass().addAll(Style.BUTTON.css());
        setOnAction(e -> eventStudio().broadcast(new ClearUsageRequestEvent()));
    }
}
