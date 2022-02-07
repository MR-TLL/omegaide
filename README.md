<p align="center">
  <img width="140" src="https://raw.githubusercontent.com/omegaui/omegaide/main/res/omega_ide_icon128.png" />  
  <h2 align="center">Omega IDE</h2>
  <p align="center">The Blazing Fast Java IDE and an Instant IDE for any programming language</p>
</p>
<p align="center">
  <a href="https://github.com/omegaui/omegaide/issues">
    <img src="https://img.shields.io/github/issues/omegaui/omegaide"/> 
  </a>
  <a href="https://github.com/omegaui/omegaide/network/members">
    <img src="https://img.shields.io/github/forks/omegaui/omegaide"/> 
  </a>  
  <a href="https://github.com/omegaui/omegaide/stargazers">
    <img src="https://img.shields.io/github/stars/omegaui/omegaide"/> 
    <a href="https://github.com/omegaui/omegaide/LICENSE">
  </a>
    <img src="https://img.shields.io/github/license/omegaui/omegaide"/> 
  </a>
</p>

<p align="center">
  <a href="https://github.com/omegaui/omegaide/Donate.md">
    <h1>do{nate}</h1>
  </a>
</p>


Remember, A [wiki](https://github.com/omegaui/omegaide/wiki/Omega-IDE-Wiki-Home-Page) is a useful stuff.

**Omega IDE v2.2 - The Awesome Beta**

![](/res/light.png)

![](/res/dark.png)


**Omega IDE v2.1 - Latest Stable**

![](/images/light.png)

![](/images/dark.png)


Omega IDE is a **blazing fast Java IDE promising performance** even on minimal resource environment.

Omega IDE can also be used as a **Instant IDE for any** other **programming language** as well.

While Using Omega IDE you need not to worry about your RAM Usage 😁️.

Omega IDE takes a minute disk space.

It has a beautiful text editor with code folding, thanks to [rsyntaxtextarea](https://github.com/bobbylight/RSyntaxTextArea).

The Default Swing Components use the [FlatLaf Layer](https://www.formdev.com/flatlaf)

It tries to reassemble a material look so that it is easy to adapt the highly customized UI.

The credit of having meaningful and beautiful icons goes to [icons8.com](https://icons8.com)

**It has a elegant UI filled with custom components(have a look at sources at src/omega/comp).**

**The component library of Omega IDE is independent of any other IDE component**

**As a result You can use this component library under the terms of GNU GPL v3**


Want to have a look before giving a try?

See what's new in the latest release on the official channel!

[![](/images/youtube-icon.png)](https://www.youtube.com/channel/UCpuQLV8MfuHaWHYSq-PRFXg)


# Editor Key Bindings

Page 12 & 13, Instruction Panel, Help Section.


# Installing
Dependency **JDK 17** or above for current beta & stable versions


If you want to get early access to optimizations and new features you can freely use latest beta available.

[Check out the dailybuild(.jar) now](https://raw.githubusercontent.com/omegaui/omegaide/main/out/Omega%20IDE-dailybuild.jar).

**Available installation formats**

**.jar** Portable Java Archive

**.deb** Debian Setup


# Please Read -- For Java Projects Only

After opening a project which was not created with Omega IDE don't forget to select the JDK from Settings first else the tools may not work as expected.

![](/images/java-settings.png)

All the algorithms of Omega IDE rely on Java Conventions.

As a result the IDE will not work as intended if you miss so.

You cannot create a class without a package, this means that using default packages
is prohibited in Omega IDE.

Also when creating a source file you have specify the full-qualified name.

Like this,

![](/images/file-wizard.png)


# Creating a Project

On a fresh install, the IDE will ask you to point to a directory to be used as a workspace.

Then, you will be greeted with a launcher window.

There are separate project wizard for Java and Non-Java Projects.

![](/images/launcher.png)


## Java

![](/images/java-wizard.png)

The Project Wizard has a field to set the JDK Root.
JDK Root is the place where all the JDKs are located.

On Linux, the path is usually **/usr/lib/jvm**

On Windows, the path is usually like **C:\Program Files\Java** or **C:\Program Files(x86)\Java** based on the architecture.

Just type in the right path or navigate in the file chooser dialog.

Then, you can choose from the available JDKs to be used.


## Any Programming Language

![](/images/non-java-wizard.png)

Just Specify your project name and setup things in the IDE itself.


# Setting Up a Project


## Java

For Java Type Projects, there is a combined management tool for managing class-path & module-path.

You can open it by clicking the **Project** Menu and by clicking **Manage Class-path** or **Manage Module-path**.

![](/images/dependency-manager.png)


## Any Programming Language

Ok! Speaking Straightforward, there is no gui tool to manage dependencies for other programming languages.

But wait, you can set this up by yourself from the settings window.

There is a different Settings Window for Non-Java Projects.

Just Open Preferences, there you will see two fields labelled compile-time and run-time.

![](/images/non-java-settings.png)

Fill in the compile-time and the run-time command(s) and don't forget to select the working directory.

*If there is no compile-time argument in your project then you can leave the compile-time field empty*

There is one more field component below.

This component can be used to make a list of paths of source files before compilation.

Like the one we need in compiling a groovy project.

The first field is asking the extension of the file and the second one is asking the file name in which the paths will be written.

The **"** component is for surrounding the paths within double-quotes if you want.

Don't forget to Click Apply else you will lose changes.

# Running a Project

If any editor is opened, then by hitting CTRL + SHIFT + R launches the Project.

Else click the Run button in the ToolMenu.

Right Clicking the Run Button launches the Project without Build.

# Contributing

Well, I recommend using Omega IDE for editing its own sources for a smoother experience.

From Omega IDE v1.9 and onwards,

Omega IDE uses portable project infomation technique (.projectInfo) which means you need not to setup your project classpath whenever you change your working computer if your dependencies are inside the project folder (say all libraries are inside **lib** and all resources are inside **res**).

Just clone this repository and open the folder **omegaide** in Omega IDE.


Contents of .projectInfo

```
>JDK Path
-/usr/lib/jvm/jdk-16
>Main Class
-omega.IDE
>Project Classpath : Required Jars
-project-root$/lib/flatlaf-0.46.jar
>Project Classpath : Required Resource Roots
-project-root$/res

```

Omega IDE will itself recognize and setup the project classpath automatically.

If you prefer using terminal,
the project classpath is:

The **lib** folder contains the class-path dependenies.

The **res** is the resource-root.

# Note

Omega IDE is not yet totally evolved which means it has no integrated support for any version control system yet and bugs are likely to come.

# See What's Included

[Here at the official Github Page](https://omegaui.github.io/omegaide)


# Hope You Like It


<p align="center">
  </a>
    <a href="https://instagram.com/i_am_arham_92">
    <img src="https://instagram.fdel1-4.fna.fbcdn.net/v/t51.2885-19/s150x150/240866652_1034091963994553_6501518522533409578_n.jpg?_nc_ht=instagram.fdel1-4.fna.fbcdn.net&_nc_cat=105&_nc_ohc=I95VnGmI9dUAX9zCQEm&edm=ABfd0MgBAAAA&ccb=7-4&oh=00_AT-CcO6awg0buE3xK_K5BqYjo8ZG6ZFoVd1maLpLLfA2GQ&oe=61FAEA1D&_nc_sid=7bff83"/> 
  </a>
</p>


Want to Donate for the Project?

**Google Pay** : UPI ID 1 -> **arhamfar22@okaxis**

**Google Pay** : UPI ID 2 -> **arhamfar22@okicici**

**QR Code**

![](/images/qr_code.jpg)

