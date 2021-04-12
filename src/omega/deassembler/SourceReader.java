package omega.deassembler;
import omega.jdk.JDKManager;
import omega.framework.CodeFramework;
import java.util.LinkedList;

public class SourceReader {
	public class Import{
		public String pack;
		public String name;
		public String staticMember;
		public boolean isStatic;
		public Import(String pack, String name){
			this.name = name;
			if(pack.startsWith("static ")){
				isStatic = true;
				staticMember = name;
				this.name = pack.substring(pack.lastIndexOf('.') + 1).trim();
				pack = pack.substring(pack.indexOf(' ') + 1, pack.lastIndexOf('.')).trim();
			}
			this.pack = pack;
		}

		public String get(){
			return pack + "." + name;
		}
	}
	private String code;
	public String pack;
	public String access;
	public String modifier;
	public String className;
	public String type;
	public String parent = "Object";
	public String[] features;
	public LinkedList<DataMember> dataMembers = new LinkedList<>();
	public LinkedList<SourceReader> internalReaders = new LinkedList<>();
	public LinkedList<Import> imports = new LinkedList<>();
	public LinkedList<DataBlock> dataBlocks = new LinkedList<>();
	public volatile boolean recordingInternal;
	public SourceReader(String code){
		this.code = code;
		try {
			read();
		}
		catch(Exception e) {
		     e.printStackTrace();
	     }
	}

	public SourceReader(String code, boolean readOnlyImports) {
		this.code = code;
		readImports();
	}

	public void readImports() {
		dataMembers.clear();
		internalReaders.clear();
		imports.clear();
		dataBlocks.clear();
		recordingInternal = false;
		boolean commentStarts = false;
		boolean canReadImports = true;
          LinkedList<String> tokens = CodeTokenizer.tokenize(code, '\n');
		for(String line : tokens){
               //Skippings Single Character like }, etc
               if(line.trim().length() <= 1 && !line.trim().equals("}"))
                    continue;
			//Skipping Strings and characters
			String cLine = line;
			line = "";
			boolean r = true;
			int in = 0;
			for(char ch : cLine.toCharArray()){
				if((ch == '\"' || ch == '\'') && in > 0 && cLine.charAt(in - 1) != '\\')
					r = !r;
				if(r && ch != '\"' && ch != '\''){
					line += ch;
				}
				in++;
			}
			cLine = "";
			//Skipping Diamonds
			if(line.contains("<") && line.contains(">") && !line.contains("&") && !line.contains("|")){
				int c = -1;
				cLine = line;
				line = "";
				for(char ch : cLine.toCharArray()){
					if(ch == '<')
						c++;
					else if(ch == '>')
						c--;
					if(c == -1 & ch != '<' & ch != '>')
						line += ch;
				}
			}
			//Skipping Comments
			if(line.startsWith("//")) continue;
			if(line.startsWith("/*")){
				commentStarts = true;
				continue;
			}
			else if(line.contains("*/")){
				commentStarts = false;
				continue;
			}
			else if(commentStarts) continue;
			if(line.startsWith("import ") && canReadImports){
				String im = line.substring(line.indexOf(' ') + 1, line.indexOf(';')).trim();
				String pack = im.substring(0, im.lastIndexOf('.'));
				String name = im.substring(im.lastIndexOf('.') + 1);
				if(pack.equals("java.lang")) continue;
				if(name.equals("*") && !JDKManager.reading){

					if(line.contains(" static ")) {
						if(CodeFramework.isSource(pack)) {
							SourceReader reader = new SourceReader(pack);
							for(DataMember m : reader.getDataMembers("static")) {
								String nameX = m.name;
								if(nameX.contains("()"))
									nameX = nameX.substring(0, nameX.indexOf('(')).trim();
								imports.add(new Import(pack, nameX));
							}
						}
						else {
							ByteReader reader = omega.Screen.getFileView().getJDKManager().prepareReader(pack);
							for(DataMember m : reader.getDataMembers("static")) {
								String nameX = m.name;
								if(nameX.contains("()"))
									nameX = nameX.substring(0, nameX.indexOf('(')).trim();
								imports.add(new Import(pack, nameX));
							}
						}
					}
					else {
						for(omega.jdk.Import impo : JDKManager.getAllImports()) {
							if(impo.getPackage().equals(pack)) {
								imports.add(new Import(pack, impo.getClassName()));
							}
						}
					}
				}
				else{
					imports.add(new Import(pack, name));
				}
				continue;
			}
		}
	}

