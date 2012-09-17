Setting up ZombieJam in Eclipse
===============================

1. Open Eclipse, and create a new Java Project.
2. Right click on your newly-created project and select 'Import'.
3. Select General->File System for the import source and click 'Next'.
4. Now, hit browse and choose the zombie-jam directory.
5. Next, hit the checkbox next to the zombie-jam folder in the middle pane.  Then, hit 'Finish'.
6. Your newly-created project should be populated with the code from zombie-jam WITH ERRORS.
7. Now, right click on the project and select 'Properties'.
8. Under 'Java Build Path', you need to add a few jars using the 'Add JARs' button.
9. Add all of the JARs in the /lib/ folder in your newly-created project.
10. Right click on lwjglw.jar under 'Referenced Libraries' and choose 'Properties'.  Under 'Native Library', set the location path to the /lib/lwjgl-natives/YOUR-OPERATING-SYSTEM.