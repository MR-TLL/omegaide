package ide.utils.systems.creators;
/*
    Copyright (C) 2021 Omega UI. All Rights Reserved.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
import java.io.File;

import creator.FileWizard;
import ide.Screen;

public class FileCreator {

	private Screen screen;
	private FileWizard fileWizard;
	
	public FileCreator(Screen screen) {
		this.screen = screen;
		fileWizard = new FileWizard(screen);
	}	
	
	public void show(String type) {
		if(Screen.getFileView().getProjectPath() != null && new File(Screen.getFileView().getProjectPath()).exists())
			fileWizard.show(type);
	}

}