	public void read(){
		dataMembers.clear();
		internalReaders.clear();
		imports.clear();
		dataBlocks.clear();
		recordingInternal = false;
		boolean commentStarts = false;
		boolean canReadImports = true;
		boolean readBlock = false;
		int openBracesCount = -1;
		int internalCount = -1;
		String internalCode = "";
		String blockCode = "";
		LinkedList<String> tokens = CodeTokenizer.tokenize(code, '\n');
          int lineN = 0;
          JDKManager.javaLangPack.forEach(im->{
               imports.add(new Import(im.getPackage(), im.getClassName()));
          });
          for(int i = 0; i < tokens.size(); i++){
               String line = tokens.get(i);
               //Skipping Single Words
               if(line.trim().length() <= 1 && !line.trim().equals("}")){
                    continue;
               }
			//Skipping Strings and characters
               lineN++;
			String cLine = line;
			line = "";
			boolean r = true;
			int in = 0;
			for(char ch : cLine.toCharArray()){
				if((ch == '\"' || ch == '\'') && in > 0 && cLine.charAt(in - 1) != '\\')
					r = !r;
				if(r && ch != '\"' && ch != '\''){
					line += ch;
				}
				in++;
			}
			cLine = "";
			//Skipping Diamonds
			if(line.contains("<") && line.contains(">") && !line.contains("&") && !line.contains("|")){
				int c = -1;
				cLine = line;
				line = "";
				for(char ch : cLine.toCharArray()){
					if(ch == '<')
						c++;
					else if(ch == '>')
						c--;
					if(c == -1 & ch != '<' & ch != '>')
						line += ch;
				}
			}
			//Skipping Comments
               
			if(line.startsWith("//")) {
			     continue;
			}
			if(line.startsWith("/*")){
				commentStarts = true;
				continue;
			}
			else if(line.contains("*/")){
				commentStarts = false;
				continue;
			}
			else if(commentStarts) {
			     continue;
			}
			//Reading code
			//Counting Braces for InternalReaders
			if(line.contains("{")) internalCount += CodeFramework.count(line, '{');
			if(line.contains("}")) internalCount -= CodeFramework.count(line, '}');
			//Balancing OpenBraces
			if(line.contains("{")) openBracesCount += CodeFramework.count(line, '{');
               //System.out.println(line);
               //System.out.println(line + " Line " + lineN + " of "+ tokens.size() + "-->\' " + openBracesCount + " \', Comment=" + commentStarts);
			//System.out.println(line+", count "+openBracesCount);
			//Unpacking Prototype_
			if(line.startsWith("import ") && line.endsWith(";") && canReadImports){
				String im = line.substring(line.lastIndexOf(' ') + 1, line.indexOf(';')).trim();
				String pack = im.substring(0, im.lastIndexOf('.'));
				String name = im.substring(im.lastIndexOf('.') + 1);
				if(pack.equals("java.lang") || !Character.isLowerCase(pack.charAt(0))) continue;
				if(name.equals("*") && !JDKManager.reading){
					//To be continued in IDE's repository
					if(line.contains(" static ")) {
						if(CodeFramework.isSource(pack)) {
							SourceReader reader = new SourceReader(pack);
							for(DataMember m : reader.getDataMembers("static")) {
								String nameX = m.name;
								if(nameX.contains("()"))
									nameX = nameX.substring(0, nameX.indexOf('(')).trim();
								imports.add(new Import(pack, nameX));
							}
						}
						else {
							ByteReader reader = omega.Screen.getFileView().getJDKManager().prepareReader(pack);
							for(DataMember m : reader.getDataMembers("static")) {
								String nameX = m.name;
								if(nameX.contains("()"))
									nameX = nameX.substring(0, nameX.indexOf('(')).trim();
								imports.add(new Import(pack, nameX));
							}
						}
					}
					else {
						for(omega.jdk.Import impo : JDKManager.getAllImports()) {
							if(impo.getPackage().equals(pack)) {
								imports.add(new Import(pack, impo.getClassName()));
							}
						}
					}
				}
				else{
					imports.add(new Import(pack, name));
				}
				continue;
			}
			if(line.startsWith("package ") && line.endsWith(";") && pack == null){
				pack = line.substring(line.indexOf(' ') + 1, line.indexOf(';')).trim();
				continue;
			}
			if(className == null) {
				String type = "";
				if(line.contains("class "))
					type = "class";
				else if(line.contains("enum "))
					type = "enum";
				else if(line.contains("interface "))
					type = "interface";
                    else if(line.contains("record "))
                         type = "record";
				if(line.contains("@interface "))
					type = "@interface";
				if(!type.equals("")){
					canReadImports = false;
					this.type = type;
					String part = line.substring(0, line.indexOf(type)).trim();
					if(!line.startsWith(type)){
						if(part.contains(" ")){
							access = part.substring(0, part.indexOf(' ')).trim();
							modifier = part.substring(part.indexOf(' ') + 1).trim();
						}
						else access = part;
					}
					int index = line.indexOf(type) + type.length();
					int indexAfter = line.indexOf('\n');
					if(line.contains("{"))
						indexAfter = line.indexOf("{");
					if(line.indexOf(' ', index + 1) > -1){

						indexAfter = line.indexOf(' ', index + 1);
					}
					className = line.substring(index + 1, indexAfter);
					if(line.contains("extends")){
						int extendsI = line.indexOf("extends") + 7;
						if(line.contains("implements")){
							indexAfter = line.indexOf(' ', extendsI + 1);
							parent = line.substring(extendsI + 1, indexAfter).trim();
							int impI = indexAfter + 1 + 10;
							String x = line.substring(impI).trim();
							if(x.contains("{"))
								x = x.substring(0, x.indexOf('{'));
							else if(x.endsWith("\n"))
								x = x.substring(0, x.indexOf('\n'));
							if(!x.contains(",")){
								features = new String[1];
								features[0] = x;
							}
							else
								features = x.split(",");
							for(int ix = 0; ix < features.length; ix++)
								features[ix] = features[ix].trim();
						}
						else{
							if(line.contains("{"))
								indexAfter = line.indexOf('{');
							else indexAfter = line.indexOf('\n');
							parent = line.substring(extendsI + 1, indexAfter).trim();
						}
					}
					if(line.contains("implements") && !line.contains("extends")){
						int impI = line.indexOf("implements") + 10;
						String x = line.substring(impI).trim();
						if(x.contains("{"))
							x = x.substring(0, x.indexOf('{'));
						else if(x.endsWith("\n"))
							x = x.substring(0, x.indexOf('\n'));
						if(!x.contains(",")){
							features = new String[1];
							features[0] = x;
						}
						else
							features = x.split(",");
						for(int ix = 0; ix < features.length; ix++)
							features[ix] = features[ix].trim();
					}
					continue;
				}
			}
			//Unpacking Internal Classess
			if(internalCount >= 1){
				if(!recordingInternal){
					if(line.contains("class ") || line.contains("enum ") || line.contains("interface ")){
						recordingInternal = true;
					}
				}
				if(recordingInternal){
					internalCode += line + "\n";
				}
			}
			//Closing Internal SourceReader
			if(internalCount == 0 && recordingInternal){
				internalCode += line;
				recordingInternal = false;
				internalReaders.add(new SourceReader(internalCode));
				internalCode = "";
			}
			//Unpacking Global _Variables and _Functions
			if(readBlock && !recordingInternal){
				blockCode += line + "\n";
			}
			if(line.contains(" ") && !isInnerLine(line)){
				String cL = line;
				if(cL.contains("=")) cL = cL.substring(0, cL.indexOf('=')).trim();
				if(cL.contains("(") && openBracesCount <= 1){
					try {
						String parameters = cL.substring(cL.indexOf('(') + 1, cL.indexOf(')')).trim();
						cL = cL.substring(0, cL.indexOf('('));
						if(cL.contains(" ")){
							String name = cL.substring(cL.lastIndexOf(' ') + 1).trim();
							cL = cL.substring(0, cL.lastIndexOf(' ')).trim();
							if(cL.contains(" ")){
								String type = cL.substring(cL.lastIndexOf(' ') + 1).trim();
								cL = cL.substring(0, cL.lastIndexOf(' ')).trim();
								if(cL.contains(" ")){
									String mods = cL.substring(cL.indexOf(' ') + 1).trim();
									cL = cL.substring(0, cL.indexOf(' ')).trim();
									String access = cL;
									if(name.equals(className)){
										access = type;
										type = "";
									}
                                             if(name.contains(".")) continue;
                                             type = evaluateType(type);
                                             name = name.contains("(") ? name.substring(0, name.indexOf('(')).trim() : name;
                                             if(type != null)
									     dataMembers.add(new DataMember(access, mods, type, name + "()", parameters));
									dataBlocks.add(new DataBlock(dataMembers.getLast()));
									readBlock = true;
									blockCode += line + "\n";
								}
								else{
									String access = cL;
									if(name.equals(className)){
										access = type;
										type = "";
									}
                                             if(name.contains(".")) continue;
                                             type = evaluateType(type);
                                             name = name.contains("(") ? name.substring(0, name.indexOf('(')).trim() : name;
                                             if(type != null)
									     dataMembers.add(new DataMember(access, "", type, name + "()", parameters));
									dataBlocks.add(new DataBlock(dataMembers.getLast()));
									readBlock = true;
									blockCode += line + "\n";
								}
							}
							else{
								String type = cL;
                                        if(name.contains(".")) continue;
								dataMembers.add(new DataMember(name.equals(className) ? type : "", "", name.equals(className) ? "" : type, name + "()", parameters));
								dataBlocks.add(new DataBlock(dataMembers.getLast()));
								readBlock = true;
								blockCode += line + "\n";
							}
						}
					}catch(Exception e) {}
				}
				else if(openBracesCount == 0){
                         if(cL.startsWith("final ")){
                              cL = cL.substring(cL.indexOf(' ') + 1).trim();
                         }
					if(cL.trim().endsWith(";")){
						cL = cL.substring(0, cL.lastIndexOf(';')).trim();
					}
					String name = cL.substring(cL.lastIndexOf(' ') + 1).trim();
                         if(!cL.contains(" ")) continue;
					cL = cL.substring(0, cL.lastIndexOf(' '));
					if(cL.contains(" ")){
						String type = cL.substring(cL.lastIndexOf(' ') + 1).trim();
						cL = cL.substring(0, cL.lastIndexOf(' '));
						if(cL.contains(" ")){
							String mods = cL.substring(cL.indexOf(' ') + 1).trim();
							cL = cL.substring(0, cL.indexOf(' ')).trim();
							String access = cL;
                                   if(name.contains(".")) continue;
                                   type = evaluateType(type);
                                   if(type != null)
							     dataMembers.add(new DataMember(access, mods, type, name, null));
						}
						else{
							String access = cL;
                                   if(name.contains(".")) continue;
                                   type = evaluateType(type);
                                   if(type != null)
							     dataMembers.add(new DataMember(access, "", type, name, null));
						}
					}
					else{
						String type = cL;
                              if(name.contains(".")) 
                                   continue;
                              type = evaluateType(type);
                              if(type != null)
						     dataMembers.add(new DataMember("", "", type, name, null));
					}
				}
			}
			//Balancing CloseBraces
			if(line.contains("}")) {
				openBracesCount -= CodeFramework.count(line, '}');
				if(openBracesCount == 0 && readBlock){
					if(blockCode.endsWith("}\n")){
						blockCode = blockCode.substring(0, blockCode.lastIndexOf('}'));
					}
                         if(blockCode.trim().length() > 1){
					     dataBlocks.getLast().read(blockCode);
                         }
					blockCode = "";
					readBlock = false;
				}
			}
		}
		String parent = this.parent;
		if(!parent.contains("."))
			parent = getPackage(parent);
		if(!CodeFramework.isSource(parent)) {
			ByteReader byteReader = null;
			if(Assembly.has(parent)) 
			     byteReader = Assembly.getReader(parent);
			else 
			     byteReader = omega.Screen.getFileView().getJDKManager().prepareReader(parent);
			byteReader.dataMembers.forEach(this::offer);
		}
		else {
			SourceReader reader = new SourceReader(CodeFramework.getContent(parent));
			reader.dataMembers.forEach(this::offer);
		}
		if(features != null) {
			for(String f : features) {
				if(!f.contains("."))
					f = getPackage(f);
				if(!CodeFramework.isSource(f)) {
					ByteReader byteReader = null;
					if(Assembly.has(f)) 
					     byteReader = Assembly.getReader(f);
					else 
					     byteReader = omega.Screen.getFileView().getJDKManager().prepareReader(f);
					byteReader.dataMembers.forEach(this::offer);
				}
				else {
					SourceReader reader = new SourceReader(CodeFramework.getContent(f));
					reader.dataMembers.forEach(this::offer);
				}
			}
		}
	}

