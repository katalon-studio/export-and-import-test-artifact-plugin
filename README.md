## Katalon Studio export and import test artifacts plugin

The main purpose is:

- Exporting test cases and test objects into .zip file.
- Importing test cases and test objects from .zip file.

#### Build
Requirements:

- JDK 1.8
- Maven 3.3+

Build

`mvn clean package`

#### How to test in Katalon Studio

- Checkout or get a build of branch `staging-plugin` of KS
- After KS opens, please click on `Plugin` menu, select `Install Plugin` and choose the generated jar file.
- If you want to reload this plugin, please click on `Plugin` menu, select `Uninstall Plugin` then select `Install Plugin` again. 
