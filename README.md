
![Splash](/images/startup_splash.png)

# omegaide

A extremely lightweight and super fast java ide for linux.
It includes, 
**Class-Path Manager**, 

**Module-Path Manager**, 

**Snippet Manager -add your own and just press TAB key**, 

**Content Assist**, 

**Auto Import -press Ctrl + SHIFT + O** , 

**Click Editor Image**, 

**Jar Creation**, 

**Elegant Design**, 

**Lots more to be added**, 

It runs faster if you have debian and gnome as the default system operator.

# important, please read
Please read the LICENSE before reading further.
If you want to edit the source code then, for completing the src and res folders you need to extract the file named
**"rsyntaxtextarea_modified_sources.zip"**. After extraction, you will find two folders "src" and "res" inside the extracted directory.
What you need to do is to add the contents of "src" and "res" folders to the project's "src" and "res" folder.
Thats all. The Project is now ready for editing, compiling or running.

# Editing

Well, you can either use Eclipse IDE or even Omega IDE for managing the project.
There is **debian installer** included in the out folder for debian based distros.
There is a **"Omega IDE.jar"** file included in the debian installer in the out directory of the project.
Extract the installer and run Omega IDE.jar located inside **extracted-directory/usr/bin**
You can either run it directly or you can install it in your system by your own.

# Compiling and Running

For compiling, open terminal in the project directory.
create a directory **bin**.
and run 
**javac -d bin -cp res/asterisk:lib/asterisk:. @.sources**

For running, open terminal in the bin directory created previously.
and run 
**java -cp res/asterisk:lib/asterisk:. ide.Screen**
replace **asterisk** by its symbol (*).