	public void offer(DataMember d) {
		for(DataMember dx : dataMembers) {
			if(dx.name.equals(d.name) && dx.parameterCount == d.parameterCount && dx.type.equals(d.type)) {
				if(dx.parameters != null && d.parameters != null && !dx.parameters.equals(d.parameters)) continue;
				return;
			}
		}
		dataMembers.add(d);
	}

	public static boolean isInnerLine(String line){
		try{
			return line.contains("else") 
			     || (line.contains("if") && !Character.isLetter(line.charAt(line.indexOf("if") + 2))) 
		          || (line.contains("for") && !Character.isLetter(line.charAt(line.indexOf("for") + 3))) 
		          || (line.contains("do") && !Character.isLetter(line.charAt(line.indexOf("do") + 2)))
		          || (line.contains("switch") 
		          && !Character.isLetter(line.charAt(line.indexOf("switch") + 5))) 
		          || line.contains("return ") 
		          || line.contains("case");
		}
		catch(Exception e){
			try{
				return line.contains("else") 
				|| (line.contains("if") && !Character.isLetter(line.charAt(line.indexOf("if") - 1))) 
				|| (line.contains("for") && !Character.isLetter(line.charAt(line.indexOf("for") - 1))) 
				|| line.contains("do") 
				|| (line.contains("switch") 
				&& !Character.isLetter(line.charAt(line.indexOf("switch") - 1))) 
				|| line.contains("return ") 
				|| line.contains("case");
			}
			catch(Exception e1){
			     return false;
		     }
		}
	}

