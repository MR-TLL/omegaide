package omega.plugin;
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
public class PlugInfo {
     public String name;
     public String fileName;
     public String size;
     public String desc = "";

     public PlugInfo(){
     	
     }

     public PlugInfo(String name, String fileName, String size, String desc){
     	this.name = name;
          this.fileName = fileName;
          this.size = size;
          this.desc = desc;
     }
}