	public LinkedList<DepthMember> getMembers(DataMember m, String code){
		LinkedList<DepthMember> depthMembers = new LinkedList<>();
		if(code != null && !code.equals(this.code)){
			this.code = code;
			read();
		}
		for(DataBlock b : dataBlocks){
			if(b.block.name.equals(m.name) && b.block.parameters != null &&b.block.parameterCount == m.parameterCount)
				return b.depthMembers;
		}
		return depthMembers;
	}

	public String getType(String name){
		for(DataMember m : dataMembers){
			if(m.name.equals(name)) return m.type;
		}
		return null;
	}

	public boolean isInternalReader(String className) {
		for(SourceReader r : internalReaders) {
			if(r.className.equals(className))
				return true;
		}
		return false;
	}

     public String evaluateType(String type){
	     if(" byte short int float double boolean long char void ".contains(type))
               return type;
          type = getPackage(type);
          return (type != null && !type.equals("")) ? type : null;
     }

	public String getPackage(String className){
		for(Import im : imports){
			if(im.name.equals(className)) return im.get();
		}
		if(CodeFramework.isSource(pack + "." + className))
			return pack + "." + className;
		for(SourceReader r : internalReaders) {
			if(r.className.equals(className))
				return pack + "." + this.className + "." + r.className;
		}
		return null;
	}

	public LinkedList<DataMember> getConstructors(){
		LinkedList<DataMember> constructors = new LinkedList<>();
		dataMembers.forEach(d->{
			if(d.parameters != null){
				if(d.name.equals(className+"()") && d.type.equals(""))
					constructors.add(d);
			}
		});
		if(constructors.isEmpty()){
			constructors.add(new DataMember("public", "", "", className, ""));
		}
		return constructors;
	}

	public LinkedList<DataMember> getDataMembers(String modifier){
		LinkedList<DataMember> members = new LinkedList<>();
		dataMembers.forEach(d->{
			String mod = d.modifier;
			if(!modifier.equals("") && mod.contains(modifier)){
				members.add(d);
			}
			else if(modifier.equals("") && !mod.contains("static")){
				members.add(d);
			}
		});
		return members;
	}

	@Override
	public String toString(){
		String f = "";
		if(features != null){
			for(String fx : features)
				f += fx + " ";
		}
		return "[type - " + type + ", name - " + className + ", modifier - " + modifier + ", access - " + access + ", parent - " + parent + ", features - " + f.trim() + "]";
	}
}